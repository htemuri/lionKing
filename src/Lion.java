
//Created: 3/11/2021
//Main class for Lion (predator)

class Lion extends Animal {

    private ArrayList<Zebra> inRange;

    public Lion(float x, float y) {
        setName("Lion");
        setEnergy(10000);
        setSpeed(10);
        setDetectRange(7);
        setX(x);
        setY(y);
    }

    private void detectPrey(ArrayList<Zebra> zebraList) {
        for (x:zebraList) { //for each thing in list

        }
    }

}