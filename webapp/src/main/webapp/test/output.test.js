/*
 * Unit tests for output.js
 */
describe('Output', function() {

  // inject the HTML fixture for the tests
  beforeEach(function() {
    var fixture = '<div id="fixture"><div id="errorOutput"></div><div id="error"></div><div id="warning">\
                  </div><div id="unimplementedNotices"></div></div>';

    document.body.insertAdjacentHTML(
      'afterbegin', 
      fixture);
  });

  // remove the html fixture from the DOM
  afterEach(function() {
    document.body.removeChild(document.getElementById('fixture'));
  });

  it('should issue column warning', function() {
    const params = {
      code: "unknown_column",
      totalNotices: 1,
      notices: [
        {
          filename: "stop_times.txt",
          fieldName: "drop_off_time",
          index: 8
        }
      ]
    };
    unknown_column(params);    // Unknown column output
    const output ='<div><p class=\"warning\">Warning - Unknown Column(s) found!</p>\
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
      code: "invalid_row_length",
      totalNotices: 2,
      notices: [
        {
          filename: "stop_times.txt",
          csvRowNumber: 17,
          rowLength: 5,
          headerCount: 9
        },
        {
          filename: "stop_times.txt",
          csvRowNumber: 18,
          rowLength: 5,
          headerCount: 9
        }
      ]
    };
    invalid_row_length(params);
    const output = "<div><p class=\"error\">Error - Invalid csv row length!</p>\
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
<p>Please set the row length as specified by the CSV header!</p><br><br></div>"
    expect(document.getElementById('error').innerHTML).toContain(output);
  });


  /** Test for feed info language and engency language mismatch template */
  it('should issue feed info language and engency language mismatch error', function() {
    const params = {
      code: "feed_info_lang_and_agency_lang_mismatch",
      totalNotices: 1,
      notices: [
        {
          feedInfoLang: "English",
          agencyLangCollection: ["Spanish", "French"]
        }
      ]
    };
    feed_info_lang_and_agency_lang_mismatch(params);
    const output = "<p class=\"error\">Error - Language mismatch found!</p>\
<p>Description: Files `agency.txt` and `feed_info.txt` must define matching `agency.agency_lang` \
and `feed_info.feed_lang`. The default language may be multilingual for datasets with \
the original text in multiple languages. In such cases, the feed_lang field should contain \
the language code mul defined by the norm ISO 639-2. If `feed_lang` is not `mul` and does not \
match with `agency_lang`, that's an error If there is more than one `agency_lang` and \
`feed_lang` isn't `mul`, that's an error If `feed_lang` is `mul` and there isn't more \
than one `agency_lang`, that's an error</p>\
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
<br><br>"
    expect(document.getElementById('error').innerHTML).toContain(output);
  });
  
  it('should call the correct functions', function() {
    const params = JSON.stringify({
      notices: [
        {
          code: "invalid_row_length",
          totalNotices: 13,
          notices: [
            {
              filename: "stop_times.txt",
              csvRowNumber: 17,
              rowLength: 5,
              headerCount: 9
            }
          ]
        },
        {
          code: "unknown_column",
          totalNotices: 1,
          notices: [
            {
              filename: "stop_times.txt",
              fieldName: "drop_off_time",
              index: 8
            }
          ]
        }
      ]
    });
    callCorrespondingFunction(params);
    const errorOutput = "<div><p class=\"error\">Error - Invalid csv row length!</p>\
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
<p>Please set the row length as specified by the CSV header!</p><br><br></div>";
    const warningOutput = "<div><p class=\"warning\">Warning - Unknown Column(s) found!</p>\
<p>Description: A column name is unknown.</p>\<p><b>1</b> unknown column(s) found in:</p>\
<table>\
<thead>\
<tr><th>Filename</th><th>Field name</th><th>Index</th></tr>\
</thead>\
<tbody>\
<tr><td>stop_times.txt</td><td>drop_off_time</td><td>8</td></tr>\
</tbody>\
</table>\
<p>Please delete or rename column!</p><br><br></div>";
    expect(document.getElementById('error').innerHTML).toContain(errorOutput);
    expect(document.getElementById('warning').innerHTML).toContain(warningOutput);
  });

  it('should output raw json if notice has not been implemented', function() {
    const params = {
      notices: [
        {
          code: "missing_required_column",
          totalNotices: 1,
          notices: [
            {
              filename: "stop_times.txt",
              fieldName: "stop_name"
            }
          ]
        }
      ]
    };
    callCorrespondingFunction(JSON.stringify(params));
    expect(document.getElementById('unimplementedNotices').innerHTML).toContain(JSON.stringify(params.notices[0]));
  })
});
