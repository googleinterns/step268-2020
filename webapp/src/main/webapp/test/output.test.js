/*
 * Unit tests for output.js
 */
describe('Output', function() {

  // inject the HTML fixture for the tests
  beforeEach(function() {
    var fixture = '<div id="fixture"><div id="warning"></div></div>';

    document.body.insertAdjacentHTML(
      'afterbegin', 
      fixture);
  });

  // remove the html fixture from the DOM
  afterEach(function() {
    document.body.removeChild(document.getElementById('fixture'));
  });

  it('should issue column warning', function() {
      unknownColumnWarning();
      // TODO: Update the test to be dependent on the notice once unknownColumnWarning uses input notice.
    expect(document.getElementById('warning').innerHTML).toContain('<p class="warning">Warning - Unknown Column(s) found!</p><p><b>1</b> unknown column(s) found in:</p><p>Filename: <i>hello.txt</i></p><p>Column name: <i>column name</i> at position <i>5</i></p><p>Please delete or rename column!</p>');
  });

});
