package q2.program;

import java.io.*;
import java.util.*;

//Nearest neighbor classifier
public class DigitClassifier {
    /*************************************************************************/

    //Record class (inner class)
    private class Record {
        private int[] attributes;         //attributes of record
        private int className;               //class of record

        //Constructor of Record
        private Record(int[] attributes, int className) {
            this.attributes = attributes;    //set attributes 
            this.className = className;      //set class
        }
    }

    /*************************************************************************/

    private int numberRecords;               //number of training records   
    private int numberAttributes;            //number of attributes   
    private int numberClasses;               //number of classes
    private int numberNeighbors;             //number of nearest neighbors
    private ArrayList<Record> records;       //list of training records

    /*************************************************************************/

    //Constructor of ClassifyApplicants
    public DigitClassifier() {
        //initial data is empty           
        numberRecords = 0;
        numberAttributes = 0;
        numberClasses = 0;
        numberNeighbors = 0;
        records = null;
    }
    /*************************************************************************/

    //Method sets number of nearest neighbors
    public void setParameters(int numberNeighbors) {
        this.numberNeighbors = numberNeighbors;
    }

    /*************************************************************************/

    //Method reads records from test file, determines their classes, 
    //and writes classes to classified file
    public void classifyData(String testFile, String classifiedFile) throws IOException {
        Scanner inFile = new Scanner(new File(testFile));
        PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile));

        //read number of records
        int numberRecords = inFile.nextInt();

        //write number of records
        outFile.println(numberRecords);

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            //create attribute array
            int[] attributeArray = new int[numberAttributes];

            //read attribute values
            for (int j = 0; j < numberAttributes; j++)
                attributeArray[j] = inFile.nextInt();

            //find class of attributes
            int className = classify(attributeArray) - 1;

            //write class name
            outFile.println(className);
        }

        inFile.close();
        outFile.close();
    }

    /*************************************************************************/

    //Method determines the class of a set of attributes
    private int classify(int[] attributes) {
        double[] distance = new double[numberRecords];
        int[] id = new int[numberRecords];

        //find distances between attributes and all records
        for (int i = 0; i < numberRecords - 1; i++) {
            distance[i] = distance(attributes, records.get(i).attributes);
            id[i] = i;
        }

        //find nearest neighbors
        nearestNeighbor(distance, id);

        //find majority class of nearest neighbors
        int className = majority(id);

        //return class
        return className;
    }

    /*************************************************************************/

    //Method finds the nearest neighbors
    private void nearestNeighbor(double[] distance, int[] id) {
        //sort distances and choose nearest neighbors
        for (int i = 0; i < numberNeighbors; i++)
            for (int j = i; j < numberRecords; j++)
                if (distance[i] > distance[j]) {
                    double tempDistance = distance[i];
                    distance[i] = distance[j];
                    distance[j] = tempDistance;

                    int tempId = id[i];
                    id[i] = id[j];
                    id[j] = tempId;
                }
    }

    /*************************************************************************/

    //Method finds the majority class of nearest neighbors
    private int majority(int[] id) {
        double[] frequency = new double[numberClasses];

        //class frequencies are zero initially
        for (int i = 0; i < numberClasses; i++)
            frequency[i] = 0;

        //each neighbor contributes 1 to its class
        for (int i = 0; i < numberNeighbors; i++)
            frequency[records.get(id[i]).className] += 1;

        //find majority class
        int maxIndex = 0;
        for (int i = 0; i < numberClasses; i++)
            if (frequency[i] > frequency[maxIndex]) maxIndex = i;

        return maxIndex + 1;
    }


    private double distance(int[] u, int[] v) {
        int mismatch = 0;
        for (int i = 0; i < u.length; i++) {
            if (u[i] != v[i]) {
                mismatch++;
            }
        }
        return mismatch;
    }

    /*************************************************************************/

    //Method validates classifier using validation file and displays error rate
    public double validate(String validationFile) throws IOException {
        Scanner inFile = new Scanner(new File(validationFile));

        //read number of records
        int numberRecords = inFile.nextInt();
        inFile.nextLine();
        //initially zero errors
        int numberErrors = 0;

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            // Update training data to be all records except `i`.
            loadTrainingDataExcept(validationFile, i);

            int[] attributeArray = new int[numberAttributes];

            //read attributes
            for (int j = 0; j < numberAttributes; j++)
                attributeArray[j] = inFile.nextInt();

            //read actual class
            int actualClass = inFile.nextInt();

            //find class predicted by classifier
            int predictedClass = classify(attributeArray) - 1;

            //error if predicted and actual classes do not match
            if (predictedClass != actualClass) numberErrors += 1;
        }

        //find and print error rate
        double errorRate = 100.0 * numberErrors / numberRecords;

        System.out.println("validation error: " + errorRate + "%");

        inFile.close();
        return errorRate;
    }

    //loads the data except one and cycles through
    void loadTrainingDataExcept(String trainingFile, int index) throws FileNotFoundException {
        Scanner inFile = new Scanner(new File(trainingFile));

        //read number of records, attributes, classes
        numberRecords = inFile.nextInt();
        numberAttributes = inFile.nextInt();
        numberClasses = inFile.nextInt();

        //create empty list of records
        records = new ArrayList<Record>();

        //for each record
        for (int i = 0; i < numberRecords; i++) {
            if (i == index) {
                continue;
            }
            //create attribute array
            int[] attributeArray = new int[numberAttributes];

            //read attribute values
            for (int j = 0; j < numberAttributes; j++)
                attributeArray[j] = inFile.nextInt();

            //read class name
            int className = inFile.nextInt();

            //create record
            Record record = new Record(attributeArray, className);

            //add record to list of records
            records.add(record);
        }

        inFile.close();
    }
    void loadTrainingData(String trainingFile) throws FileNotFoundException {
        Scanner inFile = new Scanner(new File("training_data.txt"));
        numberRecords = inFile.nextInt();
        numberAttributes = inFile.nextInt();
        numberClasses = inFile.nextInt();

        int[] arrayBits;
        int className;
        records = new ArrayList<>();

        for (int i = 0; i < numberRecords; i++) {
            arrayBits = new int[numberAttributes];
            // Read the array
            for (int j = 0; j < numberAttributes; j++) {
                int bit = inFile.nextInt();
                arrayBits[j] = bit;
            }
            // Read the class
            className = inFile.nextInt();
            records.add(new Record(arrayBits, className));
        }
    }

    public static void main(String[] args) throws IOException {
        int kValue = 5;
        Scanner userInput = new Scanner(System.in);
        System.out.print("Enter training file: ");
        String trainingFile = userInput.nextLine();
        System.out.print("Enter test file: ");
        String testFile = userInput.nextLine();
        System.out.print("Enter name for output file: ");
        String outputFile = "output/" + userInput.nextLine();

        DigitClassifier classifier = new DigitClassifier();
        classifier.loadTrainingData(trainingFile);
        classifier.setParameters(kValue);
        classifier.classifyData(testFile, outputFile);

        double validationError = classifier.validate(trainingFile);
        PrintWriter outFile = new PrintWriter(new FileWriter(outputFile, true));
        outFile.printf("\nValidation Error:  %.2f%%\n", validationError);
        outFile.print("K-Value: " + kValue);
        userInput.close();
        outFile.close();

    }

}

