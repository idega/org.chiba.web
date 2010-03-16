if (XFormsTester == null) var XFormsTester = {};

XFormsTester.OPENED_SESSIONS = 0;

XFormsTester.INPUT_ELEMENTS = null;
XFormsTester.FORM_BUTTONS = null;
XFormsTester.PDF_BUTTONS = null;

XFormsTester.openSessions = function(number) {
	if (XFormsTester.OPENED_SESSIONS < number) {
		XFormsTester.OPENED_SESSIONS++;
		window.open(window.location.href, '', '');
	}
	
	var timeOutId = window.setTimeout(function() {XFormsTester.testXForm(timeOutId);}, 3000);
}

XFormsTester.testXForm = function(timeOutId) {
	if (timeOutId != null) {
		window.clearTimeout(timeOutId);
	}
	
	var nextTimeOut = 250;
	var decision = XFormsTester.getDecisionOnTesting();
	if (decision < 80) {
		//	Set random values
		XFormsTester.setRandomValue();
	} else if (decision < 95) {
		//	"Click" random buttons
		XFormsTester.testNavigationButtons();
	} else if (decision < 99) {
		//	Test 'XForm to PDF' engine
		XFormsTester.testPDFButton();
		nextTimeOut = 45000;
	} else if (decision < 100) {
		//	Test uploads
		nextTimeOut = 10000;
		XFormsTester.testUploads();
	}
	
	var timeOutId = window.setTimeout(function() {XFormsTester.testXForm(timeOutId);}, nextTimeOut);
}

XFormsTester.getDecisionOnTesting = function() {
	return XFormsTester.getRandomNumber(100);
}

XFormsTester.getRandomNumber = function(maxValue) {
	return Math.floor(Math.random() * maxValue);
}

XFormsTester.setRandomValue = function() {
	var inputElements = XFormsTester.INPUT_ELEMENTS;
	if (inputElements == null) {
		inputElements = [];
		jQuery.each(jQuery('.value'), function() {
			var type = jQuery(this).attr('type');
			if (type == 'text' || type == 'radio' || type == 'checkbox') {
				inputElements.push(this);
			} else if (this.tagName == 'TEXTAREA') {
				inputElements.push(this);
			}
		});
		XFormsTester.INPUT_ELEMENTS = inputElements;
	}
	
	var randomElementIndex = XFormsTester.getRandomNumber(inputElements.length);
	var randomElement = inputElements[randomElementIndex];
	var type = jQuery(randomElement).attr('type');
	if (type == 'text' || randomElement.tagName == 'TEXTAREA') {
		jQuery(randomElement).attr('value', 'Random value for element: ' + randomElement.id);
	} else if (type == 'radio' || type == 'checkbox') {
		jQuery(randomElement).attr('checked', true);
	}
	jQuery(randomElement).trigger('blur');
}

XFormsTester.testNavigationButtons = function() {
	var buttons = XFormsTester.FORM_BUTTONS;
	if (buttons == null) {
		buttons = XFormsTester.getButtons(['Back', 'Next', 'Næsta skref', 'Til baka']);
		XFormsTester.FORM_BUTTONS = buttons;
	}
	
	XFormsTester.clickRandomButton(buttons);
}

XFormsTester.testPDFButton = function() {
	var pdfButtons = XFormsTester.PDF_BUTTONS;
	if (pdfButtons == null) {
		pdfButtons = XFormsTester.getButtons(['Get as PDF', 'Sækja sem PDF']);
		XFormsTester.PDF_BUTTONS = pdfButtons;
	}
	
	XFormsTester.clickRandomButton(pdfButtons);
}

XFormsTester.getButtons = function(values) {
	var buttons = [];
	jQuery.each(jQuery('input[type=button]'), function() {
		var button = this;
		var value = jQuery(button).attr('value');
		if (jQuery.inArray(value, values) != -1) {
			buttons.push(button);
		}
	});
	return buttons;
}

XFormsTester.clickRandomButton = function(buttons) {
	var randomButtonIndex = XFormsTester.getRandomNumber(buttons.length);
	var randomButton = buttons[randomButtonIndex];
	jQuery(randomButton).trigger('click');
}
	
XFormsTester.testUploads = function() {
	jQuery.each(jQuery('div.deselected-case'), function() {
		jQuery(this).removeClass('deselected-case').addClass('selected-case');
	});
	
	for (var i = 1; i < dojo.widget.widgets.length; i++) {
		dojo.widget.widgets[i]._chooseFile(true);
	}
}