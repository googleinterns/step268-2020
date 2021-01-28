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
import org.mobilitydata.gtfsvalidator.notice.MeaninglessTripNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class TripNumberOfStopsValidatorTest {
  private final GtfsTrip tripX =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("tripX").build();
  private final GtfsTrip tripY =
      new GtfsTrip.Builder().setCsvRowNumber(2).setTripId("tripY").build();
  private final GtfsTrip tripZ =
      new GtfsTrip.Builder().setCsvRowNumber(3).setTripId("tripZ").build();

  // For tripX:
  private final GtfsStopTime stopTimeXA =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(1)
          .setTripId("tripX")
          .setStopId("stopA")
          .setStopSequence(1)
          .build();
  private final GtfsStopTime stopTimeXB =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(2)
          .setTripId("tripX")
          .setStopId("stopB")
          .setStopSequence(2)
          .build();
  private final GtfsStopTime stopTimeXC =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(3)
          .setTripId("tripX")
          .setStopId("stopC")
          .setStopSequence(3)
          .build();
  // For tripZ:
  private final GtfsStopTime stopTimeZB =
      new GtfsStopTime.Builder()
          .setCsvRowNumber(4)
          .setTripId("tripZ")
          .setStopId("stopB")
          .setStopSequence(1)
          .build();

  @Test
  public void tripServingMoreThanOneStopShouldNotGenerateNotice() {
    // tripX has stopA, stopB, stopC in order
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripNumberOfStopsValidator validator = new TripNumberOfStopsValidator();

    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(tripX);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTimeXA);
    stopTimes.add(stopTimeXB);
    stopTimes.add(stopTimeXC);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void tripServingZeroStopShouldGenerateNotice() {
    // tripY has NO stop - Meaningless Trip
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripNumberOfStopsValidator validator = new TripNumberOfStopsValidator();

    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(tripY);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    validator.stopTimeTable =
        GtfsStopTimeTableContainer.forEntities(new ArrayList<>(), noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).containsExactly(new MeaninglessTripNotice("tripY", 2));
  }

  @Test
  public void tripServingOneStopShouldGenerateNotice() {
    // tripZ has only stopB - Meaningless Trip
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripNumberOfStopsValidator validator = new TripNumberOfStopsValidator();

    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(tripZ);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTimeZB);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).containsExactly(new MeaninglessTripNotice("tripZ", 3));
  }

  @Test
  public void tripsCombinationShouldGenerateCorrespondingNotices() {
    // tripX has stopA, stopB, stopC in order
    // tripY has NO stop - Meaningless Trip
    // tripZ has only stopB - Meaningless Trip
    final NoticeContainer noticeContainer = new NoticeContainer();
    TripNumberOfStopsValidator validator = new TripNumberOfStopsValidator();

    List<GtfsTrip> trips = new ArrayList<>();
    trips.add(tripX);
    trips.add(tripY);
    trips.add(tripZ);
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(stopTimeXA);
    stopTimes.add(stopTimeXB);
    stopTimes.add(stopTimeXC);
    stopTimes.add(stopTimeZB);
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new MeaninglessTripNotice("tripY", 2), new MeaninglessTripNotice("tripZ", 3));
  }
}
