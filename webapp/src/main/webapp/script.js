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

function createLoadingSpinner() {
  const loadingSpinner = document.createElement('div');
  loadingSpinner.className = 'spinner-border';
  loadingSpinner.setAttribute('role', 'status');
  const loadingText = document.createElement('span');
  loadingText.className = 'sr-only';
  loadingText.innerText = 'Loading...';
  loadingSpinner.appendChild(loadingText);
  return loadingSpinner;
}

function createResultsHeading() {
  const resultsHeading = document.createElement('h2');
  resultsHeading.id = 'resultHeading';
  resultsHeading.innerText = 'Results';
  return resultsHeading;
}

function clearHeading() {
  const errorOutput = document.getElementById('errorOutput');
  if (!(typeof errorOutput.nextSibling.id === 'undefined') &&
      errorOutput.nextSibling.id.localeCompare('resultHeading') == 0) {
    errorOutput.parentNode.removeChild(errorOutput.nextSibling);
  }
}

function clearNoticeContainers() {
  const errorOutput = document.getElementById('errorOutput');
  errorOutput.innerHTML = '';
  const error = document.getElementById('error');
  error.innerHTML = '';
  const warning = document.getElementById('warning');
  warning.innerHTML = '';
  const unimplementedNotices = document.getElementById('unimplementedNotices');
  unimplementedNotices.innerHTML = '';
}

function clearAllGood() {
  document.getElementById('allGood').style.visibility = 'hidden';
}

/**
 * Fetches notices from the notice container and
 * outputs the corresponding functions onto the DOM
 */
window.onload = function() {
  document.forms['myForm'].addEventListener('submit', (event) => {
    event.preventDefault();

    const errorOutput = document.getElementById('errorOutput');

    const loadingSpinner = this.createLoadingSpinner();

    const resultsHeading = this.createResultsHeading();

    this.clearHeading();

    this.clearNoticeContainers();

    this.clearAllGood();

    errorOutput.parentNode.insertBefore(
        loadingSpinner, errorOutput.nextSibling);
    errorOutput.parentNode.insertBefore(
        resultsHeading, errorOutput.nextSibling);

    fetch('/fileupload', {method: 'POST', body: new FormData(event.target)})
        .then((resp) => {
          // Check if output is in JSON format. If not, error has occured.
          const contentType = resp.headers.get('content-type');
          if (contentType && contentType.indexOf('application/json') !== -1) {
            return resp.text().then(data => {
              errorOutput.parentNode.removeChild(loadingSpinner);
              this.callCorrespondingFunction(data);
            });
          } else {
            // Upload Error
            document.getElementById('errorOutput').append(resp.text());
          }
        });
  });
}
