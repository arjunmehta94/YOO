//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import java.util.LinkedList;

public class Stroke {
    public LinkedList<Coordinate> coordinates;
    public int strokeColor;
    private String userID;

    public Stroke(String userID) {
        this.userID = userID;
        coordinates = new LinkedList<>();
    }

    public void setColor(int color) {
        strokeColor = color;
    }

    public void addCoordinate(float x, float y) {
        coordinates.add(new Coordinate(x,y));
    }

    @Override
    public String toString() {
        String temp = "";
        for(Coordinate c : coordinates) {
            temp += "X: " + c.X + ", Y: " + c.Y;
        }
        return temp;
    }

    public class Coordinate {
        public float X, Y;
        public Coordinate(float x, float y) {
            X = x;
            Y = y;
        }
    }
}



