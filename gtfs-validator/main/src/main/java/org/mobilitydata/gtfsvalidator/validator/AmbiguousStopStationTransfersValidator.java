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
import org.mobilitydata.gtfsvalidator.notice.AmbiguousStopStationTransfersNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

/** Uses AutoValue to record the from and to stations of a transfer. */
@AutoValue
abstract class TransferIdentifier {
  static TransferIdentifier create(String fromStationId, String toStationId) {
    return new AutoValue_TransferIdentifier(fromStationId, toStationId);
  }

  abstract String fromStationId();

  abstract String toStationId();
}

/**
 * Validates no ambiguous stop-to-station and station-to-stop transfers.
 *
 * <p>Generated notices: {@link AmbiguousStopStationTransfersNotice}.
 */
@GtfsValidator
public class AmbiguousStopStationTransfersValidator extends FileValidator {
  @Inject GtfsTransferTableContainer transferTable;
  @Inject GtfsStopTableContainer stopTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Store all checked from and to stations of transfers and their corresponding csv row numbers
    final Map<TransferIdentifier, Long> checkedStationsTransfer = new HashMap<>();
    for (GtfsTransfer transfer : transferTable.getEntities()) {
      final GtfsStop fromStop = stopTable.byStopId(transfer.fromStopId());
      final GtfsStop toStop = stopTable.byStopId(transfer.toStopId());
      // Only check station-to-stop or stop-to-station transfers
      if ((fromStop.locationType() == GtfsLocationType.STOP
              && toStop.locationType() == GtfsLocationType.STATION)
          || (fromStop.locationType() == GtfsLocationType.STATION
              && toStop.locationType() == GtfsLocationType.STOP)) {
        // If the from_stop is a STOP location type, the transfer is from STOP to STATION;
        // otherwise, the transfer is from STATION to STOP
        final TransferIdentifier transferIdentifier =
            (fromStop.locationType() == GtfsLocationType.STOP)
                ? TransferIdentifier.create(fromStop.parentStation(), toStop.stopId())
                : TransferIdentifier.create(fromStop.stopId(), toStop.parentStation());
        // Add the transferIdentifier to the HashMap storage if it has not appeared; otherwise,
        // generate a notice
        if (checkedStationsTransfer.containsKey(transferIdentifier)) {
          noticeContainer.addNotice(
              new AmbiguousStopStationTransfersNotice(
                  /* transferCsvRowNumberA= */ checkedStationsTransfer.get(transferIdentifier),
                  /* transferCsvRowNumberB= */ transfer.csvRowNumber(),
                  /* fromStationId= */ transferIdentifier.fromStationId(),
                  /* toStationId= */ transferIdentifier.toStationId()));
        } else {
          checkedStationsTransfer.put(transferIdentifier, transfer.csvRowNumber());
        }
      }
    }
  }
}
