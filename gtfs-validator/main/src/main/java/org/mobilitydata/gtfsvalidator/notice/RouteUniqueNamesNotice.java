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

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteType;

public class RouteUniqueNamesNotice extends Notice {
  public RouteUniqueNamesNotice(
      String routeId,
      long routeCsvRowNumber,
      String comparedRouteId,
      long comparedRouteCsvRowNumber,
      String routeLongName,
      String routeShortName,
      GtfsRouteType routeType,
      String agencyId) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("routeId", routeId)
            .put("routeCsvRowNumber", routeCsvRowNumber)
            .put("comparedRouteId", comparedRouteId)
            .put("comparedRouteCsvRowNumber", comparedRouteCsvRowNumber)
            .put("routeLongName", routeLongName)
            .put("routeShortName", routeShortName)
            .put("routeType", routeType.toString())
            .put("agencyId", agencyId)
            .build());
  }

  @Override
  public String getCode() {
    return "route_without_unique_names";
  }
}
