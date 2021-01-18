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

import static com.google.common.truth.Truth.assertThat;

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
    assertThat(json.exportJson()).contains("\"code\":\"invalid_row_length\",\"totalNotices\":13");
    assertThat(json.exportJson()).contains("\"code\":\"unknown_column\",\"totalNotices\":1");
  }

  @Test
  public void testBartGtfsFeed() {
    NoticeContainer json =
        UploadServlet.runValidator("src/test/resources/bart_gtfs.zip", "us-bay-area");
    assertThat(json.exportJson()).contains("\"code\":\"unknown_column\",\"totalNotices\":5");
    assertThat(json.exportJson()).contains("\"code\":\"unexpected_file\",\"totalNotices\":5");
  }
}