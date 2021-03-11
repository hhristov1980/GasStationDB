package main.car;

import main.device.FuelDispenser;
import main.gasStation.GasStation;
import main.util.Constants;
import main.util.Randomizer;

public class Car extends Thread{
    private String carName;
    private FuelType fuelType;
    private GasStation gasStation;
    private int fuelToPutInTank;
    private boolean isRefueled;
    private boolean billPaid;
    private FuelDispenser fuelDispenser;
    private double money;


    public Car(String carName, FuelType fuelType, GasStation gasStation){
        if(carName.length()>0){
            this.carName = carName;
        }
        this.fuelType = fuelType;
        this.gasStation = gasStation;
        this.isRefueled = false;
        this.billPaid = false;
        this.money = 150;
        this.fuelToPutInTank = Randomizer.getRandomInt(10,40);
    }

    @Override
    public void run() {
        gasStation.goToFuelDispenser(this);

        gasStation.goToPay(this);

        System.out.println("Car "+carName+" left the "+gasStation.getName());
    }

    public enum FuelType {
        PETROL(Constants.PETROL_PRICE), GAS(Constants.GAS_PRICE), DIESEL(Constants.DIESEL_PRICE);
        double price;


        FuelType(double price){
            this.price = price;

        }

        public double getPrice() {
            return price;
        }
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public int getFuelToPutInTank() {
        return fuelToPutInTank;
    }

    public void setRefueled(boolean refueled) {
        isRefueled = refueled;
    }

    public String getCarName() {
        return carName;
    }
    public void setFuelDispenser(FuelDispenser fuelDispenser) {
        this.fuelDispenser = fuelDispenser;
    }

    public FuelDispenser getFuelDispenser() {
        return fuelDispenser;
    }

    public void payBill(Double bill){
        if(bill<=money){
            money-=bill;
        }
    }

    public void setBillPaid(boolean billPaid) {
        this.billPaid = billPaid;
    }

    public boolean isRefueled() {
        return isRefueled;
    }


}
