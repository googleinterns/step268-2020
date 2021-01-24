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

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class StopTooFarFromTripShapeValidatorTest {
    public static GtfsStop createStop(
            long csvRowNumber,
            String stopId,
            double stopLat,
            double stopLon,
            GtfsLocationType locationType) {
        return new GtfsStop.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setStopId(stopId)
                .setStopLat(stopLat)
                .setStopLon(stopLon)
                .setLocationType(locationType.getNumber())
                .build();
    }

    public static GtfsStopTime createStopTime(
            long csvRowNumber, String tripId, String stopId, int stopSequence) {
        return new GtfsStopTime.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setTripId(tripId)
                .setStopId(stopId)
                .setStopSequence(stopSequence)
                .build();
    }

    public static GtfsShape createShape(
            long csvRowNumber,
            String shapeId,
            double shapePtLat,
            double shapePtLon,
            int shapePtSequence) {
        return new GtfsShape.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setShapeId(shapeId)
                .setShapePtLat(shapePtLat)
                .setShapePtLon(shapePtLon)
                .setShapePtSequence(shapePtSequence)
                .build();
    }

    public static GtfsTrip createTrip(long csvRowNumber, String tripId, String shapeId) {
        return new GtfsTrip.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setTripId(tripId)
                .setShapeId(shapeId)
                .build();
    }

    @Test
    public void stopWithinTripShapeShouldNotGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at
        // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
        final NoticeContainer noticeContainer = new NoticeContainer();
        StopTooFarFromTripShapeValidator validator = new StopTooFarFromTripShapeValidator();

        // Create stopTable:
        List<GtfsStop> stops = new ArrayList<>();
        stops.add(
                createStop(1, "1001", 28.05811731042478d, -82.41616877502503d, GtfsLocationType.STOP));
        stops.add(
                createStop(2, "1002", 28.05812364854794d, -82.41617370439423d, GtfsLocationType.STOP));
        validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);

        // Create stopTimeTable:
        List<GtfsStopTime> stopTimes = new ArrayList<>();
        stopTimes.add(createStopTime(1, "trip1", "1001", 1));
        stopTimes.add(createStopTime(2, "trip1", "1002", 2));
        validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

        // Create shapeTable:
        List<GtfsShape> shapes = new ArrayList<>();
        shapes.add(createShape(1, "shape1", 28.05724310653972d, -82.41350776611507d, 1));
        shapes.add(createShape(2, "shape1", 28.05746701492806d, -82.41493135129478d, 2));
        shapes.add(createShape(3, "shape1", 28.05800068503469d, -82.4159394137605d, 3));
        shapes.add(createShape(4, "shape1", 28.05808869825447d, -82.41648754043338d, 4));
        shapes.add(createShape(5, "shape1", 28.05809979887893d, -82.41773971025437d, 5));
        validator.shapeTable = GtfsShapeTableContainer.forEntities(shapes, noticeContainer);

        // Create tripTable:
        List<GtfsTrip> trips = new ArrayList<>();
        trips.add(createTrip(1, "trip1", "shape1"));
        validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

        validator.validate(noticeContainer);
        assertThat(noticeContainer.getNotices()).isEmpty();
    }

    @Test
    public void stopOutsideTripShapeShouldGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at
        // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
        final NoticeContainer noticeContainer = new NoticeContainer();
        StopTooFarFromTripShapeValidator validator = new StopTooFarFromTripShapeValidator();

        // Create stopTable:
        List<GtfsStop> stops = new ArrayList<>();
        stops.add(
                createStop(1, "1001", 28.05811731042478d, -82.41616877502503d, GtfsLocationType.STOP));
        stops.add(
                createStop(2, "1002", 28.05812364854794d, -82.41617370439423d, GtfsLocationType.STOP));
        // stopId "1003" is the location OUTSIDE buffer.
        stops.add(createStop(3, "1003", 28.05673053256373d, -82.4170801432763d, GtfsLocationType.STOP));
        validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);

        // Create stopTimeTable:
        List<GtfsStopTime> stopTimes = new ArrayList<>();
        stopTimes.add(createStopTime(1, "trip1", "1001", 1));
        stopTimes.add(createStopTime(2, "trip1", "1002", 2));
        stopTimes.add(createStopTime(3, "trip1", "1003", 3));
        validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

        // Create shapeTable:
        List<GtfsShape> shapes = new ArrayList<>();
        shapes.add(createShape(1, "shape1", 28.05724310653972d, -82.41350776611507d, 1));
        shapes.add(createShape(2, "shape1", 28.05746701492806d, -82.41493135129478d, 2));
        shapes.add(createShape(3, "shape1", 28.05800068503469d, -82.4159394137605d, 3));
        shapes.add(createShape(4, "shape1", 28.05808869825447d, -82.41648754043338d, 4));
        shapes.add(createShape(5, "shape1", 28.05809979887893d, -82.41773971025437d, 5));
        validator.shapeTable = GtfsShapeTableContainer.forEntities(shapes, noticeContainer);

        // Create tripTable:
        List<GtfsTrip> trips = new ArrayList<>();
        trips.add(createTrip(1, "trip1", "shape1"));
        validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

        validator.validate(noticeContainer);
        assertThat(noticeContainer.getNotices())
                .containsExactly(
                        new StopTooFarFromTripShapeNotice(
                                "1003", 3, "trip1", "shape1", StopTooFarFromTripShapeValidator.TRIP_BUFFER_METERS));
    }
}
