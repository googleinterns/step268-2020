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

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.TransfersAreUniqueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

/** Uses AutoValue to create an object with from_stop and to_stop. */
@AutoValue
abstract class FromToStopIdentifier {
  static FromToStopIdentifier create(String fromStopId, String toStopId) {
    return new AutoValue_FromToStopIdentifier(fromStopId, toStopId);
  }

  abstract String fromStopId();

  abstract String toStopId();
}

/**
 * Validates that transfers are unique.
 *
 * <p>Generated notices: {@link TransfersAreUniqueNotice}.
 */
@GtfsValidator
public class TransfersAreUniqueValidator extends FileValidator {
  @Inject GtfsTransferTableContainer transferTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Create Hash Map to store (key: fromToStop object, value: csvRowNumber).
    final Map<FromToStopIdentifier, Long> stopTransferMap = new HashMap<>();
    for (GtfsTransfer transfer : transferTable.getEntities()) {
      // Create fromToStopIdentifier object using the fromStopId and toStopId from each transfer.
      FromToStopIdentifier fromToStop =
          FromToStopIdentifier.create(transfer.fromStopId(), transfer.toStopId());
      // If the hash map already contains the same fromToStop, create a notice.
      if (stopTransferMap.containsKey(fromToStop)) {
        noticeContainer.addNotice(new TransfersAreUniqueNotice(
            /* fromStopId= */ fromToStop.fromStopId(),
            /* toStopId= */ fromToStop.toStopId(),
            /* duplicateCsvRowNumber= */ transfer.csvRowNumber(),
            /* originalCsvRowNUmber= */ stopTransferMap.get(fromToStop)));
      } else {
        // If the hash map does not contain the fromToStop, the transfer is unique. Add the transfer
        // into the hash map.
        stopTransferMap.put(fromToStop, transfer.csvRowNumber());
      }
    }
  }
}
