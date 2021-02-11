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
        '<div><p class=\"warning\">Warning - Unknown Column(s) found!</p>\
<p>Description: A column name is unknown.</p>\
<p><b>1</b> unknown column(s) found in:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>Field name</th><th>Index</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>drop_off_time</td><td>8</td></tr>\
</tbody>\
</table>\
<p>Please delete or rename column!</p><br><br></div>'
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
    const output = '<div><p class="error">Error - Invalid csv row length!</p>\
<p>Description: A row in the input file has a different number of values than specified by the CSV header.</p>\
<p><b>2</b> Invalid row length found in:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>CSV Row Number</th><th>Row length</th><th>Header count</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>17</td><td>5</td><td>9</td></tr>\
<tr><td>stop_times.txt</td><td>18</td><td>5</td><td>9</td></tr>\
</tbody>\
</table>\
<p>Please set the row length as specified by the CSV header!</p><br><br></div>'
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
        '<div><p class=\"error"\>Error - Wrong parent location type!</p>\
<p>Description: Incorrect type of the parent location (e.g. a parent for a stop or an entrance must be a station).</p>\
<p><b>1</b> Wrong parent location type found in:</p>\
<table>\
<thead>\
<tr><th>Stop ID</th><th>CSV Row Number</th><th>Location Type</th><th>Parent Station</th><th>Parent CSV Row Number</th><th>Parent Location Type</th><th>Expected Location Type</th></tr>\
</thead>\
<tbody>\
<tr><td>stop101</td><td>4</td><td>0</td><td>station1001</td><td>7</td><td>2</td><td>1</td></tr>\
</tbody>\
</table>\
<p>Please fix the parent location type(s) corresponding to the stop location type(s)!</p><br><br></div>'
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
<p><b>1</b> Unused shape found in:</p>\
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

  /**
   * Test for decreasing stop time distance notice
   * */
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
<p><b>1</b> decreasing stopTimeDistTraveled found in:</p>\
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
        '<div><p class="error">Error - Invalid csv row length!</p>\
<p>Description: A row in the input file has a different number of values than specified by the CSV header.</p>\
<p><b>1</b> Invalid row length found in:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>CSV Row Number</th><th>Row length</th><th>Header count</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>17</td><td>5</td><td>9</td></tr>\
</tbody>\
</table>\
<p>Please set the row length as specified by the CSV header!</p><br><br></div>';
    const warningOutput =
        '<div><p class="warning">Warning - Unknown Column(s) found!</p>\
<p>Description: A column name is unknown.</p>\<p><b>1</b> unknown column(s) found in:</p>\
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
