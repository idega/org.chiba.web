/******************************************************************************
 GENERAL STUFF
 ******************************************************************************/
// todo: make configurable
var DATE_DISPLAY_FORMAT = "%d.%m.%Y";
var DATETIME_DISPLAY_FORMAT = "%d.%m.%Y %H:%M";
var keepAliveTimer;

// Interval which keeps active session alive if xform is open in browser
//var sessionPollingInterval = 3600000;
//var noneActivityInterval = 86400000;
// global isDirty flag signals that data have changed through user input
var isDirty = false;
// skip shutdown for load and submission actions (these do it themselves)
var skipShutdown = false;

// ***** Messages to the user - you may overwrite these in your forms with inline script blocks
var confirmMsg = "There are changed data. Really exit?";

// ***** Localised variables
if(Localization == null) {
	var Localization = {};
	Localization.STANDARD_LAYER_MSG 		= 'Processing Data';
	Localization.LOADING_MSG                = 'Loading...';
	Localization.RELOAD_PAGE				= 'Unfortunately the page was not loaded correctly. Please click OK to reload it.';
	Localization.SESSION_EXPIRED 			= 'Your session has expired. Please try again.';
	Localization.DOWNLOADING_PDF_FOR_XFORM_MESSAGE = 'Downloading PDF';
}

if (FluxInterfaceHelper == null) var FluxInterfaceHelper = {};
FluxInterfaceHelper.sendingErrorMail = false;
FluxInterfaceHelper.userDeniedToReloadPageOnError = false;

/******************************************************************************
 PAGE init
 ******************************************************************************/
 
var chibaXFormsInited = false;
 
function initXForms(){
	
	if(!chibaXFormsInited) {
	
	    chibaXFormsInited = true;
	    dojo.event.connect("before",window,"onunload","close");
	}
}

/******************************************************************************
 SESSION HANDLING AND PAGE UNLOADING
 ******************************************************************************/
window.onbeforeunload = function(e) {
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

function close(){
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
    var sessionKey = document.getElementById("chibaSessionKey").value;
    dwr.engine.setErrorHandler(function(msg, ex) {});
    dwr.engine.setAsync(false);    
    Flux.close(sessionKey);
}

/******************************************************************************
 END OF SESSION HANDLING AND PAGE UNLOADING
 ******************************************************************************/

function handleExceptions(msg, ex) {
	closeAllLoadingMessages();
	
	if (msg == "Session has expired") {
			redirectForm(Localization.SESSION_EXPIRED, {callback: function (data) {
									closeAllLoadingMessages();			
					}});
	
		return false;
		
	}
	
	if (!FluxInterfaceHelper.sendingErrorMail) {
		FluxInterfaceHelper.sendingErrorMail = true;
		LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/Flux.js'], function() {
			Flux.sendEmail('[XForm JavaScript error] ERROR on: ' + window.location.href, 'Error: ' + msg + '\nException: ' + ex + '\nBrowser: ' +
				navigator.userAgent, {
				callback: function(data) {
					FluxInterfaceHelper.sendingErrorMail = false;
				}
			});
		}, null);
	}
	
	if (!FluxInterfaceHelper.userDeniedToReloadPageOnError) {
		if (window.confirm(Localization.RELOAD_PAGE)) {
			reloadPage();
			return false;
		}
		else {
			FluxInterfaceHelper.userDeniedToReloadPageOnError = true;
		}
	}
	
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

// call processor to execute a trigger
function chibaActivate(target) {
	if (typeof(target) == 'string') {
		target = document.getElementById(target);
	}
	
//	checkIfLoadedScriptsForChibaXForm(function() {
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
	    Flux.fireAction(id, sessionKey, {
	    	callback: function(data) {
	    		updateUI(data);
	    	}
	    });
	//});
  
    return false;
}

// call processor to update a controls' value
function setXFormsValue(control, forceControl) {
    dwr.engine.setErrorHandler(handleExceptions);
    var target;
    //forceControll is used to ignore the window.event
    // => set to true if you want to call this function on a control, other than the source of the event
    if (window.event && !forceControl) {
        target = window.event.srcElement;
    }
    else {
        target = control;
    }

    var id = target.id;
    if (id.substring(id.length - 6, id.length) == "-value") {
        // cut off "-value"
        id = id.substring(0, id.length - 6);
    }

    var value = jQuery('#' + target.id).attr('value');
    if (value == null && target.value) {
        value = target.value;
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
    var sessionKey = document.getElementById("chibaSessionKey").value;
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

    dwr.engine.setErrorHandler(handleExceptions);
    var sessionKey = document.getElementById("chibaSessionKey").value;
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
function updateUI(data) {
    dojo.debug("updateUI: " + data);
    
    var eventLog = null;
    if (data && data.childNodes) {
		eventLog = data.childNodes;
    }
    
    if (eventLog == null || eventLog.length == 0){
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
                }
                else {
                    properties[nameAtt] = "";
                }
            }
        }

        var context = new PresentationContext();
        _handleServerEvent(context, type, targetId, targetName, properties);
        
    }
}


function _handleServerEvent(context, type, targetId, targetName, properties) {
    dojo.debug("handleServerEvent: type=" + type + " targetId=" + targetId);
    switch (type) {
        case "chiba-load-uri":
        	if (properties["show"] == "handlemanually") {
        		closeAllLoadingMessages();
        		humanMsg.displayMsg(Localization.DOWNLOADING_PDF_FOR_XFORM_MESSAGE);
				window.location.href = properties["uri"];
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
            context.handleRenderMessage(properties["message"], properties["level"]);
            closeAllLoadingMessages();
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
                context.handleStateChanged(targetId, targetName, properties["valid"], properties["readonly"], properties["required"], properties["enabled"], properties["value"],properties["type"]);
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
}

function redirectForm(msg) {
	showLoadingMessage(msg);
	window.location.href = '/pages';	
}

FluxInterfaceHelper.initializeTextAreasAutoResize = function() {
	jQuery.each(jQuery('textarea'), function() {
		var textarea = jQuery(this);
		
		var autoResizerInitializedStyleClass = 'autoResizerInitializedStyleClass';
		if (!FluxInterfaceHelper.isTextAreaHtmlEditor(textarea) && !textarea.hasClass(autoResizerInitializedStyleClass)) {
			textarea.autoResize({
				animateDuration: 250,
				extraSpace: 20
			});
			
			if (textarea.attr('value') != '') {
				textarea.triggerHandler('keydown');
			}
			
			textarea.addClass(autoResizerInitializedStyleClass);
		}
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
				
				language: languageId,
				
				theme_advanced_buttons1: 'styleselect,formatselect,fontselect,fontsizeselect',
				theme_advanced_buttons2: 'bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,link,unlink,image,|,insertdate,inserttime,preview,|,forecolor,backcolor',
				theme_advanced_buttons3: 'cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo',
				theme_advanced_buttons4: 'tablecontrols,|,hr,removeformat,visualaid,|,charmap,iespell',
				theme_advanced_toolbar_location: 'top',
				theme_advanced_toolbar_align: 'left',
				theme_advanced_statusbar_location: 'bottom',
				theme_advanced_resizing: true,
				
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