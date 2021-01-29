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
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteLongNameShortNameCombinationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

@RunWith(JUnit4.class)
public class RouteUniqueNamesValidatorTest {
  private final GtfsRoute routeOnlyLongName_abcd =
      new GtfsRoute.Builder()
          .setCsvRowNumber(1)
          .setRouteId("routeOnlyLongName_abcd")
          .setRouteLongName("abcd")
          .build();
  private final GtfsRoute routeOnlyLongName_alpha =
      new GtfsRoute.Builder()
          .setCsvRowNumber(2)
          .setRouteId("routeOnlyLongName_alpha")
          .setRouteLongName("alpha")
          .build();
  private final GtfsRoute routeOnlyShortName_abcd =
      new GtfsRoute.Builder()
          .setCsvRowNumber(3)
          .setRouteId("routeOnlyShortName_abcd")
          .setRouteShortName("abcd")
          .build();
  private final GtfsRoute routeLongName_ab_ShortName_cd =
      new GtfsRoute.Builder()
          .setCsvRowNumber(4)
          .setRouteId("routeLongName_ab_ShortName_cd")
          .setRouteLongName("ab")
          .setRouteShortName("cd")
          .build();
  private final GtfsRoute routeLongName_cd_ShortName_ab =
      new GtfsRoute.Builder()
          .setCsvRowNumber(5)
          .setRouteId("routeLongName_cd_ShortName_ab")
          .setRouteLongName("cd")
          .setRouteShortName("ab")
          .build();
  private final GtfsRoute routeLongName_abc_ShortName_d =
      new GtfsRoute.Builder()
          .setCsvRowNumber(6)
          .setRouteId("routeLongName_abc_ShortName_d")
          .setRouteLongName("abc")
          .setRouteShortName("d")
          .build();

  @Test
  public void routeOnlyLongNameEqualsAnotherRouteOnlyShortName() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyLongName_abcd);
    routes.add(routeOnlyShortName_abcd);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new DuplicateRouteLongNameShortNameCombinationNotice(
                "routeOnlyLongName_abcd", "abcd", "", "routeOnlyShortName_abcd", "", "abcd"));
  }

  @Test
  public void routeOnlyLongNameDifferentFromAnotherRouteOnlyShortName() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyLongName_alpha);
    routes.add(routeOnlyShortName_abcd);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routeOnlyLongNameDifferentFromAnotherRouteOnlyLongName() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyLongName_abcd);
    routes.add(routeOnlyLongName_alpha);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routeOnlyLongNameEqualsAnotherRouteBothLongShortNames() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyLongName_abcd);
    routes.add(routeLongName_ab_ShortName_cd);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new DuplicateRouteLongNameShortNameCombinationNotice(
                "routeOnlyLongName_abcd", "abcd", "", "routeLongName_ab_ShortName_cd", "ab", "cd"));
  }

  @Test
  public void routeOnlyShortNameEqualsAnotherRouteBothLongShortNames() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyShortName_abcd);
    routes.add(routeLongName_ab_ShortName_cd);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new DuplicateRouteLongNameShortNameCombinationNotice(
                "routeOnlyShortName_abcd",
                "",
                "abcd",
                "routeLongName_ab_ShortName_cd",
                "ab",
                "cd"));
  }

  @Test
  public void routeOnlyShortNameDifferentFromAnotherRouteBothLongShortNames() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeOnlyShortName_abcd);
    routes.add(routeLongName_cd_ShortName_ab);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void routeBothLongShortNamesEqualsAnotherRouteBothLongShortNames() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeLongName_ab_ShortName_cd);
    routes.add(routeLongName_abc_ShortName_d);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new DuplicateRouteLongNameShortNameCombinationNotice(
                "routeLongName_ab_ShortName_cd",
                "ab",
                "cd",
                "routeLongName_abc_ShortName_d",
                "abc",
                "d"));
  }

  @Test
  public void routeBothLongShortNamesDifferentFromAnotherRouteBothLongShortNames() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    RouteUniqueNamesValidator validator = new RouteUniqueNamesValidator();

    List<GtfsRoute> routes = new ArrayList<>();
    routes.add(routeLongName_ab_ShortName_cd);
    routes.add(routeLongName_cd_ShortName_ab);
    validator.routeTable = GtfsRouteTableContainer.forEntities(routes, noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
}
