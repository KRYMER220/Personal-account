package com.example.rjdapp.Model;

public class Appeals {
    private String id, titleAppeals, textAppeals;

    public Appeals() {

    }

    public Appeals(String id, String titleAppeals, String textAppeals) {
        this.id = id;
        this.titleAppeals = titleAppeals;
        this.textAppeals = textAppeals;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitleAppeals() {
        return titleAppeals;
    }

    public void setTitleAppeals(String titleAppeals) {
        this.titleAppeals = titleAppeals;
    }

    public String getTextAppeals() {
        return textAppeals;
    }

    public void setTextAppeals(String textAppeals) {
        this.textAppeals = textAppeals;
    }
}
