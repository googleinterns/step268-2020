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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;

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

  @Test
  public void testValidFileUpload() throws IOException, ServletException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    String resourceName = "/SAMPLE.zip";
    String partName = "SAMPLE";
    byte[] fileContent =
        FileCopyUtils.copyToByteArray(getClass().getResourceAsStream(resourceName));
    // Create part & entity from resource
    Part[] parts =
        new Part[] {new FilePart(partName, new ByteArrayPartSource(resourceName, fileContent))};
    MultipartRequestEntity multipartRequestEntity =
        new MultipartRequestEntity(parts, new PostMethod().getParams());
    // Serialize request body
    ByteArrayOutputStream requestContent = new ByteArrayOutputStream();
    multipartRequestEntity.writeRequest(requestContent);
    // Set request body to HTTP servlet request
    request.setContent(requestContent.toByteArray());
    // Set content type to HTTP servlet request (important, includes Mime boundary string)
    request.setContentType(multipartRequestEntity.getContentType());

    System.out.println(request.getContentType());

    new UploadServlet().doPost(request, response);

    String validMsg = "Upload has been done successfully!";
    assertThat(response.getContentAsString()).contains(validMsg);
    assertThat(response.getContentAsString()).contains("invalid_row_length");
  }
}