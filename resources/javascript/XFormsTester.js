if (XFormsTester == null) var XFormsTester = {};

XFormsTester.OPENED_SESSIONS = 0;

XFormsTester.openSessions = function(number) {
	if (XFormsTester.OPENED_SESSIONS < number) {
		XFormsTester.OPENED_SESSIONS++;
		window.open(window.location.href, '', '');
	}
	
	window.setTimeout(XFormsTester.testUploads, 3000);
}

XFormsTester.testButtons = function() {
	jQuery.each(jQuery('input[type=button]'), function() {
		jQuery(this).trigger('click');
	});
}
	
XFormsTester.testUploads = function() {
	jQuery.each(jQuery('div.deselected-case'), function() {
		jQuery(this).removeClass('deselected-case').addClass('selected-case');
	});
	
	for (var i = 1; i < dojo.widget.widgets.length; i++) {
		dojo.widget.widgets[i]._chooseFile(true);
	}
}