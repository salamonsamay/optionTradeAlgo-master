package mycode.my_sql;

import mycode.data.AggregatesRequest;
import mycode.data.StockRequest;
import mycode.object.*;
import mycode.technical_indicator.RSIRequest;
import mycode.technical_indicator.SMARequest;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

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

    public  static void daylydata(File file) throws IOException, ParseException {
        ArrayList<StockObject> stockObjects=new ArrayList<>();
        StockRequest stock=new StockRequest("SPY");
        stockObjects=stock.Timespan("day").From("2022-04-24").To("2023-04-18").endPoint().build();
        for(StockObject s:stockObjects) {
            System.out.println(s);


            String query = "INSERT INTO spy(date_,open_,high,low,close_) VALUES(?,?,?,?,?)";
            PreparedStatement prepared = null;
            try {
                prepared = connect().prepareStatement(query);

                prepared.setDate(1, new Date(s.getTimestamp()));
                prepared.setDouble(2, s.getOpen_price());
                prepared.setDouble(3, s.getHighest_price());
                prepared.setDouble(4, s.getLowest_price());
                prepared.setDouble(5, s.getClose_price());

                prepared.executeUpdate();
                prepared.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }


    }

    public static void test() throws IOException, ParseException {
        ArrayList<StockObject> spyList=new ArrayList<>();
        StockRequest spy=new StockRequest("SPY");
        spyList=spy.Timespan("day").From("2022-10-24").To("2023-04-18").endPoint().build();


        double gapClose=0;
        double gapNotClose=0;
        double total=0;
        double a=1.005;
        double b=0.995;
        int sequnce=0;
        int max_sequnce=0;

        for(int i=0;i<spyList.size()-1;i++){
            int j=i+1;
            if(spyList.get(j).getOpen_price()>spyList.get(i).getClose_price()*a){//is gap up
                if(spyList.get(j).getLowest_price()<spyList.get(i).getClose_price()){
                    gapClose++;
                    total++;
                    sequnce=0;
                }
                else {
                    gapNotClose++;
                    total++;
                    sequnce++;
                }
            }
           else if(spyList.get(j).getOpen_price()<spyList.get(i).getClose_price()*b){//is gap down
                if(spyList.get(j).getHighest_price()>spyList.get(i).getClose_price()){
                    gapClose++;
                    total++;
                    sequnce=0;
                }
                else {
                    gapNotClose++;
                    total++;
                    sequnce++;
                }
            }
           if(max_sequnce<sequnce){
               max_sequnce=sequnce;
           }
        }

        System.out.println("the max sequnce is  "+max_sequnce);
        System.out.println("gap that close "+gapClose/total);
        System.out.println("gap that not close "+gapNotClose/total);
        System.out.println("total "+total);
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
    test();
      //  daylydata(new File("C:\\Users\\salam\\OneDrive\\שולחן העבודה\\HistoricalData_1679443188523.csv"));
//         for(int i=28;i<30;i++){
//             System.out.println("O:SPY230310P00"+(485+i)+"000");
//             init("O:SPY230310P00"+(385+i)+"000");
//             init("O:SPY230310C00"+(385+i)+"000");
//             System.out.println(i);
//         }
//         init("O:SPY230310P00411000");
//         init("O:SPY230310P00412000");
//         init("O:SPY230310P00413000");
//         init("O:SPY230310P00414000");



 //       MySQL.connection.close();

    //    System.out.println(connection.isClosed());
    }


}
