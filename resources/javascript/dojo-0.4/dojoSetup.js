LazyLoader.load('/idegaweb/bundles/org.chiba.web.bundle/resources/javascript/dojo-0.4/dojo.js', function() {
	dojo.require("dojo.event.*");

    var pulseInterval;
    var calendarInstance = false;
    var calendarActiveInstance = null;
    var clicked = false;
    var closedByOnIconClick = false;
    
    this.onclick = function(evt) {
		if(closedByOnIconClick == true) {
        	closedByOnIconClick = false;
        } else {
        	if(calendarInstance==true) {
            	if(clicked == true) {
                	calendarInstance=false;
                    clicked = false;
                    calendarActiveInstance.hideContainer();
                } else {
                	clicked = true;
                }
            }
        }
    }
    
    initXForms();
});