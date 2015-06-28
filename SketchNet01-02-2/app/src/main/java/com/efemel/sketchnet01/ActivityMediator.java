package com.efemel.sketchnet01;

import android.app.Activity;
import android.util.Log;

/**
 * Created by anurag on 4/6/15.
 */
public class ActivityMediator {
    private static ActivityMediator instance;

    private static String currentActivityName;

    private static MainActivity mainActivity;
    private static JoinCanvasActivity joinCanvasActivity;
    private static CanvasActivity canvasActivity;
    private static CanvasInfoActivity canvasInfoActivity;


    public static ActivityMediator getInstance() {
        if(instance == null) {
            instance = new ActivityMediator();
        }
        return instance;
    }

    public void setCurrentActivity(String a) {
        currentActivityName = a;
    }

    public void setMainActivity(MainActivity ma) {
        mainActivity = ma;
    }

    public void setJoinCanvasActivity(JoinCanvasActivity jca) {
        joinCanvasActivity = jca;
    }

    public void setCanvasActivity(CanvasActivity ca) {
        canvasActivity = ca;
    }

    public void setCanvasInfoActivity(CanvasInfoActivity cia) {
        canvasInfoActivity = cia;
    }

    public void updateMainActivityList() {
        if(currentActivityName.equals("MainActivity")) {
            mainActivity.checkDBCanvas();
        }
    }

    public void checkJoinCanvas(String result) {
        if(currentActivityName.equals("JoinCanvasActivity")) {
            if(result.equals("success")) {
                joinCanvasActivity.joinSuccess();
            } else {
                joinCanvasActivity.joinFail();
            }
        }
    }

    public void drawNewStrokeOnCanvasView(Stroke s) {
        Log.e("inside: ", "drawNewStrokeOnCanvasView");
        if(currentActivityName.equals("CanvasActivity")) {
            canvasActivity.canvasView.drawNewStroke(s);
        }
    }

    public void updateParticipantList() {
        Log.e("inside: ", "updateParticipantList");
        if(currentActivityName.equals("CanvasInfoActivity")) {
            canvasInfoActivity.updateInfo();
        }
    }
}