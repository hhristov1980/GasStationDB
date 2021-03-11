package main.gasStation;

import main.car.Car;
import main.connector.DBConnector;
import main.device.CashDesk;
import main.device.FuelDispenser;
import main.person.Cashier;
import main.person.Loader;
import main.util.Randomizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GasStation {
    private String name;
    private double money;
    private CopyOnWriteArrayList<FuelDispenser> dispensers;
    private CopyOnWriteArrayList<CashDesk> cashDesks;
    private CopyOnWriteArrayList<Loader> loaders;
    private CopyOnWriteArrayList<Cashier> cashiers;
    private ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Car.FuelType, ConcurrentSkipListMap<LocalDateTime,Integer>>> loadings;

    public GasStation(String name){
        if(name.length()>0){
            this.name = name;
        }
        dispensers = new CopyOnWriteArrayList<>();
        loaders = new CopyOnWriteArrayList<>();
        cashiers = new CopyOnWriteArrayList<>();
        loadings = new ConcurrentSkipListMap<>();
        cashDesks = new CopyOnWriteArrayList<>();
        this.money = 0.0;

    }

    public void addDispenser(FuelDispenser fd){
        dispensers.add(fd);
    }

    public void addLoader(Loader l){
        loaders.add(l);
    }

    public void addCashier(Cashier c){
        cashiers.add(c);
    }

    public void addCashDesk(CashDesk cd){
        cashDesks.add(cd);
    }

    public String getName() {
        return name;
    }

    public synchronized void goToFuelDispenser(Car car){
        int index = Randomizer.getRandomInt(0,dispensers.size()-1);
        FuelDispenser dispenser = dispensers.get(index);
        dispenser.addCar(car);
        car.setFuelDispenser(dispenser);
        System.out.println(car.getCarName()+" is waiting at fuel dispenser with id "+dispenser.getDispenserId());
        notifyAll();
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void refuelCar(Loader l){
        boolean foundCar = false;
        for(FuelDispenser fd: dispensers){
            if(fd.getCarQueue().size()>0){
                Car car = fd.getCarQueue().peek();
                if(!car.isRefueled()){
                    foundCar = true;
                    car.setRefueled(true);
                    System.out.println(car.getCarName()+" was successfully refueled with "+car.getFuelToPutInTank()+" of "+car.getFuelType());
                    notifyAll();
                    break;
                }

            }
        }
        if(!foundCar){
            System.out.println("No cars. Loader "+l.getLoaderId()+" is waiting!");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void goToPay(Car car){
        int index = Randomizer.getRandomInt(0,cashDesks.size()-1);
        CashDesk cashDesk = cashDesks.get(index);
        cashDesk.addCar(car);
        notifyAll();
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public synchronized void takeMoney(Cashier cashier){
        boolean foundCar = false;
        for(CashDesk cd: cashDesks){
            if(cd.getCarQueue().size()>0){
                Car car = cd.getCarQueue().peek();
                foundCar = true;
                double bill = cashier.makeBill(car);
                car.payBill(bill);
                money+=bill;
                car.setBillPaid(true);
                insertIntoCollectionAndDB(car);
                car.getFuelDispenser().getCarQueue().remove(car);

                try {
                    cd.getCarQueue().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notifyAll();
                break;
            }
        }
        if(!foundCar){
            System.out.println("No cars. Cashier "+cashier.getCashierId()+" is waiting!");
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private synchronized void insertIntoCollectionAndDB(Car car){
        int dispenserId = car.getFuelDispenser().getDispenserId();
        Car.FuelType type = car.getFuelType();
        int quantity = car.getFuelToPutInTank();
        LocalDateTime time = LocalDateTime.now();
        if(!loadings.containsKey(dispenserId)){
            loadings.put(dispenserId,new ConcurrentSkipListMap<>());
        }
        if(!loadings.get(dispenserId).containsKey(type)){
            loadings.get(dispenserId).put(type, new ConcurrentSkipListMap<>());
        }
        loadings.get(dispenserId).get(type).put(time,quantity);

        Connection connection = DBConnector.getInstance().getConnection();
        String insertQuery = "INSERT INTO station_loadings (dispenser_id, fuel_type, fuel_quantity, loading_time) VALUES (?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(insertQuery)){
            ps.setInt(1,dispenserId);
            ps.setString(2,type.toString());
            ps.setInt(3,quantity);
            ps.setTimestamp(4,new Timestamp((System.currentTimeMillis())));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Problem with the insert query!");
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }

    }

    public void open(){
        for(Cashier c: cashiers){
            c.start();
        }
        for(Loader l: loaders){
            l.start();
        }
    }

    public ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Car.FuelType, ConcurrentSkipListMap<LocalDateTime, Integer>>> getLoadings() {
        return loadings;
    }

    public double getMoney() {
        return money;
    }
}
