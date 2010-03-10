dojo.provide("chiba.widget.Upload");

dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.*");

dojo.widget.defineWidget("chiba.widget.Upload", dojo.widget.HtmlWidget,	{
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
        changedFetchingProgressInterval: false,
        uploadFinished: false,
        fillInTemplate: function() {
            this.xformsId = this.id.substring(0, this.id.length - 6);

            this.progress.id = this.xformsId + "-progress";
            this.progressBackground.id = this.xformsId + "-progress-bg";

            var xformsControl = dojo.byId(this.xformsId);
            _replaceClass(xformsControl,"upload","upload " + this.css);

            if (this.xfreadonly == "true") {
                this.inputNode.disabled = true;
            }
        },
        onChange: function() {
            if (this.xfreadonly == "true") {
                this.inputNode.disabled = true;
            } else {
            	this._submitFile(this.inputNode);
            }
        },
        updateProgress: function (value) {
        	var progressBarContainerId = this.xformsId + '-progress';
        	var progressBarId = progressBarContainerId + '-bg';
        	
            if (value != 0) {
            	document.getElementById(progressBarId).style.width = value + '%';
            }

            if (value == 100 || value < 0) {
                // stop polling
                this.uploadFinished = true;
                clearInterval(progressUpdate);

                // reset disabled controls
                for (var i = 0, j = this.disabledNodes.length; i < j; i++) {
                    this.disabledNodes.pop().disabled = false;
                }

                // reset progress bar
                var resetTimeOutId = setTimeout(function() {
                	window.clearTimeout(resetTimeOutId);
                	document.getElementById(progressBarId).style.width = 0;
                	jQuery('#' + progressBarContainerId).hide('normal');
                	closeAllLoadingMessages();
                }, 500);
            }
        },
        _chooseFile: function(useAlert) {
        	if (!this.uploadFinished) {
        		return false;
        	}
        	
        	var fileInputId = this.inputNode.id;
        	var functionToFocus = function() {
        		jQuery('#' + fileInputId).trigger('focus');
        	}
        	
        	if (useAlert) {
        		alert(Localization.INVALID_FILE_TO_UPLOAD);
        		functionToFocus();
        	} else {
	        	humanMsg.displayMsg(Localization.INVALID_FILE_TO_UPLOAD, {
					timeout: 3000,
					callback: functionToFocus
				});
        	}
        },
        _submitFile: function() {
        	var path = this.inputNode.value;
            if (path == null || path == '') {
				this._chooseFile();
            	return false;
            }
            var filename = path.substring(path.lastIndexOf("/") + 1);
            if (filename == null || filename == '') {
            	this._chooseFile();
            	return false;
            }
        	
        	if (!this.uploadFinished) {
        		return false;
        	}
        	this.uploadFinished = false;
        	
            // disable all controls contained in repeat prototypes to avoid inconsistent updates.
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

            // disable all uploads that have a different id than the current to avoid re-sending of multiple uploads.
            var uContainers = document.getElementsByClassName("upload", "chibaform");
            for (var u = 0; u < uContainers.length; u++) {
                var uControl = dojo.byId(uContainers[u].id + "-value");
                if (uControl && uControl.id != this.id && !uControl.disabled) {
                    // disable control and store for later state restoring
                    uControl.disabled = true;
                    this.disabledNodes.push(uControl);
                }
            }
            
            //	disable all triggers in xform when uploading file is in process
            var trigContainers = document.getElementsByClassName("trigger", "chibaform");
            for (var trig = 0; trig < trigContainers.length; trig++) {
                var trigControl = dojo.byId(trigContainers[trig].id + "-value");
                if (trigControl && trigControl.id != this.id && !trigControl.disabled) {
                    // disable control and store for later state restoring
                    trigControl.disabled = true;
                    this.disabledNodes.push(trigControl);
                }
            }
            
			var progressBar = document.getElementById(this.xformsId + '-progress');
			if (progressBar != null) {
				jQuery(progressBar).show('normal');
			}

            //	Polling Chiba for update information and submit the form
            var sessionKey = dojo.byId("chibaSessionKey").value;
            var xfomsId = this.xformsId;
            var widget = this;
            progressUpdate = setInterval(function() {widget._fetchUploadProgress(xfomsId, filename, sessionKey);}, 2000);

			showLoadingMessage(Localization.STANDARD_LAYER_MSG);
			
            document.forms["chibaform"].target = "UploadTarget";
            document.forms["chibaform"].submit();
         
            return true;
        },
		_fetchUploadProgress: function(xfomsId, filename, sessionKey) {
			var uploadWidget = this;
			Flux.fetchProgress(xfomsId, filename, sessionKey, {
				callback: function(data) {
					updateUI(data);
				},
				errorHandler: function(msg, exc) {
					uploadWidget.uploadFinished = true;
					clearInterval(progressUpdate);
					handleExceptions(msg, exc);
				}
			});
			if (errorUploading) {
				this.uploadFinished = true;
			}
			if (!this.uploadFinished && !this.changedFetchingProgressInterval) {
				clearInterval(progressUpdate);
				var widget = this;
				progressUpdate = setInterval(function() {widget._fetchUploadProgress(xfomsId, filename, sessionKey);}, 1000);
				this.changedFetchingProgressInterval = true;
			}
		}
    }
);