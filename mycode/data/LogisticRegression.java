package mycode.data;

import mycode.data.StockRequest;
import mycode.help.LinearRegression;
import mycode.object.StockObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogisticRegression {

    private double[] coefficients;
    private double learningRate;
    private int maxIterations;

    public LogisticRegression(int numFeatures, double learningRate, int maxIterations) {
        this.coefficients = new double[numFeatures + 1];
        this.learningRate = learningRate;
        this.maxIterations = maxIterations;
    }

    public void train(ArrayList<StockObject> stocks, int[] y) {
        int n = stocks.size();
        int m = 2; // 2 features: timestamp and price

        double[][] X_new = new double[n][m + 1];
        for (int i = 0; i < n; i++) {
            X_new[i][0] = 1;
            X_new[i][1] = stocks.get(i).getTimestamp();
            X_new[i][2] = stocks.get(i).getClose_price();
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            double[] grad = new double[m + 1];
            for (int i = 0; i < n; i++) {
                double[] x_i = X_new[i];
                double z = dot(x_i, coefficients);
                double h = sigmoid(z);
                int y_i = y[i];
                grad[0] += (h - y_i);
                for (int j = 0; j < m; j++) {
                    grad[j + 1] += (h - y_i) * x_i[j + 1];
                }
            }
            for (int j = 0; j < m + 1; j++) {
                coefficients[j] -= learningRate * grad[j];
            }
        }
    }

    public double predict(StockObject stock) {
        double[] x = {1, stock.getTimestamp(), stock.getClose_price()};
        double z = dot(x, coefficients);
        double h = sigmoid(z);
        return h >= 0.5 ? 1 : 0;
    }

    public double[] predict(ArrayList<StockObject> stocks) {
        int n = stocks.size();
        double[] predictions = new double[n];
        for (int i = 0; i < n; i++) {
            predictions[i] = predict(stocks.get(i));
        }
        return predictions;
    }

    public double accuracy(ArrayList<StockObject> stocks, int[] y) {
        double[] predictions = predict(stocks);
        int n = y.length;
        int correct = 0;
        for (int i = 0; i < n; i++) {
            if (predictions[i] == y[i]) {
                correct++;
            }
        }
        return (double) correct / n;
    }

    private static double sigmoid(double z) {
        return (1.0 / (1.0 + Math.exp(-z)));
    }

    private static double dot(double[] a, double[] b) {
        double result = 0;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    public static void main(String[] args) throws IOException, ParseException {
        StockRequest request=new StockRequest("AAPL");
        ArrayList<StockObject> stockObject=request.From("2021-10-01").To("2023-04-01").endPoint().build();
        int y[]=new int[stockObject.size()];
        y[0]=1;
        for(int i=1;i<stockObject.size();i++) {
            StockObject current=stockObject.get(i);
            StockObject befor=stockObject.get(i-1);
            if(current.getClose_price()-befor.getClose_price()<0){

                y[i]=0;
            }
            else {
                y[i]=1;
            }
        }

        LogisticRegression logisticRegression = new LogisticRegression(2, 0.01, 1000);
        logisticRegression.train(stockObject, y);

        double accuracy = logisticRegression.accuracy(stockObject, y);
        System.out.println("Accuracy: " + accuracy);
        StockObject  stockObject1=new StockObject("AAPL",new Date().getTime(),1000);
        System.out.println(logisticRegression.predict(stockObject1));


    }
}