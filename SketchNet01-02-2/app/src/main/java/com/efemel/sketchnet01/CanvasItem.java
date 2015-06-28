//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

public class CanvasItem {
    private String id, title, description;

    public CanvasItem() {

    }

    public CanvasItem(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
