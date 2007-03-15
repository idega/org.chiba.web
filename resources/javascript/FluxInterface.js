/******************************************************************************
 GENERAL STUFF
 ******************************************************************************/
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


/******************************************************************************
 PAGE UNLOAD
 ******************************************************************************/
window.onbeforeunload = function(e) {
    if (!e) e = event;
    return unload(e);
}

function unload(e) {
    if (isDirty && false) {
        msg = confirmMsg;
        if (!e && window.event) {
            e = window.event;
        }
        e.returnValue = msg;
        return msg;
    }
}

window.onunload = function() {
    dojo.debug("unloading page");
    if (!skipShutdown) closeSession();
}

function closeSession() {
    dojo.debug("closing XForms session");
    var sessionKey = document.getElementById("chibaSessionKey").value;
    DWREngine.setErrorHandler(ignoreExceptions);
    Flux.close(sessionKey, null);
}
function ignoreExceptions(msg){
    alert("ignoring exception: " + msg);
}

/******************************************************************************
 END OF PAGE UNLOAD ROUTINES
 ******************************************************************************/

function useLoadingMessage() {

    DWREngine.setPreHook(function() {
    
    	var indicator = document.getElementById('indicator');
    	
    	if(indicator != null)
	        indicator.className = 'enabled';
    });

    DWREngine.setPostHook(function() {
	    var indicator = document.getElementById('indicator');
    	
    	if(indicator != null)
	        indicator.className = 'disabled';
    });
}

//function pulse(){
//    setTimeout("Flux.keepAlive(sessionRefreshed, " + chibaSessionKey + ")",pulseInterval);
//}

//function sessionRefreshed(){
//    pulse();
//}
/*
just a starter.
*/
function handleExceptions(msg) {
    //    if(msg.indexOf(":") != -1){
    //        alert(msg.substring(msg.lastIndexOf(":") +1 ));
    //    }else{
    alert(msg);
    //    }
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
function chiba_activate(target) {
    // lookup value element fafa
    while (target && ! _hasClass(target, "value")) {
        target = target.parentNode;
    }

    var id = target.id;
    if (id.substring(id.length - 6, id.length) == "-value") {
        // cut off "-value"
        id = id.substring(0, id.length - 6);
    }

    _clear();
    dojo.debug("Flux.activate: " + id);
    useLoadingMessage();
    DWREngine.setErrorHandler(handleExceptions);
    DWREngine.setOrdered(false);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.fireAction(id, sessionKey, updateUI);
}

// call processor to update a controls' value
function setXFormsValue(control) {
    DWREngine.setErrorHandler(handleExceptions);
    var target;
    if (window.event) {
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

    var value = "";
    if (target.value) {
        value = target.value;
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
            for (i = 0; i < elements.length; i++) {
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
            for (i = 0; i < options.length; i++) {
                if (options[i].selected) {
                    multiple.push(options[i].value);
                }
            }
            value = multiple.join(" ");
            break;
        default:
            break;
    }

    _clear();
    dojo.debug("Flux.setXFormsValue: " + id + "='" + value + "'");
    useLoadingMessage();

    DWREngine.setOrdered(false);
    DWREngine.setErrorHandler(handleExceptions);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.setXFormsValue(id, value, sessionKey, updateUI);
    isDirty = true;                                    
}

/******************************************************************************
 CONTROL SPECIFIC FUNCTIONS
 ******************************************************************************/

//function setBoolean(control){
//    DWREngine.setErrorHandler(handleExceptions);
//
//    var id = control.id.substring(0, control.id.length - 6);
//    dojo.debug("Flux.setBoolean control id: " + id);
//
//    var checked="";
//    if(control.checked==true){
//        checked=true;
//    } else{
//        checked=false;
//    }
//
//    dojo.debug("Flux.setBoolean: id " + id + "=" + checked);
//
//    DWREngine.setOrdered(true);
//    DWREngine.setErrorHandler(handleExceptions);
//    var sessionKey = document.getElementById("chibaSessionKey").value;
//    Flux.setXFormsValue(updateUI, id, checked,sessionKey);
//
//}

function setRange(id, value) {
    dojo.debug("Flux.setRangeValue: " + id + "='" + value + "'");

    //todo: fix for IE
    //    var oldValue = document.getElementsByName(id + '-value')[0];
    var oldValue = document.getElementsByClassName('rangevalue', document.getElementById(id))[0];
    if (oldValue) {
        oldValue.className = "step";
        //        oldValue.removeAttribute("name");
    }

    var newValue = document.getElementById(id + value);
    newValue.className = newValue.className + " rangevalue";
    //    newValue.setAttribute("name", id + "-value");

    DWREngine.setErrorHandler(handleExceptions);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.setXFormsValue(id, value, sessionKey, updateUI);
}


// call the processor to set a repeat's index
function setRepeatIndex(e) {
    // get event target
    var target = _getEventTarget(e);

    // lookup repeat item
    while (target && ! _hasClass(target, "repeat-item")) {
        target = target.parentNode;
    }

    // maybe the user clicked on a whitespace node between to items *or*
    // on an already selected item, so there is no item to select
    if ((!target) || _hasClass(target, "repeat-index")) {
        return;
    }

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

    _clear();
    dojo.debug("Flux.setRepeatIndex: " + repeatId + "='" + targetPosition + "'");
    useLoadingMessage();
    DWREngine.setErrorHandler(handleExceptions);
    DWREngine.setOrdered(false);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.setRepeatIndex(repeatId, targetPosition, sessionKey, updateUI);
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
    //    dojo.debug("updateUI: " + data);

    var eventLog = data.documentElement.childNodes;
    //    dojo.debug("EventLog length: " + eventLog.length);

    for (var i = 0; i < eventLog.length; i++) {
        var type = eventLog[i].getAttribute("type");
        var targetId = eventLog[i].getAttribute("targetId");
        var targetName = eventLog[i].getAttribute("targetName");
        var properties = new Array;
        var name;
        for (var j = 0; j < eventLog[i].childNodes.length; j++) {
            if (eventLog[i].childNodes[j].nodeName == "property") {
                name = eventLog[i].childNodes[j].getAttribute("name");
                if (eventLog[i].childNodes[j].childNodes.length > 0) {
                    properties[name] = eventLog[i].childNodes[j].childNodes[0].nodeValue;
                }
                else {
                    properties[name] = "";
                }
            }
        }

        var context = new PresentationContext();
        _handleServerEvent(context, type, targetId, targetName, properties);
    }
}


function _handleServerEvent(context, type, targetId, targetName, properties) {
    //    dojo.debug("handleServerEvent: type=" + type + " targetId=" + targetId);
    switch (type) {
        case "chiba-load-uri":
            isDirty = false;
            if (properties["show"] == "replace") {
              skipShutdown = true;
            }
            context.handleLoadURI(properties["uri"], properties["show"]);
            break;
        case "chiba-render-message":
            context.handleRenderMessage(properties["message"], properties["level"]);
            break;
        case "chiba-replace-all":
            isDirty = false;
            skipShutdown = true;
            context.handleReplaceAll();
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
                context.handleStateChanged(targetId, properties["valid"], properties["readonly"], properties["required"], properties["enabled"], properties["value"], properties["type"]);
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
            break;
        case "chiba-item-deleted":
            context.handleItemDeleted(targetId, targetName, properties["originalId"], properties["position"]);
            break;
        case "chiba-index-changed":
            context.handleIndexChanged(targetId, properties["originalId"], properties["index"]);
            break;
        case "chiba-switch-toggled":
            context.handleSwitchToggled(properties["deselected"], properties["selected"]);
            break;
        case "upload-progress-event":
        //            _updateProgress(targetId,properties["progress"])
            var currentUpload = dojo.widget.byId(targetId + "-value");
            currentUpload.updateProgress(properties["progress"])
            break;
        case "xforms-submit-error":
            _highlightFailedRequired();
            break;
        case "chiba-script-action":
            dojo.debug("handle Script action: " + properties["script"]);
            eval(properties["script"]);
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
        alert("Please provide values for all required fields.")
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
    new Effect.Pulsate(document.getElementById("required-msg"));
    submissionErrors ++;
}


/* help function - still not ready */
function showHelp(helptext) {
    alert(helptext);
    var helpwnd = window.open('', '', 'scrollbars=no,menubar=no,height=400,width=400,resizable=yes,toolbar=no,location=no,status=no');
    helpwnd.document.getElementsByTagName("body")[0].innerHTML = helptext;

}

// Calendar.

/**
 * Initializes the calendar component according to the underlying datatype.
 */
function calendarSetup(id, value, type) {
    // initialize hidden calendar
    // todo: jsCalendar has problems with time part
    var dateTime = type == 'dateTime';
    Calendar.setup({
        date: value && value.length > 0 ? value : null, // use null for empty value
        firstDay: 1, // use monday as first day of week
        showsTime: dateTime, // configure time display
        inputField: id + '-value', // hidden input field
        displayArea: id + '-' + type + '-display', // formatted display area
        button: id + '-' + type + '-button', // date control image button
        ifFormat: dateTime ? '%Y-%m-%dT%H:%M:%S' : '%Y-%m-%d', // ISO date/dateTime format
        daFormat: dateTime ? DATETIME_DISPLAY_FORMAT : DATE_DISPLAY_FORMAT, // configurable display format
        onClose: calendarOnClose, // callback for updating the processor
        onSelect: calendarOnSelect, // callback for updating the processor
        electric: false // matches xf:incremental, should be a parameter
    });

    // jsCalendar updates the display area only on user interaction, not on
    // init/setup nor on internal value updates.
    calendarUpdate(id, value, type);
}

/**
 * Updates the calendar component, namely the display area.
 */
function calendarUpdate(id, value, type) {
    var element = document.getElementById(id + '-' + type + '-display');
    if (element) {
        var date = _parseISODate(value);
        var format = type == 'dateTime' ? DATETIME_DISPLAY_FORMAT : DATE_DISPLAY_FORMAT;

        if (element.innerHTML) {
            // update <span/>, <div/> et al.
            element.innerHTML = date ? date.print(format) : '&nbsp;';
        }
        else {
            // update <input/>
            element.value = date ? date.print(format) : '';
        }

        return true;
    }

    return false;
}

/**
 * Calendar callback for select events.
 *
 * Allows jsCalendar to use form controls as display area too.
 */
function calendarOnSelect(calendar) {
    // copied from calendar-setup.js
    var p = calendar.params;
    var update = (calendar.dateClicked || p.electric);
    if (update && p.flat) {
        if (typeof p.flatCallback == "function")
            p.flatCallback(calendar);
        else
            alert("No flatCallback given -- doing nothing.");
        return false;
    }
    if (update && p.inputField) {
        p.inputField.value = calendar.date.print(p.ifFormat);
        if (typeof p.inputField.onchange == "function")
            p.inputField.onchange();
    }
    if (update && p.displayArea)
    // start patch by unl
    // check for 'innerHTML' property, otherwise try 'value' property
        if (p.displayArea.innerHTML) {
            p.displayArea.innerHTML = calendar.date.print(p.daFormat);
        }
        else {
            p.displayArea.value = calendar.date.print(p.daFormat);
        }
    // end patch by unl
    if (update && p.singleClick && calendar.dateClicked)
        calendar.callCloseHandler();
    if (update && typeof p.onUpdate == "function")
        p.onUpdate(calendar);
}

/**
 * Calendar callback for close events.
 *
 * Hides the calendar and updates the processor.
 */
function calendarOnClose(calendar) {
    calendar.hide();

    var id = calendar.params.inputField.id;
    var value = calendar.params.inputField.value;

    // cut off '-value'
    id = id.substring(0, id.length - 6);
    var sessionKey = document.getElementById("chibaSessionKey").value;
    Flux.setXFormsValue(id, value, sessionKey, updateUI);
}

/**
 * Parses an ISO date/datetime string. Timezones not supported yet.
 */
function _parseISODate(iso) {
    if (!iso || iso.length == 0) {
        return null;
    }

    var separator = iso.indexOf('T');
    if (separator == -1) {
        iso = iso + 'T00:00:00';
    }
    var parts = iso.split('T');
    var date = parts[0].split('-');
    var time = parts[1].split(':');

    return new Date(date[0], date[1] - 1, date[2], time[0], time[1], time[2]);
}
