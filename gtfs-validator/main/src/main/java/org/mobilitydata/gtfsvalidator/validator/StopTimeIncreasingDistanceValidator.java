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

import com.google.common.collect.Multimaps;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DecreasingStopTimeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Use case to validate that for each trip, stop times have increasing `shape_dist_travelled` values.
 *
 * <p>Generated notice: {@link DecreasingStopTimeDistanceNotice}.
 */
@GtfsValidator
public class StopTimeIncreasingDistanceValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer table;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(table.byTripIdMap()).values()) {
      // GtfsStopTime objects are sorted based on @SequenceKey annotation on stop_sequence field.
      for (int i = 1; i < stopTimeList.size(); ++i) {
        GtfsStopTime prev = stopTimeList.get(i - 1);
        GtfsStopTime curr = stopTimeList.get(i);
        // If distance traveled is decreasing - generate error notice
        if (prev.hasShapeDistTraveled()
            && curr.hasShapeDistTraveled()
            && prev.shapeDistTraveled() > curr.shapeDistTraveled()) {
          noticeContainer.addNotice(
              new DecreasingStopTimeDistanceNotice(
                  curr.tripId(),
                  curr.csvRowNumber(),
                  curr.stopSequence(),
                  curr.shapeDistTraveled(),
                  prev.csvRowNumber(),
                  prev.stopSequence(),
                  prev.shapeDistTraveled()));
        }
      }
    }
  }
}