package mycode.trade;

import mycode.data.StockRequest;
import mycode.my_sql.MySQL;
import mycode.object.AggregatesObject;
import mycode.object.Pair;
import mycode.object.StockObject;

import java.util.*;
import java.util.stream.Collectors;

public class BackTest {





    public static List<List<Double>> calculateMonthlyFibonacciLevels(ArrayList<ArrayList<AggregatesObject>> data) {
        List<List<Double>> monthlySupportResistanceLevels = new ArrayList<>();
        int daysPerMonth = 20; // Assuming an average of 20 trading days per month

        for (int monthIndex = 0; monthIndex < data.size(); monthIndex += daysPerMonth) {
            ArrayList<AggregatesObject> monthlyData = new ArrayList<>();
            for (int dayIndex = monthIndex; dayIndex < monthIndex + daysPerMonth && dayIndex < data.size(); dayIndex++) {
                monthlyData.addAll(data.get(dayIndex));
            }

            List<Double> levels = calculateFibonacciLevels(monthlyData);

            monthlySupportResistanceLevels.add(levels);
        }

        return monthlySupportResistanceLevels;
    }

    public static List<Double> calculateFibonacciLevels(ArrayList<AggregatesObject> dayData) {
        List<Double> levels = new ArrayList<>();

        double high = dayData.stream().mapToDouble(AggregatesObject::getHighest_price).max().getAsDouble();
        double low = dayData.stream().mapToDouble(AggregatesObject::getLowest_price).min().getAsDouble();
        double priceRange = high - low;

        double fib23_6 = low + 0.236 * priceRange;
        double fib38_2 = low + 0.382 * priceRange;
        double fib50 = low + 0.5 * priceRange;
        double fib61_8 = low + 0.618 * priceRange;
        double fib78_6 = low + 0.786 * priceRange;

        levels.add(fib23_6);
        levels.add(fib38_2);
        levels.add(fib50);
        levels.add(fib61_8);
        levels.add(fib78_6);

        return levels;
    }


    /**
     *
     * @param ticker
     * @return array list inside array list each sub list represents a day
     */
    public static ArrayList<ArrayList<AggregatesObject>> getList(String ticker){
        ArrayList<AggregatesObject>list= (ArrayList<AggregatesObject>) MySQL.selectStar(ticker);
        ArrayList<AggregatesObject>filterList= (ArrayList<AggregatesObject>) list.stream().filter(agg -> agg.getTimestamp()%(1000*60*60*24)>=48600000
                && agg.getTimestamp()%(1000*60*60*24)<=72000000).collect(Collectors.toList());

        ArrayList<ArrayList<AggregatesObject>> mainList= new ArrayList<>();

        for(int i=0; i<filterList.size(); i++){//insert the elements to 2 dimensional arrays
            AggregatesObject agg=filterList.get(i);
            if(agg.getTimestamp()%(1000*60*60*24)==48600000){
                ArrayList<AggregatesObject> subList= new ArrayList<>();
                for(int j=i;agg.getTimestamp()%(1000*60*60*24)<72000000;j++){
//                    System.out.println(new Date(agg.getTimestamp()));
                    agg=filterList.get(j);
                    subList.add(agg);
                }
                mainList.add(subList);
            }
        }
        return mainList;
    }

    public static  void dayliVwap(String symbol){
        ArrayList<ArrayList<AggregatesObject>> mainList=getList(symbol) ;
        double failed=0;
        double success=0;
        for(int i=0;i<mainList.size();i++){//main list
            double sumVwap=0;
            double sumOpenPrice=0;
            double index=0;
            ArrayList<AggregatesObject> subList=mainList.get(i);
            for(int j=0;j< subList.size();j++){//sub list
                AggregatesObject element=subList.get(j);


                if(index++<20 || index>350){continue;}//if is after 20 minutes from beginning and after 40 minutes before the end
                sumVwap+=subList.get(j-1).getVwap()+subList.get(j-2).getVwap()+subList.get(j-3).getVwap()+
                        subList.get(j-4).getVwap()+subList.get(j-5).getVwap()+subList.get(j-6).getVwap()+
                        subList.get(j-7).getVwap()+subList.get(j-8).getVwap();

                sumOpenPrice+=subList.get(j-1).getOpen_price()+subList.get(j-2).getOpen_price()+subList.get(j-3).getOpen_price()+
                        subList.get(j-4).getOpen_price()+subList.get(j-5).getOpen_price()+subList.get(j-6).getOpen_price()+
                        subList.get(j-7).getOpen_price()+subList.get(j-8).getOpen_price();
                double avgVwap=sumVwap/8;
                double avgOpenPrice=sumOpenPrice/8;

                if(avgVwap*1.001<avgOpenPrice){//need to down
                    System.out.println("need to down");
                    System.out.println(element);
                    for(int k=j;k<=j+30;k=k+5){
                        System.out.println(new Date(subList.get(k).getTimestamp())+"   :  "+subList.get(k).getOpen_price());
                    }
                    if(subList.get(j).getOpen_price()>subList.get(j+15).getOpen_price()){
                        success++;
                    }
                    else {failed++;}

                    System.out.println();
//                    break;
                }
                if(avgVwap*0.999>avgOpenPrice) {//need to up
                    System.out.println("need to up");
                    System.out.println(element);
                    for(int k=j;k<=j+30;k=k+5){
                        System.out.println(new Date(subList.get(k).getTimestamp())+"   :  "+subList.get(k).getOpen_price());
                    }
                    if(subList.get(j).getOpen_price()<subList.get(j+15).getOpen_price()){
                        success++;
                    }
                    else {failed++;}

                    System.out.println();
//                    break;
                }
            }
        }


        System.out.println("success : "+success);
        System.out.println("failed : "+failed);
        System.out.println("success rates : "+success/(success+failed)*100);
    }

    public static void avg(String symbol){
        double numOfLastMinute=15;
        ArrayList<ArrayList<AggregatesObject>> mainList=getList(symbol) ;
        double failed=0;
        double success=0;
        for(int i=0;i<mainList.size();i++){//main list

            double index=0;
            ArrayList<AggregatesObject> subList=mainList.get(i);
            for(int j=0;j< subList.size();j++){//sub list

                AggregatesObject element=subList.get(j);
                double sumOpenPrice=0;
                if(j<numOfLastMinute || j>350){continue;}
                for(int k=0;k<numOfLastMinute;k++){
                    sumOpenPrice+=subList.get(j-k).getOpen_price();

                }

                double avgOpenPrice=sumOpenPrice/numOfLastMinute;


                if(element.getOpen_price()*1.01<avgOpenPrice){//need to up
                    System.out.println("need to up");
                    System.out.println(element);
                    for(int k=j;k<=j+15;k=k+5){
                        System.out.println(new Date(subList.get(k).getTimestamp())+"   :  "+subList.get(k).getOpen_price());
                    }
                    if(subList.get(j).getOpen_price()<subList.get(subList.size()-1).getOpen_price()){
                        success++;
                    }
                    else {failed++;}

                    System.out.println();
                    break;
                }
                if(element.getOpen_price()*0.99>avgOpenPrice) {//need to down

                    System.out.println("need to down");
                    System.out.println(element);
                    for(int k=j;k<=j+15;k=k+5){
                        System.out.println(new Date(subList.get(k).getTimestamp())+"   :  "+subList.get(k).getOpen_price());
                    }
                    if(subList.get(j).getOpen_price()>subList.get(subList.size()-1).getOpen_price()){
                        success++;
                    }
                    else {failed++;}

                    System.out.println();
                    break;
                }
            }
        }


        System.out.println("success : "+success);
        System.out.println("failed : "+failed);
        System.out.println("success rates : "+success/(success+failed)*100);
    }

    public  static  void M15(String ticker,int minute) {
        ArrayList<ArrayList<AggregatesObject>> mainList=getList(ticker) ;
        double failed=0;
        double success=0;
        double natural=0;//not get inside a position

        for(int i=0;i<mainList.size();i++){//main list
            double max=0;
            double min=10000;
            ArrayList<AggregatesObject> subList=mainList.get(i);
            for(int j=0;j<minute;j++){//choose the minimum and maximum point
                AggregatesObject agg=subList.get(j);
                if(agg.getHighest_price()>max){
                    max=agg.getHighest_price();
                }
                if(agg.getLowest_price()<min){
                    min=agg.getLowest_price();
                }
            }
            if(min/max<0.8){
                natural++;
                continue;
            }

            for(int j=minute;j<minute*2;j++){
                AggregatesObject agg=subList.get(j);
                if(agg.getLowest_price()<min){
                    System.out.println("time to buy at "+agg.getLowest_price());
                    for(int k=minute*2;k<subList.size();k++){
                        if(subList.get(k).getHighest_price()>max){
                            System.out.println("time to sell back at "+subList.get(k).getHighest_price());
                            System.out.println();
                            success++;
                            break;
                        }
                        if(k==subList.size()-1){
                            if(subList.get(k).getHighest_price()>min){
                                natural++;
                            }
                           else failed++;
                        }
                    }
                    break;
                }
                else if(agg.getHighest_price()>max){
                    System.out.println("time to sell at "+agg.getHighest_price());
                    for(int k=minute*2;k<subList.size();k++){
                        if(subList.get(k).getLowest_price()<min){
                            System.out.println("time to buy  back at "+subList.get(k).getLowest_price());
                            System.out.println();
                            success++;
                            break;
                        }
                        if(k==subList.size()-1){
                            if(subList.get(k).getLowest_price()<max){
                                natural++;
                            }
                            else failed++;
                        }
                    }
                    break;
                }
                if(j==minute*2-1){
                    natural++;
                }

            }
            System.out.println();

        }
        System.out.println("Summary");
        System.out.println("success "+success);
        System.out.println("failed "+failed);
        System.out.println("Success rate is " +success/ (success+failed)*100);
        System.out.println(natural);
    }

    public static void main(String[] args) {
     //   M15("nflx",5);

        double price=0;
        double counter=0;
      ArrayList<ArrayList<AggregatesObject>> mainList=getList("pypl");
      for(ArrayList<AggregatesObject> subList:mainList){
          price+=subList.get(0).getClose_price();
          counter++;

      }
        System.out.println(price/counter);
    }
}
