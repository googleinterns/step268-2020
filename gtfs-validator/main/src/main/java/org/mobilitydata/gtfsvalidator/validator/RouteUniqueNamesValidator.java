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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional; 
import org.javatuples.Triplet;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteUniqueNamesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;
import com.google.auto.value.AutoValue;

/**
 * Validates whether the combination of names, type and agency ID for a route is unique.
 *
 * <p>Generated notices: {@link RouteUniqueNamesNotice}.
 */
@AutoValue
abstract class RouteIdentifier {
  static RouteIdentifier create(String longName,String shortName, GtfsRouteType routeType, String agencyId) {
    return new AutoValue_RouteIdentifier(longName, shortName,routeType,agencyId);
  }

  abstract String longName();
  abstract String shortName();
  abstract GtfsRouteType routeType();
  abstract String agencyId();
}

@GtfsValidator
public class RouteUniqueNamesValidator extends FileValidator {
  @Inject GtfsRouteTableContainer routeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<Integer, Triplet<RouteIdentifier, String, Long>> testedRoutes = new HashMap<>();
    for (GtfsRoute route : routeTable.getEntities()) {
      // Grab necessary route information and transfer null to empty String to make later comparison
      // and notice generation easier
      final RouteIdentifier routeIdentifier = RouteIdentifier.create(Optional.ofNullable(route.routeLongName()).orElse(""), Optional.ofNullable(route.routeShortName()).orElse(""), route.routeType(), Optional.ofNullable(route.agencyId()).orElse(""));
      final int hashCode = routeIdentifier.hashCode();
      if (testedRoutes.containsKey(hashCode)) {
        Triplet<RouteIdentifier, String, Long> storedRouteInfo = testedRoutes.get(hashCode);
        // Ensure routes with the same hashcode do have the same route names, type and agency ID combinations
        if (routeIdentifier.equals(storedRouteInfo.getValue0())) {
          noticeContainer.addNotice(
              new RouteUniqueNamesNotice(
                  route.routeId(),
                  route.csvRowNumber(),
                  /* comparedRouteId= */ storedRouteInfo.getValue1(),
                  /* comparedRouteCsvRowNumber= */ storedRouteInfo.getValue2(),
                  routeIdentifier.longName(),
                  routeIdentifier.shortName(),
                  routeIdentifier.routeType(),
                  routeIdentifier.agencyId()));
        }
      } else {
        Triplet<RouteIdentifier, String, Long> routeInfo = Triplet.with(routeIdentifier, route.routeId(), route.csvRowNumber());
        testedRoutes.put(hashCode, routeInfo);
      }
    }
  }
}
