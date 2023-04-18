package mycode.help;

import mycode.trade.Program;

import java.util.ArrayList;

public class LinearRegression {

    public static  void linearRegression(String symbol){


    }
    public static void performLinearRegression(String data) {
        // Extract date and close values
        ArrayList<Double> dates = new ArrayList<Double>();
        ArrayList<Double> closes = new ArrayList<Double>();
        String[] lines = data.split("\\r?\\n");
        for (String line : lines) {
            String[] fields = line.split("\\s+");
            for (String field : fields) {
                String[] parts = field.split("=");
                if (parts[0].equals("date")) {
                    dates.add(Double.parseDouble(parts[1]));
                } else if (parts[0].equals("close")) {
                    closes.add(Double.parseDouble(parts[1]));
                }
            }
        }

        // Perform linear regression
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = dates.size();
        for (int i = 0; i < n; i++) {
            sumX += dates.get(i);
            sumY += closes.get(i);
            sumXY += dates.get(i) * closes.get(i);
            sumXX += dates.get(i) * dates.get(i);
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        double b = (sumXY - n * meanX * meanY) / (sumXX - n * meanX * meanX);
        double a = meanY - b * meanX;

        System.out.println("Linear regression: y = " + b + "x + " + a);
    }

    public static void main(String[] args) {
        linearRegression("SPY");
    }
}
