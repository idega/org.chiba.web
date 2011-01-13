package com.idega.chiba.event;

import org.springframework.context.ApplicationEvent;

public class SubmissionEvent extends ApplicationEvent {

	private static final long serialVersionUID = -1092697001916343281L;

	private org.w3c.dom.events.Event event;
	
	public SubmissionEvent(Object source, org.w3c.dom.events.Event event) {
		super(source);
		
		this.event = event;
	}

	public org.w3c.dom.events.Event getEvent() {
		return event;
	}
}