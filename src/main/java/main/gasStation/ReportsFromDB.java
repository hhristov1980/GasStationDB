package main.gasStation;

import main.car.Car;
import main.connector.DBConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class ReportsFromDB extends Thread{
    private GasStation gasStation;

    public ReportsFromDB(GasStation gasStation){
        this.gasStation = gasStation;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            File file = new File(LocalDateTime.now()+"DBreport.txt");
            try (PrintStream ps = new PrintStream(file)){
                printReportAll(ps);
                printFuelReport(ps);
                printReportCarsPerDispenser(ps);
                printTurnover(ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void printReportAll(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        String selectQuery = "SELECT * FROM station_loadings ORDER BY dispenser_id;";
        try(Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(selectQuery);
            while (rs.next()){
                ps.println("Entry id | Dispenser id | Fuel type | Quantity | Refueled at");
                ps.println(rs.getInt(1)+" | "+rs.getInt(2)+" | "+rs.getString(3)+" | "+rs.getInt(4)+" | "+rs.getTimestamp(5));
            }

        } catch (SQLException e) {
            System.out.println("Problem with all report query!");
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }

    }

    private void printReportCarsPerDispenser(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        String selectQuery = "SELECT dispenser_id, COUNT(entry_id) FROM station_loadings GROUP BY dispenser_id ORDER BY dispenser_id;";
        try(Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(selectQuery);
            ps.println("Dispenser id | Cars");
            while (rs.next()){
                ps.println(rs.getInt(1)+" | "+rs.getInt(2));
            }


        } catch (SQLException e) {
            System.out.println("Problem with Cars per dispenser report query!");
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }

    }

    private void printFuelReport(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        String selectQuery = "SELECT fuel_type, SUM(fuel_quantity) AS total FROM station_loadings GROUP BY fuel_type ORDER BY total DESC;";
        try(Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(selectQuery);
            ps.println("Fuel type | Total quantity refueled");
            while (rs.next()){
                ps.println(rs.getString(1)+" | "+rs.getInt(2));
            }

        } catch (SQLException e) {
            System.out.println("Problem with Fuel report query!");
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }

    }

    private void printTurnover(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        String selectSQL = "SELECT SUM(fuel_quantity*IF(fuel_type = \"PETROL\", 2,IF(fuel_type = \"DIESEL\",2.4,1.6))) from station_loadings";
        try(Statement st = connection.createStatement()){
            ResultSet rs = st.executeQuery(selectSQL);
            rs.next();
            ps.println("Total turnover: "+rs.getDouble(1));
            ps.println("Check "+gasStation.getMoney());

        } catch (SQLException e) {
            System.out.println("Problem with turnover report query!");
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }

    }
}
