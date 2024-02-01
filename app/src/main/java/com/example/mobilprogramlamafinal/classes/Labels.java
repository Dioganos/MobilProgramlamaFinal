package com.example.mobilprogramlamafinal.classes;

public class Labels {

    private String labelTitle;
    private String labelDescription;

    public Labels(String labelTitle, String labelDescription) {
        this.labelTitle = labelTitle;
        this.labelDescription = labelDescription;
    }

    public Labels() {
    }

    public String getLabelTitle() {
        return labelTitle;
    }

    public String getLabelDescription() {
        return labelDescription;
    }
}
