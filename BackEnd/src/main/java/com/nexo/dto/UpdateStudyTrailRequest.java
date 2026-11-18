package com.nexo.dto;

public class UpdateStudyTrailRequest {

    private String title;
    private String description;

    // Constructor
    public UpdateStudyTrailRequest(){}


    public String getDescription() { return description; }
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
