// This file was automatically generated from template.soy.
// Please don't edit this file by hand.

if (typeof validator == 'undefined') { var validator = {}; }
if (typeof validator.templates == 'undefined') { validator.templates = {}; }


validator.templates.unknownColumnNotice = function(opt_data, opt_ignored) {
  var output = '<p class="warning">Warning - Unknown Column(s) found!</p><p><b>' + soy.$$escapeHtml(opt_data.numNotices) + '</b> unknown column(s) found in:</p>';
  var noticeList6 = opt_data.notices;
  var noticeListLen6 = noticeList6.length;
  for (var noticeIndex6 = 0; noticeIndex6 < noticeListLen6; noticeIndex6++) {
    var noticeData6 = noticeList6[noticeIndex6];
    output += '<p>Filename: <i>' + soy.$$escapeHtml(noticeData6.filename) + '</i></p><p>Column name: <i>' + soy.$$escapeHtml(noticeData6.fieldName) + '</i> at position <i>' + soy.$$escapeHtml(noticeData6.index) + '</i></p>';
  }
  output += '<p>Please delete or rename column!</p>';
  return output;
};
