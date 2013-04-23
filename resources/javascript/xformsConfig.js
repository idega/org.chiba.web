var XFormsConfig = {};
var XFormSessionHelper = {};

XFormsConfig.maxStringValueLength = 255;

if (djConfig == null) {	
	var djConfig = {
		baseScriptUri:	'/idegaweb/bundles/org.chiba.web.bundle/resources/javascript/dojo-0.4.4/',
		debugAtAllCost:	false,
		isDebug:		false
	};
}

XFormsConfig.setConfiguration = function(config) {
	XFormsConfig.baseScriptUri = config.baseScriptUri;
	XFormsConfig.locale = config.locale;
	XFormsConfig.maxStringValueLength = config.maxStringValueLength;
	XFormsConfig.displayFullForm = config.displayFullForm;
	
	djConfig.baseScriptUri = config.baseScriptUri;
	djConfig.locale = config.locale;
	dojo.locale = config.locale;
}

jQuery(window).load(function() {
	FluxInterfaceHelper.startUsingXForm();
	
	if (XFormsConfig.displayFullForm == 'true') {
		jQuery('.fbc_button_area').each(function() {
			jQuery(this).hide();
		});
		
		jQuery('.case').each(function() {
			jQuery(this).show();
		});
	}
});

XFormSessionHelper.invalidateForm = function() {
	LazyLoader.loadMultiple(['/dwr/engine.js', '/dwr/interface/BPMApplicationXFormHandler.js'], function() {
		BPMApplicationXFormHandler.isRequiredToBeLoggedIn(window.location.href, {
			callback: function(result) {
				if (!result)
					return true;
				
				XFormSessionHelper.navigateFromXForm();
				return false;
			},
			errorHandler: handleExceptions
		});
	}, null);
}

XFormSessionHelper.navigateFromXForm = function() {
	showLoadingMessage(Localization.CLOSING);
	humanMsg.displayMsg(Localization.USER_MUST_BE_LOGGED_IN, {
		timeout: 3500,
		callback: function() {
			window.location.pathname = '/pages/';
		}
	});
}