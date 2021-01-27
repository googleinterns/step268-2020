/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.DecreasingStopTimeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

@RunWith(JUnit4.class)
public class StopTimeIncreasingDistanceValidatorTest {
  private final GtfsStopTime stopTime1 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(1)
                                             .setTripId("trip1")
                                             .setStopId("1001")
                                             .setStopSequence(1)
                                             .setShapeDistTraveled(1.0)
                                             .build();
  private final GtfsStopTime stopTime2 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(2)
                                             .setTripId("trip1")
                                             .setStopId("1002")
                                             .setStopSequence(2)
                                             .setShapeDistTraveled(1.5)
                                             .build();
  private final GtfsStopTime stopTime3 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(3)
                                             .setTripId("trip1")
                                             .setStopId("1003")
                                             .setStopSequence(3)
                                             .setShapeDistTraveled(1.4)
                                             .build();
  private final GtfsStopTime stopTime4 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(3)
                                             .setTripId("trip1")
                                             .setStopId("1003")
                                             .setStopSequence(3)
                                             .setShapeDistTraveled(1.5)
                                             .build();

  @Test
  public void stopTimeIncreasingDistanceShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    final StopTimeIncreasingDistanceValidator validator = new StopTimeIncreasingDistanceValidator();

    // Create stopTimeTable:
    final List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTime1);
    stopTimes.add(stopTime2);
    validator.table = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void stopTimeDecreasingDistanceShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    final StopTimeIncreasingDistanceValidator validator = new StopTimeIncreasingDistanceValidator();

    // Create stopTimeTable:
    final List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTime1);
    stopTimes.add(stopTime2);
    stopTimes.add(stopTime3);
    validator.table = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new DecreasingStopTimeDistanceNotice("trip1", 3, 3, 1.4, 2, 2, 1.5));
  }

  // When distance travelled doesn't increase or decrease (stays constant), it does not generate a
  // notice
  @Test
  public void stopTimeNotChangingDistanceShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    final StopTimeIncreasingDistanceValidator validator = new StopTimeIncreasingDistanceValidator();

    // Create stopTimeTable:
    final List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTime1);
    stopTimes.add(stopTime2);
    stopTimes.add(stopTime4);
    validator.table = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
}
