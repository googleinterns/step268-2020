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

import java.lang.Double;
import java.util.Collections;
import java.util.List;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopsTooCloseNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates that two stops are not too close to each other.
 *
 * <p>Generated notice: {@link StopsTooCloseNotice}.
 */
@GtfsValidator
public class StopsTooCloseValidator extends FileValidator {
  private static final int KILOMETER_TO_METER_CONVERSION_FACTOR = 1000;
  /**
   * At equator 1 degree = 110.567 kilometers.
   * 0.00005 degrees = (110.567km * 1000) * 0.00005 = 5.52835 m
   * At poles 1 degree = 111.699 kilometers.
   * 0.00005 degrees = (111.699km * 1000) * 0.00005 = 5.58495 m
   *
   * So the latitude buffer is 0.00005 degrees to check stops within at least 5.52835 m
   */
  private static final double LAT_BUFFER = 0.00005;

  // If stops are closer than 5m based on distance calculated, generate a notice
  static final int DIST_BUFFER_METERS = 5;

  @Inject GtfsStopTableContainer stopTable;

  private static ShapeFactory getShapeFactory() {
    return GEO.getShapeFactory();
  }

  private static DistanceCalculator getDistanceCalculator() {
    return GEO.getDistCalc();
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    List<GtfsStop> allStops = stopTable.getEntities();
    Collections.sort(
        allStops, (stop1, stop2) -> { return Double.compare(stop1.stopLat(), stop2.stopLat()); });

    // Set up to calculate distance
    final ShapeFactory shapeFactory = getShapeFactory();
    final DistanceCalculator distanceCalculator = getDistanceCalculator();

    for (int i = 0; i < allStops.size(); i++) {
      GtfsStop stop1 = allStops.get(i);
      Point pointStop1 = shapeFactory.pointXY(stop1.stopLon(), stop1.stopLat());

      for (int j = i + 1; j < allStops.size(); j++) {
        GtfsStop stop2 = allStops.get(j);
        if (stop2.stopLat() - stop1.stopLat() < LAT_BUFFER) {
          Point pointStop2 = shapeFactory.pointXY(stop2.stopLon(), stop2.stopLat());

          double distBetweenStopsMeters = DistanceUtils.DEG_TO_KM
              * distanceCalculator.distance(pointStop1, pointStop2)
              * KILOMETER_TO_METER_CONVERSION_FACTOR;

          if (distBetweenStopsMeters < DIST_BUFFER_METERS) {
            noticeContainer.addNotice(new StopsTooCloseNotice(stop1.stopId(), stop1.csvRowNumber(),
                stop2.stopId(), stop2.csvRowNumber(), DIST_BUFFER_METERS));
          }
          // Otherwise stop and start checking the next point
        } else {
          break;
        }
      }
    }
  }
}
