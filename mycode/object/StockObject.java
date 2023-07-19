package mycode.object;

import mycode.data.StockRequest;
import org.json.simple.parser.ParseException;

import java.io.IOException;
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
            if(list.get(i).getTimestamp()%(1000*60*60*24)==48600000){

                for(int j=i;j<i+390;j++){
                    newList.add(list.get(j));
                }
                i=i+390;
            }
//            String arr[]=new Date(list.get(i).getTimestamp()).toString().split(" ");
//
//            if(arr[3].equals("10:00:00")){
//                start="10:00:00";
//            }
//            else if(arr[3].equals("11:00:00")){
//                start="11:00:00";
//            }
//
//            if(start.equals("10:00:00") && arr[3].equals("15:30:00")
//                    || (start.equals("11:00:00") && arr[3].equals("16:30:00"))){
//                start="";
//                for(int j=i;j<i+390 && j<list.size();j++){
//                    newList.add(list.get(j));
//                }
//                // i=i+390;
//            }
        }

        int row=0;
        for(int i=0;i<newList.size();i++){
            if(i%390==0){
                row++;
//                System.out.println("----------------------");
            }
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

    public static double[] findSupportAndResistanceWithStdDev(ArrayList<StockObject> stocks) {
        // Find min and max prices
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        for (StockObject stock : stocks) {
            if (stock.getClose_price() < minPrice) {
                minPrice = stock.getClose_price();
            }
            if (stock.getClose_price() > maxPrice) {
                maxPrice = stock.getClose_price();
            }
        }

        // Calculate range and threshold
        double range = maxPrice - minPrice;
        double threshold = 0.05 * range; // 5% of the range

        // Find support and resistance levels
        double support = 0, resistance = 0;
        int numSupport = 0, numResistance = 0;
        ArrayList<Double> supportPrices = new ArrayList<>();
        ArrayList<Double> resistancePrices = new ArrayList<>();
        for (StockObject stock : stocks) {
            double price = stock.getClose_price();
            if (price <= minPrice + threshold) {
                support += price;
                supportPrices.add(price);
                numSupport++;
            }
            if (price >= maxPrice - threshold) {
                resistance += price;
                resistancePrices.add(price);
                numResistance++;
            }
        }

        // Calculate average support and resistance levels
        if (numSupport > 0) {
            support /= numSupport;
        }
        if (numResistance > 0) {
            resistance /= numResistance;
        }

        // Calculate standard deviations
        double supportStdDev = 0, resistanceStdDev = 0;
        if (numSupport > 1) {
            for (double price : supportPrices) {
                supportStdDev += Math.pow(price - support, 2);
            }
            supportStdDev = Math.sqrt(supportStdDev / (numSupport - 1));
        }
        if (numResistance > 1) {
            for (double price : resistancePrices) {
                resistanceStdDev += Math.pow(price - resistance, 2);
            }
            resistanceStdDev = Math.sqrt(resistanceStdDev / (numResistance - 1));
        }

        // Print results
//        System.out.println("Support: " + support);
//        System.out.println("Support Standard Deviation: " + supportStdDev);
//        System.out.println("Resistance: " + resistance);
//        System.out.println("Resistance Standard Deviation: " + resistanceStdDev);

        return new double[]{support, resistance};
    }

    public static double[] calculateFibonacci(ArrayList<StockObject> stockList) {

        // Determine highest and lowest prices in the stock data
        double highestPrice = Double.MIN_VALUE;
        double lowestPrice = Double.MAX_VALUE;
        for (StockObject stock : stockList) {
            if (stock.getHighest_price() > highestPrice) {
                highestPrice = stock.getHighest_price();
            }
            if (stock.getLowest_price() < lowestPrice) {
                lowestPrice = stock.getLowest_price();
            }
        }

        // Calculate the Fibonacci retracement levels
        double[] fibLevels = new double[]{0.236, 0.382, 0.5, 0.618, 0.786};
        double priceRange = highestPrice - lowestPrice;
        double[] fibValues = new double[fibLevels.length];
        for (int i = 0; i < fibLevels.length; i++) {
            fibValues[i] = highestPrice - (fibLevels[i] * priceRange);
        }

        // Determine support and resistance levels
        double support = Double.MIN_VALUE;
        double resistance = Double.MAX_VALUE;
        for (StockObject stock : stockList) {
            if (stock.getClose_price() <= support || support == Double.MIN_VALUE) {
                for (double fibValue : fibValues) {
                    if (stock.getClose_price() >= fibValue) {
                        support = fibValue;
                        break;
                    }
                }
            }
            if (stock.getClose_price() >= resistance || resistance == Double.MAX_VALUE) {
                for (double fibValue : fibValues) {
                    if (stock.getClose_price() <= fibValue) {
                        resistance = fibValue;
                        break;
                    }
                }
            }
        }

//        System.out.println("Support: " + support);
//        System.out.println("Resistance: " + resistance);
        return new double[]{support, resistance};
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

    public static void main(String[] args) throws IOException, ParseException {
        StockRequest stock_request=new StockRequest("SPY");
        stock_request.From("2023-01-09").To("2023-04-05").endPoint();
        ArrayList<StockObject> list=stock_request.build();
        findSupportAndResistanceWithStdDev(list);
    }
}
