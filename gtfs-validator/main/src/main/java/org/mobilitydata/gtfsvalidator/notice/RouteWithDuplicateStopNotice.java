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

public class RouteWithDuplicateStopNotice extends Notice {
  public RouteWithDuplicateStopNotice(String stopName, String stopId1, long csvRowNumberStop1,
      String stopId2, long csvRowNumberStop2, String routeId) {
    super(new ImmutableMap.Builder<String, Object>()
              .put("stopName", stopName)
              .put("stopId1", stopId1)
              .put("csvRowNumberStop1", csvRowNumberStop1)
              .put("stopId2", stopId2)
              .put("csvRowNumberStop2", csvRowNumberStop2)
              .put("routeId", routeId)
              .build());
  }

  @Override
  public String getCode() {
    return "route_with_duplicate_stop_notice";
  }
}
