/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.TransfersAreUniqueNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

@RunWith(JUnit4.class)
public class TransfersAreUniqueValidatorTest {
  private TransfersAreUniqueValidator validator = new TransfersAreUniqueValidator();
  private final GtfsTransfer transfer_1 = new GtfsTransfer.Builder()
                                              .setFromStopId("stop_1")
                                              .setToStopId("stop_2")
                                              .setCsvRowNumber(1)
                                              .build();
  private final GtfsTransfer transfer_1_duplicate = new GtfsTransfer.Builder()
                                                        .setFromStopId("stop_1")
                                                        .setToStopId("stop_2")
                                                        .setCsvRowNumber(10)
                                                        .build();
  private final GtfsTransfer transfer_2 = new GtfsTransfer.Builder()
                                              .setFromStopId("stop_3")
                                              .setToStopId("stop_7")
                                              .setCsvRowNumber(2)
                                              .build();
  private final GtfsTransfer transfer_2_duplicate = new GtfsTransfer.Builder()
                                                        .setFromStopId("stop_3")
                                                        .setToStopId("stop_7")
                                                        .setCsvRowNumber(4)
                                                        .build();
  private final GtfsTransfer transfer_3 = new GtfsTransfer.Builder()
                                              .setFromStopId("stop_5")
                                              .setToStopId("stop_9")
                                              .setCsvRowNumber(3)
                                              .build();

  @Test
  public void allDifferentTransfersShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTransfer> transfers =
        new ArrayList<>(Arrays.asList(transfer_1, transfer_2, transfer_3));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void onlyDuplicateTransfersShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTransfer> transfers = new ArrayList<>(Arrays.asList(transfer_1, transfer_1_duplicate));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new TransfersAreUniqueNotice(
            /* fromStopId= */ "stop_1",
            /* toStopId= */ "stop_2",
            /* duplicateCsvRowNumber= */ 10,
            /* originalCsvRowNumber= */ 1));
  }

  @Test
  public void someDuplicateTransfersShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTransfer> transfers = new ArrayList<>(Arrays.asList(
        transfer_1, transfer_2, transfer_3, transfer_1_duplicate, transfer_2_duplicate));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new TransfersAreUniqueNotice(
                             /* fromStopId= */ "stop_1",
                             /* toStopId= */ "stop_2",
                             /* duplicateCsvRowNumber= */ 10,
                             /* originalCsvRowNumber= */ 1),
            new TransfersAreUniqueNotice(
                /* fromStopId= */ "stop_3",
                /* fromStopId= */ "stop_7",
                /* duplicateCsvRowNumber= */ 4,
                /* originalCsvRowNumber= */ 2));
  }

  @Test
  public void moreDuplicateTransfersShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsTransfer> transfers =
        new ArrayList<>(Arrays.asList(transfer_1_duplicate, transfer_2, transfer_3, transfer_1));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new TransfersAreUniqueNotice(
            /* fromStopId= */ "stop_1",
            /* toStopId= */ "stop_2",
            /* duplicateCsvRowNumber= */ 1,
            /* originalCsvRowNumber= */ 10));
  }
}
