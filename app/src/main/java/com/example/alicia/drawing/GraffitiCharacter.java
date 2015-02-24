package com.example.alicia.drawing;

import android.graphics.PointF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Alicia on 2/9/15.
 */
public class GraffitiCharacter {

    private double height;
    private double width;
    private ArrayList<PointF> points;
    private PointF topLeft;


    public GraffitiCharacter(){
        points = new ArrayList<>();
    }

    public GraffitiCharacter(MotionEvent event){
        this();
        PointF start = new PointF(event.getX(),event.getY());
        points.add(start);
    }

    public void calculateTopLeft(){
        topLeft = new PointF(getMinX(),getMinY());
    }

    public float getMinX(){
        float min = 999999;
        for(PointF p: points){
            if(p.x<=min)
                min=p.x;
        }
        return min;
    }
    public float getMinY(){
        float min = 999999;
        for(PointF p: points){
            if(p.y<=min)
                min=p.y;
        }
        return min;
    }
    public float getMaxX(){
        float max = -1;
        for(PointF p: points){
            if(p.x>=max)
                max=p.x;
        }
        return max;
    }
    public float getMaxY(){
        float max = -1;
        for(PointF p: points){
            if(p.y>=max)
                max=p.y;
        }
        return max;
    }

    public void calculateHeight(){
        height = getMaxY()-getMinY();
    }

    public void calculateWidth(){
        width = getMaxX()-getMinX();
    }

    public ArrayList<PointF> getPoints(){
        return points;
    }

    public ArrayList<PointF> normalizeLocation(ArrayList<PointF> list){
        ArrayList<PointF> toReturn = new ArrayList<>();
        calculateTopLeft();
        for(PointF p: list){
            PointF newPoint = new PointF();
            newPoint.x = p.x - topLeft.x;
            newPoint.y = p.y - topLeft.y;
            toReturn.add(newPoint);
        }
        //topLeft = new PointF(0,0);
        return toReturn;
    }


    public ArrayList<PointF> normalizeScale(ArrayList<PointF>list){
        ArrayList<PointF> toReturn = new ArrayList<>();
        calculateHeight();
        calculateWidth();
        if(height>=width){
            //divide both by height
            for(PointF p: list){
                PointF newPoint = new PointF();
                newPoint.x = (float)(p.x/height);
                newPoint.y = (float)(p.y/height);
                toReturn.add(newPoint);
            }
        }
        else if(width>height){
            //divide both by width
            for(PointF p: list){
                PointF newPoint = new PointF();
                newPoint.x = (float)(p.x/width);
                newPoint.y = (float)(p.y/width);
                toReturn.add(newPoint);
            }

        }
        return toReturn;
    }


    private ArrayList<PointF> removeDuplicates(ArrayList<PointF> list){
        ArrayList<PointF>toReturn = new ArrayList<>();
        for(PointF currPt: list){
            if(!toReturn.contains(currPt))
                toReturn.add(currPt);
        }
        return toReturn;
    }


    public ArrayList<PointF> thin(int numPoints, ArrayList<PointF> list){
        ArrayList<PointF> thinnedPoints = new ArrayList<>();
        for(int i=0; i<numPoints; i++){
            int indexToAdd = list.size()/numPoints;
            thinnedPoints.add(list.get(indexToAdd));
        }
        thinnedPoints.add(list.get(list.size()-1));

        list = new ArrayList<>();
        for(int i=0; i<thinnedPoints.size(); i++){
            list.add(thinnedPoints.get(i));
        }

        return list;

    }

    public void addPoint(float x, float y){
        PointF toAdd = new PointF((int)x,(int)y);
        points.add(toAdd);
    }
    public void setPoints(ArrayList<PointF>newList){
        points = new ArrayList<>();
        for(int i=0; i<newList.size(); i++){
            points.add(newList.get(i));
        }
    }

    public void cleanUp(){
        setPoints(cleanedUpList());
    }
    public ArrayList<PointF> cleanedUpList(){
        ArrayList<PointF>cleaned = new ArrayList<>();
        cleaned = removeDuplicates(points);
        cleaned = normalizeLocation(cleaned);
        cleaned = normalizeScale(cleaned);
        cleaned = thin(30,cleaned);

        return cleaned;
    }
    public void endChar(float x, float y){
        PointF end = new PointF((int)x,(int)y);
        points.add(end);
    }

    public String toString(){
        String toReturn = "";
        for(int i=0; i<points.size(); i++){
            toReturn += points.get(i).x+" "+points.get(i).y+"\n";
        }
        return toReturn;
    }
    public static GraffitiCharacter fromString(String input){
        GraffitiCharacter toReturn = new GraffitiCharacter();
        Scanner sc = new Scanner(input);
        while(sc.hasNext())
            toReturn.parseLine(sc.nextLine());

        return toReturn;
    }

    private void parseLine(String line){
        Scanner sc = new Scanner(line);
        float x = (float)sc.nextDouble();
        float y = (float)sc.nextDouble();
        points.add(new PointF(x,y));
    }


}
