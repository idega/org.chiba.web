package com.idega.chiba.event;

import org.chiba.web.flux.IdegaFluxAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class SubmissionEventHandler implements ApplicationListener {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SubmissionEvent) {
			Object source = event.getSource();
			if (source instanceof IdegaFluxAdapter) {
				IdegaFluxAdapter adapter = (IdegaFluxAdapter) source;
				SubmissionEvent submission = (SubmissionEvent) event;
				adapter.addSubmissionEvetLog(submission.getEvent());
			}
		}
	}
	
}