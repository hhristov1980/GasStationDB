package main;

import main.car.Car;
import main.device.CashDesk;
import main.device.FuelDispenser;
import main.gasStation.GasStation;
import main.gasStation.ReportsFromDB;
import main.gasStation.StatisticsToFiles;
import main.person.Cashier;
import main.person.Loader;
import main.util.Randomizer;

public class Demo {
    public static void main(String[] args) {
        GasStation gasStation = new GasStation("IT Gas");
        for(int i = 0; i<5; i++){
            gasStation.addDispenser(new FuelDispenser(gasStation));
        }
        for(int i = 0; i<2; i++){
            gasStation.addLoader(new Loader(gasStation));
            gasStation.addCashier(new Cashier(gasStation));
            gasStation.addCashDesk(new CashDesk(gasStation));
        }
        for(int i = 0; i<20; i++){
            Car.FuelType fuelType = Car.FuelType.values()[Randomizer.getRandomInt(0,Car.FuelType.values().length-1)];
            Car car = new Car("Car "+(i+1),fuelType,gasStation);
            car.start();
        }
        gasStation.open();
        StatisticsToFiles statisticsToFiles = new StatisticsToFiles(gasStation);
        statisticsToFiles.setDaemon(true);
        statisticsToFiles.start();
        ReportsFromDB reportsFromDB = new ReportsFromDB(gasStation);
        reportsFromDB.setDaemon(true);
        reportsFromDB.start();

    }
}
