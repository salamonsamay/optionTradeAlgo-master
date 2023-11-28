package mycode.trade;

import mycode.data.StockRequest;
import mycode.help.LinearEquation;
import mycode.help.Tools;
import mycode.object.StockObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
public class Test {


    static String symbol="NVDA";


    public static void test(StockRequest request) throws IOException, ParseException, InterruptedException {

        int success=0;
        int failed=0;
        LocalDateTime dateTime = LocalDateTime.of(2023, 4, 21, 16, 30, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 4, 21, 23, 00, 0);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTime2 = dateTime2.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Instant instant2 = zonedDateTime2.toInstant();
        long timestamp = instant.toEpochMilli();
        long timestamp2 = instant2.toEpochMilli();

        ArrayList<StockObject> list=request.From(timestamp+"").To(timestamp2+"").endPoint().build();
        int counter=0;
        while (counter++<100) {
            System.out.println();
            long t=1000*60*60*24;
            System.out.println(new Date(timestamp-(t*(counter-1))));
            boolean succes_=false;
            System.out.println("couunter is "+counter);
            double max = list.get(0).getHighest_price();
            double min = list.get(0).getLowest_price();

            for (int i = 0; i < 15; i++) {
                if (list.get(i).getLowest_price() < min) {
                    min = list.get(i).getLowest_price();
                }
                if (list.get(i).getHighest_price() > max) {
                    max = list.get(i).getHighest_price();
                }
            }


            if (max - min <= list.get(0).getHighest_price() * 0.002) {// check  the range
                list=new StockRequest(symbol).From(timestamp-t+"").To(timestamp2-t+"").endPoint().build();
//                Thread.sleep(1000*30);
                continue;
            }
            boolean needTOup = false;
            boolean neetTOdown = false;

            double shortPoint = 0;
            double longPoint = 0;
            for (int i = 15; i < 30; i++) {
                if (list.get(i).getHighest_price() > max) {
                    shortPoint = list.get(i).getHighest_price();
                    neetTOdown = true;
                    System.out.println("short at "+list.get(i).getHighest_price());
                    break;
                } else if (list.get(i).getLowest_price() < min) {
                    longPoint = list.get(i).getLowest_price();
                    needTOup = true;
                    System.out.println("long at "+list.get(i).getLowest_price());
                    break;
                }
            }

            for (int i = 30; i < list.size(); i++) {
                if (shortPoint != 0) {//need to down
                    if (list.get(i).getLowest_price() < min) {
                        success++;
                        System.out.println("the value buy back at " + list.get(i).getLowest_price());
                        succes_=true;
                        break;
//
                    }
                }
                if (longPoint != 0) {//need to up
                    if (list.get(i).getHighest_price() > max) {
                        success++;
                        System.out.println("the value sell  bake at " + list.get(i).getHighest_price());
                        succes_=true;
                        break;
                    }
                }
            }

            if ((neetTOdown || needTOup) &&  !succes_) {
                failed++;
                System.out.println("the funcdtion failed");
//                System.out.println(list.get(list.size() - 1).getLowest_price());
            }
            boolean flag=true;
            while (flag){
                try {
                    list=new StockRequest(symbol).From(timestamp-t*counter+"").To(timestamp2-t*counter+"").endPoint().build();
                    flag=false;
//                    Thread.sleep(1000*30);
                }catch (NullPointerException e){
                    counter++;
                }
            }


        }
        System.out.println("the succus is "+success);
        System.out.println("the faield is "+failed);


    }



    public  static boolean timeToDo(String symbol,int day) throws IOException, ParseException {
        LocalDateTime dateTime = LocalDateTime.of(2023, 4, day, 16, 30, 0);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        long timestamp = instant.toEpochMilli();

        StockRequest stock_request=new StockRequest(symbol);
        Long t=timestamp;//17-4-23
        Long t2=t+(long) (1000*60*60*6.5);

        stock_request.From(t+"").To(t2+"").endPoint();
        ArrayList<StockObject> stockObject=stock_request.build();
        double max=stockObject.get(0).getHighest_price();
        double min=stockObject.get(0).getLowest_price();

        for(int i=0;i<15;i++){
            if(stockObject.get(i).getLowest_price()<min ){
                min=stockObject.get(i).getHighest_price();
            }
            if(stockObject.get(i).getHighest_price()>max ){
                max=stockObject.get(i).getHighest_price();
            }
        }
        if(max-min<=stockObject.get(0).getHighest_price()*0.002){
            System.out.println("trade "+symbol+" in the next day");
            return false;
        }
        boolean needTOup=false;
        boolean neetTOdown=false;

        double shortPoint=0;
        double longPoint=0;
        for(int i=15;i<30;i++) {
            if(stockObject.get(i).getHighest_price()>max){
                System.out.println("time to trade "+symbol);
                return true;
            }
            else if(stockObject.get(i).getLowest_price()<min){
                System.out.println("time to trade "+symbol);
                return true;
            }
        }

        return false;
    }


    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
//        LocalDateTime dateTime = LocalDateTime.of(2023, 4, 20, 16, 30, 0);
//        LocalDateTime dateTime2 = LocalDateTime.of(2023, 4, 20, 23, 00, 0);
//        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
//        ZonedDateTime zonedDateTime2 = dateTime2.atZone(ZoneId.systemDefault());
//        Instant instant = zonedDateTime.toInstant();
//        Instant instant2 = zonedDateTime2.toInstant();
//        long timestamp = instant.toEpochMilli();
//        long timestamp2 = instant2.toEpochMilli();
        StockRequest stockRequest=new StockRequest(symbol);
             test(stockRequest);



//        System.out.println(timeToDo("AAPL",day));
//      //  System.out.println(timeToDo("META",day));
//        System.out.println(timeToDo("GOOG",day));
//        System.out.println(timeToDo("AMZN",day));
//        System.out.println(timeToDo("SPY",day));
//        System.out.println(timeToDo("QQQ",day));
//        Thread.sleep(20000);
//
//        LocalDateTime dateTime = LocalDateTime.of(2023, 4, day, 16, 30, 0);
//        LocalDateTime dateTime2 = LocalDateTime.of(2023, 4, day, 23, 00, 0);
//        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
//        ZonedDateTime zonedDateTime2 = dateTime2.atZone(ZoneId.systemDefault());
//        Instant instant = zonedDateTime.toInstant();
//        Instant instant2 = zonedDateTime2.toInstant();
//        long timestamp = instant.toEpochMilli();
//        long timestamp2 = instant2.toEpochMilli();
//
//        StockRequest stock_request=new StockRequest("TSLA");
//        Long t=timestamp;//17-4-23
//        Long t2=timestamp2;
//
//        stock_request.From(t+"").To(t2+"").EndPoint();
//        ArrayList<StockObject> stockObject=stock_request.build();
//
//        double max=stockObject.get(0).getHighest_price();
//        double min=stockObject.get(0).getLowest_price();
//
//        for(int i=0;i<15;i++){
//            if(stockObject.get(i).getLowest_price()<min ){
//                min=stockObject.get(i).getLowest_price();
//            }
//            if(stockObject.get(i).getHighest_price()>max ){
//                max=stockObject.get(i).getHighest_price();
//            }
//        }
//        System.out.println("max point "+max);
//        System.out.println("min point "+min);
//        if(max-min<=stockObject.get(0).getHighest_price()*0.002){return;}
//        boolean needTOup=false;
//        boolean neetTOdown=false;
//
//        double shortPoint=0;
//        double longPoint=0;
//        for(int i=15;i<30;i++) {
//            if(stockObject.get(i).getHighest_price()>max){
//                shortPoint=stockObject.get(i).getHighest_price();
//                neetTOdown=true;
//                System.out.println("short at "+stockObject.get(i).getHighest_price());
//                break;
//            }
//            else if(stockObject.get(i).getLowest_price()<min){
//                longPoint=stockObject.get(i).getLowest_price();
//                needTOup=true;
//                System.out.println("long at "+stockObject.get(i).getLowest_price());
//                break;
//            }
//        }
//
//        for(int i=30;i<stockObject.size();i++ ){
//            if(shortPoint!=0){//need to down
//                if(stockObject.get(i).getLowest_price()<min){
//                    System.out.println("the value buy back at "+stockObject.get(i).getLowest_price());
//                    return;
//                }
//            }
//            if(longPoint!=0){//need to up
//                if(stockObject.get(i).getHighest_price()>max){
//                    System.out.println("the value sell  bake at "+stockObject.get(i).getHighest_price());
//                    return;
//                }
//            }
//        }
//        if((neetTOdown || needTOup)){
//            System.out.println("the funcdtion failed");
//            System.out.println(stockObject.get(stockObject.size()-1).getLowest_price());
//        }


    }

}
