package mycode.help;

import mycode.object.Option;
import mycode.object.StockObject;
import mycode.strategy_.BearSpread;
import mycode.strategy_.BullSpread;
import mycode.strategy_.Strategy;

import java.util.ArrayList;
import java.util.Date;

public class LinearEquation {
    private String symbol;
    private  double slope;
    private  double yIntercept;

    public LinearEquation() {

    }

    public  LinearEquation buildLinearRegression(ArrayList<StockObject> stockObjects) {
        symbol=stockObjects.get(0).getOptionsTicker();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        double n = stockObjects.size();
        for (StockObject stockObject : stockObjects) {
            double x = stockObject.getTimestamp();
            double y = stockObject.getClose_price();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double yIntercept = (sumY - slope * sumX) / n;
        System.out.println("y = " + slope + "x + " + yIntercept);
        this.slope=slope;
        this.yIntercept=yIntercept;
        return null;
    }

    public boolean isGood(Strategy strategy){
        if(strategy instanceof BullSpread  ){
            Option option =((BullSpread) strategy).sell.getOpt();
            double result=predict(new Date().getTime()+ 1000*60*60*24*option.daysToExpiration());
            if(result>option.getUnderlying_price()*1.05){
               // System.out.println("at isGood function "+true);
                return true;
            }
        //    System.out.println("at isGood function "+false);
            return false;
        }
        if(strategy instanceof BearSpread){
            Option option =((BearSpread) strategy).sell.getOpt();
            double result=predict(new Date().getTime()+ 1000*60*60*24*option.daysToExpiration());
            if(result<option.getUnderlying_price()*0.95){
                return true;
            }
            return false;
        }
        throw new RuntimeException();
    }

    public double getSlope() {
        return slope;
    }

    public double getYIntercept() {
        return yIntercept;
    }

    public double predict(long x) {
        return slope * x + yIntercept;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getyIntercept() {
        return yIntercept;
    }

    public void setyIntercept(double yIntercept) {
        this.yIntercept = yIntercept;
    }

    @Override
    public String toString() {
        return "y = " + slope + "x + " + yIntercept;
    }
}
