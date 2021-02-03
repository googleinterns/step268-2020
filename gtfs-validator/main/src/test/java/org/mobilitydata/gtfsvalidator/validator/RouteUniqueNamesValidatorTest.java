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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteUniqueNamesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

@RunWith(JUnit4.class)
public class RouteUniqueNamesValidatorTest {
  private RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();
  private final GtfsRoute routeA =
      new GtfsRoute.Builder()
          .setCsvRowNumber(1)
          .setRouteId("routeA")
          .setRouteLongName("abcd")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();
  private final GtfsRoute routeB =
      new GtfsRoute.Builder()
          .setCsvRowNumber(2)
          .setRouteId("routeB")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();
  private final GtfsRoute routeC =
      new GtfsRoute.Builder()
          .setCsvRowNumber(3)
          .setRouteId("routeC")
          .setRouteLongName("abcd-long-name")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();
  private final GtfsRoute routeD =
      new GtfsRoute.Builder()
          .setCsvRowNumber(4)
          .setRouteId("routeD")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.LIGHT_RAIL.getNumber())
          .build();
  private final GtfsRoute routeE =
      new GtfsRoute.Builder()
          .setCsvRowNumber(5)
          .setRouteId("routeE")
          .setRouteLongName("abcd-long-name")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.SUBWAY.getNumber())
          .setAgencyId("agency1")
          .build();
  private final GtfsRoute routeF =
      new GtfsRoute.Builder()
          .setCsvRowNumber(6)
          .setRouteId("routeF")
          .setRouteLongName("abcd-long-name")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.SUBWAY.getNumber())
          .setAgencyId("agency2")
          .build();
  private final GtfsRoute routeG =
      new GtfsRoute.Builder()
          .setCsvRowNumber(7)
          .setRouteId("routeG")
          .setRouteLongName("abcd-long-name")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.SUBWAY.getNumber())
          .setAgencyId("agency1")
          .build();
  private final GtfsRoute routeH =
      new GtfsRoute.Builder()
          .setCsvRowNumber(8)
          .setRouteId("routeH")
          .setRouteShortName("abcd")
          .setRouteType(GtfsRouteType.BUS.getNumber())
          .build();

  @Test
  public void routeLongNameEqualsAnotherRouteShortName() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeA, routeB));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routeWithExtraFieldComparedToAnother() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeB, routeC));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routesWithDifferentRouteTypes() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeB, routeD));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routesWithDifferentAgencyIds() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeE, routeF));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routesWithSameNamesTypeAndAgencyShouldGenerateNoticeWithAllFields() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeE, routeG));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new RouteUniqueNamesNotice(
                /* routeId= */ "routeG",
                /* comparedRouteId= */ "routeE",
                /* routeLongName= */ "abcd-long-name",
                /* routeShortName= */ "abcd",
                /* routeType= */ GtfsRouteType.SUBWAY,
                /* agencyId= */ "agency1"));
  }

  @Test
  public void routesWithSameShortNameAndTypeShouldGenerateNoticeWithMissingFields() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes = new ArrayList<>(Arrays.asList(routeB, routeH));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new RouteUniqueNamesNotice(
                /* routeId= */ "routeH",
                /* comparedRouteId= */ "routeB",
                /* routeLongName= */ "",
                /* routeShortName= */ "abcd",
                /* routeType= */ GtfsRouteType.BUS,
                /* agencyId= */ ""));
  }

  @Test
  public void routesCombinationGenerateNotices() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsRoute> routes =
        new ArrayList<>(
            Arrays.asList(routeA, routeB, routeC, routeD, routeE, routeF, routeG, routeH));
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new RouteUniqueNamesNotice(
                /* routeId= */ "routeG",
                /* comparedRouteId= */ "routeE",
                /* routeLongName= */ "abcd-long-name",
                /* routeShortName= */ "abcd",
                /* routeType= */ GtfsRouteType.SUBWAY,
                /* agencyId= */ "agency1"),
            new RouteUniqueNamesNotice(
                /* routeId= */ "routeH",
                /* comparedRouteId= */ "routeB",
                /* routeLongName= */ "",
                /* routeShortName= */ "abcd",
                /* routeType= */ GtfsRouteType.BUS,
                /* agencyId= */ ""));
  }
}
