package org.deploymentobjects.ui.server;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.deploymentobjects.core.domain.shared.DomainEvent;
import org.deploymentobjects.core.domain.shared.EventHistory;
import org.deploymentobjects.core.domain.shared.EventStore;
import org.deploymentobjects.core.infrastructure.persistence.Composite;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.EventWatcher;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.JobsWatcher;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperEventStore;
import org.deploymentobjects.core.infrastructure.persistence.zookeeper.ZookeeperPersistence;
import org.deploymentobjects.ui.client.shared.EventDto;
import org.deploymentobjects.ui.client.shared.Initialize;
import org.deploymentobjects.ui.client.shared.JobDto;

//import org.deploymentobjects.
/**
 * A very simple CDI based service.
 */
@ApplicationScoped
public class SimpleCDIService implements EventWatcher.EventAppears, JobsWatcher.JobAppears {
	private EventStore eventStore;
	private ZookeeperPersistence persistence;
	private EventWatcher eventWatcher;
	private JobsWatcher jobWatcher;
	
	public void handleMessage(@Observes Initialize event) {
		try {
			persistence = new ZookeeperPersistence("ino:2181");
			eventStore = new ZookeeperEventStore(persistence);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		jobWatcher = new JobsWatcher(this, persistence);
		System.out.println("EventBridge looping previous jobs" + jobWatcher);
		for(String job : jobWatcher.getPreviousJobs()){
			System.out.println("EventBridge Previous Job:" + job);
			jobEvent.fire(new JobDto(job));
		}
	}

	@Inject
	private Event<EventDto> responseEvent;

	@Inject
	private Event<JobDto> jobEvent;

	@Inject
	private Event<Initialize> initEvent;


	public void handleMessage(@Observes JobDto event) {
		
		System.out.println("Received HelloMessage from Client: "
				+ event.getMessage());
		String jobName = event.getMessage();
		EventHistory history = eventStore.lookup(jobName);
		//Logger.getLogger(this.getClass().getSimpleName()).severe("history: " + history);
		for(DomainEvent<?> domainEvent : history.events){
			responseEvent.fire(new EventDto(domainEvent.toString()));
		}
		
		eventWatcher = new EventWatcher(this, persistence, jobName);
		
		

		// Note that because Response is declared @Conversational, this message
		// only goes to the client who sent the HelloEvent.
	}
	


	@Override
	public void eventAppears(Composite appeared) {
		System.out.println("EventBridge Event Appeared:" + appeared);
		responseEvent.fire(new EventDto(appeared.getKey() + "-" + appeared.getValue()));
		
	}

	@Override
	public void jobAppears(Composite appeared) {
		System.out.println("EventBridge Job Composite Appeared:" + appeared);
		String jobName = appeared.getKey().replace("jobs/", "");
		System.out.println("EventBridge JobName:" + jobName);
		jobEvent.fire(new JobDto(jobName));
	}
}
