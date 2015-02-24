package com.example.alicia.drawing;

import android.graphics.PointF;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created by Alicia on 2/16/15.
 */
public class TrainAndTest {
    public Map<Character, ArrayList<GraffitiCharacter>> charToGraffiti;
    public Map<Character,Double> minDistanceToCharacter;

    public TrainAndTest(){
        charToGraffiti = new TreeMap<>();
        minDistanceToCharacter = new TreeMap<>();
    }

    public void addTrainingExample(char currChar, GraffitiCharacter toAdd){
        ArrayList<GraffitiCharacter>listToAdd = new ArrayList<>();
        if(charToGraffiti.get(currChar)!=null){
            listToAdd = charToGraffiti.get(currChar);
        }
        listToAdd.add(toAdd);
        charToGraffiti.put(currChar,listToAdd);
    }

    private void addFileToTrainingData(char currChar, String fileName){
//        System.out.println("TRYING TO LOAD: "+fileName);
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/"+fileName;
        File currFile = new File(filePath);
        try {
            Scanner sc = new Scanner(currFile);
            sc.useDelimiter("-\n");
            while(sc.hasNext()){
                String line = sc.next();
                GraffitiCharacter currGraffitiChar = GraffitiCharacter.fromString(line);
                currGraffitiChar.cleanUp();

                addTrainingExample(currChar,currGraffitiChar);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void train(){
        //here's where we add all the training data
        addFileToTrainingData('a', "a.jpg");
        addFileToTrainingData('b',"b.jpg");
        addFileToTrainingData('b',"b2.jpg");
        addFileToTrainingData('c',"c1.jpg");
        addFileToTrainingData('c',"c.jpg");
        addFileToTrainingData('d',"d.jpg");
        addFileToTrainingData('d',"d2.jpg");
        addFileToTrainingData('e',"e.jpg");
        addFileToTrainingData('e',"e2.jpg");
        addFileToTrainingData('e',"e3.jpg");
        addFileToTrainingData('f',"f.jpg");
        addFileToTrainingData('f',"f2.jpg");
        addFileToTrainingData('g',"g.jpg");
        addFileToTrainingData('g',"g2.jpg");
        addFileToTrainingData('g',"g3.jpg");
        addFileToTrainingData('h',"h.jpg");

    }

    private double distanceBetweenTwoPts(PointF one, PointF two){
        return Math.sqrt(Math.pow((one.x-two.x),2)+Math.pow((one.y-two.y),2));
    }


    public char test(GraffitiCharacter testing){
        ArrayList<PointF> points = testing.getPoints();
        Iterator<Character> iterator = charToGraffiti.keySet().iterator();

        while(iterator.hasNext()){
            char currChar = iterator.next();
//            System.out.println("CURR CHAR: "+currChar);
            ArrayList<GraffitiCharacter> characters = charToGraffiti.get(currChar);
            for(GraffitiCharacter currGraffitiChar: characters){
                ArrayList<PointF> pointsForCurrChar = currGraffitiChar.getPoints();
                double totalDistance = 0;

                for(int i=0; i<points.size(); i++){
                    PointF p = points.get(i);
                    PointF curr = pointsForCurrChar.get(i);
                    totalDistance += distanceBetweenTwoPts(p, curr);
                }

//                System.out.println("TOTAL DISTANCE: "+totalDistance);
                if (minDistanceToCharacter.get(currChar) == null)
                    minDistanceToCharacter.put(currChar, totalDistance);
                else {
                    double oldMin = minDistanceToCharacter.get(currChar);
                    if (totalDistance < oldMin)
                        minDistanceToCharacter.put(currChar, totalDistance);
                }

            }
        }

        double minDistance = 99999;
        char solution = '\0';
        Iterator<Character> iterator1 = minDistanceToCharacter.keySet().iterator();
        while(iterator1.hasNext()){
            char currChar = iterator1.next();
            double dist = minDistanceToCharacter.get(currChar);
            if(dist<minDistance) {
                minDistance = dist;
                solution = currChar;
            }
        }

//        System.out.println("MIN DISTANCE: "+minDistance);
//        System.out.println("SOLUTION: "+solution);

//        System.out.println("MAP: ");
//        printMap();
        minDistanceToCharacter = new TreeMap<>();
        return solution;
    }

//    public void printMap(){
//        Iterator<Character> iterator = minDistanceToCharacter.keySet().iterator();
//        while(iterator.hasNext()){
//            char curr = iterator.next();
//            double dist = minDistanceToCharacter.get(curr);
//            System.out.println(curr+":"+dist);
//
//        }
//    }



}
