var XFormsConfig = {};

if (djConfig == null) {	
	var djConfig = {
		baseScriptUri:	'/idegaweb/bundles/org.chiba.web.bundle/resources/javascript/dojo-0.4.3/',
		debugAtAllCost:	false,
		isDebug:		false
	};
}

XFormsConfig.setConfiguration = function(config) {
	XFormsConfig.baseScriptUri = config.baseScriptUri;
	XFormsConfig.locale = config.locale;
	
	djConfig.baseScriptUri = config.baseScriptUri;
	djConfig.locale = config.locale;
	dojo.locale = config.locale;
}

jQuery(window).load(function() {
	FluxInterfaceHelper.startUsingXForm();
});