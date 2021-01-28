/*
 * Copyright 2020 Google LLC
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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.MeaninglessTripNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that each trip must have more than one stop to be meaningful.
 *
 * <p>Generated notice: {@link MeaninglessTripNotice}.
 */
@GtfsValidator
public class TripNumberOfStopsValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;

  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      final String tripId = trip.tripId();
      // When the number of stops for the corresponding trip is no more than one, a meaningless trip
      // notice is generated.
      if ((stopTimeTable.byTripId(tripId)).size() <= 1) {
        noticeContainer.addNotice(new MeaninglessTripNotice(tripId, trip.csvRowNumber()));
      }
    }
  }
}
