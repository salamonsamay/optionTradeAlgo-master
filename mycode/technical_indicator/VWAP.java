package mycode.technical_indicator;

import java.util.ArrayList;

/**
 * This Java class implements the Volume Weighted Average Price (VWAP) indicator
 * for financial markets. The VWAP is a technical analysis tool
 * that calculates the average price of a financial asset over a given time period,
 * weighted by its trading volume during that period.
 */
public class VWAP {


    public  static double vwap=0;
    public  static double lastPrice=0;

    public static double calculateVWAP(String data) {

        ArrayList<Double> volumes=extractVolumes(data);
        ArrayList<Double> prices=extractClosePrices(data);
        if (volumes.size() != prices.size()) {
            throw new IllegalArgumentException("The number of volumes and prices must be the same.");
        }

        double totalVolume = 0;
        double totalPriceVolume = 0;

        for (int i = 0; i < volumes.size(); i++) {
            totalVolume += volumes.get(i);
            totalPriceVolume += volumes.get(i) * prices.get(i);
        }
        vwap= totalPriceVolume / totalVolume;
        return totalPriceVolume / totalVolume;
    }

    public static ArrayList<Double> extractClosePrices(String input) {
        ArrayList<Double> closePrices = new ArrayList<Double>();

        String[] lines = input.split("\\r?\\n"); // split input into lines
        for (String line : lines) {
            String[] fields = line.split(" "); // split line into fields
            for (String field : fields) {
                if (field.startsWith("close=")) {
                    String closeStr = field.substring(6); // extract close value as string
                    double close = Double.parseDouble(closeStr); // convert to double
                    closePrices.add(close); // add close value to list
                }
            }
        }
        lastPrice=closePrices.get(closePrices.size()-1);

        return closePrices;
    }

    public static ArrayList<Double> extractVolumes(String input) {
        ArrayList<Double> volumes = new ArrayList<Double>();

        String[] lines = input.split("\n");
        for (String line : lines) {
            String[] parts = line.split(" ");
            for (String part : parts) {
                if (part.startsWith("volume=")) {
                    double volume = Double.parseDouble(part.substring(7));
                    volumes.add(volume);
                }
            }
        }

        return volumes;
    }
    public  static  void print(){
        System.out.println("VWAP : "+vwap +" last price : "+lastPrice);
    }

    public static void main(String[] args) {

    }

}


