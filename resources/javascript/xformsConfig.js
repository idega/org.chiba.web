if (djConfig == null) {	
	var djConfig = {
		baseScriptUri:	'/idegaweb/bundles/org.chiba.web.bundle/resources/javascript/dojo-0.4.3/',
		debugAtAllCost:	false,
		isDebug:		false
	};
}

jQuery(window).load(function() {
	FluxInterfaceHelper.startUsingXForm();
});