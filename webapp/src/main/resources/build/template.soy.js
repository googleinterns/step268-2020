// This file was automatically generated from template.soy.
// Please don't edit this file by hand.

if (typeof validator == 'undefined') { var validator = {}; }
if (typeof validator.templates == 'undefined') { validator.templates = {}; }


validator.templates.unknownColumnNotice = function(opt_data, opt_ignored) {
  var output = '';
  var numNotices__soy3 = opt_data.notices.length;
  output += '<p class="warning">Warning - Unknown Column(s) found!</p><p><b>' + soy.$$escapeHtml(numNotices__soy3) + '</b> unknown column(s) found in:</p>';
  var noticeList7 = opt_data.notices;
  var noticeListLen7 = noticeList7.length;
  for (var noticeIndex7 = 0; noticeIndex7 < noticeListLen7; noticeIndex7++) {
    var noticeData7 = noticeList7[noticeIndex7];
    output += '<p>Filename: <i>' + soy.$$escapeHtml(noticeData7.filename) + '</i></p><p>Column name: <i>' + soy.$$escapeHtml(noticeData7.fieldName) + '</i> at position <i>' + soy.$$escapeHtml(noticeData7.index) + '</i></p>';
  }
  output += '<p>Please delete or rename column!</p>';
  return output;
};
