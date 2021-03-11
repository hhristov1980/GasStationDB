package main.person;

import main.car.Car;
import main.gasStation.GasStation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Cashier extends Thread{
    private static int uniqueId = 1;
    private int cashierId;
    private GasStation gasStation;


    public Cashier(GasStation gasStation){
        this.gasStation = gasStation;
        cashierId = uniqueId++;

    }

    @Override
    public void run() {

        while(true){
                gasStation.takeMoney(this);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public int getCashierId() {
        return cashierId;
    }

    public double makeBill(Car car){
        return car.getFuelToPutInTank()*car.getFuelType().getPrice();
    }
}
