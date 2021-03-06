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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.CalendarHasNoActiveDaysNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarService;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

/**
 * Validates days in calendar.txt has at least one active (available) day.
 *
 * <p>Generated notices: {@link CalendarHasNoActiveDaysNotice}.
 */
@GtfsValidator
public class CalendarHasNoActiveDaysValidator extends FileValidator {
  private final static GtfsCalendarService NOT_AVAILABLE = GtfsCalendarService.NOT_AVAILABLE;

  @Inject GtfsCalendarTableContainer calendarTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Loop through calendar and add up enum values for each day. If value is less than 1, then no
    // days are active.
    for (GtfsCalendar calendar : calendarTable.getEntities()) {
      if (calendar.monday() == NOT_AVAILABLE && calendar.tuesday() == NOT_AVAILABLE
          && calendar.wednesday() == NOT_AVAILABLE && calendar.thursday() == NOT_AVAILABLE
          && calendar.friday() == NOT_AVAILABLE && calendar.saturday() == NOT_AVAILABLE
          && calendar.sunday() == NOT_AVAILABLE) {
        noticeContainer.addNotice(new CalendarHasNoActiveDaysNotice(
            /* serviceId= */ calendar.serviceId(),
            /* calendarCsvRowNumber= */ calendar.csvRowNumber()));
      }
    }
  }
}