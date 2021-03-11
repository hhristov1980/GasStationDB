package main.device;

import main.car.Car;
import main.gasStation.GasStation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FuelDispenser {
    private static int uniqueId = 1;
    private int dispenserId;
    private BlockingQueue<Car> carQueue;
    private GasStation gasStation;


    public FuelDispenser(GasStation gasStation){
        this.gasStation = gasStation;
        dispenserId = uniqueId++;
        carQueue = new LinkedBlockingQueue<>();
    }

    public void addCar(Car c){
        try {
            carQueue.put(c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void removeCar(Car c){
        carQueue.remove(c);
    }

    public BlockingQueue<Car> getCarQueue() {
        return carQueue;
    }

    public int getDispenserId() {
        return dispenserId;
    }


}
