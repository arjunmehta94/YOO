//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

public class Canvas {
    String CanvasName;
    String CanvasID;
    String Owner;

    public Canvas() {

    }

    public Canvas(String name, String owner) {
        CanvasName = name;
        Owner = owner;
    }

    public Canvas(String name, String id, String owner) {
        CanvasName = name;
        Owner = owner;
        CanvasID = id;
    }

    public String getName() {
        return CanvasName;
    }

    public void setName(String name) {
        CanvasName = name;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public void setID(String id) {
        CanvasID = id;
    }

    public String getID() {
        return CanvasID;
    }
}
