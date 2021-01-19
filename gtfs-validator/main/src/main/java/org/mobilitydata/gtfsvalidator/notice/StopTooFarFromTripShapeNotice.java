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

import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;

import com.google.common.collect.ImmutableMap;

public class StopTooFarFromTripShapeNotice extends Notice {
    public StopTooFarFromTripShapeNotice(GtfsStopTime stopTime, GtfsTrip trip, double tripBufferMeters) {
        super(ImmutableMap.of(
            "stopId", stopTime.stopId(),
            "stopSequence", stopTime.stopSequence(),
            "tripId", trip.tripId(),
            "shapeId", trip.shapeId(),
            "tripBufferMeters", tripBufferMeters
        ));
    }

    @Override
    public String getCode() {
        return "stop_too_far_from_trip_shape";
    }
}
