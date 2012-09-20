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
        uploadFinished: true,
        progressBarContainerId: '',
        progressBarId: '',
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
            	if (this.inputNode.files == null) {
            		this._submitFile(this.inputNode);
            	} else {
            		//	Will check the size of file
            		var attemptingToUpload = 0;
            		for (var fileIndex = 0; fileIndex < this.inputNode.files.length; fileIndex++) {
            			attemptingToUpload += this.inputNode.files[fileIndex].size;
            		}
            		
            		var uploadWidget = this;
            		LazyLoader.load('/dwr/interface/WebUtil.js', function() {
            			WebUtil.getApplicationProperty('xform_upload_limit', {
            				callback: function(maxSize) {
            					if (maxSize == null || maxSize <= 0) {
            						uploadWidget._submitFile(uploadWidget.inputNode);
            						return true;
            					} else {
            						maxSize--;
            						maxSize++;
            					}
            					
            					if (attemptingToUpload < maxSize) {
            						uploadWidget._submitFile(uploadWidget.inputNode);
            						return true;
            					} else {
            						WebUtil.getLocalizedString('org.chiba.web', 'exceeded_max_upload_limit', 'Sorry, file can not be uploaded: it exceeds maximum allowed size (' + ((maxSize / 1024) / 1024) + ' MB)', {
            							callback: function(message) {
            								alert(message);
            							}
            						});
            						jQuery(uploadWidget.inputNode).attr('value', '');
            						FluxInterfaceHelper.UPLOAD_IN_PROGRESS = false;
            						return false;
            					}
            				}
            			});
            		});
            	}
            }
        },
        updateProgress: function (value) {
        	this.progressBarContainerId = this.xformsId + '-progress';
        	this.progressBarId = this.progressBarContainerId + '-bg';
        	
            if (value != 0 && (this.progressBarId != null && this.progressBarId != '')) {
            	var element = document.getElementById(this.progressBarId);
            	if (element != null) {
            		element.style.width = value + '%';
            	}
            }

            if (value == 100 || value < 0) {
                this._doActionsAfterUpload();
            }
        },
        _doActionsAfterUpload: function() {
        	FluxInterfaceHelper.UPLOAD_IN_PROGRESS = false;
        	
        	// stop polling
            this.uploadFinished = true;
            if (progressUpdate != null) {
            	clearInterval(progressUpdate);
            }

            // reset disabled controls
            for (var i = 0, j = this.disabledNodes.length; i < j; i++) {
                this.disabledNodes.pop().disabled = false;
            }

            // reset progress bar
            var uploadWidget = this;
            var resetTimeOutId = setTimeout(function() {
            	if (resetTimeOutId != null) {
            		window.clearTimeout(resetTimeOutId);
            	}
            	
            	if (uploadWidget.progressBarId != null && uploadWidget.progressBarId != '') {
	               	var element = document.getElementById(uploadWidget.progressBarId);
	               	if (element != null) {
	               		element.style.width = 0;
	               	}
            	}
            	if (uploadWidget.progressBarContainerId != null && uploadWidget.progressBarContainerId != '') {
               		jQuery('#' + uploadWidget.progressBarContainerId).hide('normal');
            	}
            	
               	closeAllLoadingMessages();
            }, 500);
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
        	
        	FluxInterfaceHelper.UPLOAD_IN_PROGRESS = true;
            // disable all controls contained in repeat prototypes to avoid inconsistent updates.
            var rPrototypes = document.getElementsByClassName("repeat-prototype", "chibaform");
            for (var p = 0; p < rPrototypes.length; p++) {
                var rControls = document.getElementsByClassName("value", rPrototypes[p].id);
                for (var c = 0; c < rControls.length; c++) {
                    var rControl = dojo.byId(rControls[c]);
                    if (rControl && this.id != rControl.id) {
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
					uploadWidget._doActionsAfterUpload();
					handleExceptions(msg, exc);
				}
			});
			if (!this.uploadFinished && !this.changedFetchingProgressInterval) {
				clearInterval(progressUpdate);
				var widget = this;
				progressUpdate = setInterval(function() {widget._fetchUploadProgress(xfomsId, filename, sessionKey);}, 1000);
				this.changedFetchingProgressInterval = true;
			}
		}
    }
);