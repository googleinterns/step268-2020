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

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates for each trip that the travel speed between each stop is not too fast.
 *
 * <p>Generated notice: {@link FastTravelBetweenStopsNotice}.
 */
@GtfsValidator
public class TripTravelSpeedValidator extends FileValidator {
  private final static float METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR = 3.6f;
  private final static int FAST_SPEED_METERS_PER_SECOND = 42; // approx 150 km/h
  private final static int KILOMETER_TO_METER_CONVERSION_FACTOR = 1000;
  // TODO consider all timezones and world timings
  private final static int SECONDS_IN_DAY = 24 * 60 * 60;

  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Inject GtfsTripTableContainer tripTable;

  @Inject GtfsStopTableContainer stopTable;

  private static ShapeFactory getShapeFactory() {
    return GEO.getShapeFactory();
  }

  private static DistanceCalculator getDistanceCalculator() {
    return GEO.getDistCalc();
  }

  public double distanceBetweenMeter(double fromLat, double fromLng, double toLat, double toLng) {
    final ShapeFactory shapeFactory = getShapeFactory();
    final DistanceCalculator distanceCalculator = getDistanceCalculator();
    final Point origin = shapeFactory.pointXY(fromLng, fromLat);
    final Point destination = shapeFactory.pointXY(toLng, toLat);
    return (DistanceUtils.DEG_TO_KM * distanceCalculator.distance(origin, destination)
        * KILOMETER_TO_METER_CONVERSION_FACTOR);
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      final String tripId = trip.tripId();
      List<GtfsStopTime> stopTimesForTrip = stopTimeTable.byTripId(tripId);

      // Save the information about the previous stop to compare
      GtfsTime prevStopDepartureTime = null;
      double prevStopLatitude = -91;
      double prevStopLongitude = -181;

      // used to accumulate distance between stops with same arrival and departure times
      int prevStopAccumulatedDistanceMeter = 0;
      final List<Integer> prevStopAccumulatedStopSequence = new ArrayList<>();

      // Iterate through the stops consecutively based on time
      for (GtfsStopTime stopTime : stopTimesForTrip) {
        GtfsStop currentStop = stopTable.byStopId(stopTime.stopId());
        double currentStopLat = currentStop.stopLat();
        double currentStopLon = currentStop.stopLon();

        GtfsTime currentArrivalTime = stopTime.arrivalTime();
        double distanceFromPreviousStopMeter = 0;

        boolean sameArrivalAndDeparture = false;
        if (prevStopDepartureTime != null && currentArrivalTime != null) {
          sameArrivalAndDeparture = currentArrivalTime.equals(prevStopDepartureTime);
          if (!sameArrivalAndDeparture) {
            int durationSecond = currentArrivalTime.getSecondsSinceMidnight()
                - prevStopDepartureTime.getSecondsSinceMidnight();

            // Deal with the case stops go over midnight
            if (currentArrivalTime.isBefore(prevStopDepartureTime)) {
              durationSecond = SECONDS_IN_DAY
                  - (prevStopDepartureTime.getSecondsSinceMidnight()
                      - currentArrivalTime.getSecondsSinceMidnight());
            }

            distanceFromPreviousStopMeter = distanceBetweenMeter(
                prevStopLatitude, prevStopLongitude, currentStopLat, currentStopLon);
            double distanceMeter = distanceFromPreviousStopMeter + prevStopAccumulatedDistanceMeter;
            double speedMeterPerSecond = distanceMeter / durationSecond;
            if (speedMeterPerSecond > FAST_SPEED_METERS_PER_SECOND) {
              prevStopAccumulatedStopSequence.add(stopTime.stopSequence());
              noticeContainer.addNotice(new FastTravelBetweenStopsNotice(tripId,
                  speedMeterPerSecond * METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR,
                  new ArrayList<>(prevStopAccumulatedStopSequence)));
            }
          }
        }

        // Prepare data for next iteration
        if (sameArrivalAndDeparture) {
          prevStopAccumulatedDistanceMeter += distanceFromPreviousStopMeter;
        } else {
          prevStopAccumulatedDistanceMeter = 0;
          prevStopAccumulatedStopSequence.clear();
        }

        prevStopDepartureTime = stopTime.departureTime();
        prevStopLatitude = currentStopLat;
        prevStopLongitude = currentStopLon;
        prevStopAccumulatedStopSequence.add(stopTime.stopSequence());
      }
    }
  }
}
