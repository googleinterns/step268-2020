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

public class DuplicateRouteLongNameShortNameCombinationNotice extends Notice {
  public DuplicateRouteLongNameShortNameCombinationNotice(
      String comparedRouteId,
      String comparedRouteLongName,
      String comparedRouteShortName,
      String routeId,
      String routeLongName,
      String routeShortName) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("comparedRouteId", comparedRouteId)
            .put("comparedRouteLongName", comparedRouteLongName)
            .put("comparedRouteShortName", comparedRouteShortName)
            .put("routeId", routeId)
            .put("routeLongName", routeLongName)
            .put("routeShortName", routeShortName)
            .build());
  }

  @Override
  public String getCode() {
    return "duplicate_route_long_name_short_name_combination";
  }
}
