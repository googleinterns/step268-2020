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
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.BlockTripsWithInconsistentRouteTypesNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@RunWith(JUnit4.class)
public class BlockTripsWithInconsistentRouteTypesValidatorTest {
  private BlockTripsWithInconsistentRouteTypesValidator validator =
      new BlockTripsWithInconsistentRouteTypesValidator();
  private final GtfsTrip trip11 =
      new GtfsTrip.Builder()
          .setCsvRowNumber(1)
          .setTripId("trip11")
          .setRouteId("R1")
          .setBlockId("BlockApple")
          .build();
  private final GtfsTrip trip111 =
      new GtfsTrip.Builder()
          .setCsvRowNumber(2)
          .setTripId("trip111")
          .setRouteId("R2")
          .setBlockId("BlockApple")
          .build();
  private final GtfsTrip trip1111 =
      new GtfsTrip.Builder()
          .setCsvRowNumber(3)
          .setTripId("trip1111")
          .setRouteId("R3")
          .setBlockId("BlockBanana")
          .build();
  private final GtfsTrip trip22 =
      new GtfsTrip.Builder()
          .setCsvRowNumber(4)
          .setTripId("trip22")
          .setRouteId("R4")
          .setBlockId("BlockApple")
          .build();
  private final GtfsTrip trip222 =
      new GtfsTrip.Builder()
          .setCsvRowNumber(5)
          .setTripId("trip222")
          .setRouteId("R5")
          .setBlockId("BlockBanana")
          .build();
  private final GtfsRoute route1 =
      new GtfsRoute.Builder()
          .setCsvRowNumber(1)
          .setRouteId("R1")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();
  private final GtfsRoute route2 =
      new GtfsRoute.Builder()
          .setCsvRowNumber(2)
          .setRouteId("R2")
          .setRouteType(GtfsRouteType.RAIL.getNumber())
          .build();
  private final GtfsRoute route3 =
      new GtfsRoute.Builder()
          .setCsvRowNumber(3)
          .setRouteId("R3")
          .setRouteType(GtfsRouteType.FERRY.getNumber())
          .build();
  private final GtfsRoute route4 =
      new GtfsRoute.Builder()
          .setCsvRowNumber(4)
          .setRouteId("R4")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();
  private final GtfsRoute route5 =
      new GtfsRoute.Builder()
          .setCsvRowNumber(5)
          .setRouteId("R5")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();

  @Test
  public void sameBlockTripsWithDifferentRouteTypesShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTrip> trips = new ArrayList<>(Arrays.asList(trip11, trip111));
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(route1, route2));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new BlockTripsWithInconsistentRouteTypesNotice(
                /* blockId= */ "BlockApple",
                /* tripCsvRowNumberA= */ 1,
                /* tripIdA= */ "trip11",
                /* routeTypeA= */ GtfsRouteType.BUS,
                /* routeIdA= */ "R1",
                /* tripCsvRowNumberB= */ 2,
                /* tripIdB= */ "trip111",
                /* routeTypeB= */ GtfsRouteType.RAIL,
                /* routeIdB= */ "R2"));
  }

  @Test
  public void differentBlockTripsWithDifferentRouteTypesShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTrip> trips = new ArrayList<>(Arrays.asList(trip11, trip1111));
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(route1, route3));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void differentBlockTripsWithSameRouteTypesShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTrip> trips = new ArrayList<>(Arrays.asList(trip11, trip222));
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(route1, route5));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void sameBlockTripsWithSameRouteTypesShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTrip> trips = new ArrayList<>(Arrays.asList(trip11, trip22));
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(route1, route4));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void blockTripsCombination() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTrip> trips =
        new ArrayList<>(Arrays.asList(trip11, trip111, trip1111, trip22, trip222));
    validator.tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(route1, route2, route3, route4, route5));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new BlockTripsWithInconsistentRouteTypesNotice(
                /* blockId= */ "BlockApple",
                /* tripCsvRowNumberA= */ 1,
                /* tripIdA= */ "trip11",
                /* routeTypeA= */ GtfsRouteType.BUS,
                /* routeIdA= */ "R1",
                /* tripCsvRowNumberB= */ 2,
                /* tripIdB= */ "trip111",
                /* routeTypeB= */ GtfsRouteType.RAIL,
                /* routeIdB= */ "R2"),
            new BlockTripsWithInconsistentRouteTypesNotice(
                /* blockId= */ "BlockBanana",
                /* tripCsvRowNumberA= */ 3,
                /* tripIdA= */ "trip1111",
                /* routeTypeA= */ GtfsRouteType.FERRY,
                /* routeIdA= */ "R3",
                /* tripCsvRowNumberB= */ 5,
                /* tripIdB= */ "trip222",
                /* routeTypeB= */ GtfsRouteType.BUS,
                /* routeIdB= */ "R5"));
  }
}
