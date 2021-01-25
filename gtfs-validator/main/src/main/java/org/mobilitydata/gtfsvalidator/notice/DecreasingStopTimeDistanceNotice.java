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

public class DecreasingStopTimeDistanceNotice extends ValidationNotice {
  public DecreasingStopTimeDistanceNotice(
      String tripId,
      long csvRowNumber,
      int stopSequence,
      double shapeDistTraveled
      long prevCsvRowNumber,
      int prevStopSequence,
      double prevShapeDistTraveled) {
    super(
        new ImmutableMap.Builder<String, Object>()
            .put("tripId", tripId)
            .put("csvRowNumber", csvRowNumber)
            .put("stopSequence", stopSequence)
            .put("shapeDistTraveled", shapeDistTraveled)
            .put("prevCsvRowNumber", prevCsvRowNumber)
            .put("prevStopSequence", prevStopSequence)
            .put("prevShapeDistTraveled", prevShapeDistTraveled)
            .build());
  }

  @Override
  public String getCode() {
    return "decreasing_stop_time_distance";
  }
}
