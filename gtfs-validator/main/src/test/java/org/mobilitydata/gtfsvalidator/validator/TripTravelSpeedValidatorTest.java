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

import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class TripTravelSpeedValidatorTest {
  private final GtfsTrip trip1 =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip1").build();

  private final GtfsStopTime stopTime1 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(3)
                                             .setTripId("trip1")
                                             .setStopId("stopA")
                                             .setStopSequence(1)
                                             .setDepartureTime(GtfsTime.fromString("12:20:30"))
                                             .build();
  private final GtfsStopTime stopTime2 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(6)
                                             .setTripId("trip1")
                                             .setStopId("stopB")
                                             .setStopSequence(2)
                                             .setArrivalTime(GtfsTime.fromString("12:20:31"))
                                             .build();
  private final GtfsStopTime stopTime3 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(6)
                                             .setTripId("trip1")
                                             .setStopId("stopB")
                                             .setStopSequence(2)
                                             .setArrivalTime(GtfsTime.fromString("23:20:31"))
                                             .build();
  private final GtfsStop stop1 = new GtfsStop.Builder()
                                     .setCsvRowNumber(1)
                                     .setStopId("stopA")
                                     .setStopLat(28.06d)
                                     .setStopLon(-82.416d)
                                     .build();
  private final GtfsStop stop2 = new GtfsStop.Builder()
                                     .setCsvRowNumber(2)
                                     .setStopId("stopB")
                                     .setStopLat(28.08d)
                                     .setStopLon(-82.416d)
                                     .build();

  private final NoticeContainer noticeContainer = new NoticeContainer();
  private final TripTravelSpeedValidator validator = new TripTravelSpeedValidator();                                   
  
  @Test
  public void fastTravelGenerateNotice() {
    validator.tripTable = GtfsTripTableContainer.forEntities(Arrays.asList(trip1), noticeContainer);

    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTime1, stopTime2), noticeContainer);

    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stop1, stop2), noticeContainer);

    validator.validate(noticeContainer);
    
    assertThat(noticeContainer.getNotices().size()).isEqualTo(1);
    Map<String, Object> noticeInfo = noticeContainer.getNotices().get(0).getContext();
    assertThat(noticeInfo.get("tripId")).isEqualTo("trip1");
    // To compare doubles need to consider precision
    assertThat((double)noticeInfo.get("speedkmh")).isWithin(1.0e-10).of(8006.045528786268);
    assertThat(noticeInfo.get("stopSequenceList")).isEqualTo(Arrays.asList(1,2));
  }

  @Test
  public void slowTravelDoesNotGenerateNotice() {
    validator.tripTable = GtfsTripTableContainer.forEntities(Arrays.asList(trip1), noticeContainer);

    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTime1, stopTime3), noticeContainer);

    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stop1, stop2), noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
}
