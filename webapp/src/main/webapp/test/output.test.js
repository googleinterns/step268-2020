
/*
 * Unit tests for output.js
 */
describe('Output', function() {
  // inject the HTML fixture for the tests
  beforeEach(function() {
    var fixture =
        '<div id="fixture"><div id="errorOutput"></div><div id="error"></div><div id="warning">\
                  </div><div id="unimplementedNotices"></div></div>';

    document.body.insertAdjacentHTML('afterbegin', fixture);
  });

  // remove the html fixture from the DOM
  afterEach(function() {
    document.body.removeChild(document.getElementById('fixture'));
  });

  it('should issue column warning', function() {
    const params = {
      code: 'unknown_column',
      totalNotices: 1,
      notices:
          [{filename: 'stop_times.txt', fieldName: 'drop_off_time', index: 8}]
    };
    unknown_column(params);  // Unknown column output
    const output =
        '<div><button data-toggle="collapse" data-target="#unknownColumnNotice" class="warning collapsed">Warning - Unknown Column(s) found!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="unknownColumnNotice">\
<p>Description: A column name is unknown.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>Field name</th><th>Index</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>drop_off_time</td><td>8</td></tr>\
</tbody>\
</table>\
<p>Please delete or rename column!</p><br><br></div></div>'
    expect(document.getElementById('warning').innerHTML).toContain(output);
  });

  it('should issue invalid row length warning', function() {
    const params = {
      code: 'invalid_row_length',
      totalNotices: 2,
      notices: [
        {
          filename: 'stop_times.txt',
          csvRowNumber: 17,
          rowLength: 5,
          headerCount: 9
        },
        {
          filename: 'stop_times.txt',
          csvRowNumber: 18,
          rowLength: 5,
          headerCount: 9
        }
      ]
    };
    invalid_row_length(params);
    const output = '<div><button data-toggle="collapse" data-target="#invalidRowLength" class="error collapsed">Error - Invalid csv row length!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="invalidRowLength">\
<p>Description: A row in the input file has a different number of values than specified by the CSV header.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>CSV Row Number</th><th>Row length</th><th>Header count</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>17</td><td>5</td><td>9</td></tr>\
<tr><td>stop_times.txt</td><td>18</td><td>5</td><td>9</td></tr>\
</tbody>\
</table>\
<p>Please set the row length as specified by the CSV header!</p><br><br></div></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  it('should issue wrong parent location type error', function() {
    const params = {
      code: 'wrong_parent_location_type',
      totalNotices: 1,
      notices: [{
        stopId: 'stop101',
        csvRowNumber: 4,
        locationType: 0,
        parentStation: 'station1001',
        parentCsvRowNumber: 7,
        parentLocationType: 2,
        expectedLocationType: 1
      }]
    };
    wrong_parent_location_type(params);
    const output =
        '<div><button data-toggle="collapse" data-target="#wrongParentLocationType" class="error collapsed">Error - Wrong parent location type!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="wrongParentLocationType">\
<p>Description: Incorrect type of the parent location (e.g. a parent for a stop or an entrance must be a station).</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Stop ID</th><th>CSV Row Number</th><th>Location Type</th><th>Parent Station</th><th>Parent CSV Row Number</th><th>Parent Location Type</th><th>Expected Location Type</th></tr>\
</thead>\
<tbody>\
<tr><td>stop101</td><td>4</td><td>0</td><td>station1001</td><td>7</td><td>2</td><td>1</td></tr>\
</tbody>\
</table>\
<p>Please fix the parent location type(s) corresponding to the stop location type(s)!</p><br><br></div></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });


  it('should issue unused shape error', function() {
    const params = {
      code: 'unused_shape',
      totalNotices: 1,
      notices: [{shapeId: 'star2', csvRowNumber: 13}]
    };
    unused_shape(params);
    const output = '<div><p class=\"error"\>Error - Unused shape!</p>\
<p>Description: The shape in shapes.txt is never used by any trip from trips.txt.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Shape ID</th><th>CSV Row Number</th></tr>\
</thead>\
<tbody>\
<tr><td>star2</td><td>13</td></tr>\
</tbody>\
</table>\
<p>Please delete the unused shape(s)!</p><br><br></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for decreasing stop time distance notice */
  it('should issue decreasing stop time distance error', function() {
    const params = {
      code: 'decreasing_stop_time_distance',
      totalNotices: 1,
      notices: [{
        tripId: 'trip1',
        csvRowNumber: 11,
        stopSequence: 3,
        shapeDistTraveled: 5.1,
        prevCsvRowNumber: 10,
        prevStopSequence: 2,
        prevShapeDistTraveled: 5.5,
      }]
    };
    decreasing_stop_time_distance(params);
    const output =
        '<div><p class="error">Error - Decreasing Stop Time Distance(s) found!</p>\
<p>Description: For some trip, stop times have decreasing `shape_dist_travelled` values.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Trip ID</th><th>CSV Row Number</th><th>Stop Sequence</th><th>Shape Distance Traveled</th><th>Previous CSV Row Number</th><th>Previous Stop Sequence</th><th>Previous Shape Distance Traveled</th></tr>\
</thead>\
<tbody>\
<tr><td>trip1</td><td>11</td><td>3</td><td>5.1</td><td>10</td><td>2</td><td>5.5</td></tr>\
</tbody>\
</table>\
<p>Please check distance traveled for the above rows in \'stop_times.txt\'!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Decreasing shape distance notice template test */
  it('should issue decreasing shape distance error', function() {
    const params = {
      code: 'decreasing_shape_distance',
      totalNotices: 1,
      notices: [{
        shapeId: 'shape1',
        csvRowNumber: 17,
        shapeDistTraveled: 5.1,
        shapePtSequence: 5,
        prevCsvRowNumber: 16,
        prevShapeDistTraveled: 5.5,
        prevShapePtSequence: 4
      }]
    };
    decreasing_shape_distance(params);
    const output =
        '<div><p class="error">Error - Decreasing Shape Distance(s) found!</p>\
<p>Description: shape_dist_traveled along a shape in "shapes.txt" are not all increasing.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Shape ID</th><th>CSV Row Number</th><th>Shape Distance Traveled</th><th>Shape Pt Sequence</th><th>Previous CSV Row Number</th><th>Previous Shape Distance Traveled</th><th>Previous Shape Pt Sequence</th></tr>\
</thead>\
<tbody>\
<tr><td>shape1</td><td>17</td><td>5.1</td><td>5</td><td>16</td><td>5.5</td><td>4</td></tr>\
</tbody>\
</table>\
<p>Please check shape dist traveled for the above rows in \'shapes.txt\'!</p>\
<br><br></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for feed info language and engency language mismatch template */
  it('should issue feed info language and engency language mismatch error',
     function() {
       const params = {
         code: 'feed_info_lang_and_agency_lang_mismatch',
         totalNotices: 1,
         notices: [{
           feedInfoLang: 'English',
           agencyLangCollection: ['Spanish', 'French']
         }]
       };
       feed_info_lang_and_agency_lang_mismatch(params);
       const output =
           '<div><p class="error">Error - Language mismatch found!</p>\
<p>Description: Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` \
and `feed_info.feed_lang`. The default language may be multilingual for datasets with \
the original text in multiple languages. In such cases, the feed_lang field should contain \
the language code mul defined by the norm ISO 639-2. If `feed_lang` is not `mul` and does not \
match with `agency_lang`, that\'s an error If there is more than one `agency_lang` and \
`feed_lang` isn\'t `mul`, that\'s an error If `feed_lang` is `mul` and there isn\'t more \
than one `agency_lang`, that\'s an error</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Feed Info Language</th><th>Agency Language Collection</th></tr>\
</thead>\
<tbody>\
<tr><td>English</td><td>Spanish,French</td></tr>\
</tbody>\
</table>\
<p>Please check languages!</p>\
<br><br></div>'
       expect(document.getElementById('error').innerHTML).toContain(output);
     });

  /** Test for inconsistent agency field template */
  it('should issue inconsistent agency field error', function() {
    const params = {
      code: 'inconsistent_agency_field',
      totalNotices: 1,
      notices: [{
        csvRowNumber: 15,
        fieldName: 'Language',
        expected: 'English',
        actual: 'French'
      }]
    };
    inconsistent_agency_field(params);
    const output =
        '<div><p class="error">Error - Inconsistent Agency Field(s) found!</p>\
<p>Description: There is more than 1 agency and timezones or languages are inconsistent among the agencies</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>CSV Row Number</th><th>Field Name</th><th>Expected</th><th>Actual</th></tr>\
</thead>\
<tbody>\
<tr><td>15</td><td>Language</td><td>English</td><td>French</td></tr>\
</tbody>\
</table>\
<p>Please check timezones/languages!</p>\
<br><br></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

/** Test for location without parent station notice */
  it('should issue location without parent station error', function() {
    const params = {
      code: 'location_without_parent_station',
      totalNotices: 1,
      notices: [{
        stopId: "stop1",
        csvRowNumber: 15,
        locationType: 1,
      }]
    };
    location_without_parent_station(params);
    const output = '<p class=\"error\">Error - Location(s) without a parent station found!</p>\
<p>Description: A location that must have `parent_station` field does not have it.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Stop ID</th><th>CSV Row Number</th><th>Location Type</th></tr>\
</thead>\
<tbody>\
<tr><td>stop1</td><td>15</td><td>1</td></tr>\
</tbody>\
</table>\
<p>Please check the parent locations for the above stops!</p>\
<br><br>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** 
   * Test for meaningless trip notice 
   * */
  it('should issue meaingless trip error', function() {
    const params = {
      code: 'meaningless_trip_with_no_more_than_one_stop',
      totalNotices: 1,
      notices: [{
        tripId: "trip1",
        csvRowNumber: 15,
      }]
    };
    meaningless_trip_with_no_more_than_one_stop(params);
    const output = '<p class="error">Error - Meaningless trip(s) found!</p>\
<p>Description: A trip must have at least 2 stops.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Trip ID</th><th>CSV Row Number</th></tr>\
</thead>\
<tbody>\
<tr><td>trip1</td><td>15</td></tr>\
</tbody>\
</table>\
<p>Please above trip(s)!</p>\
<br><br>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });
  /** 
   * Test for missing trip edge arrival or departure time 
   * */
  it('should issue missing trip edge stop time error', function() {
    const params = {
      code: 'missing_trip_edge_arrival_time_departure_time',
      totalNotices: 1,
      notices: [{
        tripId: "Trip1",
        csvRowNumber: 21,
        arrivalOrDepartureTime: "Arrival",
        stopSequence: 21
      }]
    };
    missing_trip_edge_arrival_time_departure_time(params);
    const output = '<p class="error">Error - Missing arrival or departure time for trip(s)!</p>\
<p>Description: The first and last stop for each trip should have both an arrival and departure time.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Trip ID</th><th>CSV Row Number</th><th>Arrival / Depature Time</th><th>Stop Sequence</th></tr>\
</thead>\
<tbody>\
<tr><td>Trip1</td><td>21</td><td>Arrival</td><td>21</td></tr>\
</tbody>\
</table>\
<p>Please check above trip(s)!</p>\
<br><br>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** 
   * Test for overlapping frequency
   * */
  it('should issue overlapping frequency error', function() {
    const params = {
      code: 'overlapping_frequency',
      totalNotices: 1,
      notices: [{
        tripId: "Trip1",
        prevCsvRowNumber: 18,
        prevEndTime: "18:00:00",
        currCsvRowNumber: 19,
        currStartTime:  "18:00:00"
      }]
    };
    overlapping_frequency(params);
    const output = '<p class="error">Error - Overlapping frequency entries found!</p>\
<p>Description: Two frequency entries referring to the same trip may not have an overlapping time range.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Trip ID</th><th>Current CSV Row Number</th><th>Current Start Time</th><th>Previous CSV Row Number</th><th>Previous End Time</th></tr>\
</thead>\
<tbody>\
<tr><td>Trip1</td><td>19</td><td>18:00:00</td><td>18</td><td>18:00:00</td></tr>\
</tbody>\
</table>\
<p>Please check above trip(s)!</p>\
<br><br>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for fast_travel_between_stops */
  it('should issue fast travel between stops warning', function() {
    const params = {
      code: 'fast_travel_between_stops',
      totalNotices: 2,
      notices: [
        {tripId: 'trip1', speedkmh: 4000, stopSequenceList: [6, 7]},
        {tripId: 'trip89', speedkmh: 5000, stopSequenceList: [1, 2]}
      ]
    };
    fast_travel_between_stops(params);
    const output =
        '<div><p class="warning">Warning - Fast Travel Between Stops found!</p>\
<p>Description: Travel speed between stops is very fast!.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>Trip ID</th><th>Travel Speed km/h</th><th>Stop Sequence</th></tr>\
</thead>\
<tbody>\
<tr><td>trip1</td><td>4000</td><td>6,7</td></tr>\
<tr><td>trip89</td><td>5000</td><td>1,2</td></tr>\
</tbody>\
</table>\
<p>Please check travel speed for the above trip(s)!</p>\
<br><br></div>';
    expect(document.getElementById('warning').innerHTML).toContain(output);
  });

  /** Test for trip with duplicate stops template */
  it('should issue trip with duplicate stops warning', function() {
    const params = {
      code: 'route_with_duplicate_stop_notice',
      totalNotices: 2,
      notices: [
        {
          stopName: 'AppleStop',
          stopId1: 'stop101',
          csvRowNumberStop1: 2,
          stopId2: 'stop103',
          csvRowNumberStop2: 3,
          routeId: 'routeA',
          exampleTripId: 'trip1'
        },
        {
          stopName: 'OrangeStop',
          stopId1: 'stop305',
          csvRowNumberStop1: 7,
          stopId2: 'stop308',
          csvRowNumberStop2: 8,
          routeId: 'routeC',
          exampleTripId: 'trip3'
        }
      ]
    };
    trip_with_duplicate_stops(params);
    const output =
        '<div><p class=\"warning"\>Warning - Trip with duplicate stops!</p>\
<p>Description: For a trip, consecutive stop times have the same stop name.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>Stop Name</th><th>Stop ID 1</th><th>CSV Row Number Stop 1</th><th>Stop ID 2</th><th>CSV Row Number Stop 2</th><th>Route ID</th><th>Example Trip ID</th></tr>\
</thead>\
<tbody>\
<tr><td>AppleStop</td><td>stop101</td><td>2</td><td>stop103</td><td>3</td><td>routeA</td><td>trip1</td></tr>\
<tr><td>OrangeStop</td><td>stop305</td><td>7</td><td>stop308</td><td>8</td><td>routeC</td><td>trip3</td></tr>\
</tbody>\
</table>\
<p>Please fix the problem of stop names for the corresponding trip(s)!</p><br><br></div>'
    expect(document.getElementById('warning').innerHTML).toContain(output);
  });

  /** Test for stops too close template */
  it('should issue stops too close warning', function() {
    const params = {
      code: 'stops_too_close_to_each_other_warning',
      totalNotices: 3,
      notices: [
        {
          stopId1: 'stop101',
          csvRowNumberStop1: 2,
          stopId2: 'stop103',
          csvRowNumberStop2: 4,
          tripBufferMeters: 5
        },
        {
          stopId1: 'stop307',
          csvRowNumberStop1: 3,
          stopId2: 'stop325',
          csvRowNumberStop2: 17,
          tripBufferMeters: 5
        },
        {
          stopId1: 'stop501',
          csvRowNumberStop1: 1,
          stopId2: 'stop507',
          csvRowNumberStop2: 5,
          tripBufferMeters: 5
        }
      ]
    };
    stops_too_close(params);
    const output = '<div><p class=\"warning"\>Warning - Stops too close!</p>\
<p>Description: Two stops are too close with each other.</p>\
<p><b>3</b> found:</p>\
<table>\
<thead>\
<tr><th>Stop ID 1</th><th>CSV Row Number Stop 1</th><th>Stop ID 2</th><th>CSV Row Number Stop 2</th><th>Trip Buffer in Meters</th></tr>\
</thead>\
<tbody>\
<tr><td>stop101</td><td>2</td><td>stop103</td><td>4</td><td>5</td></tr>\
<tr><td>stop307</td><td>3</td><td>stop325</td><td>17</td><td>5</td></tr>\
<tr><td>stop501</td><td>1</td><td>stop507</td><td>5</td><td>5</td></tr>\
</tbody>\
</table>\
<p>Please fix the stops positions to make their distance further than the trip buffer meters!</p><br><br></div>'
    expect(document.getElementById('warning').innerHTML).toContain(output);
  });

  /** Test for stop too far from trip shape template */
  it('should issue stop too far from trip shape error', function() {
    const params = {
      code: 'stop_too_far_from_trip_shape',
      totalNotices: 1,
      notices: [{
        stopId: 'stop101',
        stopSequence: 3,
        tripId: 'tripC',
        shapeId: 'circle3',
        tripBufferMeters: 100
      }]
    };
    stop_too_far_from_trip_shape(params);
    const output =
        '<div><p class=\"error"\>Error - Stop too far from trip shape!</p>\
<p>Description: Stop is too far away from the trip shape.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Stop ID</th><th>Stop Sequence</th><th>Trip ID</th><th>Shape ID</th><th>Trip Buffer Meters</th></tr>\
</thead>\
<tbody>\
<tr><td>stop101</td><td>3</td><td>tripC</td><td>circle3</td><td>100</td></tr>\
</tbody>\
</table>\
<p>Please fix the stop position to be within the trip buffer of the trip shape!</p><br><br></div>'
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for stop time with only arrival or departure time template */
  it('should issue stop time with only arrival or departure time warning',
     function() {
       const params = {
         code: 'stop_time_with_only_arrival_or_departure_time',
         totalNotices: 1,
         notices: [{
           csvRowNumber: 8,
           tripId: 'tripC',
           stopSequence: 3,
           specifiedField: 'ARRIVAL_TIME_FIELD_NAME'
         }]
       };
       stop_time_with_only_arrival_or_departure_time(params);
       const output =
           '<div><p class=\"warning"\>Warning - Stop time with only arrival or departure time!</p>\
<p>Description: Stop time is with only arrival time or departure time.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>CSV Row Number</th><th>Trip ID</th><th>Stop Sequence</th><th>Specified Field</th></tr>\
</thead>\
<tbody>\
<tr><td>8</td><td>tripC</td><td>3</td><td>ARRIVAL_TIME_FIELD_NAME</td></tr>\
</tbody>\
</table>\
<p>Please fill in the missing arrival time or departure time for the stop time!</p><br><br></div>'
       expect(document.getElementById('warning').innerHTML).toContain(output);
     });

  /** Test for stop time with departure before arrival time template */
  it('should issue stop time with departure before arrival time error',
     function() {
       const params = {
         code: 'stop_time_with_departure_before_arrival_time',
         totalNotices: 1,
         notices: [{
           csvRowNumber: 11,
           tripId: 'tripB',
           stopSequence: 4,
           departureTime: '10:20:30',
           arrivalTime: '11:18:30'
         }]
       };
       stop_time_with_departure_before_arrival_time(params);
       const output =
           '<div><p class=\"error"\>Error - Stop time with departure before arrival time!</p>\
<p>Description: Departure time is before arrival time for the stop time.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>CSV Row Number</th><th>Trip ID</th><th>Stop Sequence</th><th>Departure Time</th><th>Arrival Time</th></tr>\
</thead>\
<tbody>\
<tr><td>11</td><td>tripB</td><td>4</td><td>10:20:30</td><td>11:18:30</td></tr>\
</tbody>\
</table>\
<p>Please fix the departure time or the arrival time!</p><br><br></div>'
       expect(document.getElementById('error').innerHTML).toContain(output);
     });

  it('should issue stop time with arrival before previous departure time error',
     function() {
       const params = {
         code: 'stop_time_with_arrival_before_previous_departure_time',
         totalNotices: 2,
         notices: [
           {
             csvRowNumber: 11,
             prevCsvRowNumber: 3,
             tripId: 'tripB',
             departureTime: '10:20:30',
             arrivalTime: '10:19:00'
           },
           {
             csvRowNumber: 28,
             prevCsvRowNumber: 26,
             tripId: 'tripV',
             departureTime: '07:18:15',
             arrivalTime: '07:18:00'
           }
         ]
       };
       stop_time_with_arrival_before_previous_departure_time(params);
       const output =
           '<div><p class=\"error"\>Error - Stop time with arrival before previous departure time!</p>\
<p>Description: Arrival for the stop time is before its corresponding previous departure time.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>CSV Row Number</th><th>Previous CSV Row Number</th><th>Trip ID</th><th>Previous Departure Time</th><th>Arrival Time</th></tr>\
</thead>\
<tbody>\
<tr><td>11</td><td>3</td><td>tripB</td><td>10:20:30</td><td>10:19:00</td></tr>\
<tr><td>28</td><td>26</td><td>tripV</td><td>07:18:15</td><td>07:18:00</td></tr>\
</tbody>\
</table>\
<p>Please fix the arrival time or the previous departure time for the stop time!</p><br><br></div>'
       expect(document.getElementById('error').innerHTML).toContain(output);
     });

  /** Test for route unique names notice */
  it('should issue route without unique names error', function() {
    const params = {
      code: 'route_without_unique_names',
      totalNotices: 2,
      notices: [
        {
          routeId: 'routeC',
          routeCsvRowNumber: 8,
          comparedRouteId: 'routeF',
          comparedRouteCsvRowNumber: 2,
          routeLongName: 'RouteApple',
          routeShortName: 'apple',
          routeType: 'SUBWAY',
          agencyId: 'agency3'
        },
        {
          routeId: 'routeW',
          routeCsvRowNumber: 15,
          comparedRouteId: 'routeV',
          comparedRouteCsvRowNumber: 11,
          routeLongName: 'RoutePear',
          routeShortName: 'pear',
          routeType: 'BUS',
          agencyId: 'agency1'
        }
      ]
    };
    route_unique_names(params);
    const output =
        '<div><p class="error">Error - Route without unique names!</p>\
<p>Description: The combination of long name, short name, route type and agency ID for a route is not unique.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>Route ID</th><th>Route CSV Row Number</th><th>Compared Route ID</th><th>Compared Route CSV Row Number</th><th>Route Long Name</th><th>Route Short Name</th><th>Route Type</th><th>Agency ID</th></tr>\
</thead>\
<tbody>\
<tr><td>routeC</td><td>8</td><td>routeF</td><td>2</td><td>RouteApple</td><td>apple</td><td>SUBWAY</td><td>agency3</td></tr>\
<tr><td>routeW</td><td>15</td><td>routeV</td><td>11</td><td>RoutePear</td><td>pear</td><td>BUS</td><td>agency1</td></tr>\
</tbody>\
</table>\
<p>Please change the long name or short name of the route to make it unique!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for station with parent station notice */
  it('should issue station with parent station error', function() {
    const params = {
      code: 'station_with_parent_station',
      totalNotices: 1,
      notices:
          [{stopId: 'StationB', csvRowNumber: 8, parentStation: 'StationW'}]
    };
    station_with_parent_station(params);
    const output =
        '<div><p class="error">Error - Station with parent station!</p>\
<p>Description: A station has parent_station field set.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Station ID</th><th>CSV Row Number</th><th>Parent Station</th></tr>\
</thead>\
<tbody>\
<tr><td>StationB</td><td>8</td><td>StationW</td></tr>\
</tbody>\
</table>\
<p>Please delete the parent station of the station!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for route with short name too long */
  it('should issue route with short name warning', function() {
    const params = {
      code: 'route_short_name_too_long',
      totalNotices: 1,
      notices: [{
        routeId: 'RouteA',
        csvRowNumber: 5,
        routeShortName: 'veryLoooooooooonnnnnnnnnnngggggggggName'
      }]
    };
    route_short_name_too_long(params);
    const output =
        '<button data-toggle="collapse" data-target="#routeShortNameTooLong" class="warning collapsed">Warning - Route short name too long!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="routeShortNameTooLong">\
<p>Description: The short name of a route is too long.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Route ID</th><th>CSV Row Number</th><th>Route Short Name</th></tr>\
</thead>\
<tbody>\
<tr><td>RouteA</td><td>5</td><td>veryLoooooooooonnnnnnnnnnngggggggggName</td></tr>\
</tbody>\
</table>\
<p>Please shorten the route name\'s short name!</p>\
<br><br></div></div>';
    expect(document.getElementById('warning').innerHTML).toContain(output);
  });

  /** Test for start and end time out of order notice */
  it('should issue start and end time out of order error', function() {
    const params = {
      code: 'start_and_end_time_out_of_order',
      totalNotices: 1,
      notices: [{
        filename: 'frequencies.txt',
        csvRowNumber: 12,
        entityId: 'frequencyC',
        startTime: '14:20:10',
        endTime: '14:00:10'
      }]
    };
    start_and_end_time_out_of_order(params);
    const output =
        '<div><p class="error">Error - Start and end time out of order!</p>\
<p>Description: start_time is after the end_time for a row in frequencies.txt.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>File Name</th><th>CSV Row Number</th><th>Entity ID</th><th>Start Time</th><th>End Time</th></tr>\
</thead>\
<tbody>\
<tr><td>frequencies.txt</td><td>12</td><td>frequencyC</td><td>14:20:10</td><td>14:00:10</td></tr>\
</tbody>\
</table>\
<p>Please adjust the start time or end time of the entity!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for start and end date out of order notice */
  it('should issue start and end date out of order error', function() {
    const params = {
      code: 'start_and_end_date_out_of_order',
      totalNotices: 2,
      notices: [
        {
          filename: 'calendar.txt',
          csvRowNumber: 3,
          startDate: '20201230',
          endDate: '20201129'
        },
        {
          filename: 'feed_info.txt',
          csvRowNumber: 4,
          startDate: '20190121',
          endDate: '20181230'
        }
      ]
    };
    start_and_end_date_out_of_order(params);
    const output =
        '<div><p class="error">Error - Start and end date out of order!</p>\
<p>Description: Start date is later than the end date.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>File Name</th><th>CSV Row Number</th><th>Start Date</th><th>End Date</th></tr>\
</thead>\
<tbody>\
<tr><td>calendar.txt</td><td>3</td><td>20201230</td><td>20201129</td></tr>\
<tr><td>feed_info.txt</td><td>4</td><td>20190121</td><td>20181230</td></tr>\
</tbody>\
</table>\
<p>Please adjust the start or the end date!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  /** Test for the same route name and description notice */
  it('should issue the same route name and description error', function() {
    const params = {
      code: 'same_route_name_and_description',
      totalNotices: 2,
      notices: [
        {
          filename: 'FILENAME',
          routeId: 'RouteB',
          csvRowNumber: 12,
          routeDesc: 'RouteFromAppleBlockToOrangeBlock',
          specifiedField: 'route_short_name'
        },
        {
          filename: 'FILENAME',
          routeId: 'RouteP',
          csvRowNumber: 4,
          routeDesc: 'Route12345',
          specifiedField: 'route_long_name'
        }
      ]
    };
    same_route_name_and_description(params);
    const output =
        '<div><p class="error">Error - Same route name and description!</p>\
<p>Description: Name and description of the route are the same.</p>\
<p><b>2</b> found:</p>\
<table>\
<thead>\
<tr><th>File Name</th><th>Route ID</th><th>CSV Row Number</th><th>Route Description</th><th>Specified Field</th></tr>\
</thead>\
<tbody>\
<tr><td>FILENAME</td><td>RouteB</td><td>12</td><td>RouteFromAppleBlockToOrangeBlock</td><td>route_short_name</td></tr>\
<tr><td>FILENAME</td><td>RouteP</td><td>4</td><td>Route12345</td><td>route_long_name</td></tr>\
</tbody>\
</table>\
<p>Please adjust the specified name or the description of the route to make them different!</p>\
<br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(output);
  });

  it('should call the correct functions', function() {
    const params = JSON.stringify({
      notices: [
        {
          code: 'invalid_row_length',
          totalNotices: 13,
          notices: [{
            filename: 'stop_times.txt',
            csvRowNumber: 17,
            rowLength: 5,
            headerCount: 9
          }]
        },
        {
          code: 'unknown_column',
          totalNotices: 1,
          notices: [
            {filename: 'stop_times.txt', fieldName: 'drop_off_time', index: 8}
          ]
        }
      ]
    });
    callCorrespondingFunction(params);
    const errorOutput =
        '<div><button data-toggle="collapse" data-target="#invalidRowLength" class="error collapsed">Error - Invalid csv row length!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="invalidRowLength">\
<p>Description: A row in the input file has a different number of values than specified by the CSV header.</p>\
<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>CSV Row Number</th><th>Row length</th><th>Header count</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>17</td><td>5</td><td>9</td></tr>\
</tbody>\
</table>\
<p>Please set the row length as specified by the CSV header!</p><br><br></div></div>';
    const warningOutput =
        '<div><button data-toggle="collapse" data-target="#unknownColumnNotice" class="warning collapsed">Warning - Unknown Column(s) found!<span>+</span><p>-</p></button>\
<div class="content collapse in" id="unknownColumnNotice">\
<p>Description: A column name is unknown.</p>\<p><b>1</b> found:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>Field name</th><th>Index</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>drop_off_time</td><td>8</td></tr>\
</tbody>\
</table>\
<p>Please delete or rename column!</p><br><br></div>';
    expect(document.getElementById('error').innerHTML).toContain(errorOutput);
    expect(document.getElementById('warning').innerHTML)
        .toContain(warningOutput);
  });

  it('should output raw json if notice has not been implemented', function() {
    const params = {
      notices: [{
        code: 'missing_required_column',
        totalNotices: 1,
        notices: [{filename: 'stop_times.txt', fieldName: 'stop_name'}]
      }]
    };
    callCorrespondingFunction(JSON.stringify(params));
    expect(document.getElementById('unimplementedNotices').innerHTML)
        .toContain(JSON.stringify(params.notices[0]));
  })
});
