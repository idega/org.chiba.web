/*
	Copyright 2001-2007 ChibaXForms GmbH
	All Rights Reserved.


*/
dojo.provide("chiba.widget.DropdownDatePicker");

dojo.require("dojo.widget.DropdownDatePicker");

dojo.widget.defineWidget(
        "chiba.widget.DropdownDatePicker",
        dojo.widget.DropdownDatePicker,
{
    widgetType: "chiba:DropdownDatePicker",
    chibaTime:"T00:00:00+00:00",
    datatype:"date",
    xfreadonly:false,
    id:"",

    postMixInProperties: function(localProperties, frag) {
        if(this.value == null){
            this.value = "";
        }
        this.chibaTime = this.value.substring(10, this.value.length);
        chiba.widget.DropdownDatePicker.superclass.postMixInProperties.apply(this, arguments);
    },

    fillInTemplate: function(args, frag) {
        chiba.widget.DropdownDatePicker.superclass.fillInTemplate.apply(this, arguments);
        this.domNode.setAttribute("xfreadonly", this.xfreadonly);
        this.inputNode.setAttribute("class", "value");

        if (this.xfreadonly == true) {
            this.inputNode.setAttribute("disabled", "disabled");
        }
        else {
            this.inputNode.removeAttribute("disabled");
        }

    },


    onIconClick: function(evt) {
        if (this.xfreadonly == true) {
            this.isEnabled = false;
        }
        else {
            this.isEnabled = true;
        }

        chiba.widget.DropdownDatePicker.superclass.onIconClick.apply(this, arguments);

    },

    updateReadonly: function(readonly) {
        this.xfreadonly = readonly;
        if (this.xfreadonly == true) {
            this.inputNode.setAttribute("disabled", "disabled");
            this.isEnabled = false;
        }
        else {
            this.isEnabled = true;
            this.inputNode.removeAttribute("disabled");
        }
    },
    
    onInputChange: function(/*Date*/dateObj) {
        chiba.widget.DropdownDatePicker.superclass.onInputChange.call(this);
        var sessionKey = document.getElementById("chibaSessionKey").value;
        Flux.setXFormsValue(updateUI, this.id.substring(0, this.id.length - 6), this.getValue(), sessionKey);

        //summary: triggered when this.value is changed
    },


    onSetDate: function() {
        var oldDate = this.getValue();
        chiba.widget.DropdownDatePicker.superclass.onSetDate.call(this);
        var newDate = this.getValue();
        if (oldDate != newDate) {
            var sessionKey = document.getElementById("chibaSessionKey").value;
            if (this.datatype == "dateTime") {
                newDate = newDate + this.chibaTime;
            }
            Flux.setXFormsValue(updateUI, this.id.substring(0, this.id.length - 6), newDate, sessionKey);
        }
    }
},
        "html"
        );