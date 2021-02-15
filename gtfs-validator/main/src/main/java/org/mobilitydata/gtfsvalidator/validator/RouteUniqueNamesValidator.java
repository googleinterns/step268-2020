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

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteUniqueNamesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

/** Uses AutoValue to compare necessary route information easier. */
@AutoValue
abstract class RouteIdentifier {
  static RouteIdentifier create(
      String longName, String shortName, GtfsRouteType routeType, String agencyId) {
    return new AutoValue_RouteIdentifier(longName, shortName, routeType, agencyId);
  }

  abstract String longName();

  abstract String shortName();

  abstract GtfsRouteType routeType();

  abstract String agencyId();
}

/** Uses AutoValue to record route information which is needed for notice generation. */
@AutoValue
abstract class RouteNoticeInfo {
  static RouteNoticeInfo create(String routeId, long csvRowNumber) {
    return new AutoValue_RouteNoticeInfo(routeId, csvRowNumber);
  }

  abstract String routeId();

  abstract long csvRowNumber();
}

/**
 * Validates whether the combination of names, type and agency ID for a route is unique.
 *
 * <p>Generated notices: {@link RouteUniqueNamesNotice}.
 */
@GtfsValidator
public class RouteUniqueNamesValidator extends FileValidator {
  @Inject GtfsRouteTableContainer routeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<RouteIdentifier, RouteNoticeInfo> testedRoutes = new HashMap<>();
    for (GtfsRoute route : routeTable.getEntities()) {
      // Grab necessary route information and transfer null to empty String to make later comparison
      // and notice generation easier
      final RouteIdentifier routeIdentifier =
          RouteIdentifier.create(
              Optional.ofNullable(route.routeLongName()).orElse(""),
              Optional.ofNullable(route.routeShortName()).orElse(""),
              route.routeType(),
              Optional.ofNullable(route.agencyId()).orElse(""));
      // If the combination of names, type and agency ID for a route appeared before, a notice is
      // generated
      if (testedRoutes.containsKey(routeIdentifier)) {
        // Generate a notice
        noticeContainer.addNotice(
            new RouteUniqueNamesNotice(
                route.routeId(),
                route.csvRowNumber(),
                /* comparedRouteId= */ testedRoutes.get(routeIdentifier).routeId(),
                /* comparedRouteCsvRowNumber= */ testedRoutes.get(routeIdentifier).csvRowNumber(),
                routeIdentifier.longName(),
                routeIdentifier.shortName(),
                // Get routeType as a string without "GtfsRouteType."
                routeIdentifier.routeType().toString().replace("GtfsRouteType.",""),
                routeIdentifier.agencyId()));
      } else {
        // Add necessary route information for notice to the storage with the routeIdentifier as key
        testedRoutes.put(
            routeIdentifier, RouteNoticeInfo.create(route.routeId(), route.csvRowNumber()));
      }
    }
  }
}
