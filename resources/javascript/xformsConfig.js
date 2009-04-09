if(djConfig == null) {

var djConfig = {
	//if enabled js optimizer then dojo.baseScriptUri is null 
				baseScriptUri: "/idegaweb/bundles/org.chiba.web/resources/javascript/dojo-0.4.3/",
                debugAtAllCost:  false,
                isDebug: false
               };
}

jQuery(window).load(function() {
   			FluxInterfaceHelper.startUsingXForm();
});

     		