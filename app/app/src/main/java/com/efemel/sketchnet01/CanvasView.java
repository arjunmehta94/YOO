//# COMP 4521    #  Anurag Sahoo        STUDENT ID 20068498         EMAIL ADDRESS asahoo@ust.hk
//# COMP 4521    #  Farhad Bin Siddique        STUDENT ID 20088450         EMAIL ADDRESS fsiddique@connect.ust.hk
package com.efemel.sketchnet01;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.LinkedList;


public class CanvasView extends View {
    private static int NONE = 0;
    private static int DRAW = 1;
    private static int ZOOMnDRAG = 2;

    private static int mode;

    private float DstartX = 0f;
    private float DstartY = 0f;

    private float DtranslateX = 0f;
    private float DtranslateY = 0f;

    private float DpreviousTranslateX = 0f;
    private float DpreviousTranslateY = 0f;

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 20f;

    private static float scaleFactor = 1.f;
    private ScaleGestureDetector scaleDetector;

    private Path drawPath;
    private Stroke thisStroke;
    private Paint drawPaint, canvasPaint;
    private int paintColor = Color.BLACK;
    private Canvas drawCanvas;//android.graphics.Canvas
    private Bitmap canvasBitmap;
    private LinkedList<Stroke> strokes;
    //private CanvasViewConnect export;
    ConnectionHandler connectionHandler;
    //need to add a bluetooth

    private String userID;
    private String canvasID;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDrawing(context);

        //export = new CanvasViewConnect(strokes, context, this);
        connectionHandler = ConnectionHandler.getInstance();
    }


    public void setupDrawing(Context context) {
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        strokes = new LinkedList<>();

        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();

                scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
                invalidate();
                //Log.e("Coming", "" + scaleFactor);

                return true;
            }
        });
    }

    public void redrawStrokes() {//implement vector graphics here

        drawCanvas.drawColor(Color.WHITE);

        for(int i=0; i<strokes.size(); i++) {

            drawPath.moveTo((strokes.get(i).coordinates.get(0).X), (strokes.get(i).coordinates.get(0).Y));

            for(int j=1; j<strokes.get(i).coordinates.size(); j++) {
                drawPath.lineTo((strokes.get(i).coordinates.get(j).X), (strokes.get(i).coordinates.get(j).Y));
                Log.e("" + strokes.get(i).coordinates.get(j).X, "" +strokes.get(i).coordinates.get(j).Y);
            }
            drawPaint.setStrokeWidth(20/scaleFactor);
            Paint dp = new Paint();

            dp.setColor(strokes.get(i).strokeColor);
            dp.setStrokeWidth(20/scaleFactor);

            dp.setAntiAlias(true);
            dp.setStyle(Paint.Style.STROKE);
            dp.setStrokeJoin(Paint.Join.ROUND);
            dp.setStrokeCap(Paint.Cap.ROUND);



            drawCanvas.drawPath(drawPath, dp);
            drawPath.reset();

            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("Drawing canvas", "CanvasView!");
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);
        canvas.translate(DtranslateX / scaleFactor, DtranslateY / scaleFactor);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAW;
                drawPath.moveTo((touchX - DpreviousTranslateX)/scaleFactor, (touchY - DpreviousTranslateY)/scaleFactor);
                thisStroke = new Stroke(this.userID);
                thisStroke.setColor(paintColor);
                thisStroke.addCoordinate((touchX - DpreviousTranslateX)/scaleFactor,
                        (touchY - DpreviousTranslateY)/scaleFactor);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOMnDRAG;
                scaleDetector.onTouchEvent(event);

                DstartX = event.getX() - DpreviousTranslateX;
                DstartY = event.getY() - DpreviousTranslateY;

                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == DRAW) {
                    drawPath.lineTo((touchX - DpreviousTranslateX)/scaleFactor, (touchY - DpreviousTranslateY)/scaleFactor);
                    thisStroke.addCoordinate((touchX - DpreviousTranslateX)/scaleFactor,
                            (touchY - DpreviousTranslateY)/scaleFactor);
                } else if(mode == ZOOMnDRAG) {
                    //add code to translate.
                    scaleDetector.onTouchEvent(event);

                    DtranslateX = event.getX() - DstartX;
                    DtranslateY = event.getY() - DstartY;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mode == ZOOMnDRAG) {
                    DpreviousTranslateX = DtranslateX;
                    DpreviousTranslateY = DtranslateY;

                    redrawStrokes();

                } else if(mode == DRAW) {
                    drawCanvas.drawPath(drawPath, drawPaint);
                    strokes.add(thisStroke);
                    //export.sendStroke(thisStroke);
                    connectionHandler.executeCommand("new-stroke", thisStroke);
                    drawPath.reset();
                }
                mode = NONE;

                break;
            default:
                break;
        }

        invalidate();
        return true;
    }

    public void drawNewStroke(Stroke s) {
        Log.e("Debug", "Starting to draw");
        strokes.add(s);
        drawPath.moveTo(s.coordinates.get(0).X,s.coordinates.get(0).Y);

        for(int i=1; i<s.coordinates.size(); i++) {
            drawPath.lineTo(s.coordinates.get(i).X, s.coordinates.get(i).Y);
        }
        Paint dp = new Paint();

        dp.setColor(s.strokeColor);

        dp.setAntiAlias(true);
        dp.setStyle(Paint.Style.STROKE);
        dp.setStrokeWidth(20);
        dp.setStrokeJoin(Paint.Join.ROUND);
        dp.setStrokeCap(Paint.Cap.ROUND);

        drawCanvas.drawPath(drawPath, dp);
        drawPath.reset();

        invalidate();
    }

    public float convertDpToPx(float dp) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public float convertPxToDp(float px) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public LinkedList getAllStrokes() {
        return this.strokes;
    }

    public void setAllStrokes(LinkedList strokes) {
        this.strokes = strokes;
//        Log.e("setAllStrokes", "this.strokes = " + this.strokes);
        if(this.strokes != null) {
            Log.e("before drawAllStrokes", "reaches here");
            drawAllStrokes(this.strokes);
        }
    }

    public void drawAllStrokes(LinkedList<Stroke> strokes) {
      for (Stroke stroke : strokes) {
          drawStroke(stroke);
      }
    }

    // a bit redundant with drawNewStroke method because of similarity
    public void drawStroke(Stroke s) {
        Log.e("Debug", "Starting to draw");
        drawPath.moveTo(convertDpToPx(s.coordinates.get(0).X), convertDpToPx(s.coordinates.get(0).Y));

        for (int i = 1; i < s.coordinates.size(); i++) {
            drawPath.lineTo(convertDpToPx(s.coordinates.get(i).X), convertDpToPx(s.coordinates.get(i).Y));
        }

        // drawCanvas is probably a null object here
        // possible cause: this method is being called before onSizeChanged method above
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();

        invalidate();
    }

    public void setCurrentUserId(String userID) {
        Log.e("userID set", userID);
        this.userID = userID;
    }

    public String getCurrentUserId() {
        Log.e("get userID", userID);
        return this.userID;
    }

    public void setCurrentCanvasId(String canvasID) {
        this.canvasID = canvasID;
    }

    public String getCurrentCanvasId() {
        Log.e("canvasid in canvasview", this.canvasID);
        return this.canvasID;
    }

    public void setStrokeColor(int color) {
        paintColor = color;
        drawPaint.setColor(color);
    }
}