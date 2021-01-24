package com.webApplication.Search_Engine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class implements the vector handler
 */
public class VectorHandler {
    //This method returns the addition of multiple vectors
    public static ArrayList<Double> addVecs(ArrayList<ArrayList<Double>> vecsToBeAdded){
        ArrayList<Double> total = new ArrayList<>();

           if(vecsToBeAdded.size() == 0){

               vecsToBeAdded.add(new ArrayList<>(QueryProcessor.vectorDimensions));

           }
           int numOfVecs = vecsToBeAdded.size();

            int vecDimension = vecsToBeAdded.get(0).size();

            for(int i = 0; i < vecDimension; i++) {
                double sumOfCoors = 0;
                for (int j = 0; j < numOfVecs; j++) {

                    sumOfCoors = sumOfCoors + vecsToBeAdded.get(j).get(i);
                }
                total.add(sumOfCoors);

            }


        return total;
    }

    //This method returns the multiplication of a vector and a number
    public static ArrayList<Double> vecMult(ArrayList<Double> vector, double number){
        for(int i = 0; i < vector.size(); i++){
            vector.set(i, number*vector.get(i));
        }
        return vector;
    }
    //This method returns the division of a vector and a number
    public static ArrayList<Double> vecDiv(ArrayList<Double> vector, double number){

            for(int i = 0; i < vector.size(); i++){
                vector.set(i, vector.get(i)/number);
            }

        return vector;
    }

    //This function returns the addition/subtraction of two vectors
    public static ArrayList<Double> vecCalculator(ArrayList<Double> vec1, ArrayList<Double> vec2, boolean addition){
        ArrayList<Double> result= new ArrayList<>();
        if(vec1.size() == 0){
            for(int i = 0; i < QueryProcessor.vectorDimensions; i++){
                vec1.add(0.0);

            }
        }

        if(vec2.size() == 0){
            for(int i = 0; i < QueryProcessor.vectorDimensions; i++){
                vec2.add(0.0);

            }

        }
        if(addition){
            for(int i = 0; i < vec1.size(); i++){
                result.add(vec1.get(i) + vec2.get(i));
            }
        }else{
            for(int i = 0; i < vec1.size(); i++){
                result.add(vec1.get(i) - vec2.get(i));
            }
        }
        return result;
    }
    //This method rounds up a decimal to two decimal points
    public static ArrayList<Double> roundUpToTwoDecimals(ArrayList<Double> vector){
        for(int i = 0; i < vector.size(); i++){
            double roundOf = Math.round(vector.get(i) * 100.0) / 100.0;
            vector.set(i, roundOf);
        }

        return vector;
    }
}
