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
// function loadNotices() {
//   fetch('/fileupload')
//   .then(response => {
//     console.log("response: " + response.json());
//     return response.json();
//   }).then((jsonResp) => {
//     callCorrespondingFunction(jsonResp);
//   })
//   .catch(error => console.log('error'))
// }

window.onload=function(){
  document.forms['myForm'].addEventListener('submit', (event) => {
    event.preventDefault();
    console.log("event target: " + event.target);
    console.log("event target action: " + event.target.action);
    fetch('/fileupload', {
      method: 'POST',
      // headers: {
      //   'Content-Type': 'multipart/form-data'
      // },
      body: new FormData(event.target)
    }).then((resp) => {
      console.log("response content type: " + resp.headers.get("Content-Type"));
      return resp.text();
    }).then((body) => {
      console.dir("body" + body);
      callCorrespondingFunction(body);
    });
  })
}

function callCorrespondingFunction(noticesJSON) {
  console.log("test");
  const noticeContainer = JSON.parse(noticesJSON);
  console.log("notice container: " + JSON.stringify(noticeContainer));
  for (var i = 0; i < noticeContainer.notices.length; i++) {
    const notice = noticeContainer.notices[i];
    console.log("notice code: " + notice.code);
    console.log("notices: " + JSON.stringify(notice.notices));
    runFunction(notice.code, notice);
  }
}

function runFunction(name, arguments) {
  const fn = window[name];
  console.log("window: " + fn);
  console.log("arguments: " + JSON.stringify(arguments));
  // check if fn is a function
  if (typeof fn !== 'function') return;
  fn.apply(window, [arguments]);
}