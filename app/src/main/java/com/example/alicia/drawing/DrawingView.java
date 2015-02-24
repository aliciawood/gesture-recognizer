package com.example.alicia.drawing;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Alicia on 1/14/15.
 */
public class DrawingView extends View {


    private Path drawPath;                          //drawing path
    private Paint drawPaint, canvasPaint;           //drawing and canvas paint
    private int paintColor = Color.MAGENTA;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private ArrayList<GraffitiCharacter> chars;
    private GraffitiCharacter currChar;
    private TrainAndTest trainAndTest;
    private char characterReturned;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();

    }
    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        chars = new ArrayList<>();
        currChar = null;

        trainAndTest = new TrainAndTest();
        trainAndTest.train();

        characterReturned = '\0';
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);     //draw the view
        canvas.drawPath(drawPath, drawPaint);
    }

    public char getCharacterReturned(){
        ArrayList<PointF> newList = currChar.cleanedUpList();
        GraffitiCharacter temp = new GraffitiCharacter();
        temp.setPoints(newList);

        characterReturned = trainAndTest.test(temp);
        return characterReturned;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                currChar = new GraffitiCharacter(event);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                currChar.addPoint(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                currChar.endChar(touchX, touchY);
                chars.add(currChar);
//                startNew();                   //uncomment this line if you want to have the character erase after you draw one

                break;
            default:
                return false;
        }
        invalidate();           //causes the onDraw method to execute
        return true;
    }

    public String getSerializedCharacters(){
        String toReturn = "";
        for(int i=0; i<chars.size(); i++){
            toReturn += chars.get(i).toString();
            toReturn += "-\n";
        }
        return toReturn;
    }
    public void clearOldChars(){
        chars = new ArrayList<>();
    }
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    public void loadFile(File fileToLoad) throws FileNotFoundException {
        chars = new ArrayList<>();
        Scanner sc = new Scanner(fileToLoad);
        sc.useDelimiter("-\n");
        while(sc.hasNext()){
            String nextChar = sc.next();
            GraffitiCharacter currChar = GraffitiCharacter.fromString(nextChar);
            chars.add(currChar);
        }
    }
    public void redraw(){
        startNew();
        for(int i=0; i<chars.size(); i++){
            GraffitiCharacter curr = chars.get(i);
            ArrayList<PointF> points = curr.getPoints();
            for(int j=0; j<points.size(); j++){
                PointF p = points.get(j);
                if(j==0)
                    drawPath.moveTo(p.x, p.y);
                else if(j==(points.size()-1)){
                    drawCanvas.drawPath(drawPath, drawPaint);
                    drawPath.reset();
                }
                else
                    drawPath.lineTo(p.x,p.y);
            }
        }

    }


}
