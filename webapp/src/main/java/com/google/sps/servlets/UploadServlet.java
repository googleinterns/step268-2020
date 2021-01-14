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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
  // Store the files temporarily in /tmp
  private static final String UPLOAD_DIRECTORY = "/tmp";

  // Upload settings
  private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
  private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
  private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Ensure the request contains a file
    if (!ServletFileUpload.isMultipartContent(request)) {
      response.getWriter().println("Error: Form must has enctype=multipart/form-data.");
      return;
    }

    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(MEMORY_THRESHOLD);
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(MAX_FILE_SIZE);
    upload.setSizeMax(MAX_REQUEST_SIZE);

    String uploadPath = UPLOAD_DIRECTORY;

    // Create the directory if it does not exist
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdir();
    }

    try {
      List<FileItem> formItems = upload.parseRequest(request);
      if (formItems != null && formItems.size() > 0) {
        for (FileItem item : formItems) {
          // Only processes files
          if (!item.isFormField()) {
            String fileName = new File(item.getName()).getName();
            String filePath = uploadPath + File.separator + fileName;
            File storeFile = new File(filePath);

            // Save the file on disk
            item.write(storeFile);

            // TO DO: get feed name and thread specification from form
            String validatorOutputJson = runValidator(filePath, 1, "nl-openov");

            response.getWriter().println("Upload has been done successfully!");
            // Print the json output 
            response.getWriter().println(validatorOutputJson);
          }
        }
      }
    } catch (Exception ex) {
      response.getWriter().println("There was an error: " + ex.getMessage());
    }
  }

  // Calls the global mobility validator function to load and validate the transit data
  private String runValidator(String filePath, int numThreads, String feedNameString) {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    GtfsFeedLoader feedLoader = new GtfsFeedLoader();

    GtfsFeedName feedName = GtfsFeedName.parseString(feedNameString);
    final long startNanos = System.nanoTime();

    // Input.
    feedLoader.setNumThreads(numThreads);
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedContainer feedContainer;
    GtfsInput gtfsInput;

    try {
      gtfsInput = GtfsInput.createFromPath(Paths.get(filePath));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    feedContainer =
        feedLoader.loadAndValidate(gtfsInput, feedName, validatorLoader, noticeContainer);

    // Write the output to the temporary file: what happens when multiple files are written?
    // Need a unique name ... or just write it to reponse and display it as HTML?
    try {
      Files.write(Paths.get("/tmp/", "report.json"),
          noticeContainer.exportJson().getBytes(StandardCharsets.UTF_8));
    } catch (IOException exception) {
      exception.printStackTrace();
    }

    final long endNanos = System.nanoTime();
    System.out.println(
        String.format("Validation took %.3f seconds", (endNanos - startNanos) / 1e9));
    System.out.println("Table totals: " + feedContainer.tableTotals());
    return noticeContainer.exportJson();
  }
}