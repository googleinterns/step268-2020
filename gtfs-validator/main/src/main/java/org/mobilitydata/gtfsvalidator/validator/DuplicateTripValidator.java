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

import java.util.Map;
import java.util.HashMap;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DuplicateTripNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that trip edges (first and last stops) for a trip define both arrival and departure
 * stop times, for all trips.
 *
 * <p>Generated notice: {@link MissingTripEdgeStopTimeNotice} each time this is false.
 */
@GtfsValidator
public class DuplicateTripValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<String, Long> tripIdAndCsv = new HashMap<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      final String tripId = trip.tripId();
      if (tripIdAndCsv.containsKey(tripId)) {
        noticeContainer.addNotice(new DuplicateTripNotice(tripId, trip.csvRowNumber(),tripIdAndCsv.get(tripId)));
      }
      tripIdAndCsv.put(tripId, trip.csvRowNumber());
    }
  }
}
