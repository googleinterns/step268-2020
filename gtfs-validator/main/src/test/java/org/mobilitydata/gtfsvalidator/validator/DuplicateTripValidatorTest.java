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
import org.mobilitydata.gtfsvalidator.notice.DuplicateTripNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class DuplicateTripValidatorTest {
  private final GtfsTrip trip1_line21 =
      new GtfsTrip.Builder().setCsvRowNumber(21).setTripId("trip1").build();
  private final GtfsTrip trip1_line39 =
      new GtfsTrip.Builder().setCsvRowNumber(39).setTripId("trip1").build();
  private final GtfsTrip trip3_line43 =
      new GtfsTrip.Builder().setCsvRowNumber(43).setTripId("trip3").build();

  @Test
  public void validShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateTripValidator validator = new DuplicateTripValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_line21, trip3_line43), noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void duplicateTripCreateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateTripValidator validator = new DuplicateTripValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_line21, trip1_line39), noticeContainer);

    validator.validate(noticeContainer);
    System.out.println(noticeContainer.exportJson());
    assertThat(noticeContainer.getNotices())
        .contains(new DuplicateTripNotice(
            "trip1", /* csvRowNumberTrip1 = */ 39, /* csvRowNumberTrip2 = */ 21));
  }
}
