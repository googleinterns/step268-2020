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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DuplicateRouteLongNameShortNameCombinationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsRoute;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableContainer;

/**
 * Validates short or long names for a route are unique.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link DuplicateRouteLongNameShortNameCombinationNotice}
 *   <li>{@link DuplicateRouteShortNameNotice}
 *   <li>{@link DuplicateRouteLongNameNotice}
 * </ul>
 */
@GtfsValidator
public class RouteUniqueNamesValidator extends FileValidator {
  @Inject GtfsRouteTableContainer routeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    final Map<String, GtfsRoute> routesByLongNameShortName = new HashMap<>();
    for (GtfsRoute route : routeTable.getEntities()) {
      String routeLongName = Optional.ofNullable(route.routeLongName()).orElse("");
      String routeShortName = Optional.ofNullable(route.routeShortName()).orElse("");
      if (routeLongName != "") {
        // For DuplicateRouteLongNameNotice
      }
      if (routeShortName != "") {
        // For DuplicateRouteShortNameNotice
      }
      // Checks uniqueness of combination of fields `route_long_name` and `route_short_name`
      if (routesByLongNameShortName.containsKey(routeLongName + routeShortName)) {
        GtfsRoute comparedRoute = routesByLongNameShortName.get(routeLongName + routeShortName);
        noticeContainer.addNotice(
            new DuplicateRouteLongNameShortNameCombinationNotice(
                comparedRoute.routeId(),
                Optional.ofNullable(comparedRoute.routeLongName()).orElse(""),
                Optional.ofNullable(comparedRoute.routeShortName()).orElse(""),
                route.routeId(),
                routeLongName,
                routeShortName));
      } else {
        routesByLongNameShortName.put(routeLongName + routeShortName, route);
      }
    }
  }
}
