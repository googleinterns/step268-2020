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
import org.mobilitydata.gtfsvalidator.notice.CalendarHasNoActiveDaysNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarService;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

@RunWith(JUnit4.class)
public class CalendarHasNoActiveDaysValidatorTest {
  private CalendarHasNoActiveDaysValidator validator = new CalendarHasNoActiveDaysValidator();

  private final GtfsCalendar all_active_calendar =
      new GtfsCalendar.Builder()
          .setServiceId("service1")
          .setMonday(GtfsCalendarService.AVAILABLE.getNumber())
          .setTuesday(GtfsCalendarService.AVAILABLE.getNumber())
          .setWednesday(GtfsCalendarService.AVAILABLE.getNumber())
          .setThursday(GtfsCalendarService.AVAILABLE.getNumber())
          .setFriday(GtfsCalendarService.AVAILABLE.getNumber())
          .setSaturday(GtfsCalendarService.AVAILABLE.getNumber())
          .setSunday(GtfsCalendarService.AVAILABLE.getNumber())
          .build();

  private final GtfsCalendar no_active_calendar =
      new GtfsCalendar.Builder()
          .setServiceId("service2")
          .setMonday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setTuesday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setWednesday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setThursday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setFriday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setSaturday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setSunday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .build();

  private final GtfsCalendar one_active_calendar =
      new GtfsCalendar.Builder()
          .setServiceId("service_3")
          .setMonday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setTuesday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setWednesday(GtfsCalendarService.AVAILABLE.getNumber())
          .setThursday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setFriday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setSaturday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setSunday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .build();

  private final GtfsCalendar more_active_calendar =
      new GtfsCalendar.Builder()
          .setServiceId("service_4")
          .setMonday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setTuesday(GtfsCalendarService.AVAILABLE.getNumber())
          .setWednesday(GtfsCalendarService.AVAILABLE.getNumber())
          .setThursday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setFriday(GtfsCalendarService.AVAILABLE.getNumber())
          .setSaturday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .setSunday(GtfsCalendarService.NOT_AVAILABLE.getNumber())
          .build();

  @Test
  public void allActiveDaysShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsCalendar> days = new ArrayList<>(Arrays.asList(all_active_calendar));
    validator.calendarTable = GtfsCalendarTableContainer.forEntities(days, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }

  @Test
  public void oneActiveDayShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsCalendar> days = new ArrayList<>(Arrays.asList(one_active_calendar));
    validator.calendarTable = GtfsCalendarTableContainer.forEntities(days, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
  @Test
  public void noActiveDaysShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsCalendar> days = new ArrayList<>(Arrays.asList(no_active_calendar));
    validator.calendarTable = GtfsCalendarTableContainer.forEntities(days, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new CalendarHasNoActiveDaysNotice(
            /* serviceId= */ "service2",
            /* monday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* tuesday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* wednesday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* thursday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* friday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* saturday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* sunday= */ GtfsCalendarService.NOT_AVAILABLE));
  }

  @Test
  public void activeAndNoActiveDaysShouldGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsCalendar> days =
        new ArrayList<>(Arrays.asList(no_active_calendar, more_active_calendar));
    validator.calendarTable = GtfsCalendarTableContainer.forEntities(days, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices())
        .containsExactly(new CalendarHasNoActiveDaysNotice(
            /* serviceId= */ "service2",
            /* monday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* tuesday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* wednesday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* thursday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* friday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* saturday= */ GtfsCalendarService.NOT_AVAILABLE,
            /* sunday= */ GtfsCalendarService.NOT_AVAILABLE));
  }

  @Test
  public void multipleActiveDaysShouldNotGenerateNotice() {
    final NoticeContainer noticeContainer = new NoticeContainer();
    List<GtfsCalendar> days =
        new ArrayList<>(Arrays.asList(one_active_calendar, more_active_calendar));
    validator.calendarTable = GtfsCalendarTableContainer.forEntities(days, noticeContainer);
    validator.validate(noticeContainer);
    assertThat(noticeContainer.getNotices()).isEmpty();
  }
}