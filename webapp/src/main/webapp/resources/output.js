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
  const unimplementedNoticesArray = [];
  const noticeContainer = JSON.parse(noticesJSON);
  for (var i = 0; i < noticeContainer.notices.length; i++) {
    const notice = noticeContainer.notices[i];
    // Notice has not been implemented, output raw json
    if (!runFunctionName(notice.code, notice)) {
      unimplementedNoticesArray.push(JSON.stringify(notice));
    }
  }
  // Print all unimplemented notices as raw JSON in the unimplemented notices
  // container
  if (unimplementedNoticesArray.length !== 0) {
    document.getElementById('unimplementedNotices').innerHTML =
        unimplementedNoticesArray;
  }
}

function runFunctionName(name, arguments) {
  const fn = window[name];
  // check if fn is a function
  if (typeof fn !== 'function')
    return false;
  else {
    fn.apply(window, [arguments]);
    return true;
  }
}

function unknown_column(params) {
  const template =
      goog.soy.renderAsElement(validator.templates.unknownColumnNotice, params);
  document.getElementById('warning').appendChild(template);
}

function invalid_row_length(params) {
  const template =
      goog.soy.renderAsElement(validator.templates.invalidRowLength, params);
  document.getElementById('error').appendChild(template);
}

function wrong_parent_location_type(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.wrongParentLocationType, params);
  document.getElementById('error').appendChild(template);
}

function unused_shape(params) {
  const template =
      goog.soy.renderAsElement(validator.templates.unusedShape, params);
  document.getElementById('error').appendChild(template);
}

function inconsistent_agency_field(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.inconsistentAgencyField, params);
  document.getElementById('error').appendChild(template);
}

function feed_info_lang_and_agency_lang_mismatch(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.feedInfoLangAndAgencyLangMismatch, params);
  document.getElementById('error').appendChild(template);
}

function decreasing_stop_time_distance(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.decreasingStopTimeDistance, params);
  document.getElementById('error').appendChild(template);
}

function decreasing_shape_distance(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.decreasingShapeDistance, params);
  document.getElementById('error').appendChild(template);
}

function fast_travel_between_stops(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.fastTravelBetweenStops, params);
  document.getElementById('warning').appendChild(template);
}
