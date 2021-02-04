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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.javatuples.Triplet;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteUniqueNamesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

/** Uses AutoValue to create hash code and compare easier. */
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
    final Map<Integer, List<Triplet<RouteIdentifier, String, Long>>> testedRoutes = new HashMap<>();
    for (GtfsRoute route : routeTable.getEntities()) {
      // Grab necessary route information and transfer null to empty String to make later comparison
      // and notice generation easier
      final RouteIdentifier routeIdentifier =
          RouteIdentifier.create(
              Optional.ofNullable(route.routeLongName()).orElse(""),
              Optional.ofNullable(route.routeShortName()).orElse(""),
              route.routeType(),
              Optional.ofNullable(route.agencyId()).orElse(""));
      final int hashCode = routeIdentifier.hashCode();

      if (testedRoutes.containsKey(hashCode)) {
        List<Triplet<RouteIdentifier, String, Long>> storedRouteInfoList =
            testedRoutes.get(hashCode);
        // Go through all recorded routes with the same hash code (though most of the time when the
        // data size is not extremely large, there is only one route recorded in the list)
        for (int i = 0; i < storedRouteInfoList.size(); i++) {
          // Ensure routes with the same hash code do have the same route names, type and agency ID
          // combinations to generate a notice
          if (routeIdentifier.equals(storedRouteInfoList.get(i).getValue0())) {
            noticeContainer.addNotice(
                new RouteUniqueNamesNotice(
                    route.routeId(),
                    route.csvRowNumber(),
                    /* comparedRouteId= */ storedRouteInfoList.get(i).getValue1(),
                    /* comparedRouteCsvRowNumber= */ storedRouteInfoList.get(i).getValue2(),
                    routeIdentifier.longName(),
                    routeIdentifier.shortName(),
                    routeIdentifier.routeType(),
                    routeIdentifier.agencyId()));
            // Stop checking the left routes in the list if we already found a same-information pair
            break;
            // After checking the last recorded route in the list, the current route needs to be
            // added into the record with the same hash code key
          } else if (i == storedRouteInfoList.size() - 1) {
            storedRouteInfoList.add(
                Triplet.with(routeIdentifier, route.routeId(), route.csvRowNumber()));
            testedRoutes.put(hashCode, storedRouteInfoList);
          }
        }
      } else {
        // Add the new route information to the record with the hash code as the key
        Triplet<RouteIdentifier, String, Long> routeInfo =
            Triplet.with(routeIdentifier, route.routeId(), route.csvRowNumber());
        testedRoutes.put(hashCode, new ArrayList<>(Arrays.asList(routeInfo)));
      }
    }
  }
}
