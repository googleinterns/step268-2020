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

/** Fetches notices from the notice container and 
 * outputs the corresponding functions onto the DOM */
window.onload=function(){
  document.forms['myForm'].addEventListener('submit', (event) => {
    event.preventDefault();
    fetch('/fileupload', {
      method: 'POST',
      body: new FormData(event.target)
    }).then((resp) => {
      // Check if output is in JSON format. If not, error has occured.
      const contentType = resp.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        return resp.text().then(data => {
          this.callCorrespondingFunction(data);
        });
      } else {
        // Upload Error
        this.alert(resp.text());
      }
    });
  });
}

function callCorrespondingFunction(noticesJSON) {
  const noticeContainer = JSON.parse(noticesJSON);
  for (var i = 0; i < noticeContainer.notices.length; i++) {
    const notice = noticeContainer.notices[i];
    runFunction(notice.code, notice);
  }
}

function runFunction(name, arguments) {
  const fn = window[name];
  // check if fn is a function
  if (typeof fn !== 'function') return;
  fn.apply(window, [arguments]);
}