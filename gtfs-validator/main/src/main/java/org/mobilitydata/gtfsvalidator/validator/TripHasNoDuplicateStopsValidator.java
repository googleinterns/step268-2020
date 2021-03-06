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

import com.google.auto.value.AutoValue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.TripWithDuplicateStopNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Uses AutoValue to compare duplicate stop information easier.
 * And avoid hash collisions.
 */
@AutoValue
abstract class DuplicateStopIdentifier {
  static DuplicateStopIdentifier create(String routeId, String stopId1, String stopId2) {
    return new AutoValue_DuplicateStopIdentifier(routeId, stopId1, stopId2);
  }

  abstract String routeId();

  abstract String stopId1();

  abstract String stopId2();
}

/**
 * Validates that for each trip that consecutive stop times do not have the same stop name.
 * It will only generate one notice per route, so if multiple trips on the same route have
 * the duplicate there will only be 1 notice.
 *
 * [Warning]
 *
 * <p>Generated notice: {@link TripWithDuplicateStopNotice}.
 */
@GtfsValidator
public class TripHasNoDuplicateStopsValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;

  @Inject GtfsStopTableContainer stopTable;

  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Set<DuplicateStopIdentifier> seenDuplicateStops = new HashSet<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      final String tripId = trip.tripId();
      List<GtfsStopTime> stopTimesForTrip = stopTimeTable.byTripId(tripId);

      // Save the information about the previous stop to compare
      String prevStopName = null;
      String prevStopId = null;
      long prevCsvRowNumber = -1;

      // Iterate through the stops consecutively based on time
      for (GtfsStopTime stopTime : stopTimesForTrip) {
        GtfsStop stop = stopTable.byStopId(stopTime.stopId());
        if (prevStopName != null) {
          // Create the key with the route and the stop id's alphabetically
          DuplicateStopIdentifier duplicateStopIdentifier = null;
          if (prevStopId.compareTo(stop.stopId()) >= 0) {
            duplicateStopIdentifier =
                DuplicateStopIdentifier.create(trip.routeId(), prevStopId, stop.stopId());
          } else {
            duplicateStopIdentifier =
                DuplicateStopIdentifier.create(trip.routeId(), stop.stopId(), prevStopId);
          }

          if (prevStopName.equals(stop.stopName())) {
            // Only create the notice once for each combination of stops
            if (!seenDuplicateStops.contains(duplicateStopIdentifier)) {
              noticeContainer.addNotice(
                  new TripWithDuplicateStopNotice(stop.stopName(), prevStopId, prevCsvRowNumber,
                      stop.stopId(), stop.csvRowNumber(), trip.routeId(), trip.tripId()));
            }
          }
          seenDuplicateStops.add(duplicateStopIdentifier);
        }
        prevStopName = stop.stopName();
        prevStopId = stop.stopId();
        prevCsvRowNumber = stop.csvRowNumber();
      }
    }
  }
}