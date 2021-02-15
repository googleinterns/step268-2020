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

function location_without_parent_station(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.locationWithoutParentStation, params);
  document.getElementById('error').appendChild(template);
}

function meaningless_trip_with_no_more_than_one_stop(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.meaninglessTripWithNoMoreThanOneStop, params);
  document.getElementById('error').appendChild(template);
}

function missing_trip_edge_arrival_time_departure_time(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.missingTripEdgeStopTime, params);
  document.getElementById('error').appendChild(template);
}

function overlapping_frequency(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.overlappingFrequency, params);
  document.getElementById('error').appendChild(template);
}

function trip_with_duplicate_stops(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.tripWithDuplicateStops, params);
  document.getElementById('warning').appendChild(template);
}

function stops_too_close(params) {
  const template =
      goog.soy.renderAsElement(validator.templates.stopsTooClose, params);
  document.getElementById('warning').appendChild(template);
}

function stop_too_far_from_trip_shape(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.stopTooFarFromTripShape, params);
  document.getElementById('error').appendChild(template);
}

function stop_time_with_only_arrival_or_departure_time(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.stopTimeWithOnlyArrivalOrDepartureTime, params);
  document.getElementById('warning').appendChild(template);
}

function stop_time_with_departure_before_arrival_time(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.stopTimeWithDepartureBeforeArrivalTime, params);
  document.getElementById('error').appendChild(template);
}

function stop_time_with_arrival_before_previous_departure_time(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.stopTimeWithArrivalBeforePreviousDepartureTime,
      params);
  document.getElementById('error').appendChild(template);
}

function route_unique_names(params) {
  const template =
      goog.soy.renderAsElement(validator.templates.routeUniqueNames, params);
  document.getElementById('error').appendChild(template);
}

function station_with_parent_station(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.stationWithParentStation, params);
  document.getElementById('error').appendChild(template);
}

function start_and_end_time_out_of_order(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.startAndEndTimeOutOfOrder, params);
  document.getElementById('error').appendChild(template);
}

function start_and_end_date_out_of_order(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.startAndEndDateOutOfOrder, params);
  document.getElementById('error').appendChild(template);
}

function same_route_name_and_description(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.sameNameAndDescriptionForRoute, params);
  document.getElementById('error').appendChild(template);
}

function transfers_are_unique(params) {
  const template = goog.soy.renderAsElement(
      validator.templates.transfersAreUnique, params);
  document.getElementById('warning').appendChild(template);
}
