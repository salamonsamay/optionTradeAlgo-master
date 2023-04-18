package mycode.my_sql;

import mycode.data.AggregatesRequest;
import mycode.object.AggregatesObject;
import mycode.object.EMAObject;
import mycode.object.RSIObject;
import mycode.object.SMAObject;
import mycode.technical_indicator.RSIRequest;
import mycode.technical_indicator.SMARequest;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class MySQL {
    public static Connection connection;

    public static Connection connect(){
        if(connection!=null){return connection;}
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/polygon",
                    "root", "1234");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database
            return connection;
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return null;
    }

    public  static void insertSMA(SMAObject smaObject){

        String query="INSERT INTO SMA(ticker,timespan,value_) VALUES(?,?,?)";
        PreparedStatement prepared = null;
        try {
            prepared = connect().prepareStatement(query);
            prepared.setString(1,smaObject.getOptionTicker());
            prepared.setLong(2,  smaObject.getTimestamp());
            prepared.setDouble(3,smaObject.getValue());
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }

    public  static void insertEMA(EMAObject emaObject){
        String query="INSERT INTO EMA(ticker,timespan,value_) VALUES(?,?,?)";
        PreparedStatement prepared = null;
        try {
            prepared = connect().prepareStatement(query);
            prepared.setString(1,emaObject.getOptionsTicker());
            prepared.setLong(2, emaObject.getTimestamp());
            prepared.setDouble(3,emaObject.getValue());
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }

    public  static void insertRSI(RSIObject rsiObject){
        String query="INSERT INTO RSI(ticker,timespan,value_) VALUES(?,?,?)";
        PreparedStatement prepared = null;
        try {
            prepared = connect().prepareStatement(query);
            prepared.setString(1,rsiObject.getOptionTicker());
            prepared.setLong(2,  rsiObject.getTimestamp());
            prepared.setDouble(3,rsiObject.getValue());
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }
    public static void insertAggregates(AggregatesObject aggObject){
        //    aggObject.get
        String query="INSERT INTO AGGREGATES(ticker,vwap,close_price,timespan,volume,highest_price," +
                "lowest_price,number_of_transactions,open_price) VALUES(?,?,?,?,?,?,?,?,?)";
        PreparedStatement prepared = null;
        try {
            prepared = connect().prepareStatement(query);
            prepared.setString(1,aggObject.getOptionsTicker());
            prepared.setDouble(2,aggObject.getVwap());
            prepared.setDouble(3,aggObject.getClose_price());
            prepared.setLong(4,aggObject.getTimestamp());
            prepared.setDouble(5,aggObject.getVolume());
            prepared.setDouble(6,aggObject.getHighest_price());
            prepared.setDouble(7,aggObject.getLowest_price());
            prepared.setInt(8,aggObject.getNumber_of_transactions());
            prepared.setDouble(9,aggObject.getOpen_price());
            prepared.executeUpdate();
            prepared.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }





    }

    public static void init(String data) throws IOException, ParseException {
        int limit=500;
        MySQL sql=new MySQL();
        ///////////////////////////////////////////////////////////////////////////////////////////
        SMARequest smaRequest=new SMARequest(data);
        smaRequest.Timestamp_date_gt("2023-02-20").Timestamp_date_lt("2023-03-11").endPoint();

        ArrayList<SMAObject> smaObjectList=smaRequest.sma_list();
        System.out.println("start sma");
        for(SMAObject smaObject: smaObjectList){
            sql.insertSMA(smaObject);

        }
        ///////////////////////////////////////////////////////////////////////////////////////////
        RSIRequest rsiRequest=new RSIRequest(data);
        rsiRequest.Timestamp_date_gt("2023-02-20").Timestamp_date_lt("2023-03-11").endPoint();

        ArrayList<RSIObject> rsiObjectList=rsiRequest.rsi_list();
        System.out.println("start rsi");
        for(RSIObject rsiObject: rsiObjectList){
            sql.insertRSI(rsiObject);

        }
        ///////////////////////////////////////////////////////////////
        AggregatesRequest aggregates=new AggregatesRequest(data);
        aggregates.From("2023-02-21").To("2023-03-10").endPoint();
        ArrayList<AggregatesObject> aggList=aggregates.build();
        System.out.println("start agg");
        for(AggregatesObject value:aggList){
            sql.insertAggregates(value);
        }
        System.out.println("end ...");

    }
    public static void main(String[] args) throws SQLException, ParseException, IOException {
         for(int i=28;i<30;i++){
             System.out.println("O:SPY230310P00"+(485+i)+"000");
             init("O:SPY230310P00"+(385+i)+"000");
             init("O:SPY230310C00"+(385+i)+"000");
             System.out.println(i);
         }
//         init("O:SPY230310P00411000");
//         init("O:SPY230310P00412000");
//         init("O:SPY230310P00413000");
//         init("O:SPY230310P00414000");



       MySQL.connection.close();

        System.out.println(connection.isClosed());
    }


}
