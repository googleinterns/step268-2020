// // Copyright 2019 Google LLC
// //
// // Licensed under the Apache License, Version 2.0 (the "License");
// // you may not use this file except in compliance with the License.
// // You may obtain a copy of the License at
// //
// //     https://www.apache.org/licenses/LICENSE-2.0
// //
// // Unless required by applicable law or agreed to in writing, software
// // distributed under the License is distributed on an "AS IS" BASIS,
// // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// // See the License for the specific language governing permissions and
// // limitations under the License.
package com.google.sps.servlets;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

@RunWith(JUnit4.class)
public class RunValidatorTest {
  @Test
  public void testSimpleGtfsFeed() {
    NoticeContainer json =
        UploadServlet.runValidator("src/test/resources/SAMPLE.zip", "au-sydney-buses");
    // String expected =
    // "{\"notices\":[{\"code\":\"unknown_column\",\"totalNotices\":1,\"notices\":[{\"filename\":\"stop_times.txt\",\"fieldName\":\"drop_off_time\",\"index\":8}]},{\"code\":\"invalid_row_length\",\"totalNotices\":13,\"notices\":[{\"filename\":\"stop_times.txt\",\"csvRowNumber\":17,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":18,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":19,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":20,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":21,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":22,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":23,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":24,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":25,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":26,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":27,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":28,\"rowLength\":5,\"headerCount\":9},{\"filename\":\"stop_times.txt\",\"csvRowNumber\":29,\"rowLength\":5,\"headerCount\":9}]}]}";
    // Assert.assertEquals(json, expected);
  }

  @Test
  public void testBartGtfsFeed() {
    NoticeContainer json =
        UploadServlet.runValidator("src/test/resources/bart_gtfs.zip", "us-bay-area");
    // String expected =
    // "{\"notices\":[{\"code\":\"unknown_column\",\"totalNotices\":5,\"notices\":[{\"filename\":\"transfers.txt\",\"fieldName\":\"from_route_id\",\"index\":5},{\"filename\":\"transfers.txt\",\"fieldName\":\"to_route_id\",\"index\":6},{\"filename\":\"transfers.txt\",\"fieldName\":\"from_trip_id\",\"index\":7},{\"filename\":\"transfers.txt\",\"fieldName\":\"to_trip_id\",\"index\":8},{\"filename\":\"trips.txt\",\"fieldName\":\"trip_load_information\",\"index\":8}]},{\"code\":\"unexpected_file\",\"totalNotices\":5,\"notices\":[{\"filename\":\"calendar_attributes.txt\"},{\"filename\":\"rider_categories.txt\"},{\"filename\":\"fare_rider_categories.txt\"},{\"filename\":\"realtime_routes.txt\"},{\"filename\":\"directions.txt\"}]}]}";
    // Assert.assertEquals(json, expected);
  }
}