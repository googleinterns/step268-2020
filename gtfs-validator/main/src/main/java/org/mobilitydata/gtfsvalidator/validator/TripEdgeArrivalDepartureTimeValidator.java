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

import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.MissingTripEdgeStopTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that trip edges (first and last stops) for a trip define both arrival and departure
 * stop times, for all trips.
 *
 * <p>Generated notice: {@link MissingTripEdgeStopTimeNotice} each time this is false.
 */
@GtfsValidator
public class TripEdgeArrivalDepartureTimeValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;

  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      final String tripId = trip.tripId();
      List<GtfsStopTime> stopTimesForTrip = stopTimeTable.byTripId(tripId);

      if (stopTimesForTrip.isEmpty()) {
        return;
      }

      GtfsStopTime tripFirstStop = stopTimesForTrip.get(0);
      GtfsStopTime tripLastStop = stopTimesForTrip.get(stopTimesForTrip.size() - 1);

      if (!tripFirstStop.arrivalTime()) {
        noticeContainer.addNotice(new MissingTripEdgeStopTimeNotice(
            "arrival_time", tripId, tripFirstStop.csvRowNumber(), tripFirstStop.stopSequence()));
      }
      if (!tripFirstStop.departureTime()) {
        noticeContainer.addNotice(new MissingTripEdgeStopTimeNotice(
            "departure_time", tripId, tripFirstStop.csvRowNumber(), tripFirstStop.stopSequence()));
      }
      if (!tripLastStop.arrivalTime()) {
        noticeContainer.addNotice(new MissingTripEdgeStopTimeNotice(
            "arrival_time", tripId, tripLastStop.csvRowNumber(), tripLastStop.stopSequence()));
      }
      if (!tripLastStop.departureTime()) {
        noticeContainer.addNotice(new MissingTripEdgeStopTimeNotice(
            "departure_time", tripId, tripLastStop.csvRowNumber(), tripLastStop.stopSequence()));
      }
    }
  }
}
