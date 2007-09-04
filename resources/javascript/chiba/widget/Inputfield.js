/*
	Copyright 2001-2007 ChibaXForms GmbH
	All Rights Reserved.


*/

dojo.provide("chiba.widget.Inputfield");

dojo.require("dojo.widget.*");
dojo.require("dojo.event");
dojo.require("dojo.html.*");

/*
todo:
- support incremental
- fix tabindex
*/
dojo.widget.defineWidget(
	"chiba.widget.Inputfield",
	dojo.widget.HtmlWidget,
	{
		widgetType: "chiba:Inputfield",
        templatePath: dojo.uri.dojoUri('../chiba/widget/templates/HtmlInputfield.html'),

        // parameters
        id: "",
        name: "",
        title:"",
        tabIndex: "",
        xfreadonly: "false",
        xfincremental: "false",
        value: "",
        fillInTemplate: function(args, frag) {
            var sourceNode = this.getFragNodeRef(frag);
            if(this.xfreadonly == "true"){
                this.domNode.disabled = true;
            }

/*
            dojo.event.connect("before",this.inputNode, "onkeydown",
                function(evt){
                    dojo.debug("onkeydown");
                    if (evt && evt.keyCode && evt.keyCode == 13) {
                        dojo.debug("event target: " + evt.target);
                        dojo.debug("event currenttarget: " + evt.currentTarget);
                        evt.returnValue = false;
                    }
            });
*/

            if(this.xfincremental == "true"){
                dojo.event.connect(this.inputNode, "onkeyup", this, "_updateControl");
            }else{
/*                dojo.event.connect(this.inputNode, "onkeyup",
	                function(evt){
	                    dojo.debug("Keep Alive");
                        return keepAlive(this);
                });*/
                dojo.event.connect(this.inputNode, "onchange", this, "_updateControl");
                dojo.debug("on blur");
            }



        },
        _updateControl: function(){
           dojo.debug("_updateControl");
            if(this.xfreadonly != "true"){
                DWREngine.setOrdered(true);
                DWREngine.setErrorHandler(handleExceptions);
                var sessionKey = document.getElementById("chibaSessionKey").value;
                Flux.setXFormsValue(updateUI,  this.widgetId.substring(0,this.widgetId.length - 6), this.inputNode.value, sessionKey);                
            }else{
               this.inputNode.disabled = true;
           }
        }
    }
);


