package mycode.trade;

import mycode.data.StockRequest;
import mycode.help.LinearEquation;
import mycode.help.Tools;
import mycode.object.StockObject;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class BackTest3 {

    public static void M15(StockObject[][] list, int start, int middle, int end) {
        double balance = 1000;
        double success = 0;
        double failed = 0;
        for (int i = 0; i < list.length; i++) {
            double max_point = list[i][start].getHighest_price();
            double min_point = list[i][start].getLowest_price();
            for (int j = start; j < middle; j++) {//find the min/max point
                if (max_point < list[i][j].getHighest_price()) {
                    max_point = list[i][j].getHighest_price();
                }
                if (min_point > list[i][j].getLowest_price()) {
                    min_point = list[i][j].getLowest_price();
                }
            }
            if (max_point - min_point <0.4) {
                continue;
            }
            double buy_point = 0;
            double sell_point = 0;
            for (int j = middle; j < end; j++) {//get into position
                if (list[i][j].getLowest_price() < min_point) {//get into long position
                    buy_point = list[i][j].getLowest_price();
                    break;
                }
                if (list[i][j].getHighest_price() > max_point) {//get into short position
                    sell_point = list[i][j].getHighest_price();
                    break;
                }
            }

            if (sell_point == 0 && buy_point == 0) {
                System.out.println("not time to buy or sell ");
                continue;
            }

            for (int j = end; j < list[i].length; j++) {//chek if the trade success or failed
                if (buy_point != 0) {
                    if (list[i][j].getHighest_price() > max_point) {
                        success++;
                        balance += (max_point - buy_point) * 100 - 4;
                        break;
                    }
                } else if (sell_point != 0) {
                    if (list[i][j].getLowest_price() < min_point) {
                        success++;
                        balance += (sell_point - min_point) * 100 - 4;
                        break;
                    }
                }
                if (j == list[i].length - 1) {
                    failed++;
                    if (buy_point != 0) {
                        balance -= (buy_point - list[i][j].getClose_price()) * 100 - 4;
                    } else if (sell_point != 0) {
                        balance -= (list[i][j].getClose_price() - sell_point) * 100 - 4;
                    }
                }
            }
        }
        System.out.println("the balance is " + balance);
        System.out.println("the success is " + success);
        System.out.println("the failed is " + failed);
        System.out.println("presentge " + (100 * (success / (success + failed)) + "%"));
    }

    public static void daylyAverage(StockObject[][] list,int numOfDay) {
        double succsess = 0;
        double failed = 0;
        int time = 10;
        for (int i = numOfDay; i < list.length; i++) {
            double yesterday_open_average = 0;
            double yesterday_close_average = 0;
            for(int k=numOfDay;k>0;k--){
                for (int j = 0; j < time; j++) {
                    yesterday_open_average += list[i - k][j].getClose_price();
                }
                yesterday_open_average /= (time);

                for (int j = 390 - time; j < 390; j++) {
                    yesterday_close_average += list[i - k][j].getClose_price();
                }
                yesterday_close_average /= (time);
            }

            double all_average = (yesterday_close_average + yesterday_open_average) / 2;
            //  double all_average=yesterday_open_average;

            double today_open = list[i][0].getClose_price();
            double today_close = list[i][390 - 1].getClose_price();

            if (today_open > all_average) {
                if (today_open > today_close) {
                    succsess++;
                } else {
                    failed++;
                }
            }
            if (today_open < all_average) {
                if (today_open < today_close) {
                    succsess++;
                } else {
                    failed++;
                }
            }

        }
        System.out.println("success " + succsess);
        System.out.println("failed " + failed);
        System.out.println("precent is " + succsess / (succsess + failed) * 100);

    }

    public static void d(StockObject[][] list) {
        double succsess = 0;
        double failed = 0;
        double time = 15;
        for (int i = 1; i < list.length; i++) {
            double average = 0;
            for (int j = 0; j < time; j++) {
                average += list[i - 1][j].getClose_price();
            }
            average /= time;


            double today_open_price = list[i][0].getClose_price();
            double today_close_price = list[i][390 - 1].getClose_price();

            if (average > today_open_price) {
                if (Math.floor(average) > today_close_price) {
                    for (int j = 0; j < 15; j++) {
                        if (Math.floor(average) < list[i][j].getLowest_price()) {
                            succsess++;
                            System.out.println("average day befor " + average);
                            System.out.println("open price is " + today_open_price);
                            System.out.println("close price is " + today_close_price);
                            System.out.println("time to bear call spread in strike " + Math.floor(average));
                            System.out.println();
                            break;
                        }
                    }
                } else {
                    failed++;
                }
            }
            if (average < today_open_price) {
                if (Math.ceil(average) < today_close_price) {
                    for (int j = 0; j < 15; j++) {
                        if (Math.ceil(average) > list[i][j].getHighest_price()) {
                            succsess++;
                            System.out.println("average day befor " + average);
                            System.out.println("open price is " + today_open_price);
                            System.out.println("close price is " + today_close_price);
                            System.out.println("time to bull put spread in strike " + Math.ceil(average));
                            System.out.println();
                            break;
                        }
                    }

                } else {
                    failed++;
                }
            }

        }
        System.out.println("succses " + succsess);
        System.out.println("failed " + failed);
        System.out.println("the success present is " + Math.round(succsess / (succsess + failed) * 100) + "%");
    }

    public static double getStockPrice(String symbol) throws IOException {
        String apiUrl = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject chart = jsonResponse.getJSONObject("chart");
            JSONObject result = chart.getJSONArray("result").getJSONObject(0);
            JSONObject meta = result.getJSONObject("meta");
            double price = meta.getDouble("regularMarketPrice");
            return price;
        } else {
            throw new IOException("Request failed with response code: " + responseCode);
        }
    }


    public  static void s(StockObject[][] list,int numOfDays) {
        double success = 0;
        double failed = 0;
        StockObject arr[]=new StockObject[list.length*390];
        int index=0;
        for(int i=0;i<list.length;i++){
            for(int j=0;j<list[i].length;j++){
                arr[index++]=list[i][j];
            }
        }

        for (int i = numOfDays; i < list.length-numOfDays; i++) {
            double[] support_and_resist;
            double support = 0;
            double resist = 0;
            for (int k = numOfDays; k > 0; k--) {
                support_and_resist = supportandresist(list[i - k]);
                support += support_and_resist[0];
                resist += support_and_resist[1];
            }
            support/=numOfDays;
            resist/=numOfDays;

            for(int j=0;j<list[i].length;j++){
                if(list[i][j].getClose_price()<support){
                    boolean flag=false;
                    for(int k=i*list[i].length+j;k<(i*list[i].length+j)+390*numOfDays;k++){
                        if(arr[k].getClose_price()>resist){
                            success++;
                            flag=true;
                            break;
                        }
                    }
                    System.out.println();
                    if(!flag){
                        failed++;
                    }
                }
                else if(list[i][j].getClose_price()>resist){
                    boolean flag=false;
                    for(int k=i*list[i].length+j;k<(i*list[i].length+j)+390*numOfDays;k++){
                        if(arr[k].getClose_price()<support){
                            success++;
                            flag=true;
                            break;
                        }
                    }
                    System.out.println();
                    if(!flag){
                        failed++;
                    }
                }
            }

        }
        System.out.println("success "+success);
        System.out.println("failed "+failed);
        System.out.println("percent "+success/(success+failed)*100);
    }


    public static  void supportandresistAVG(StockObject[][] list,int numOfDays){
        double success=0;
        double failed=0;
        for(int i=numOfDays;i<list.length;i++){
            double[] support_and_resist;
            double support=0;
            double resist=0;
            for(int k=numOfDays;k>0;k--) {
                support_and_resist = supportandresist(list[i - k]);
                support=support_and_resist[0];
                resist=support_and_resist[1];
            }
            support/=numOfDays;
            resist/=numOfDays;


            double open_price=list[i][0].getOpen_price();
            double close_price=list[i][390-1].getOpen_price();
            for(int j=0;j<30;j++){
                if(list[i][j].getClose_price()<support){//time to buy
                    if(close_price>list[i][j].getClose_price()){
                        success++;
                    }
                    else {
                        failed++;
                    }
                    break;
                }
                if(list[i][j].getClose_price()>resist){//time to sell
                    if(close_price<list[i][j].getClose_price()){
                        success++;
                    }
                    else {
                        failed++;
                    }
                    break;
                }
            }

        }
        System.out.println("success "+success);
        System.out.println("failed "+failed);
        System.out.println("percent "+success/(success+failed)*100);
    }
    private static double[] supportandresist(StockObject[] list) {

        double highestPrice = Double.MIN_VALUE;
        double lowestPrice = Double.MAX_VALUE;
        for (int i=0;i<list.length;i++) {
            if (list[i].getHighest_price() > highestPrice) {
                highestPrice = list[i].getHighest_price();
            }
            if (list[i].getLowest_price() < lowestPrice) {
                lowestPrice = list[i].getLowest_price();
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
        for(int i=0;i<list.length;i++ ) {
            if (list[i].getClose_price() <= support || support == Double.MIN_VALUE) {
                for (double fibValue : fibValues) {
                    if (list[i].getClose_price() >= fibValue) {
                        support = fibValue;
                        break;
                    }
                }
            }
            if (list[i].getClose_price() >= resistance || resistance == Double.MAX_VALUE) {
                for (double fibValue : fibValues) {
                    if (list[i].getClose_price() <= fibValue) {
                        resistance = fibValue;
                        break;
                    }
                }
            }
        }

        return new double[]{support, resistance};
    }






    public static void main(String[] args) throws IOException, ParseException {
        Hashtable<String,Double> table=new Hashtable<>();
        ArrayList<String> symbolList = Tools.readCompanyFromFile();

        for(String s:symbolList) {

            String symbol =s;
            StockRequest request = new StockRequest(symbol);
            ArrayList<StockObject> stockObjects = request.From("2023-05-10").To("2023-05-22").endPoint().build();


            double support_and_resist[] = StockObject.calculateFibonacci(stockObjects);
            double support = support_and_resist[0];
            double resist = support_and_resist[1];
            double price = getStockPrice(symbol);
            //     double price = stockObjects.get(stockObjects.size()-1).getClose_price();
            //  System.out.println(new Date(stockObjects.get(stockObjects.size()/3).getTimestamp()));
//            System.out.println("the support on " + symbol + " is " + support);
//            System.out.println("the resist on " + symbol + " is " + resist);
//            System.out.println("the price on " + symbol + " is " + price);
            if (price > resist * 1.05) {
                System.out.println("the support on " + symbol + " is " + support);
                System.out.println("the resist on " + symbol + " is " + resist);
                System.out.println("the price on " + symbol + " is " + price);
                System.out.println("time to sell");
                table.put(symbol, price - resist);
                System.out.println();
                // table.put(symbol,support-resist);
            }
            if (price < support * 0.95) {
                System.out.println("the support on " + symbol + " is " + support);
                System.out.println("the resist on " + symbol + " is " + resist);
                System.out.println("the price on " + symbol + " is " + price);
                System.out.println("time to buy");
                System.out.println();
                table.put(symbol, support - price);
                //   table.put(symbol,support-resist);
            }
        }
        //       s(StockObject.filter_(stockObjects),15);
        //  supportandresistAVG(StockObject.filter_(stockObjects),2);
        //       supportandresist(StockObject.filter_(stockObjects)[0]);
//        daylyAverage(StockObject.filter_(stockObjects),1);
//        M15(StockObject.filter_(stockObjects),0,15,30);





        List<Map.Entry<String, Double>> entryList = new ArrayList<>(table.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        // Print the sorted entries
        for (Map.Entry<String, Double> entry : entryList) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }








//        StockObject[][] list=StockObject.filter_(stockObjects);
//
//        M15(list,0,15,30);

    }
}
