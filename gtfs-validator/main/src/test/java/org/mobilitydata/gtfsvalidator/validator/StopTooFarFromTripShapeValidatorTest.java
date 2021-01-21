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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.ArrayList;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class StopTooFarFromTripShapeValidatorTest {

    public static GtfsStop createStop(long csvRowNumber, String stopId, double stopLat, double stopLon, GtfsLocationType locationType) {
        return new GtfsStop.Builder().setCsvRowNumber(csvRowNumber).setStopId(stopId).setStopLat(stopLat).setStopLon(stopLon).setLocationType(locationType.getNumber()).build();
    }

    public static GtfsStopTime createStopTime(long csvRowNumber, String tripId, String stopId, int stopSequence) {
        return new GtfsStopTime.Builder().setCsvRowNumber(csvRowNumber).setTripId(tripId).setStopId(stopId).setStopSequence(stopSequence).build();
    }

    public static GtfsShape createShape(long csvRowNumber, String shapeId, double shapePtLat, double shapePtLon, int shapePtSequence) {
        return new GtfsShape.Builder().setCsvRowNumber(csvRowNumber).setShapeId(shapeId).setShapePtLat(shapePtLat).setShapePtLon(shapePtLon).setShapePtSequence(shapePtSequence).build();
    }

    public static GtfsTrip createTrip(long csvRowNumber, String tripId, String shapeId) {
        return new GtfsTrip.Builder().setCsvRowNumber(csvRowNumber).setTripId(tripId).setShapeId(shapeId).build();
    }

    private static GtfsStopTableContainer createStopTable(String[] stopIds, double[] stopLats, double[] stopLons, GtfsLocationType[] locationTypes, NoticeContainer noticeContainer) {
        Preconditions.checkArgument(stopIds.length == stopLats.length, "stopIds.length must be equal to stopLats.length");
        Preconditions.checkArgument(stopLats.length == stopLons.length, "stopLats.length must be equal to stopLons.length");
        Preconditions.checkArgument(stopIds.length == locationTypes.length, "stopIds.length must be equal to locationTypes.length");
        List<GtfsStop> stops = new ArrayList<>();
        for (int i = 0; i < stopIds.length; i++) {
            stops.add(createStop(stops.size() + 1, stopIds[i], stopLats[i], stopLons[i], locationTypes[i]));
        }
        return GtfsStopTableContainer.forEntities(stops, noticeContainer);
    }

    private static GtfsStopTimeTableContainer createStopTimeTable(String[] tripIds, String[] stopIds, int[] stopSequences, NoticeContainer noticeContainer) {
        Preconditions.checkArgument(tripIds.length == stopIds.length, "tripIds.length must be equal to stopIds.length");
        Preconditions.checkArgument(stopIds.length == stopSequences.length, "stopIds.length must be equal to stopSequences.length");
        List<GtfsStopTime> stopTimes = new ArrayList<>();
        for (int i = 0; i < stopIds.length; i++) {
            stopTimes.add(createStopTime(stopTimes.size() + 1, tripIds[i], stopIds[i], stopSequences[i]));
        }
        return GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
    }

    private static GtfsShapeTableContainer createShapeTable(String[] shapeIds, double[] shapePtLats, double[] shapePtLons, int[] shapePtSequences, NoticeContainer noticeContainer) {
        Preconditions.checkArgument(shapeIds.length == shapePtLats.length, "shapeIds.length must be equal to shapePtLats.length");
        Preconditions.checkArgument(shapePtLats.length == shapePtLons.length, "shapePtLats.length must be equal to shapePtLons.length");
        Preconditions.checkArgument(shapeIds.length == shapePtSequences.length, "shapeIds.length must be equal to shapePtSequences.length");
        List<GtfsShape> shapes = new ArrayList<>();
        for (int i = 0; i < shapePtLats.length; i++) {
            shapes.add(createShape(shapes.size() + 1, shapeIds[i], shapePtLats[i], shapePtLons[i], shapePtSequences[i]));
        }
        return GtfsShapeTableContainer.forEntities(shapes, noticeContainer);
    }

    private static GtfsTripTableContainer createTripTable(String[] tripIds, String[] shapeIds, NoticeContainer noticeContainer) {
        Preconditions.checkArgument(tripIds.length == shapeIds.length, "tripIds.length must be equal to shapeIds.length");
        List<GtfsTrip> trips = new ArrayList<>();
        for (int i = 0; i < tripIds.length; i++) {
            trips.add(createTrip(trips.size() + 1, tripIds[i], shapeIds[i]));
        }
        return GtfsTripTableContainer.forEntities(trips, noticeContainer);
    }

    @Test
    public void stopWithinTripShapeShouldNotGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
        final NoticeContainer noticeContainer = new NoticeContainer();
        StopTooFarFromTripShapeValidator validator = new StopTooFarFromTripShapeValidator();
        validator.stopTable = createStopTable(
            new String[]{"1001", "1002"},
            new double[]{28.05811731042478d, 28.05812364854794d},
            new double[]{-82.41616877502503d, -82.41617370439423d},
            new GtfsLocationType[]{GtfsLocationType.STOP, GtfsLocationType.STOP},
            noticeContainer);
        validator.stopTimeTable = createStopTimeTable(
            new String[]{"trip1", "trip1"},
            new String[]{"1001", "1002"},
            new int[]{1, 2},
            noticeContainer);
        validator.shapeTable = createShapeTable(
            new String[]{"shape1", "shape1", "shape1", "shape1", "shape1"},
            new double[]{28.05724310653972d, 28.05746701492806d, 28.05800068503469d, 28.05808869825447d, 28.05809979887893d},
            new double[]{-82.41350776611507d, -82.41493135129478d, -82.4159394137605d, -82.41648754043338d, -82.41773971025437d},
            new int[]{1, 2, 3, 4, 5},
            noticeContainer);
        validator.tripTable = createTripTable(
            new String[]{"trip1"},
            new String[]{"shape1"},
            noticeContainer);
        validator.validate(noticeContainer);
        assertThat(noticeContainer.getNotices()).isEmpty();
    }

    @Test
    public void stopOutsideTripShapeShouldGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
        final NoticeContainer noticeContainer = new NoticeContainer();
        StopTooFarFromTripShapeValidator validator = new StopTooFarFromTripShapeValidator();
        validator.stopTable = createStopTable(
            // stopId "1003" is the location OUTSIDE buffer.
            new String[]{"1001", "1002", "1003"},
            new double[]{28.05811731042478d, 28.05812364854794d, 28.05673053256373d},
            new double[]{-82.41616877502503d, -82.41617370439423d, -82.4170801432763d},
            new GtfsLocationType[]{GtfsLocationType.STOP, GtfsLocationType.STOP, GtfsLocationType.STOP},
            noticeContainer);
        validator.stopTimeTable = createStopTimeTable(
            new String[]{"trip1", "trip1", "trip1"},
            new String[]{"1001", "1002", "1003"},
            new int[]{1, 2, 3},
            noticeContainer);
        validator.shapeTable = createShapeTable(
            new String[]{"shape1", "shape1", "shape1", "shape1", "shape1"},
            new double[]{28.05724310653972d, 28.05746701492806d, 28.05800068503469d, 28.05808869825447d, 28.05809979887893d},
            new double[]{-82.41350776611507d, -82.41493135129478d, -82.4159394137605d, -82.41648754043338d, -82.41773971025437d},
            new int[]{1, 2, 3, 4, 5},
            noticeContainer);
        validator.tripTable = createTripTable(
            new String[]{"trip1"},
            new String[]{"shape1"},
            noticeContainer);
        validator.validate(noticeContainer);
        assertThat(noticeContainer.getNotices()).containsExactly(
            new StopTooFarFromTripShapeNotice("1003", 3, "trip1", "shape1", StopTooFarFromTripShapeValidator.TRIP_BUFFER_METERS)
        );
    }
}
