package mycode.my_sql;

import mycode.data.AggregatesRequest;
import mycode.object.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {
    public static Connection connection;

    public static Connection connect(){
        if(connection!=null){return connection;}
        try {
            // below two lines are used for connectivity.
            System.out.println("Connecting to MySQL...");
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

    public static List<AggregatesObject> selectStar(String ticker) {
        connect();
        List<AggregatesObject> aggregatesList = new ArrayList<>();
        String query = "SELECT ticker, vwap, close_price, Timespan, volume, highest_price, lowest_price, " +
                "number_of_transactions, open_price FROM AGGREGATES" +
                " WHERE ticker = '" + ticker + "'" +
                "GROUP BY ticker,Timespan";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                AggregatesObject aggObject = new AggregatesObject();
                aggObject.setOptionsTicker(resultSet.getString("ticker"));
                aggObject.setVwap(resultSet.getDouble("vwap"));
                aggObject.setClose_price(resultSet.getDouble("close_price"));
                aggObject.setTimestamp(resultSet.getLong("Timespan"));
                aggObject.setVolume(resultSet.getDouble("volume"));
                aggObject.setHighest_price(resultSet.getDouble("highest_price"));
                aggObject.setLowest_price(resultSet.getDouble("lowest_price"));
                aggObject.setNumber_of_transactions(resultSet.getInt("number_of_transactions"));
                aggObject.setOpen_price(resultSet.getDouble("open_price"));

                aggregatesList.add(aggObject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aggregatesList;
    }


    public  static void insertSMA(SMAObject smaObject){

        String query="INSERT INTO Indicator(ticker,Timespan,value_) VALUES(?,?,?)";
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



    public  static void insertRSI(RSIObject rsiObject){
        String query="INSERT INTO RSI(ticker,Timespan,value_) VALUES(?,?,?)";
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
        String query="INSERT INTO AGGREGATES(ticker,vwap,close_price,Timespan,volume,highest_price," +
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
        } catch (SQLException e   ) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }


    }


    public static void main(String[] args) throws SQLException, ParseException, IOException {


//        List<AggregatesObject> list= selectStar("QQQ");
//        for(AggregatesObject agg:list){
//            System.out.println(new Date(agg.getTimestamp()));
//        }
//        System.out.println(list.size());

        ArrayList<String> tickers=new ArrayList<>();

        tickers.add("QQQ");tickers.add("DIA");tickers.add("IWM");
        tickers.add("BABA");tickers.add("SHOP");tickers.add("TLT");
        tickers.add("XLE");tickers.add("BAC");tickers.add("XOM");
        tickers.add("ROKU");tickers.add("ENPH");tickers.add("MRNA");

        for(int i=0;i<tickers.size();i++){
            AggregatesRequest request = new AggregatesRequest(tickers.get(i));
            ArrayList<AggregatesObject> list ;
            list=request.From("2021-01-01").To("2023-08-01").Timespan("minute").endPoint().build();

            System.out.println("start to add information to database ...");
            for(AggregatesObject agg:list){
                try {
                    insertAggregates(agg);
                }catch (ClassCastException e){
                    e.printStackTrace();
                }

            }
            System.out.println("ended ...");
        }




    }


}
