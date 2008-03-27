/*
	Copyright 2001-2007 ChibaXForms GmbH
	All Rights Reserved.
*/

dojo.provide("chiba.widget.Upload");

dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");

dojo.widget.defineWidget(
	"chiba.widget.Upload",
	dojo.widget.HtmlWidget,
	{
		widgetType: "chiba:Upload",
        templatePath: dojo.uri.dojoUri('../chiba/widget/templates/HtmlUpload.html'),

        // parameters
        disabled: "",
        id: "",
        xformsId:"",
        name: "",
        title:"",
        xfreadonly: "false",
        tabIndex: -1,
        css:"",
        disabledNodes: new Array(),
        inputNode: null,
        progress: null,
        progressBackground: null,
        fillInTemplate: function() {
  
            // todo: this var is a candidate for a (to be implemented) superclass
            this.xformsId = this.id.substring(0, this.id.length - 6);
//            dojo.debug("Upload xformsId: " + this.xformsId);

            this.progress.id = this.xformsId + "-progress";
            this.progressBackground.id = this.xformsId + "-progress-bg";

            var xformsControl = dojo.byId(this.xformsId);
            _replaceClass(xformsControl,"upload","upload " + this.css);
//            _addClass(xformsControl, this.css);

            if (this.xfreadonly == "true") {
                this.inputNode.disabled = true;
            }

        },
        onChange: function() {
        
            if (this.xfreadonly == "true") {
                this.inputNode.disabled = true;
            }
            else {
                    this._submitFile(this.inputNode);
            }
        },
        updateProgress: function (value) {
        
            var progressDiv = document.getElementById(this.xformsId + "-progress-bg");

            if (value != 0) {
                progressDiv.style.width = value + "%";
            }

            if (value == 100 || value < 0) {
                if(value < 0){
                    alert("Upload failed");
                }
                // stop polling
                clearInterval(progressUpdate);

                // reset disabled controls
                for (var i = 0, j = this.disabledNodes.length; i < j; i++) {
                    this.disabledNodes.pop().disabled = false;
                }

                // reset progress bar
                var elemId = this.xformsId + "-progress-bg";

                setTimeout("document.getElementById('" + elemId + "').style.width=0", 2000);
                setTimeout("Effect.BlindUp('" + this.xformsId + "-progress')", 1500);
            }
        },
        _submitFile: function(){
            // disable all controls contained in repeat prototypes to avoid
            // inconsistent updates.
            var rPrototypes = document.getElementsByClassName("repeat-prototype", "chibaform");
            for (var p = 0; p < rPrototypes.length; p++) {
                var rControls = document.getElementsByClassName("value", rPrototypes[p].id);
                for (var c = 0; c < rControls.length; c++) {
                    var rControl = dojo.byId(rControls[c]);
                    if (rControl) {
                        // disable control and store for later state restoring
                        rControl.disabled = true;
                        this.disabledNodes.push(rControl);
                    }
                }
            }

            // disable all uploads that have a different id than the current
            // to avoid re-sending of multiple uploads.
            var uContainers = document.getElementsByClassName("upload", "chibaform");
            for (var u = 0; u < uContainers.length; u++) {
                var uControl = dojo.byId(uContainers[u].id + "-value");
                if (uControl && uControl.id != this.id && !uControl.disabled) {
                    // disable control and store for later state restoring
                    uControl.disabled = true;
                    this.disabledNodes.push(uControl);
                }
            }

            Effect.BlindDown(this.xformsId + "-progress");

            var path = this.inputNode.value;
            
            var filename = path.substring(path.lastIndexOf("/") + 1);
		
            //polling Chiba for update information and submit the form
            var sessionKey = dojo.byId("chibaSessionKey").value;
            //Flux.fetchProgress(updateUI, this.xformsId, filename, sessionKey);
            progressUpdate = setInterval("Flux.fetchProgress('" + this.xformsId + "','" + filename + "','" + sessionKey + "', updateUI)", 1000);
        
            document.forms["chibaform"].target = "UploadTarget";
            document.forms["chibaform"].submit();
         
            return true;
        }
    }
);