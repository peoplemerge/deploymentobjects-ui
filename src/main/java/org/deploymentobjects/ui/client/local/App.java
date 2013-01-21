package org.deploymentobjects.ui.client.local;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.deploymentobjects.ui.client.shared.EventDto;
import org.deploymentobjects.ui.client.shared.Initialize;
import org.deploymentobjects.ui.client.shared.JobDto;
import org.jboss.errai.ioc.client.api.EntryPoint;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main application entry point.
 */
@EntryPoint
public class App {

	@Inject
	private Event<JobDto> messageEvent;
	@Inject
	private Event<Initialize> initializeEvent;
	
	private final ListBox responseLabel = new ListBox();
	// private final Button button = new Button("Click!");
	// private final TextBox message = new TextBox();
	private final ListBox jobSelect = new ListBox();

	private String initialJob = "select job";
	private List<String> jobs = new ArrayList<String>();

	@PostConstruct
	public void buildUI() {
		jobSelect.setSelectedIndex(1);

		jobSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String jobName = jobSelect.getValue(jobSelect
						.getSelectedIndex());
				changeJob(jobName);
			}
		});

		VerticalPanel verticalPanel = new VerticalPanel();
		// horizontalPanel.add(message);
		// horizontalPanel.add(button);
		verticalPanel.add(jobSelect);
		verticalPanel.add(responseLabel);
		responseLabel.setVisibleItemCount(50);
		responseLabel.setWidth("1000px");

		RootPanel.get().add(verticalPanel);

		changeJob(initialJob);
		System.out.println("UI Constructed!");
	}

	/**
	 * Fires a CDI HelloMessage with the current contents of the message
	 * textbox.
	 */
	void changeJob(String jobName) {
		if (jobName.equals(initialJob)) {
			Initialize event = new Initialize();
			initializeEvent.fire(event);

		}else {
			JobDto event = new JobDto(jobName);
			messageEvent.fire(event);
		}
		responseLabel.clear();
		/*
		 * Iterator<Widget> it = responseLabel.iterator(); while(it.hasNext()){
		 * Widget w = it.next(); //responseLabel.remove(w); if(w instanceof
		 * Label){ ((Label) w).setText(((Label) w).getText() + " --- deleted");
		 * } }
		 */
	}

	public void response(@Observes EventDto event) {
		System.out.println("Got a Response!");
		responseLabel.addItem("HelloMessage from Server: "
				+ event.getMessage().toUpperCase());
	}

	public void response(@Observes JobDto event) {
		System.out.println("Got a JobDto!");

		String jobName = event.getMessage();
		if (!jobs.contains(jobName)) {
			jobSelect.addItem(jobName, jobName);
			jobs.add(jobName);
			// If you want to switch to view new jobs as they appear
			jobSelect.setSelectedIndex(jobs.indexOf(jobName));
			changeJob(jobName);
		} else {
			System.out.println("skipping add");
		}
	}

	/**
	 * Returns the response label. Exposed for testing.
	 */
	Label getResponseLabel() {
		return null;
	}

}