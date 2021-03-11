package main.gasStation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.car.Car;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class StatisticsToFiles extends Thread{
    private GasStation gasStation;

    public StatisticsToFiles(GasStation gasStation){
        this.gasStation = gasStation;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        printStatToTXT();
        printStatToJSON();

    }

    private void printStatToTXT(){
        File f = new File(LocalDateTime.now()+" status.txt");
        try(PrintStream ps = new PrintStream(f)){
            ps.println("===================== STATISTICS ========================");
            for(Map.Entry<Integer, ConcurrentSkipListMap<Car.FuelType, ConcurrentSkipListMap<LocalDateTime,Integer>>> d:gasStation.getLoadings().entrySet()){
                ps.println("Fuel dispenser: "+d.getKey());
                for(Map.Entry<Car.FuelType, ConcurrentSkipListMap<LocalDateTime,Integer>> t: d.getValue().entrySet()){
                    ps.println("\tFuel type: "+t.getKey());
                    for(Map.Entry<LocalDateTime,Integer> l:t.getValue().entrySet()){
                        ps.println("\t\t"+l.getKey()+" : "+l.getValue()+" litters.");
                    }
                }
            }
            ps.println("===================== END OF STATISTICS ========================");

        } catch (FileNotFoundException e) {
            System.out.println("Problem while writing statistics to txt.");
        }
    }

    private void printStatToJSON(){
        File f = new File(LocalDateTime.now()+" status.json");
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();

            for(Map.Entry<Integer, ConcurrentSkipListMap<Car.FuelType, ConcurrentSkipListMap<LocalDateTime,Integer>>> d:gasStation.getLoadings().entrySet()){
                jo.addProperty("Fuel dispenser: ",d.getKey());
                for(Map.Entry<Car.FuelType, ConcurrentSkipListMap<LocalDateTime,Integer>> t: d.getValue().entrySet()){
                    jo.addProperty("Fuel type: ", String.valueOf(t.getKey()));
                    for(Map.Entry<LocalDateTime,Integer> l:t.getValue().entrySet()){
                        jo.addProperty(l.getKey()+" : ",l.getValue());
                    }
                }
            }
            String text = gson.toJson(jo);

        try(PrintStream ps = new PrintStream(f)){
            ps.println(text);

        } catch (FileNotFoundException e) {
            System.out.println("Problem while writing statistics to json.");
        }
    }

}
