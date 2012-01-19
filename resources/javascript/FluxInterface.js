// todo: make configurable
var DATE_DISPLAY_FORMAT = "%d.%m.%Y";
var DATETIME_DISPLAY_FORMAT = "%d.%m.%Y %H:%M";
var keepAliveTimer;

// global isDirty flag signals that data have changed through user input
var isDirty = false;
// skip shutdown for load and submission actions (these do it themselves)
var skipShutdown = false;

// ***** Messages to the user - you may overwrite these in your forms with inline script blocks
var confirmMsg = "There are changed data. Really exit?";

// ***** Localized variables
if(Localization == null) {
	var Localization = {};
	Localization.STANDARD_LAYER_MSG 				= 'Processing data...';
	Localization.LOADING_MSG                		= 'Loading...';
	Localization.RELOAD_PAGE						= 'Unfortunately the page was not loaded correctly. Please click OK to reload it.';
	Localization.SESSION_EXPIRED		 			= 'Your session has expired. Please try again.';
	Localization.DOWNLOADING_PDF_FOR_XFORM_MESSAGE	= 'Downloading PDF';
	Localization.UPLOADING_FAILED 					= 'Sorry, uploading failed. Please try again.';
	Localization.INVALID_FILE_TO_UPLOAD				= 'Invalid file provided to upload!';
	Localization.CLOSING							= 'Closing...';
	Localization.ERROR_SAVING_FORM					= 'Unable to save data. Please re-fill form with data';
	Localization.CONTINUE_OR_STOP_FILLING_FORM		= 'The form was successfully saved. Do you want to continue filling the form?';
	Localization.USER_MUST_BE_LOGGED_IN				= 'Your session has expired, you must to login to continue your work';
}

if (FluxInterfaceHelper == null) var FluxInterfaceHelper = {};
FluxInterfaceHelper.changingUriManually = false;
FluxInterfaceHelper.WINDOW_KEY = null;

FluxInterfaceHelper.CLOSED_SESSIONS = [];

var chibaXFormsInited = false;
FluxInterfaceHelper.closeLoadingMessageAfterUIUpdated = false; 

FluxInterfaceHelper.getXFormSessionKey = function() {
	return jQuery('#chibaSessionKey').attr('value');
}

function initXForms(){
	if(!chibaXFormsInited) {
	    chibaXFormsInited = true;
	    dojo.event.connect("before",window,"onunload","close");
	}
}

registerEvent(window, 'load', function() {
	var loadedAt = new Date();
	FluxInterfaceHelper.WINDOW_KEY = loadedAt.getTime();
});

/******************************************************************************
 SESSION HANDLING AND PAGE UNLOADING
 ******************************************************************************/
window.onbeforeunload = function(e) {
	if (FluxInterfaceHelper.changingUriManually) {
		return;
	}
	
	showLoadingMessage(Localization.CLOSING);
	//	We want to close session on before unload event
	closeSession();
	
    if (!e) e = event;
    return unload(e);
}

function unload(e) {
    if (isDirty && false) {
        var msg = confirmMsg;
        if (!e && window.event) {
            e = window.event;
        }
        e.returnValue = msg;
        return msg;
    }
}

function close() {
    dojo.debug("close called");
    if (!skipShutdown) closeSession();
}

// Call this whenever we use the session, so we know not to call updateUI needlessly
var lastUpdateTime=0;
function localActivity() {
    lastUpdateTime=new Date().getTime();
}

// call processor to note user typing activity (not value change
// because user hasn't exited field yet), which extends session lifetime.
// Also note that navigating off is no longer OK since we've potentially
// modified something.
// noteActivity will call Flux.noteActivity
// if nothing has happened in the last minute.
function keepAlive() {
    isDirty = true;
    var x = (new Date().getTime());
    if ((x - lastUpdateTime) > (60000)) {
        localActivity();
        if(dwr && dwr.engine){
            dwr.engine.setErrorHandler(handleExceptions);
            dwr.engine.setOrdered(true);
            var sessionKey = document.getElementById("chibaSessionKey").value;
            Flux.keepAlive(sessionKey);
        }
    }
    return false;
}

function closeSession() {
	var sessionKeyElement = document.getElementById("chibaSessionKey");
	if (sessionKeyElement != null) {
    	var originalSessionKey = sessionKeyElement.value;
    	if (existsElementInArray(FluxInterfaceHelper.CLOSED_SESSIONS, originalSessionKey)) {
    		closeAllLoadingMessages();
    		return;
    	}
		
    	dwr.engine.setAsync(false);
    	
    	var sessionKey = originalSessionKey;
    	if (FluxInterfaceHelper.WINDOW_KEY != null) {
    		sessionKey += '@' + FluxInterfaceHelper.WINDOW_KEY;
    	}
    	Flux.close(sessionKey, {
    		callback: function() {
    			skipShutdown = true;
    			FluxInterfaceHelper.CLOSED_SESSIONS.push(originalSessionKey);
    		},
    		errorHandler: function(msg, ex) {
    		}
    	});
	}
}

/******************************************************************************
 END OF SESSION HANDLING AND PAGE UNLOADING
 ******************************************************************************/

function handleExceptions(msg, ex) {
	closeAllLoadingMessages();
	
	if (ex != null && ex.messageToClient != null && ex.messageToClient != '-') {
		humanMsg.displayMsg(ex.messageToClient, {
			timeout: 5000,
			callback: function() {
				FluxInterfaceHelper.sendExceptionNotification(msg, ex);
			}
		});
	} else {
		FluxInterfaceHelper.sendExceptionNotification(msg, ex);
	}
}

FluxInterfaceHelper.sendExceptionNotification = function(msg, ex) {
	if (msg != null && msg.indexOf('Session has expired!') == 0) {
		redirectForm(Localization.SESSION_EXPIRED, {
			callback: function (data) {
				closeAllLoadingMessages();
			}
		});
		return false;
	}
	
	IWCORE.sendExceptionNotification(msg, ex, Localization.RELOAD_PAGE);
	
	return false;
}

/*
This function is called whenever the user presses ENTER in an input or secret
or on a radiobutton or checkbox. Normally this should not result in a post request
in an AJAX environment. The current function simply does nothing. If something is
expected to happen on an ENTER it has to be handled here.
*/
function submitFunction(control) {
    return false;
}

FluxInterfaceHelper.afterChibaActivate = null;

// call processor to execute a trigger
function chibaActivate(target) {
	if (typeof(target) == 'string') {
		target = document.getElementById(target);
	}
	
	forceRepeatIndex(dojo.byId(target));

    // lookup value element
    while (target && !_hasClass(target, "value")) {
        target = target.parentNode;
    }
	if (!target) {
		return false;
	}
	
    var id = target.id;
    if (id.substring(id.length - 6, id.length) == "-value") {
        // cut off "-value"
        id = id.substring(0, id.length - 6);
    }

	showLoadingMessage(Localization.STANDARD_LAYER_MSG);
    
	dwr.engine.setErrorHandler(handleExceptions);
	dwr.engine.setOrdered(true);
	var sessionKey = document.getElementById("chibaSessionKey").value;
	Flux.fireAction(id, sessionKey, window.location.href, {
		callback: function(data) {
			updateUI(data, function() {
				if (FluxInterfaceHelper.afterChibaActivate != null)
					FluxInterfaceHelper.afterChibaActivate();
			});
		}
	});

    return false;
}

// call processor to update a controls' value
function setXFormsValue(control, forceControl) {
	
	var sessionKey = document.getElementById("chibaSessionKey").value;
    if (existsElementInArray(FluxInterfaceHelper.CLOSED_SESSIONS, sessionKey)) {
    	redirectForm(Localization.SESSION_EXPIRED, {
			callback: function (data) {
				closeAllLoadingMessages();
			}
		});
    	return;
    }
	
    dwr.engine.setErrorHandler(handleExceptions);
    var target = null;
    
    //	forceControll is used to ignore the window.event => set to true if you want to call this function on a control, other than the source of the event
    if (window.event && !forceControl) {
        target = window.event.srcElement;
        if (target == null || target.id == null) {
        	target = control;
        }
    } else {
        target = control;
    }

	if (target == null || target.id == null) {
		IWCORE.sendExceptionNotification('Target or target id is unknown in setXFormsValue method in FluxInterface.js (line 240). Target object: ' + target + ', target ID: ' +
			target.id, null, null);
		return;
	}

	var id = null;	
	var value = null;
	if (target.tagName == 'TEXTAREA' && jQuery(target).parent().hasClass('enableHTMLEditor')) {
		var frame = jQuery('#' + target.id + '_ifr');
		if (frame != null && frame.length > 0) {
			value = frame[0].contentWindow.document.body.innerHTML
		}
	}
	if (target.id == '') {
		value = jQuery(target).attr('value');
		var parentElement = jQuery(target).parent();
		while (parentElement != null && (parentElement.attr('id') == null || parentElement.attr('id') == ''))
			parentElement = parentElement.parent();
		id = parentElement.attr('id');
	} else {
	    id = target.id;
	    if (id.substring(id.length - 6, id.length) == "-value") {
	        // cut off "-value"
	        id = id.substring(0, id.length - 6);
	    }
	
		if (value == null) {
		    jQuery('#' + target.id).attr('value');
		    if (value == null && target.value) {
		        value = target.value;
		    }
		}
	}
    if (value == null) {
    	value = '';
    }

    switch (target.type) {
        case "radio":
        // get target id from parent control, since the id passed in is the item's id
            while (! _hasClass(target, "select1")) {
                target = target.parentNode;
            }
            id = target.id;
            break;
        case "checkbox":
        // keep name
            var name = target.name;

        // get target id from parent control, since the id passed in is the item's id
            while (! _hasClass(target, "select")) {
                target = target.parentNode;
            }
            id = target.id;

        // assemble value from selected checkboxes
            var elements = eval("document.chibaform.elements");
            var checkboxes = new Array();
            for (var i = 0; i < elements.length; i++) {
                if (elements[i].name == name && elements[i].type != "hidden" && elements[i].checked) {
                    checkboxes.push(elements[i].value);
                }
            }
            value = checkboxes.join(" ");
            break;
        case "select-multiple":
        // assemble value from selected options
            var options = target.options;
            var multiple = new Array();
            for (var i = 0; i < options.length; i++) {
                if (options[i].selected) {
                    multiple.push(options[i].value);
                }
            }
            value = multiple.join(" ");
            break;
        default:
            break;
    }
    
    dojo.debug("Flux.setXFormsValue: " + id + "='" + value + "'");

    dwr.engine.setOrdered(true);
	dwr.engine.setErrorHandler(handleExceptions);
    Flux.setXFormsValue(id, value, sessionKey, updateUI);
    isDirty = true;             
}

/******************************************************************************
 CONTROL SPECIFIC FUNCTIONS
 ******************************************************************************/

function setRange(id, value) {
    dojo.debug("Flux.setRangeValue: " + id + "='" + value + "'");

    //todo: fix for IE
    var oldValue = document.getElementsByClassName('rangevalue', document.getElementById(id))[0];
    if (oldValue) {
        oldValue.className = "step";
    }

    var newValue = document.getElementById(id + value);
    newValue.className = newValue.className + " rangevalue";

    var sessionKey = document.getElementById("chibaSessionKey").value;
    if (existsElementInArray(FluxInterfaceHelper.CLOSED_SESSIONS, sessionKey)) {
    	redirectForm(Localization.SESSION_EXPIRED, {
			callback: function (data) {
				closeAllLoadingMessages();
			}
		});
    	return;
    }

	dwr.engine.setErrorHandler(handleExceptions);    
    Flux.setXFormsValue(id, value, sessionKey, updateUI);
}

var FluxInterfaceRepeatedIndexes = [];
var FluxInterfaceBusyWithRepeatIndex = false;

// call the processor to set a repeat's index
function setRepeatIndex(e) {
	if (FluxInterfaceBusyWithRepeatIndex) {
		return false;
	}
	FluxInterfaceBusyWithRepeatIndex = true;
	
    // get event target
    var target = _getEventTarget(e);
    if (!target) {
    	return;
    } 
	if (FluxInterfaceRepeatedIndexes[target.id] != null) {
		return false;
	}
	FluxInterfaceRepeatedIndexes[target.id] = 'true';

    // lookup repeat item
    while (target && ! _hasClass(target, "repeat-item")) {
        target = target.parentNode;
    }

    // maybe the user clicked on a whitespace node between to items *or*
    // on an already selected item, so there is no item to select
    if ((!target) || _hasClass(target, "repeat-index")) {
        return;
    }

    chiba.setRepeatIndex(target);
}

/*
Unconditionally forces the repeat index if a trigger in a repeat-item is clicked. Called from activate()
*/
function forceRepeatIndex(control) {
    // get event target
    var target = control;
    
    // lookup repeat item
    while (target && ! _hasClass(target, "repeat-item")) {
        target = target.parentNode;
    }

    if(target == null){
        return;
    }

    chiba.setRepeatIndex(target);
}

function _getEventTarget(event) {
    var target;
    if (window.event) {
        target = window.event.srcElement;
    }
    else {
        target = event.target;
    }

    return target;
}

// callback for updating any control
function updateUI(data, callback) {
	
    dojo.debug("updateUI: " + data);
    
    var eventLog = null;
    if (data && data.childNodes) {
		eventLog = data.childNodes;
    }
    
    if (eventLog == null || eventLog.length == 0) {
    	closeAllLoadingMessages();
    	return;
    }
    
    for (var i = 0; i < eventLog.length; i++) {	
        var type = eventLog[i].getAttribute("type");  
        var targetId = eventLog[i].getAttribute("targetId");  
        var targetName = eventLog[i].getAttribute("targetName");
        var properties = new Array;
        var nameAtt;
        for (var j = 0; j < eventLog[i].childNodes.length; j++) {
            if (eventLog[i].childNodes[j].nodeName == "property") {
                nameAtt = eventLog[i].childNodes[j].getAttribute("name");
                if (eventLog[i].childNodes[j].childNodes.length > 0) {
                    properties[nameAtt] = eventLog[i].childNodes[j].childNodes[0].nodeValue;
                } else {
                    properties[nameAtt] = "";
                }
            }
        }

        var context = new PresentationContext();
        _handleServerEvent(context, type, targetId, targetName, properties);
    }
    
    if (FluxInterfaceHelper.closeLoadingMessageAfterUIUpdated) {
    	closeAllLoadingMessages();
    	FluxInterfaceHelper.closeLoadingMessageAfterUIUpdated = false;
    }

	try {    
	    if (typeof ChibaWorkarounds != 'undefined' && ChibaWorkarounds.publishUIUpdatedEvent) {
	    	jQuery(document).trigger('ChibaWorkarounds-UIUpdatedEvent', [data]);
	    }
	} catch (e) {}
	
	if (callback != null)
		callback();
}

function _handleServerEvent(context, type, targetId, targetName, properties) {
    dojo.debug("handleServerEvent: type=" + type + " targetId=" + targetId);
    switch (type) {
        case "chiba-load-uri":
        	if (properties["show"] == "handlemanually") {
        		FluxInterfaceHelper.changingUriManually = true;
        		closeAllLoadingMessages();
        		humanMsg.displayMsg(Localization.DOWNLOADING_PDF_FOR_XFORM_MESSAGE);
				window.location.href = properties["uri"];
				FluxInterfaceHelper.changingUriManually = false;
				
				break;
            } if (properties["show"] == "handlemanually_window") {
            	FluxInterfaceHelper.changingUriManually = true;
        		
        		var fancyBoxId = 'idFancyBoxForXFormPreview' + new Date().getTime();
        		var link = properties["uri"] + '&showPDF=true';
        		jQuery(document.body).append('<a id="' + fancyBoxId + '" class="pdf" style="display: none;" />');
        		jQuery('#' + fancyBoxId).fancybox({
        			autoScale: false,
					autoDimensions: false,
					hideOnOverlayClick: false,
					width: windowinfo.getWindowWidth() * 0.8,
					height: windowinfo.getWindowHeight() * 0.8,
					onClosed: function() {
						jQuery('#' + fancyBoxId).remove();
					},
					onComplete : function() {
						closeAllLoadingMessages();
					},
					content: '<embed src="'+link+'#nameddest=self&page=1&view=FitH,0&zoom=80,0,0" type="application/pdf" height="100%" width="100%" />'
        		});
        		jQuery('#' + fancyBoxId).trigger('click');
        		
        		FluxInterfaceHelper.changingUriManually = false;
        		
        		break;
    		} else {
	            isDirty = false;
	            if (properties["show"] == "replace") {
	              skipShutdown = true;
	            }
	            context.handleLoadURI(properties["uri"], properties["show"]);
	            closeAllLoadingMessages();
            }
            break;
        case "chiba-render-message":
        	closeAllLoadingMessages();
        	if (properties["level"] == "handlemanually") {
        		if (properties['message'] == 'true') {
        			if (!window.confirm(Localization.CONTINUE_OR_STOP_FILLING_FORM)) {
        				redirectForm(Localization.CLOSING);
        			}
        		} else {
        			redirectForm(Localization.ERROR_SAVING_FORM);
        		}
        	} else {
            	context.handleRenderMessage(properties["message"], properties["level"]);
        	}
            break;
        case "chiba-replace-all":
            isDirty = false;
            skipShutdown = true;
            context.handleReplaceAll(properties["webcontext"]);
            break;
        case "chiba-state-changed":
            if (properties["value"] != "") {
                isDirty = true;                                
            }
            // this is a bit clumsy but needed to distinguish between controls and helper elements
            if (properties["parentId"]) {
                context.handleHelperChanged(properties["parentId"], targetName, properties["value"]);
            }
            else {
                context.handleStateChanged(targetId, targetName, properties["valid"], properties["readonly"], properties["required"], properties["enabled"],
                	properties["value"],properties["type"]);
            }
            break;
        case "chiba-prototype-cloned":
            context.handlePrototypeCloned(targetId, targetName, properties["originalId"], properties["prototypeId"]);
            break;
        case "chiba-id-generated":
            context.handleIdGenerated(targetId, properties["originalId"]);
            break;
        case "chiba-item-inserted":
            context.handleItemInserted(targetId, targetName, properties["originalId"], properties["position"]);
            closeAllLoadingMessages();          
            break;
        case "chiba-item-deleted":
            context.handleItemDeleted(targetId, targetName, properties["originalId"], properties["position"]);
            closeAllLoadingMessages();     
            break;
        case "chiba-index-changed":
            context.handleIndexChanged(targetId, properties["originalId"], properties["index"]);
            break;
        case "chiba-switch-toggled":
            context.handleSwitchToggled(properties["deselected"], properties["selected"]);
            closeAllLoadingMessages();
            break;
        case "upload-progress-event":
            var currentUpload = dojo.widget.byId(targetId + "-value");
            if (currentUpload != null) {
            	currentUpload.updateProgress(properties["progress"]);
            }
            break;
        case "xforms-submit-error":
            _highlightFailedRequired();
            break;
        case "chiba-script-action":
            dojo.debug("handle Script action: " + properties["script"]);
            eval(properties["script"]);
            break;
        case "xforms-focus":
            context.handleFocus(targetId);
            break;
        case "xforms-submit-done":
        	var uri = properties["uri"];
        	if (uri != null) {
        		window.setTimeout(function() {
        			jQuery('#backToCaseOverviewAfterSubmitted').trigger('click');
        		}, 500);
        	}
        	break;
        default:
            dojo.debug("Event " + type + " unknown");
            break;
    }
}

var submissionErrors = 0;
function _highlightFailedRequired() {

    // show an alert if the user repeatedly sends incomplete data
    if (submissionErrors >= 1) {
        //alert("Please provide values for all required fields.")
        submissionErrors = 0;
    }

    //lookup all required fields and check if they contain a value
    var foo = document.getElementsByClassName("required", "chibaform");
    for (var i = 0,j = foo.length; i < j; i++) {
        var control = $(foo[i].id);

        var value = getXFormsControlValue(control);
        if (value == null || value == "") {
            new Effect.Pulsate($(foo[i].id + "-label"));
        }

    }

    submissionErrors ++;
}


/* help function - still not ready */
function showHelp(helptext) {
    var helpwnd = window.open('', '', 'scrollbars=no,menubar=no,height=400,width=400,resizable=yes,toolbar=no,location=no,status=no');
    helpwnd.document.getElementsByTagName("body")[0].innerHTML = helptext;

}
/******************************************************************************
 FUNCTIONS calling Chiba processor - should become an API one day
 ******************************************************************************/

var chiba  = function() {
};

chiba.setRepeatIndex = function(targetRepeatElement){
    var target = targetRepeatElement;
    target.setAttribute("selected", "true");

    var repeatItems = target.parentNode.childNodes;
    var currentPosition = 0;
    var targetPosition = 0;

    // lookup target to compute logical position
    for (var index = 0; index < repeatItems.length; index++) {
        if (repeatItems[index].nodeType == 1 && _hasClass(repeatItems[index], "repeat-item")) {
            currentPosition++;

            if (repeatItems[index].getAttribute("selected") == "true") {
                repeatItems[index].removeAttribute("selected");
                targetPosition = currentPosition;

                // optimistic update
                _addClass(repeatItems[index], "repeat-index-pre");
            }

            _removeClass(repeatItems[index], "repeat-index")
        }
    }

    // lookup repeat id
    while (! _hasClass(target, "repeat")) {
        target = target.parentNode;
    }
    var repeatId = target.id;

    dojo.debug("Flux.setRepeatIndex: " + repeatId + "='" + targetPosition + "'");

    dwr.engine.setErrorHandler(handleExceptions);
    dwr.engine.setOrdered(true);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.setRepeatIndex(repeatId, targetPosition, sessionKey, {
    	callback: function(result) {
   			updateUI(result);
   			FluxInterfaceBusyWithRepeatIndex = false;
   		}
    });
}

FluxInterfaceHelper.startUsingXForm = function() {
	closeAllLoadingMessages();
	manageHelpTextIconsForForm();
	FluxInterfaceHelper.initializeTextAreasAutoResize();
	FluxInterfaceHelper.initializeTinyMCE();
	FluxInterfaceHelper.initializeMaskedInputs();
}

function redirectForm(msg) {
	showLoadingMessage(Localization.LOADING_MSG);
	humanMsg.displayMsg(msg, {
		timeout: 3000,
		callback: function() {
			window.location.href = '/pages';
		}
	});
}

FluxInterfaceHelper.initializeTextAreasAutoResize = function() {
	jQuery.each(jQuery('textarea'), function() {
		var textarea = jQuery(this);
		
		/*var autoResizerInitializedStyleClass = 'autoResizerInitializedStyleClass';
		if (!FluxInterfaceHelper.isTextAreaHtmlEditor(textarea) && !textarea.hasClass(autoResizerInitializedStyleClass)) {
			textarea.autoResize({
				animateDuration: 250,
				extraSpace: 20
			});
			
			if (textarea.attr('value') != '') {
				textarea.triggerHandler('keydown');
			}
			
			textarea.addClass(autoResizerInitializedStyleClass);
		}*/
		textarea.autoGrow();
		
		textarea.blur(function() {
			var value = jQuery(this).attr('value');
			if (value != null && value.length > XFormsConfig.maxStringValueLength) {
				value = value.substring(0, XFormsConfig.maxStringValueLength);
				jQuery(this).attr('value', value);
			}
			textarea.trigger('change');
		});
	});
}

FluxInterfaceHelper.initializeTinyMCE = function() {
	var textAreas = jQuery('textarea');
	if (textAreas == null || textAreas.length == 0) {
		return;
	}
	
	jQuery.each(textAreas, function() {
		var textArea = jQuery(this);
		if (FluxInterfaceHelper.isTextAreaHtmlEditor(textArea)) {
			var readOnly = textArea.attr('disabled');
			
			var languageId = dojo.locale;
			languageId = languageId.split('_')[0];
			
			textArea.tinymce({
				theme: 'advanced',
				plugins: 'safari,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template',
				
				entity_encoding : 'raw',
				
				language: languageId,
				
				theme_advanced_buttons1: 'fullscreen,|,styleselect,formatselect,fontselect,fontsizeselect',
				theme_advanced_buttons2: 'bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,link,unlink,image,|,insertdate,inserttime,preview,|,forecolor,backcolor',
				theme_advanced_buttons3: 'cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo',
				theme_advanced_buttons4: 'tablecontrols,|,hr,removeformat,visualaid,|,charmap,iespell',
				theme_advanced_toolbar_location: 'top',
				theme_advanced_toolbar_align: 'left',
				theme_advanced_statusbar_location: 'bottom',
				theme_advanced_resizing: false,
				
				readonly: readOnly,
				
				init_instance_callback: function(ed) {
					tinymce.dom.Event.add(ed.getWin(), 'blur', function(e) {
						textArea.trigger('change');
					});
				},
				
				setup: function(ed) {
					ed.onChange.add(function() {
						textArea.trigger('change');
					});
				}
			});
		}
	});
}

FluxInterfaceHelper.isTextAreaHtmlEditor = function(textArea) {
	return textArea.parent().hasClass('enableHTMLEditor');
}

FluxInterfaceHelper.initializeMaskedInputs = function() {
	jQuery.each(jQuery('.xFormInputMask_time'), function() {
		var input = jQuery(jQuery('input', jQuery(this))[0]);
		
		input.mask('99:99');
	});
	jQuery.each(jQuery('.xFormInputMask_time_from_to'), function() {
		var input = jQuery(jQuery('input', jQuery(this))[0]);
		
		input.mask('99:99-99:99');
	});
	jQuery.each(jQuery('.xFormInputMask_personalId'), function() {
		var input = jQuery(jQuery('input', jQuery(this))[0]);
		
		if (XFormsConfig.locale == 'sv_SE')
			input.mask('9999999999');
	});
	
	jQuery.each(jQuery('.xFormInputMask_positiveNumber'), function() {
		var input = jQuery(jQuery('input', jQuery(this))[0]);
		
		input.keypress(function(event) {
			// Allow only backspace and delete
			//	46:	delete
			//	8:	backspace
			//	0:	navigation arrows (up, down, left, right)
			if (event.which == 46 || event.which == 8 || event.which == 0) {
				// let it happen, don't do anything
			} else {
				// Ensure that it is a number and stop the keypress
				if (event.which < 48 || event.which > 57) {
					event.preventDefault();
				}
			}
		});
	});
	
	LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/WebUtil.js'], function() {
		WebUtil.getLocalizedString('org.chiba.web', 'characters_left', 'Characters left', {
			callback: function(text) {
				jQuery.each(jQuery('.xFormTextAreaMask_limit-1000'), function() {
					var textArea = jQuery(jQuery('textarea', jQuery(this))[0]);
					FluxInterfaceHelper.initializeCharactersCounter(textArea, text, 1000);
				});
			}
		});
	}, null);
}

FluxInterfaceHelper.initializeCharactersCounter = function(textArea, text, limit) {
	var hasInitializedMark = textArea.hasClass('xFormTextAreaMask_limit-initialized');
	var nameWithoutRepeatKeyword = textArea.attr('name').indexOf('repeat') == -1;
	if (nameWithoutRepeatKeyword) {
		if (hasInitializedMark)
			return true;
	}
	
	textArea.addClass('xFormTextAreaMask_limit-initialized');
	var addCharactersCounter = function(textArea, text) {
		jQuery('span.xformTextAreaCharactersCounter', textArea.parent()).each(function() {
			jQuery(this).remove();
		});
		var leftCharacters = limit - textArea.attr('value').length;
		textArea.parent().append('<span class="xformTextAreaCharactersCounter">' + text + ': <span id="' + textArea.attr('id') + '-counter">' + leftCharacters + '<span></span>');
		textArea.keyup(function(event) {
			FluxInterfaceHelper.countCharacters(event, limit);
		});
	};
	
	if (text == null) {
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/WebUtil.js'], function() {
			WebUtil.getLocalizedString('org.chiba.web', 'characters_left', 'Characters left', {
				callback: function(localizedText) {
					addCharactersCounter(textArea, localizedText);
				}
			});
		}, null);
	} else {
		addCharactersCounter(textArea, text);
	}
}

FluxInterfaceHelper.countCharacters = function(event, limit) {
	if (event == null)
		return false;
	
	var textArea = event.target;
	if (textArea == null)
		return false;
	
	var value = jQuery(textArea).attr('value');
	if (value == null)
		return true;
	
	if (value.length > limit) {
		jQuery(textArea).attr('value', value.substring(0, limit));
		jQuery('#' + textArea.id + '-counter').text(0);
		event.preventDefault();
	} else {
		jQuery('#' + textArea.id + '-counter').text(1000 - value.length);
		return true;
	}
}