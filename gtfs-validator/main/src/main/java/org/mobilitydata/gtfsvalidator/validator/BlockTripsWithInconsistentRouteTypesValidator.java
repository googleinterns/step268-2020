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
import java.util.HashMap;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.BlockTripsWithInconsistentRouteTypesNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/** Uses AutoValue to record necessary trip information. */
@AutoValue
abstract class TripRouteTypeIdentifier {
  static TripRouteTypeIdentifier create(
      GtfsRouteType routeType, String tripId, long tripCsvRowNumber, String routeId) {
    return new AutoValue_TripRouteTypeIdentifier(routeType, tripId, tripCsvRowNumber, routeId);
  }

  abstract GtfsRouteType routeType();

  abstract String tripId();

  abstract long tripCsvRowNumber();

  abstract String routeId();
}

/**
 * Validates trips in the same block have consistent route types.
 *
 * <p>Generated notices: {@link BlockTripsWithInconsistentRouteTypesNotice}.
 */
@GtfsValidator
public class BlockTripsWithInconsistentRouteTypesValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsRouteTableContainer routeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Store trip information that includes route type and other fields which need to be added to
    // the notice
    final Map<String, TripRouteTypeIdentifier> blockTripInfoStorage = new HashMap<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      // Referring to route table, find the route type corresponding to the routeId of a specific
      // trip
      final GtfsRouteType routeType = routeTable.byRouteId(trip.routeId()).routeType();
      // If the block has appeared, check whether the corresponding route type is the same as record
      if (blockTripInfoStorage.containsKey(trip.blockId())) {
        final TripRouteTypeIdentifier recordedBlockTripInfo =
            blockTripInfoStorage.get(trip.blockId());
        // If the route types for the trips in the same block are different, a notice is generated
        if (recordedBlockTripInfo.routeType().getNumber() != routeType.getNumber()) {
          noticeContainer.addNotice(
              new BlockTripsWithInconsistentRouteTypesNotice(
                  /* blockId= */ trip.blockId(),
                  /* tripCsvRowNumberA= */ recordedBlockTripInfo.tripCsvRowNumber(),
                  /* tripIdA= */ recordedBlockTripInfo.tripId(),
                  /* routeTypeA= */ recordedBlockTripInfo.routeType(),
                  /* routeIdA= */ recordedBlockTripInfo.routeId(),
                  /* tripCsvRowNumberB= */ trip.csvRowNumber(),
                  /* tripIdB= */ trip.tripId(),
                  /* routeTypeB= */ routeType,
                  /* routeIdB= */ trip.routeId()));
        }
      } else {
        // If the block has never appeared, combine the necessary trip information as a
        // TripRouteTypeIdentifier autovalue and put it as the value of the HashMap with blockId of
        // the trip as key
        blockTripInfoStorage.put(
            trip.blockId(),
            TripRouteTypeIdentifier.create(
                routeType, trip.tripId(), trip.csvRowNumber(), trip.routeId()));
      }
    }
  }
}
