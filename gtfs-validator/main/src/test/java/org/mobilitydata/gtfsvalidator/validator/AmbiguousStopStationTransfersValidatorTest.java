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
import org.mobilitydata.gtfsvalidator.notice.AmbiguousStopStationTransfersNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

@RunWith(JUnit4.class)
public class AmbiguousStopStationTransfersValidatorTest {
  private AmbiguousStopStationTransfersValidator validator =
      new AmbiguousStopStationTransfersValidator();

  // For stopTable:
  private final GtfsStop stationA =
      new GtfsStop.Builder()
          .setCsvRowNumber(1)
          .setStopId("stationA")
          .setLocationType(GtfsLocationType.STATION.getNumber())
          .build();
  private final GtfsStop stationB =
      new GtfsStop.Builder()
          .setCsvRowNumber(2)
          .setStopId("stationB")
          .setLocationType(GtfsLocationType.STATION.getNumber())
          .build();
  private final GtfsStop stationC =
      new GtfsStop.Builder()
          .setCsvRowNumber(3)
          .setStopId("stationC")
          .setLocationType(GtfsLocationType.STATION.getNumber())
          .build();
  private final GtfsStop stop1_in_stationA =
      new GtfsStop.Builder()
          .setCsvRowNumber(11)
          .setStopId("stop1")
          .setLocationType(GtfsLocationType.STOP.getNumber())
          .setParentStation("stationA")
          .build();
  private final GtfsStop stop2_in_stationB =
      new GtfsStop.Builder()
          .setCsvRowNumber(12)
          .setStopId("stop2")
          .setLocationType(GtfsLocationType.STOP.getNumber())
          .setParentStation("stationB")
          .build();
  private final GtfsStop stop3_in_stationC =
      new GtfsStop.Builder()
          .setCsvRowNumber(13)
          .setStopId("stop3")
          .setLocationType(GtfsLocationType.STOP.getNumber())
          .setParentStation("stationC")
          .build();
  private final GtfsStop boarding_area1_in_stationA =
      new GtfsStop.Builder()
          .setCsvRowNumber(14)
          .setStopId("boarding_area1")
          .setLocationType(GtfsLocationType.BOARDING_AREA.getNumber())
          .setParentStation("stationA")
          .build();
  private final GtfsStop entrance2_in_stationB =
      new GtfsStop.Builder()
          .setCsvRowNumber(15)
          .setStopId("entrance2")
          .setLocationType(GtfsLocationType.ENTRANCE.getNumber())
          .setParentStation("stationB")
          .build();

  // For transferTable:
  private final GtfsTransfer transfer_from_stop1_in_stationA_to_stationB =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(1)
          .setFromStopId("stop1")
          .setToStopId("stationB")
          .build();
  private final GtfsTransfer transfer_from_stationA_to_stop2_in_stationB =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(2)
          .setFromStopId("stationA")
          .setToStopId("stop2")
          .build();
  private final GtfsTransfer transfer_from_stop2_in_stationB_to_stationA =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(3)
          .setFromStopId("stop2")
          .setToStopId("stationA")
          .build();
  private final GtfsTransfer transfer_from_stop1_in_stationA_to_stationC =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(4)
          .setFromStopId("stop1")
          .setToStopId("stationC")
          .build();
  private final GtfsTransfer transfer_from_stop3_in_stationC_to_stationB =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(5)
          .setFromStopId("stop3")
          .setToStopId("stationB")
          .build();
  private final GtfsTransfer transfer_from_boarding_area1_in_stationA_to_stationB =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(6)
          .setFromStopId("boarding_area1")
          .setToStopId("stationB")
          .build();
  private final GtfsTransfer transfer_from_stationA_to_entrance2_in_stationB =
      new GtfsTransfer.Builder()
          .setCsvRowNumber(7)
          .setFromStopId("stationA")
          .setToStopId("entrance2")
          .build();

  @Test
  public void sameStationSameDirectionTransfersShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsStop> stops = 
        new ArrayList<>(Arrays.asList(stationA, stationB, stop1_in_stationA, stop2_in_stationB));
    validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsTransfer> transfers =
        new ArrayList<>(
            Arrays.asList(
                transfer_from_stop1_in_stationA_to_stationB,
                transfer_from_stationA_to_stop2_in_stationB));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(
            new AmbiguousStopStationTransfersNotice(
                /* transferCsvRowNumberA= */ 1,
                /* transferCsvRowNumberB= */ 2,
                /* fromStationId= */ "stationA",
                /* toStationId= */ "stationB"));
  }

  @Test
  public void sameStationDifferentDirectionTransfersShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsStop> stops =
        new ArrayList<>(Arrays.asList(stationA, stationB, stop1_in_stationA, stop2_in_stationB));
    validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsTransfer> transfers =
        new ArrayList<>(
            Arrays.asList(
                transfer_from_stop1_in_stationA_to_stationB,
                transfer_from_stop2_in_stationB_to_stationA));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void differentStationTransfersShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsStop> stops =
        new ArrayList<>(
            Arrays.asList(
                stationA,
                stationB,
                stationC,
                stop1_in_stationA,
                stop2_in_stationB,
                stop3_in_stationC));
    validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsTransfer> transfers =
        new ArrayList<>(
            Arrays.asList(
                transfer_from_stationA_to_stop2_in_stationB,
                transfer_from_stop1_in_stationA_to_stationC,
                transfer_from_stop3_in_stationC_to_stationB));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void otherLocationTypesWithSameStationTransfersShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsStop> stops =
        new ArrayList<>(
            Arrays.asList(
                stationA,
                stationB,
                stop1_in_stationA,
                boarding_area1_in_stationA,
                entrance2_in_stationB));
    validator.stopTable = GtfsStopTableContainer.forEntities(stops, noticeContainer);
    List<GtfsTransfer> transfers =
        new ArrayList<>(
            Arrays.asList(
                transfer_from_stop1_in_stationA_to_stationB,
                transfer_from_boarding_area1_in_stationA_to_stationB,
                transfer_from_stationA_to_entrance2_in_stationB));
    validator.transferTable = GtfsTransferTableContainer.forEntities(transfers, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
}
