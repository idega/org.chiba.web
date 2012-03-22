if (XFormsUtil == null) var XFormsUtil = {};

function _setElementText (element, text) {
     if (element.firstChild) {
            element.firstChild.data = text;
        }
        else {
            element.appendChild(document.createTextNode(text));
        }
    return true;
}

function _getElementById (element, id) {
    if (element.getAttribute("id") == id) {
        return element;
    }

    var hit;
    var children = element.childNodes;
    for (var index = 0; index < children.length; index++) {
        if (children[index].nodeType == 1) {
            hit = _getElementById(children[index], id);
            if (hit) {
                return hit;
            }
        }
    }

    return null;
}

function _hasClass (element, clazz) {
    if (!element || !element.className) {
        return false;
    }

    // surround classes with spaces to guarantee non-ambigous lookups
    if ((" " + element.className + " ").indexOf(" " + clazz + " ") == -1) {
        return false;
    }

    return true;
}

function _addClass (element, clazz) {
    if (!element || !element.className) {
        return false;
    }

    // surround classes with spaces to guarantee non-ambigous lookups
    if ((" " + element.className + " ").indexOf(" " + clazz + " ") == -1) {
        element.className = element.className + " " + clazz;
        return true;
    }

    return false;
}

function _removeClass (element, clazz) {
    if (!element || !element.className) {
        return false;
    }

    // surround classes with spaces to guarantee non-ambigous lookups
    if ((" " + element.className + " ").indexOf(" " + clazz + " ") > -1) {
        var classList = (" " + element.className + " ").replace(new RegExp(" " + clazz + " "), " ");
        element.className = classList.slice(1, classList.length - 1);
        return true;
    }

    return false;
}

/**
 * replaces a CSS class within the css attribute of given element. 'current' class will be replaced by 'update'.
 */
function _replaceClass (element, current, update) {
    if (!element || !element.className) {
        return false;
    }

    var oldClassName = element.className;

    // surround all strings with spaces to guarantee non-ambigous lookups
    var classList = " " + oldClassName + " ";
    var classCurrent = " " + current + " ";
    var classUpdate = " " + update + " ";

    if (classList.indexOf(classUpdate) == -1) {
        var newClassName = classList.replace(new RegExp(classCurrent), classUpdate);
        if (newClassName.indexOf(classUpdate) == -1) {
            // ensure the new class name, even if no replacement happened
            newClassName = classList + update + " ";
        }

        // remove leading and trailing spaces and update the element's class name
        newClassName = newClassName.slice(1, newClassName.length - 1);
        element.className = newClassName;

        return true;
    }

    return false;
}

function getClassComponent(className, position){
    var components = className.split(" ");
// make an warning msg get position variable  
    if (components[position] = " "){
    	components[position] = "anyURI";
    }
    
    return components[position];
}

function getXFormsControlValue(xformsControl){
    var widget = $(xformsControl.id + "-value");
    dojo.debug("getXFormsControlValue: xfromsControl: " + widget);
    if(!widget){
        return null;
    }
    var value = null;
    if (_hasClass(xformsControl,"input")){
        if(_hasClass(xformsControl,"date")){
            var dateWidget = dojo.widget.byId(xformsControl.id + "-value");
            value = dateWidget.getValue();
        } else if(widget.type.toLowerCase() == "checkbox") {
            if(widget.checked){
                value = "true";
            }else{
                value = "false";
            }
        }else{
            value = widget.value;
        }
    }else if (_hasClass(xformsControl,"output")){
        if (widget.type.toLowerCase() == "a") {
            value = widget.href;
        }else if (widget.type.toLowerCase() == "span"){
            value = widget.innerText;
        }
    }else if (_hasClass(xformsControl,"range")){
        value = widget.value;
    }else if (_hasClass(xformsControl,"secret")){
        value = widget.value;
    }else if (_hasClass(xformsControl,"select")){
        var result="";
        if (widget.type.toLowerCase() == "select-multiple") {
            var options = widget.options.length;
            var option;
            for (var i = 0; i < options; i++) {
                option = widget.options[i];
                if (option.selected == true) {
                    result += " " + option.value;
                }
            }
            value = Trim(result);
        }else{
            var elements = eval("document.chibaform.elements");
            for (var i = 0; i < elements.length; i++) {
                //todo: hack for now - this will break when dataprefix is changed !
                if (elements[i].name == "d_" + xformsControl.id && elements[i].type != "hidden" && elements[i].checked) {
                    result += " " + elements[i].value;
                }
            }
            value=result;
        }
    }else if (_hasClass(xformsControl,"select1")){
        if (widget.type.toLowerCase() == "select-one") {
            var options = widget.options.length;
            var option;
            for (var i = 0; i < options; i++) {
                option = widget.options[i];
                if (option.selected == true) {
                    value = option.value;
                    break;
                }
            }
        }else {
            var elements = eval("document.chibaform.elements");
            for (var i = 0; i < elements.length; i++) {
                //todo: hack for now - this will break when dataprefix is changed !
                if (elements[i].name == "d_" + xformsControl.id && elements[i].type != "hidden" && elements[i].checked) {
                    value = elements[i].value;
                }
            }
        }
    }else if(_hasClass(xformsControl,"submit")){
    }else if(_hasClass(xformsControl,"textarea")){
        value = widget.value;
    }else if(_hasClass(xformsControl,"trigger")){
    }else if(_hasClass(xformsControl,"upload")){

    }
    dojo.debug("widget=" + widget + ":value='" + value + "'");
    return value;
}

function Trim(TRIM_VALUE){
    if(TRIM_VALUE.length < 1){
    return"";
    }
    TRIM_VALUE = RTrim(TRIM_VALUE);
    TRIM_VALUE = LTrim(TRIM_VALUE);
    if(TRIM_VALUE==""){
    return "";
    }
    else{
    return TRIM_VALUE;
    }
}

function RTrim(VALUE){
    var w_space = String.fromCharCode(32);
    var v_length = VALUE.length;
    var strTemp = "";
    if(v_length < 0){
    return"";
    }
    var iTemp = v_length -1;

    while(iTemp > -1){
        if(VALUE.charAt(iTemp) == w_space){
        }
        else{
            strTemp = VALUE.substring(0,iTemp +1);
            break;
        }
        iTemp = iTemp-1;

    }
    return strTemp;
}

function LTrim(VALUE){
    var w_space = String.fromCharCode(32);
    if(v_length < 1){
    return"";
    }
    var v_length = VALUE.length;
    var strTemp = "";

    var iTemp = 0;

    while(iTemp < v_length){
        if(VALUE.charAt(iTemp) == w_space){
        }
        else{
            strTemp = VALUE.substring(iTemp,v_length);
            break;
        }
        iTemp = iTemp + 1;
    }
    return strTemp;
}

function manageHelpTextIconsForForm() {
	jQuery('.help-text').each(function() {
		jQuery(this).children('span.output').hide();
		
		if (jQuery(this).attr('helptextinited') == null) {
			jQuery(this).attr('helptextinited', 'true');
			
			jQuery(this).prepend('<span class="helpIcon"></span>');
			jQuery(this).children('.helpIcon').mouseover(function() {
				jQuery('.help-text span.output').hide();
				jQuery(this).siblings('span.output').show('fast').bind('click mouseout', function() {
						jQuery('.help-text span.output').hide('slow');
			
				});
			});
		}
	});
}

XFormsUtil.downloadAgreement = function() {
	var processName = null;
	if (typeof BPMConfiguration != 'undefined') {
		if (BPMConfiguration.processName) {
			processName = BPMConfiguration.processName;
		}
	}
	
	XFormsUtil.downloadAgreementWithProcessName(processName);
}

XFormsUtil.downloadAgreementWithProcessName = function(processName) {
	FluxInterfaceHelper.changingUriManually = true;
	showLoadingMessage(Localization.LOADING_MSG);
	LazyLoader.loadMultiple(['/dwr/interface/ProcessAgreementProvider.js', '/dwr/engine.js'], function() {
		try {
			ProcessAgreementProvider.getAgreementForProcess(processName, {
				callback: function(uri) {
					closeAllLoadingMessages();
					window.location.href = uri;
					FluxInterfaceHelper.changingUriManually = false;
				}
			});
		} catch(e) {
			FluxInterfaceHelper.changingUriManually = false;
			closeAllLoadingMessages();
			alert('Sorry, some error occurred while downloading agreement');
			handleExceptions('Error downloading agreement for process: ' + processName, e);
		}
	}, null);
}