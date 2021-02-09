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

public class BlockTripsWithInconsistentRouteTypesNotice extends Notice {
  public BlockTripsWithInconsistentRouteTypesNotice(
      String blockId,
      long tripCsvRowNumberA,
      String tripIdA,
      GtfsRouteType routeTypeA,
      String routeIdA,
      long tripCsvRowNumberB,
      String tripIdB,
      GtfsRouteType routeTypeB,
      String routeIdB) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("blockId", blockId)
            .put("tripCsvRowNumberA", tripCsvRowNumberA)
            .put("tripIdA", tripIdA)
            .put("routeTypeA", routeTypeA)
            .put("routeIdA", routeIdA)
            .put("tripCsvRowNumberB", tripCsvRowNumberB)
            .put("tripIdB", tripIdB)
            .put("routeTypeB", routeTypeB)
            .put("routeIdB", routeIdB)
            .build());
  }

  @Override
  public String getCode() {
    return "block_trips_with_inconsistent_route_types";
  }
}
