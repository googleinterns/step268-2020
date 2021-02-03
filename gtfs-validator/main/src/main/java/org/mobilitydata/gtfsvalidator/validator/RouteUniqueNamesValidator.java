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
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RouteUniqueNamesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

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
    final Map<Integer, List<Object>> testedRoutes = new HashMap<>();
    for (GtfsRoute route : routeTable.getEntities()) {
      // Grab necessary route information and transfer null to empty String to make later comparison
      // and notice genration easier
      final String routeLongName = Optional.ofNullable(route.routeLongName()).orElse("");
      final String routeShortName = Optional.ofNullable(route.routeShortName()).orElse("");
      final GtfsRouteType routeType = route.routeType();
      final String agencyId = Optional.ofNullable(route.agencyId()).orElse("");

      final int hashCode = Objects.hash(routeLongName, routeShortName, routeType, agencyId);
      if (testedRoutes.containsKey(hashCode)) {
        List<Object> storedRouteInfo = testedRoutes.get(hashCode);
        // Ensure routes with the same hashcode do have the same route names, types and agency IDs
        if (routeLongName.equals(storedRouteInfo.get(1))
            && routeShortName.equals(storedRouteInfo.get(2))
            && routeType.getNumber() == (int) (storedRouteInfo.get(3))
            && agencyId.equals(storedRouteInfo.get(4))) {
          noticeContainer.addNotice(
              new RouteUniqueNamesNotice(
                  route.routeId(),
                  /* comparedRouteId= */ storedRouteInfo.get(0).toString(),
                  routeLongName,
                  routeShortName,
                  routeType,
                  agencyId));
        }
      } else {
        List<Object> routeInfo =
            new ArrayList<>(
                Arrays.asList(
                    route.routeId(),
                    routeLongName,
                    routeShortName,
                    routeType.getNumber(),
                    agencyId));
        testedRoutes.put(hashCode, routeInfo);
      }
    }
  }
}
