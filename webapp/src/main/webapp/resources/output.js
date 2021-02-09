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

function callCorrespondingFunction(noticesJSON) {
  const noticeContainer = JSON.parse(noticesJSON);
  for (var i = 0; i < noticeContainer.notices.length; i++) {
    const notice = noticeContainer.notices[i];
    // Notice has not been implemented, output raw json
    if (runFunctionName(notice.code, notice) === -1) {
      const unknownNotice = document.createElement('p');
      unknownNotice.innerHTML = 'NOTICE CONTAINER NOT IMPLEMENTED - RAW JSON DATA: <br><br>' + JSON.stringify(notice);
      document.getElementById("noticeContainer").appendChild(unknownNotice);
    } else {
      console.log("Notice has been handled!");
    };
  }
}

function runFunctionName(name, arguments) {
  const fn = window[name];
  // check if fn is a function
  if (typeof fn !== 'function') return -1;
  else {
    fn.apply(window, [arguments]);
    return 1;
  }
}

function unknown_column(params) {
  const template = goog.soy.renderAsElement(validator.templates.unknownColumnNotice, params);
  document.getElementById("warning").appendChild(template);
}

function invalid_row_length(params) {
  const template = goog.soy.renderAsElement(validator.templates.invalidRowLength, params);
  document.getElementById("error").appendChild(template);
}