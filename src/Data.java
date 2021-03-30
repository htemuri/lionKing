
//Created: 3/27/2021
// Main class for data collection and recording

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Data {

    static String simFilename = "SimulationData.txt";
    static String zebraFilename = "ZebraData.txt";
    static String lionFilename = "LionData.txt";

    //Method to create data files for storage
    public static void CreateFiles() {
        try {
            File newSimFile = new File(simFilename);
            File newZebraFile = new File(zebraFilename);
            File newLionFile = new File(lionFilename);

            if (newSimFile.createNewFile()) {
                System.out.println("New simulation data file created!");
            }
            else {
                System.out.println("Simulation data already exists!");
            }
            if (newZebraFile.createNewFile()) {
                System.out.println("New zebra data file created!");
            }
            else {
                System.out.println("Zebra data already exists!");
            }
            if (newLionFile.createNewFile()) {
                System.out.println("New lion data file created!");
            }
            else {
                System.out.println("Lion data already exists!");
            }
        }
        catch (IOException e) {
            System.out.println("An error occurred when creating files.");
            e.printStackTrace();
        }
    }

    public static void RecordData(Object animal, String objType) {
        FileWriter writer;
        switch(objType) {
            case "Zebra": {
                try {
                    writer = new FileWriter(zebraFilename, true);
                }
                catch (IOException e) {
                    System.out.println(zebraFilename + " cannot be opened!");
                    e.printStackTrace();
                    break;
                }

                break;
            }
            case "Lion": {
                try {
                    writer = new FileWriter(lionFilename,true);
                }
                catch (IOException e) {
                    System.out.println(lionFilename + " cannot be opened!");
                    e.printStackTrace();
                    break;
                }
                break;
            }
            case "Sim": {
                try {
                    writer = new FileWriter(simFilename,true);
                }
                catch (IOException e) {
                    System.out.println(simFilename + " cannot be opened!");
                    e.printStackTrace();
                    break;
                }
                break;
            }
        }
    }

    public static void CreateHeaders() {
        System.out.println("hi");
        try {
            FileWriter zWriter = new FileWriter(zebraFilename);
            zWriter.append("Generation,StartingEnergy,Speed,DetectRange,BreedEnergy,BabyEnergy,AttentionSpan,Age");
            System.out.println("wrote");
            zWriter.close();
        }
        catch (IOException e) {
            System.out.println(zebraFilename + " cannot be opened!");
            e.printStackTrace();
        }
        try {
            FileWriter lWriter = new FileWriter(lionFilename);
            lWriter.close();
        }
        catch (IOException e) {
            System.out.println(lionFilename + " cannot be opened!");
            e.printStackTrace();
        }
        try {
            FileWriter sWriter = new FileWriter(simFilename);
            sWriter.close();
        }
        catch (IOException e) {
            System.out.println(simFilename + " cannot be opened!");
            e.printStackTrace();
        }



    }

}