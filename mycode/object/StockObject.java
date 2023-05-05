package mycode.object;

import java.util.ArrayList;
import java.util.Date;

public class StockObject{

    private String optionsTicker;
    private double vwap;
    private double close_price;
    private long timestamp;
    private double volume;
    private double highest_price;
    private double  lowest_price;
    private int number_of_transactions;
    private double open_price;

    public StockObject(String ticker,long timestamp,double close_price){
        setOptionsTicker(ticker);
        setTimestamp(timestamp);
        setClose_price(close_price);
    }
    public StockObject(String optionsTicker,double vwap, double close_price, long timestamp, double volume, double highest_price, double lowest_price, int number_of_transactions, double open_price) {
        this.optionsTicker=optionsTicker;
        this.vwap = vwap;
        this.close_price = close_price;
        this.timestamp = timestamp;
        this.volume = volume;
        this.highest_price = highest_price;
        this.lowest_price = lowest_price;
        this.number_of_transactions = number_of_transactions;
        this.open_price = open_price;
    }

    public String getOptionsTicker() {
        return optionsTicker;
    }

    public void setOptionsTicker(String optionsTicker) {
        this.optionsTicker = optionsTicker;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getClose_price() {
        return close_price;
    }

    public void setClose_price(double close_price) {
        this.close_price = close_price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getHighest_price() {
        return highest_price;
    }

    public void setHighest_price(double highest_price) {
        this.highest_price = highest_price;
    }

    public double getLowest_price() {
        return lowest_price;
    }

    public void setLowest_price(double lowest_price) {
        this.lowest_price = lowest_price;
    }

    public int getNumber_of_transactions() {
        return number_of_transactions;
    }

    public void setNumber_of_transactions(int number_of_transactions) {
        this.number_of_transactions = number_of_transactions;
    }

    public double getOpen_price() {
        return open_price;
    }

    public void setOpen_price(double open_price) {
        this.open_price = open_price;
    }


    public static ArrayList<StockObject> filter(ArrayList<StockObject> list){
        ArrayList<StockObject> newList=new ArrayList<>();

        String start="";
        for(int i=0;i<list.size();i++) {

            String arr[]=new Date(list.get(i).getTimestamp()).toString().split(" ");

            if(arr[3].equals("10:00:00")){
                start="10:00:00";
                System.out.println("10"+i);
            }
            else if(arr[3].equals("11:00:00")){
                System.out.println("11 "+i);
                start="11:00:00";
            }

            if(start.equals("10:00:00") && arr[3].equals("15:30:00")
                    || (start.equals("11:00:00") && arr[3].equals("16:30:00"))){
                start="";
                for(int j=i;j<i+390 && j<list.size();j++){
                    newList.add(list.get(j));
                }
                // i=i+390;
            }
        }
        return newList;
    }
    public static StockObject[][] filter_(ArrayList<StockObject> list){
        ArrayList<StockObject> newList=new ArrayList<>();

        String start="";
        for(int i=0;i<list.size();i++) {

            String arr[]=new Date(list.get(i).getTimestamp()).toString().split(" ");

            if(arr[3].equals("10:00:00")){
                start="10:00:00";
            }
            else if(arr[3].equals("11:00:00")){
                start="11:00:00";
            }

            if(start.equals("10:00:00") && arr[3].equals("15:30:00")
                    || (start.equals("11:00:00") && arr[3].equals("16:30:00"))){
                start="";
                for(int j=i;j<i+390 && j<list.size();j++){
                    newList.add(list.get(j));
                }
                // i=i+390;
            }
        }
        int row=0;
        for(int i=0;i<newList.size();i++){
            if(i%390==0){
                row++;
//                System.out.println("----------------------");
            }
//            System.out.println(new Date(newList.get(i).getTimestamp()));
        }
        StockObject matrix[][]=new StockObject[row][390];
        int counter=0;
        for(int i=0;i<matrix.length;i++){
            for(int j=0;j<matrix[i].length;j++){
                matrix[i][j]=newList.get(counter++);
            }
        }
        return  matrix;

    }
    @Override
    public String toString() {
        return "StockObject{" +
                "optionsTicker='" + optionsTicker + '\'' +
                ", vwap=" + vwap +
                ", close_price=" + close_price +
                ", timestamp=" + timestamp +
                ", volume=" + volume +
                ", highest_price=" + highest_price +
                ", lowest_price=" + lowest_price +
                ", number_of_transactions=" + number_of_transactions +
                ", open_price=" + open_price +
                '}';
    }
}
