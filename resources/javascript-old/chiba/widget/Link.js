/*
	Copyright 2001-2007 ChibaXForms GmbH
	All Rights Reserved.
*/
dojo.provide("chiba.widget.Link");

dojo.widget.defineWidget(
	// widget name and class
	"chiba.widget.Link",

	// superclass
	dojo.widget.HtmlWidget,

	// properties and methods
	{
		// settings
		widgetType: "Link",
		isContainer: true,
		templatePath: dojo.uri.dojoUri("../chiba/widget/templates/Link.html"),

        // parameters : This is the parameters of the Boolean widget
        id: "",
        href: "",

        postMixInProperties: function(){
        },

        fillInTemplate: function(args, frag) {
        	dojo.debug("Link Dojo widget : id=" + this.id + " href=" + this.href + " containerNode=" +this.containerNode)
        }

	}
);