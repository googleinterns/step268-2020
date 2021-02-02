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

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteWithDuplicateStopNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class RouteHasNoDuplicateStopsValidatorTest {
  private final GtfsTrip trip1_route1 =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip1").setRouteId("route1").build();
  private final GtfsTrip trip2_route1 =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip2").setRouteId("route1").build();
  private final GtfsTrip trip3_route2 =
      new GtfsTrip.Builder().setCsvRowNumber(1).setTripId("trip3").setRouteId("route2").build();

  private final GtfsStopTime stopTimeTrip1_Stop1 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(18)
                                             .setTripId("trip1")
                                             .setStopId("1001")
                                             .setStopSequence(1)
                                             .build();
  private final GtfsStopTime stopTimeTrip1_Stop2 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(15)
                                             .setTripId("trip1")
                                             .setStopId("1002")
                                             .setStopSequence(2)
                                             .build();
  private final GtfsStopTime stopTimeTrip1_Stop3 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(15)
                                             .setTripId("trip1")
                                             .setStopId("1003")
                                             .setStopSequence(3)
                                             .build();                                           
  private final GtfsStopTime stopTimeTrip2_Stop1 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(19)
                                             .setTripId("trip2")
                                             .setStopId("1001")
                                             .setStopSequence(1)
                                             .build();
  private final GtfsStopTime stopTimeTrip2_Stop2 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(16)
                                             .setTripId("trip2")
                                             .setStopId("1002")
                                             .setStopSequence(2)
                                             .build();      
  private final GtfsStopTime stopTimeTrip3_Stop1 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(19)
                                             .setTripId("trip3")
                                             .setStopId("1001")
                                             .setStopSequence(1)
                                             .build();
  private final GtfsStopTime stopTimeTrip3_Stop2 = new GtfsStopTime.Builder()
                                             .setCsvRowNumber(16)
                                             .setTripId("trip3")
                                             .setStopId("1002")
                                             .setStopSequence(2)
                                             .build();                                                                                     
  private final GtfsStop stopId1001 =
      new GtfsStop.Builder().setCsvRowNumber(3).setStopId("1001").setStopName("stop1").build();
  private final GtfsStop stopId1002 =
      new GtfsStop.Builder().setCsvRowNumber(4).setStopId("1002").setStopName("stop1").build();
  private final GtfsStop stopId1003 =
      new GtfsStop.Builder().setCsvRowNumber(18).setStopId("1003").setStopName("stop3").build();    
  
  @Test
  public void routeWithDuplicateShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteHasNoDuplicateStopsValidator validator = new RouteHasNoDuplicateStopsValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_route1), noticeContainer);

    // Create stopTimeTable:
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTimeTrip1_Stop1, stopTimeTrip1_Stop2), noticeContainer);

    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stopId1001, stopId1002), noticeContainer);
    
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .contains(new RouteWithDuplicateStopNotice(/* stopName = */ "stop1",
            /* prevStopId  = */ "1001", /* prevCsvRowNumber = */ 3, /* stopId  = */ "1002",
            /* csvRowNumber = */ 4, /* routeId = */ "route1"));
  }
  
  @Test
  public void routeWithRepeatedDuplicateShouldGenerateOneNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteHasNoDuplicateStopsValidator validator = new RouteHasNoDuplicateStopsValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_route1, trip2_route1), noticeContainer);

    // Create stopTimeTable:
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTimeTrip1_Stop1, stopTimeTrip1_Stop2, stopTimeTrip2_Stop1, stopTimeTrip2_Stop2), noticeContainer);

    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stopId1001, stopId1002), noticeContainer);
    
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new RouteWithDuplicateStopNotice(/* stopName = */ "stop1",
            /* prevStopId  = */ "1001", /* prevCsvRowNumber = */ 3, /* stopId  = */ "1002",
            /* csvRowNumber = */ 4, /* routeId = */ "route1"));
  }

  @Test
  public void routeNoDuplicatesShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteHasNoDuplicateStopsValidator validator = new RouteHasNoDuplicateStopsValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_route1), noticeContainer);

    // Create stopTimeTable:
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTimeTrip1_Stop2, stopTimeTrip1_Stop3), noticeContainer);

    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stopId1002, stopId1003), noticeContainer);
    
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
  
  @Test
  public void shouldGenerateNoticeForEachRepeatedDuplicateOnEachRoute() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteHasNoDuplicateStopsValidator validator = new RouteHasNoDuplicateStopsValidator();

    // Create tripTable:
    validator.tripTable =
        GtfsTripTableContainer.forEntities(Arrays.asList(trip1_route1, trip2_route1, trip3_route2), noticeContainer);

    // Create stopTimeTable:
    validator.stopTimeTable = GtfsStopTimeTableContainer.forEntities(
        Arrays.asList(stopTimeTrip1_Stop1, stopTimeTrip1_Stop2, stopTimeTrip2_Stop1, stopTimeTrip2_Stop2, stopTimeTrip3_Stop1, stopTimeTrip3_Stop2), noticeContainer);

    // Create stopTable:
    validator.stopTable =
        GtfsStopTableContainer.forEntities(Arrays.asList(stopId1001, stopId1002), noticeContainer);
    
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new RouteWithDuplicateStopNotice(/* stopName = */ "stop1",
            /* prevStopId  = */ "1001", /* prevCsvRowNumber = */ 3, /* stopId  = */ "1002",
            /* csvRowNumber = */ 4, /* routeId = */ "route1"), new RouteWithDuplicateStopNotice(/* stopName = */ "stop1",
            /* prevStopId  = */ "1001", /* prevCsvRowNumber = */ 3, /* stopId  = */ "1002",
            /* csvRowNumber = */ 4, /* routeId = */ "route2"));

  }
}
