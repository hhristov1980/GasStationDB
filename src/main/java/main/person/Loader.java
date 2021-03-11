package main.person;

import main.gasStation.GasStation;

public class Loader extends Thread {
    private static int uniqueId = 1;
    private int loaderId;
    private GasStation gasStation;


    public Loader(GasStation gasStation){
        this.gasStation = gasStation;
        loaderId = uniqueId++;
    }

    @Override
    public void run() {

        while (true){
            gasStation.refuelCar(this);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public int getLoaderId() {
        return loaderId;
    }
}
