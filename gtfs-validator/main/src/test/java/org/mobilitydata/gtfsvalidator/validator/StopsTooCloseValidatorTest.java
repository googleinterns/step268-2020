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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopsTooCloseNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

@RunWith(JUnit4.class)
public class StopsTooCloseValidatorTest {
  private final GtfsStop stop1 = new GtfsStop.Builder()
                                     .setCsvRowNumber(1)
                                     .setStopId("1001")
                                     .setStopLat(28.0581d)
                                     .setStopLon(-82.416d)
                                     .build();
  private final GtfsStop stop2 = new GtfsStop.Builder()
                                     .setCsvRowNumber(2)
                                     .setStopId("1002")
                                     .setStopLat(28.0582d)
                                     .setStopLon(-82.416d)
                                     .build();
  private final GtfsStop stop3 = new GtfsStop.Builder()
                                     .setCsvRowNumber(3)
                                     .setStopId("1003")
                                     .setStopLat(28.05811d)
                                     .setStopLon(-82.416d)
                                     .build();

  private final NoticeContainer noticeContainer = new NoticeContainer();
  private final StopsTooCloseValidator validator = new StopsTooCloseValidator();

  @Test
  public void stopsSpacedOutShouldNotGenerateNotice() {
    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stop1, stop2), noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void stopsTooCloseGenerateNotice() {
    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stop1, stop2, stop3), noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new StopsTooCloseNotice(/** stopId1 = */ "1001", /** csvRowNumberStop1 = */ 1,
                /** stopId2 = */ "1003", /** csvRowNumberStop2 = */ 3,
                StopsTooCloseValidator.DIST_BUFFER_METERS));
  }
}
