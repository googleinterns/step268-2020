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

    @Test
    public void stopWithinTripShapeShouldNotGenerateNotice() {
        // See map of trip shape and stops (in GeoJSON) at
        // https://gist.github.com/barbeau/d9c0b90a26a3e2ba105cae5f0e8aec4a
        final NoticeContainer noticeContainer = new NoticeContainer();
        StopTooFarFromTripShapeValidator validator = new StopTooFarFromTripShapeValidator();

        // Create stopTable:
        List<GtfsStop> stops = new ArrayList<>();
        stops.add(
                new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("1001")
                        .setStopLat(28.05811731042478d)
                        .setStopLon(-82.41616877502503d)
                        .setLocationType(GtfsLocationType.STOP.getNumber())
                        .build());
        stops.add(
                new GtfsStop.Builder()
                        .setCsvRowNumber(2)
                        .setStopId("1002")
                        .setStopLat(28.05812364854794d)
                        .setStopLon(-82.41617370439423d)
                        .setLocationType(GtfsLocationType.STOP.getNumber())
                        .build());
        validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);

        // Create stopTimeTable:
        List<GtfsStopTime> stopTimes = new ArrayList<>();
        stopTimes.add(
                new GtfsStopTime.Builder()
                        .setCsvRowNumber(1)
                        .setTripId("trip1")
                        .setStopId("1001")
                        .setStopSequence(1)
                        .build());
        stopTimes.add(
                new GtfsStopTime.Builder()
                        .setCsvRowNumber(2)
                        .setTripId("trip1")
                        .setStopId("1002")
                        .setStopSequence(2)
                        .build());
        validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

        // Create shapeTable:
        List<GtfsShape> shapes = new ArrayList<>();
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(1)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05724310653972d)
                        .setShapePtLon(-82.41350776611507d)
                        .setShapePtSequence(1)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(2)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05746701492806d)
                        .setShapePtLon(-82.41493135129478d)
                        .setShapePtSequence(2)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(3)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05800068503469d)
                        .setShapePtLon(-82.4159394137605d)
                        .setShapePtSequence(3)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(4)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05808869825447d)
                        .setShapePtLon(-82.41648754043338d)
                        .setShapePtSequence(4)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(5)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05809979887893d)
                        .setShapePtLon(-82.41773971025437d)
                        .setShapePtSequence(5)
                        .build());
        validator.shapeTable = GtfsShapeTableContainer.forEntities(shapes, noticeContainer);

        // Create tripTable:
        List<GtfsTrip> trips = new ArrayList<>();
        trips.add(
                new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip1").setShapeId("shape1").build());
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
                new GtfsStop.Builder()
                        .setCsvRowNumber(1)
                        .setStopId("1001")
                        .setStopLat(28.05811731042478d)
                        .setStopLon(-82.41616877502503d)
                        .setLocationType(GtfsLocationType.STOP.getNumber())
                        .build());
        stops.add(
                new GtfsStop.Builder()
                        .setCsvRowNumber(2)
                        .setStopId("1002")
                        .setStopLat(28.05812364854794d)
                        .setStopLon(-82.41617370439423d)
                        .setLocationType(GtfsLocationType.STOP.getNumber())
                        .build());
        // stopId "1003" is the location OUTSIDE buffer.
        stops.add(
                new GtfsStop.Builder()
                        .setCsvRowNumber(3)
                        .setStopId("1003")
                        .setStopLat(28.05673053256373d)
                        .setStopLon(-82.4170801432763d)
                        .setLocationType(GtfsLocationType.STOP.getNumber())
                        .build());
        validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);

        // Create stopTimeTable:
        List<GtfsStopTime> stopTimes = new ArrayList<>();
        stopTimes.add(
                new GtfsStopTime.Builder()
                        .setCsvRowNumber(1)
                        .setTripId("trip1")
                        .setStopId("1001")
                        .setStopSequence(1)
                        .build());
        stopTimes.add(
                new GtfsStopTime.Builder()
                        .setCsvRowNumber(2)
                        .setTripId("trip1")
                        .setStopId("1002")
                        .setStopSequence(2)
                        .build());
        stopTimes.add(
                new GtfsStopTime.Builder()
                        .setCsvRowNumber(3)
                        .setTripId("trip1")
                        .setStopId("1003")
                        .setStopSequence(3)
                        .build());
        validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

        // Create shapeTable:
        List<GtfsShape> shapes = new ArrayList<>();
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(1)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05724310653972d)
                        .setShapePtLon(-82.41350776611507d)
                        .setShapePtSequence(1)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(2)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05746701492806d)
                        .setShapePtLon(-82.41493135129478d)
                        .setShapePtSequence(2)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(3)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05800068503469d)
                        .setShapePtLon(-82.4159394137605d)
                        .setShapePtSequence(3)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(4)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05808869825447d)
                        .setShapePtLon(-82.41648754043338d)
                        .setShapePtSequence(4)
                        .build());
        shapes.add(
                new GtfsShape.Builder()
                        .setCsvRowNumber(5)
                        .setShapeId("shape1")
                        .setShapePtLat(28.05809979887893d)
                        .setShapePtLon(-82.41773971025437d)
                        .setShapePtSequence(5)
                        .build());
        validator.shapeTable = GtfsShapeTableContainer.forEntities(shapes, noticeContainer);

        // Create tripTable:
        List<GtfsTrip> trips = new ArrayList<>();
        trips.add(
                new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip1").setShapeId("shape1").build());
        validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

        validator.validate(noticeContainer);
        assertThat(noticeContainer.getNotices())
                .containsExactly(
                        new StopTooFarFromTripShapeNotice(
                                "1003", 3, "trip1", "shape1", StopTooFarFromTripShapeValidator.TRIP_BUFFER_METERS));
    }
}
