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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

// TO DO: unit tests
@WebServlet("/fileupload")
public class UploadServlet extends HttpServlet {
  // Log any issues
  private static final Logger LOGGER = Logger.getLogger(UploadServlet.class.getName());

  private static final int NUM_THREADS = 1;

  // Calls the global mobility validator function to load and validate the transit data
  public static NoticeContainer runValidator(String filePath, String feedNameString) {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    GtfsFeedLoader feedLoader = new GtfsFeedLoader();

    GtfsFeedName feedName = GtfsFeedName.parseString(feedNameString);
    final long startNanos = System.nanoTime();

    // Input.
    feedLoader.setNumThreads(NUM_THREADS);
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedContainer feedContainer;
    GtfsInput gtfsInput;

    try {
      gtfsInput = GtfsInput.createFromPath(Paths.get(filePath));
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Exception occured", e);
      return null;
    }
    feedContainer =
        feedLoader.loadAndValidate(gtfsInput, feedName, validatorLoader, noticeContainer);

    final long endNanos = System.nanoTime();
    LOGGER.log(
        Level.INFO, String.format("Validation took %.3f seconds", (endNanos - startNanos) / 1e9));
    LOGGER.log(Level.INFO, "Table totals: " + feedContainer.tableTotals());

    return noticeContainer;
  }
}