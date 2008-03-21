/*
	Copyright 2001-2007 ChibaXForms GmbH
	All Rights Reserved.


*/

dojo.provide("chiba.widget.Boolean");
dojo.require("dojo.widget.*");
dojo.require("dojo.event");
dojo.require("dojo.html.*");

/*
todo:
- support incremental
- fix tabindex
*/
dojo.widget.defineWidget(
	"chiba.widget.Boolean",
	dojo.widget.HtmlWidget,
	{
		widgetType: "chiba:Boolean",
        templatePath: dojo.uri.dojoUri('../chiba/widget/templates/HtmlCheckBox.html'),

        // parameters
        id: "",
        name: "",
        title:"",
        checked:false,
        tabIndex: "",
        xfreadonly: "false",
        xfincremental: "true",

        fillInTemplate: function(args, frag) {
            var sourceNode = this.getFragNodeRef(frag);

            //this is a bad hack - the initial prototype of a repeat simply contains a input type="text"
            //with a value which must be mapped to the checkbox checked state

            if(sourceNode.value=="false"){
                this.checked=false;
                this.domNode.removeAttribute("checked");
            }

            else{
                this.domNode.setAttribute("checked", this.checked);
            }

            if(this.xfreadonly == "true"){
                this.domNode.disabled = true;
            }

            if(this.xfincremental == "true"){
                dojo.event.connect(this.domNode, "onclick", this, "_updateControl");

            }else{
                dojo.event.connect(this.domNode, "onblur", this, "_updateControl");
            }

        },
        
        postCreate: function(){
          this.domNode.checked = this.checked;
        },
        _updateControl: function(){
           if(this.xfreadonly != "true"){
               if(!this.domNode.checked == true){
                    this.checked = false;
                    this.domNode.removeAttribute("checked");
               }else {
                   this.checked = true;
                   this.domNode.setAttribute("checked", this.checked);
               }
               dojo.debug("Boolean._updateControl > checked: " + this.checked );

                DWREngine.setOrdered(true);
                DWREngine.setErrorHandler(handleExceptions);
                var sessionKey = document.getElementById("chibaSessionKey").value;
                Flux.setXFormsValue(updateUI,  this.widgetId.substring(0,this.widgetId.length - 6), this.checked,sessionKey);
            }else{
               this.inputNode.disabled = true;
           }
        }
    }
);


