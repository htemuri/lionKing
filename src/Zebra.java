
//Created: 3/11/2021
//Main class for Zebra (prey)
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.lang.reflect.Array;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import java.util.*;

class Zebra extends Animal {

    private final String name = "Zebra";

    private float breedEnergy; //Energy required to make baby
    private float babyEnergy; //How much energy given to baby
    private int state; //State of the zebra, 0 = wander, 1 = food, 2 = mate, 3 = run, 4 = mating
    private int passion; //How willing the zebra is to mate than to run. (innate)
    private int excitement;
    private int breedTimer = 0;
    private int maxBreedTimer = 500;

    private Plant targetPlant;
    private Zebra targetMate;
    private Lion targetLion;

    public Zebra() {
        this.state = 10;
    }
//
//    public Zebra(int x, int y, int speed, int environmentSize) {
//        setName("Zebra");
//        setX(x);
//        setY(y);
//        setSpeed(speed);
//        setEnvironmentSize(environmentSize);
//    }

    public Zebra(float x, float y, float speed, float energy, float detectRange, float breedEnergy, float babyEnergy, int maxWanderDirTimer, int attentionSpan) {
        setName("Zebra");
        setEnergy(energy);
        setSpeed(speed);
        setDetectRange(detectRange);
        setBreedEnergy(breedEnergy);
        setX(x);
        setY(y);
        setWanderDirTimer(maxWanderDirTimer);
        setMaxWanderDirTimer(maxWanderDirTimer);
        setAttentionSpan(attentionSpan);
        if (babyEnergy > breedEnergy) {babyEnergy = breedEnergy;};
        setBabyEnergy(babyEnergy);
        this.PickNewDir();
        Vector<Float> newDir = new Vector<Float>();
        newDir.add(0f);
        newDir.add(0f);
        this.setTargetDir(newDir);
    }

    //Searches for plants in detection range, sets target plant to nearest plant
    private void DetectPlant(ArrayList<Plant> plantList) {
        HashMap<Float, Plant> plantMap = new HashMap<Float, Plant>();
        ArrayList<Plant> plantInRange = new ArrayList<Plant>();

        //Looking for food in range
        for (Plant plant:plantList) {
            float distBetween = Equations.EuclDist(plant.getX(),plant.getY(),getX(),getY());
            if (distBetween < getDetectRange()) {
                //plantMap.put(distBetween, plant);
                plantInRange.add(plant);
            }
        }

        int pickNum = (int) Math.random()*plantInRange.size();

//        if (plantMap.size()>0) {
//            Set<Float> distances = plantMap.keySet();
//            float minDist = Collections.min(distances);
//            this.targetPlant = plantMap.get(minDist);
//            this.state = 1;
//        }
        if (plantInRange.size()>0) {
            this.targetPlant = plantInRange.get(pickNum);
            this.state = 1;
        }

        else {
            this.state = 0;
        }
    }

    //Searches for mate in detection range and sets mate target to closest available mate
    private void DetectMate(ArrayList<Zebra> zebraList) {
        HashMap<Float, Zebra> mateMap = new HashMap<Float, Zebra>();
        for (Zebra zebra:zebraList) {
            if (zebra != this) {
                float distBetween = Equations.EuclDist(zebra.getX(), zebra.getY(), getX(), getY());
                //Checks if the target zebra is in range and also available to mate
                if ((distBetween < getDetectRange()) && (zebra.getEnergy()>zebra.getBreedEnergy())) {
                //if (distBetween < getDetectRange()) {
                    mateMap.put(distBetween, zebra);
                }
            }
        }
        if (mateMap.size()>0) {
            Set<Float> distances = mateMap.keySet();
            float minDist = Collections.min(distances);
            this.targetMate = mateMap.get(minDist);
            this.state = 2;
        }
//        else {
//            this.state = 0;
//        }
    }

    //Searches for nearby enemies within detection range, sets nearest lion to target lion
    private void DetectEnemy(ArrayList<Lion> lionList) {
        HashMap<Float, Lion> lionMap = new HashMap<Float, Lion>();

        //Looking for lions in range
        for (Lion lion:lionList) {
            float distBetween = Equations.EuclDist(lion.getX(),lion.getY(),getX(),getY());
            if (distBetween < getDetectRange()) {
                lionMap.put(distBetween, lion);
            }
        }
        //Enters run phase if lions are found
        if (lionMap.size()>0) {
            Set<Float> distances = lionMap.keySet();
            float minDist = Collections.min(distances);
            this.targetLion = lionMap.get(minDist);
            this.state = 3;
        }
        //Enters wander phase if lions are out of detection range
        else {
            this.targetLion = null;
            this.state = 0;
        }
    }

    //Call this method every time step, controls what the zebra is deciding to do
    public ArrayList<Zebra> Update(ArrayList<Plant> plantList, ArrayList<Zebra> zebraList, ArrayList<Lion> lionList, int mapSize) {
        ArrayList<Zebra> addZebras = new ArrayList<Zebra>();

        DetectEnemy(lionList);

        if (this.state != 3) {
            if (this.breedTimer == 0) {
                DetectMate(zebraList);
            }
            else {
                this.breedTimer-=1;
            }
            if (this.state != 2) {
                DetectPlant(plantList);
            }
        }
        //Search for mate if enough energy and not running from lion
//        if (this.getEnergy() > this.breedEnergy && this.targetMate != null) {
//            this.state = 2;
//        }
        System.out.println("State: "+this.state);
        //System.out.println("Direction: "+this.getDirection());
        switch (this.state) {
            case 0: //wandering phase
                //System.out.println("NO FOOD");
                Wander(mapSize);
                break;
            case 1: //searching for food
                //System.out.println("FOOD");
                Vector<Float> plantDir = new Vector<Float>();
                plantDir.add(this.targetPlant.getX());
                plantDir.add(this.targetPlant.getY());
                this.setTargetDir(plantDir);
                if (this.targetPlant != null) {
                    Move(this.targetPlant.getX(),this.targetPlant.getY(),1,mapSize);
                    Eat(plantList);
                }
                break;
            case 2: //searching for mate
                Vector<Float> zebraDir = new Vector<Float>();
                zebraDir.add(this.targetMate.getX());
                zebraDir.add(this.targetMate.getY());
                this.setTargetDir(zebraDir);
                Move(this.targetMate.getX(),this.targetMate.getY(),1,mapSize);
                Zebra baby = Mate();
                if (baby.state != 10) {
                    addZebras.add(baby);
                }
                break;
            case 3: //running from predator
                Move(this.targetLion.getX(),this.targetLion.getY(),0,mapSize);
                break;
        }
        return addZebras;
    }

    //Handles the eating of plants
    private void Eat(ArrayList<Plant> plantList) {
        float distBetween = Equations.EuclDist(this.targetPlant.getX(),this.targetPlant.getY(),getX(),getY());
        if (distBetween < 0.5) {
            // distBetween < speed : eat
            this.setEnergy(this.getEnergy()+this.targetPlant.getFOODVAL());
            plantList.remove(targetPlant); //removes this plant
            this.targetPlant = null; //clear targetPlant variable
            this.PickNewDir();
        }
    }

    //Checks for available mate in proximity and mates
    public Zebra Mate() {
        Zebra baby = new Zebra();
        float distBetween = Equations.EuclDist(this.targetMate.getX(),this.targetMate.getY(),getX(),getY());
        float prevSpeed = this.getSpeed();
        if (distBetween < 0.5) {
            //this.setSpeed(0f);
            if (this.targetMate.state == 4 && this.state == 2) {
                baby = new Zebra(this.getX(),this.getY(),(this.getSpeed()+this.targetMate.getSpeed())/2f,(this.getBabyEnergy()+this.targetMate.getBabyEnergy())/2f,(this.getDetectRange()+this.targetMate.getDetectRange())/2f,(this.getBreedEnergy()+this.targetMate.getBreedEnergy())/2f,(this.getBabyEnergy()+this.targetMate.getBabyEnergy())/2f,(this.getMaxWanderDirTimer()+this.targetMate.getMaxWanderDirTimer())/2,(this.getAttentionSpan()+this.targetMate.getAttentionSpan())/2);
                this.setEnergy(this.getEnergy()-this.babyEnergy);
                this.targetMate.setEnergy(this.targetMate.getEnergy()-this.targetMate.babyEnergy);
            }
            this.state = 4;
            if (this.targetMate.state == 4) {
                this.state = 0;
                this.targetMate.state = 0;
                this.PickNewDir();
                this.targetMate.PickNewDir();
                baby.breedTimer = baby.maxBreedTimer;
                this.targetMate.breedTimer = this.targetMate.maxBreedTimer;
                this.breedTimer = this.maxBreedTimer;
                this.targetMate.targetMate = null;
                this.targetMate = null;
            }
        }
        return baby;
    }

    //Call every time step during wander phase
    private void Wander(int mapSize) {
        //System.out.println("Wander Timer: "+this.getWanderDirTimer());
        if (this.getWanderDirTimer() == 0) {
            this.setWanderDirTimer(this.getMaxWanderDirTimer());
            this.PickNewDir();
        }
        else {
            this.setWanderDirTimer(this.getWanderDirTimer()-1);
        }
        //System.out.println("direction: "+this.getDirection());
        float nextX = this.getX()+this.getDirection().get(0)*this.getSpeed()*100f;
        float nextY = this.getY()+this.getDirection().get(1)*this.getSpeed()*100f;
//        System.out.println("dirx: "+this.getDirection().get(0));
//        System.out.println("next xy: "+nextX+" "+nextY);
        this.targetDir.set(0,nextX);
        this.targetDir.set(1,nextY);
        Advance(mapSize);
    }

    public void setBreedEnergy(float breedEnergy) {
        this.breedEnergy = breedEnergy;
    }

    public void setBabyEnergy(float babyEnergy) {
        this.babyEnergy = babyEnergy;
    }

    public int getState() {
        return state;
    }

    public float getBreedEnergy() {
        return breedEnergy;
    }

    public float getBabyEnergy() {
        return babyEnergy;
    }

    //   Runs in opposite direction of the target lion
//    private void Run() {
//        Vector<Float> enemyDir = new Vector<Float>();
//        enemyDir.set(0,this.getX()-this.targetLion.getX());
//        enemyDir.set(1,this.getY()-this.targetLion.getY());
//
//        this.setDirection(enemyDir);
//        Move();
//    }

    //Call every time step during food targeting phase
//    private void GoEat() {
//        Vector<Float> foodDir = new Vector<Float>();
//        foodDir.set(0,this.targetPlant.getX()-this.getX());
//        foodDir.set(1,this.targetPlant.getY()-this.getY());
//
//        this.setDirection(foodDir);
//        Move();
//    }

}