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
                    dojo.addOnLoad(initXForms);