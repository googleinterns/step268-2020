// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.sps.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
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

@WebServlet("/fileupload")
public class UploadServlet extends HttpServlet {
  // Store the files temporarily in /tmp
  private static final String UPLOAD_DIRECTORY = "/tmp";

  // Upload settings
  private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
  private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
  private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

  // Log any issues
  private static final Logger logger = Logger.getLogger(UploadServlet.class.getName());

  // Required to call the global mobility validate function
  private static final int NUM_THREADS = 1;
  // Feed name required but does not affect code, so hard coded in
  private static final String FEED_NAME = "au-transit";

  // Calls the global mobility validator function to load and validate the transit data
  public static NoticeContainer runValidator(String filePath) {
    final ValidatorLoader validatorLoader = new ValidatorLoader();
    final GtfsFeedLoader feedLoader = new GtfsFeedLoader();

    final GtfsFeedName feedName = GtfsFeedName.parseString(FEED_NAME);
    final long startNanos = System.nanoTime();

    // Input.
    feedLoader.setNumThreads(NUM_THREADS);
    final NoticeContainer noticeContainer = new NoticeContainer();
    final GtfsFeedContainer feedContainer;
    final GtfsInput gtfsInput;

    try {
      gtfsInput = GtfsInput.createFromPath(Paths.get(filePath));
    } catch (ZipException e) {
      logger.log(Level.SEVERE, "Exception occured, not a zip file", e);
      return null;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Exception occured", e);
      return null;
    }
    feedContainer =
        feedLoader.loadAndValidate(gtfsInput, feedName, validatorLoader, noticeContainer);

    final long endNanos = System.nanoTime();
    logger.log(
        Level.INFO, String.format("Validation took %.3f seconds", (endNanos - startNanos) / 1e9));
    logger.log(Level.INFO, "Table totals: " + feedContainer.tableTotals());

    return noticeContainer;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Ensure the request type allows for file upload
    if (!request.getContentType().contains("multipart/form-data")) {
      response.getWriter().println("Error: Form must has enctype=multipart/form-data.");
      return;
    }

    final DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(MEMORY_THRESHOLD);
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    final ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(MAX_FILE_SIZE);
    upload.setSizeMax(MAX_REQUEST_SIZE);

    final String uploadPath = UPLOAD_DIRECTORY;

    // Create the directory if it does not exist
    final File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdir();
    }
    try {
      final List<FileItem> formItems = upload.parseRequest(request);
      if (formItems != null && formItems.size() > 0) {
        for (FileItem item : formItems) {
          // Only processes files
          if (!item.isFormField()) {
            final String fileName = new File(item.getName()).getName();
            final String filePath = uploadPath + File.separator + fileName;
            final File storeFile = new File(filePath);
            
            // Save the file on disk
            item.write(storeFile);
            
            final NoticeContainer validatorNotices = runValidator(filePath);
            
            // Print the json output to the user
            if (validatorNotices != null) {
              response.setContentType("application/json");
              response.getWriter().println(validatorNotices.exportJson());
            } else {
              response.getWriter().println("The validator was unable to process this file.");
            }
          }
        }
      }
    } catch (Exception e) {
      response.getWriter().println("There was an error: " + e.getMessage());
    }
  }
}
