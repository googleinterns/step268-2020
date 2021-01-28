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
import org.mobilitydata.gtfsvalidator.notice.MissingTripEdgeStopTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class TripEdgeArrivalDepartureTimeValidatorTest {
  private final GtfsTrip trip1 =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip1").build();
  private final GtfsTrip trip2 =
      new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("trip2").build();

  private final GtfsStopTime startStopTimeHasBoth =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(18)
          .setTripId("trip1")
          .setStopId("1001")
          .setStopSequence(1)
          .setArrivalTime(GtfsTime.fromString("12:02:34"))
          .setDepartureTime(GtfsTime.fromString("12:03:34"))
          .build();
  private final GtfsStopTime startStopTimeHasArrival =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(18)
          .setTripId("trip1")
          .setStopId("1001")
          .setStopSequence(1)
          .setArrivalTime(GtfsTime.fromString("12:02:34"))
          .build();
  private final GtfsStopTime startStopTimeHasDeparture =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(18)
          .setTripId("trip2")
          .setStopId("1001")
          .setStopSequence(1)
          .setDepartureTime(GtfsTime.fromString("12:03:34"))
          .build();
  private final GtfsStopTime startStopTimeHasNeither = new GtfsStopTime.Builder()
                                                           .setCsvRowNumber(18)
                                                           .setTripId("trip2")
                                                           .setStopId("1001")
                                                           .setStopSequence(1)
                                                           .build();
  private final GtfsStopTime endStopTimeHasBoth =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(23)
          .setTripId("trip1")
          .setStopId("1003")
          .setStopSequence(3)
          .setArrivalTime(GtfsTime.fromString("12:52:34"))
          .setDepartureTime(GtfsTime.fromString("12:53:34"))
          .build();
  private final GtfsStopTime endStopTimeHasArrival =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(23)
          .setTripId("trip1")
          .setStopId("1003")
          .setStopSequence(3)
          .setArrivalTime(GtfsTime.fromString("12:52:34"))
          .build();
  private final GtfsStopTime endStopTimeHasDeparture =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(23)
          .setTripId("trip2")
          .setStopId("1003")
          .setStopSequence(3)
          .setDepartureTime(GtfsTime.fromString("12:53:34"))
          .build();
  private final GtfsStopTime endStopTimeHasNeither = new GtfsStopTime.Builder()
                                                         .setCsvRowNumber(23)
                                                         .setTripId("trip2")
                                                         .setStopId("1003")
                                                         .setStopSequence(3)
                                                         .build();
  private final GtfsStopTime middleStop1 = new GtfsStopTime.Builder()
                                               .setCsvRowNumber(41)
                                               .setTripId("trip1")
                                               .setStopId("1002")
                                               .setStopSequence(2)
                                               .build();
  private final GtfsStopTime middleStop2 = new GtfsStopTime.Builder()
                                               .setCsvRowNumber(41)
                                               .setTripId("trip2")
                                               .setStopId("1002")
                                               .setStopSequence(2)
                                               .build();
  @Test
  public void validShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripEdgeArrivalDepartureTimeValidator validator = new TripEdgeArrivalDepartureTimeValidator();

    // Create stopTimeTable:
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(startStopTimeHasBoth);
    stopTimes.add(middleStop1);
    stopTimes.add(endStopTimeHasBoth);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    // Create tripTable:
    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(trip1);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.validate(noticeContainer);
    System.out.println(noticeContainer.exportJson());
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void missingDepartureTimesForStartAndEndStop() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripEdgeArrivalDepartureTimeValidator validator = new TripEdgeArrivalDepartureTimeValidator();

    // Create stopTimeTable:
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(startStopTimeHasArrival);
    stopTimes.add(middleStop1);
    stopTimes.add(endStopTimeHasArrival);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    // Create tripTable:
    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(trip1);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices().size()).isEqualTo(2);
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip1", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip1", 3));
  }

  @Test
  public void missingArrivalTimeForStartAndEndStops() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripEdgeArrivalDepartureTimeValidator validator = new TripEdgeArrivalDepartureTimeValidator();

    // Create stopTimeTable:
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(startStopTimeHasDeparture);
    stopTimes.add(middleStop2);
    stopTimes.add(endStopTimeHasDeparture);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    // Create tripTable:
    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(trip2);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices().size()).isEqualTo(2);
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 3));
  }

  @Test
  public void missingArrivalAndDepartureForStartAndEndStop() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripEdgeArrivalDepartureTimeValidator validator = new TripEdgeArrivalDepartureTimeValidator();

    // Create stopTimeTable:
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(startStopTimeHasNeither);
    stopTimes.add(middleStop2);
    stopTimes.add(endStopTimeHasNeither);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    // Create tripTable:
    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(trip2);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices().size()).isEqualTo(4);
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip2", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip2", 3));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 3));
  }

  @Test
  public void testMultipleTripsShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripEdgeArrivalDepartureTimeValidator validator = new TripEdgeArrivalDepartureTimeValidator();

    // Create stopTimeTable:
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(startStopTimeHasBoth);
    stopTimes.add(middleStop1);
    stopTimes.add(endStopTimeHasArrival);
    stopTimes.add(startStopTimeHasNeither);
    stopTimes.add(middleStop2);
    stopTimes.add(endStopTimeHasNeither);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    // Create tripTable:
    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(trip1);
    trips.add(trip2);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices().size()).isEqualTo(5);
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip1", 3));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip2", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("departure_time", "trip2", 3));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 1));
    assertThat(noticeContainer.getNotices())
        .contains(new MissingTripEdgeStopTimeNotice("arrival_time", "trip2", 3));
  }
}
