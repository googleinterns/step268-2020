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

import com.google.common.collect.Multimaps;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

/**
 * Validates that a stop is within a distance threshold for a trip shape.
 * <p>
 * Generated notices:
 * * StopTooFarFromTripShapeNotice
 */
@GtfsValidator
public class StopTooFarFromTripShapeValidator extends FileValidator {
    @Inject GtfsStopTimeTableContainer stopTimeTable;

    @Inject GtfsTripTableContainer tripTable;

    @Inject GtfsShapeTableContainer shapeTable;

    @Inject GtfsStopTableContainer stopTable;

    // Spatial operation buffer values
    public static final double TRIP_BUFFER_METERS = 100;
    private static final double TRIP_BUFFER_DEGREES = DistanceUtils.KM_TO_DEG * (TRIP_BUFFER_METERS / 1000.0d);

    @Override
    public void validate(NoticeContainer noticeContainer) {
        // Cache for previously tested shape_id and stop_id pairs - no need to test them more than once.
        final Map<String, List<String>> testedCache = new HashMap<>();
        // Go through the pair of tripId and the corresponding stop time list one by one.
        for (Map.Entry<String, List<GtfsStopTime>> tripIdStopTimeListEntry : Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
            final String tripId = tripIdStopTimeListEntry.getKey();
            final List<GtfsStopTime> stopTimeList = tripIdStopTimeListEntry.getValue();
            final GtfsTrip trip = tripTable.byTripId(tripId);
            if (trip == null || stopTimeList == null || stopTimeList.isEmpty() || trip.shapeId() == null) {
                // This rule only applies when all necessary fields exist.
                continue;
            }
            final List<GtfsShape> shapeList = shapeTable.byShapeId(trip.shapeId());
            if (shapeList == null || shapeList.isEmpty()) {
                continue;
            }
            // Get the trip shape polygon given the Gtfs shapes data and TRIP_BUFFER_DEGREES.
            Shape tripShapeGivenThreshold = createTripShapePolygonGivenThreshold(shapeList);
            // Check if each stop is within the buffer polygon.
            for (GtfsStopTime stopTime : stopTimeList) {
                GtfsStop stop = stopTable.byStopId(stopTime.stopId());
                if (stop == null) {
                    continue;
                }
                if (!(stop.locationType() == GtfsLocationType.STOP) && !(stop.locationType() == GtfsLocationType.BOARDING_AREA)) {
                    // This rule only applies to stops of location_type "STOP" or "BOARDING_AREA".
                    continue;
                }
                if (!testedCache.containsKey(trip.shapeId())) {
                    // Record the shape_id and stop_id pair when shape_id never appeared before.
                    List<String> stopIds = new ArrayList<>();
                    stopIds.add(stop.stopId());
                    testedCache.put(trip.shapeId(), stopIds);
                } else if (testedCache.get(trip.shapeId()).contains(stop.stopId())) {
                    // Skip tested shape_id and stop_id pair.
                    continue;
                } else {
                    // Record the new shape_id and stop_id pair when shape_id has appeared.
                    testedCache.get(trip.shapeId()).add(stop.stopId());
                }
                // Check whether the stop position is within an acceptable distance threshold from the trip shape.
                Point p = getShapeFactory().pointXY(stop.stopLon(), stop.stopLat());
                if (!tripShapeGivenThreshold.relate(p).equals(SpatialRelation.CONTAINS)) {
                    noticeContainer.addNotice(new StopTooFarFromTripShapeNotice(stop.stopId(), stopTime.stopSequence(), trip.tripId(), trip.shapeId(), TRIP_BUFFER_METERS));
                }
            }
        }
    }

    /** Create the trip shape polygon from the Gtfs shapes data and a given distance threshold from a stop to a trip shape. */
    private static Shape createTripShapePolygonGivenThreshold(List<GtfsShape> shapeList) {
        // Create a polyline from the Gtfs shapes data.
        ShapeFactory.LineStringBuilder lineBuilder = getShapeFactory().lineString();
        for (GtfsShape shape : shapeList) {
            lineBuilder.pointXY(shape.shapePtLon(), shape.shapePtLat());
        }
        Shape shapeLine = lineBuilder.build();
        // Create the buffered version of the trip as a polygon considering distance threshold.
        Shape shapeBuffer = shapeLine.getBuffered(TRIP_BUFFER_DEGREES, shapeLine.getContext());
        return shapeBuffer;
    }

    private static ShapeFactory getShapeFactory() {
        return GEO.getShapeFactory();
    }
}
