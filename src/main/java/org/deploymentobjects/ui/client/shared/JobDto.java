package org.deploymentobjects.ui.client.shared;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.enterprise.client.cdi.api.Conversational;


@Portable
@Conversational
public class JobDto {
    private int id;
    private String jobName;

    public JobDto() {
    }

    public JobDto(String message) {
        this.jobName = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return jobName;
    }

    public void setMessage(String message) {
        this.jobName = message;
    }
}