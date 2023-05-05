package mycode.trade;

import mycode.data.StockRequest;
import mycode.object.Pair;
import mycode.object.StockObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;

public class BackTest {

    private  long start;
    private  long end;
    private String symbol;
    private ArrayList<StockObject> list;


    public static Hashtable<String,String> hashtable=new Hashtable<>();

    static  int thread_number=0;


    public  BackTest(int year,int month,int day){
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 16, 30, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(year, month, day, 23, 00, 0);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTime2 = dateTime2.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Instant instant2 = zonedDateTime2.toInstant();
        start = instant.toEpochMilli();
        end = instant2.toEpochMilli();
    }

    public  void build(String symbol) {
        this.symbol=symbol;

        try {
            list=new StockRequest(symbol).From(start+"").To(end+"").endPoint().build();
        } catch (IOException e) {
            try {
                list=new StockRequest(symbol).From((start- 1000*60*60*24)+"").To((end- 1000*60*60*24)+"").endPoint().build();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        } catch (ParseException e) {
            try {
                list=new StockRequest(symbol).From((start- 1000*60*60*24)+"").To((end- 1000*60*60*24)+"").endPoint().build();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }

        }


    }

    public int  M15(int first,int second,int therd){
        double max=list.get(first).getLowest_price();
        double min=list.get(first).getLowest_price();

        for(int i=first+1;i<second;i++){
            if(list.get(i).getHighest_price()>max){
                max=list.get(i).getHighest_price();
            }
            if(list.get(i).getLowest_price()<min){
                min=list.get(i).getClose_price();
            }
        }
        if(max-min<list.get(first).getHighest_price()*0.002 || max-min<1){
            System.out.println("try next day");
            return 0;
        }

        double long_position=0;
        double short_position=0;
        for(int i=second;i<therd;i++){//define the long/short point
            if(list.get(i).getLowest_price()<min ){
                long_position=list.get(i).getLowest_price();
                System.out.println("long position at "+long_position);
                break;
            }
            if(list.get(i).getHighest_price()>max){
                short_position=list.get(i).getClose_price();
                System.out.println("short position at "+short_position);
                break;
            }
        }
        if(long_position==0 && short_position ==0){
            System.out.println("need to try next day");
            return 0;
        }//check if have opportunity to buy or sell

        for(int i=therd;i<list.size();i++){
            if(long_position!=0){
                if(list.get(i).getHighest_price()>max){
                    System.out.println("sell back at "+list.get(i).getHighest_price());
                    return 1;
                }
            }
            if(short_position!=0){
                if(list.get(i).getLowest_price()<min){
                    System.out.println("buy back at "+list.get(i).getLowest_price());
                    return 1;
                }
            }
        }
        System.out.println("the function field");
        return -1;

    }

    public    double simulation() throws IOException, ParseException {
        double success=0;
        double faild=0;
        for(int i=0;i<30;i++) {
            System.out.println("current data "+new Date(start));
            setStart(start-1000*60*60*24);
            setEnd(end-1000*60*60*24);
            try {
                build(symbol);
                int result=M15(0,15,30);

                if(result==1){
                    success++;
                }
                else if(result==-1){
                    faild++;
                }
            }
            catch (NullPointerException e){
                System.out.println(e);
            }


        }
        DecimalFormat df = new DecimalFormat("#.##");
        hashtable.put(symbol,"["+(int)(success)+","+(int)(faild)+"] "+df.format((success/(success+faild))*100)+"%");

        System.out.println("succes "+success);
        System.out.println("faield "+faild);
        System.out.println(symbol+" "+(success/(success+faild))*100 +"%");
        return(success/success+faild)*100;
    }

    public void setStart(long start)  {
        this.start = start;

    }

    public static synchronized void counter(int n){
        if(n==1){
            thread_number++;

        }
        if(n==-1){

            thread_number--;
        }
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public static void run(String symbol) throws IOException, ParseException, InterruptedException {
        Thread t= new Thread(new Runnable() {

            public void run() {
                BackTest b=new BackTest(2023,04,26);

                b.build(symbol);


                b.M15(0,15,30);

                try {
                    counter(1);
                    b.simulation();
                    counter(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }


    public static void main(String[] args) throws IOException, ParseException, InterruptedException {

//        StockRequest request=new StockRequest("SPY");
//        ArrayList<StockObject> stockObjects=request.From("2023-03-02").To("2023-04-26").endPoint().build();
//        System.out.println(stockObjects.size());
//        ArrayList<StockObject> new_list=StockObject.filter(stockObjects);
//        System.out.println(new_list.size());
//        for(int i=0;i<new_list.size();i++){
//            System.out.println(new Date(new_list.get(i).getTimestamp()));
//        }



        String symbols[]={"AAPL","AMZN","BABA","GOOG","META","MSFT","NFLX","NVDA","QQQ","SPY","TSLA"};
        for(int i=0;i<symbols.length;i++ ){
            run(symbols[i]);
        }
        Thread.sleep(5000);
        System.out.println(thread_number);
        while (BackTest.thread_number!=0){
            Thread.sleep(2000);
        }

        System.out.println();
        System.out.println("AAPL \t"+hashtable.get("AAPL"));
        System.out.println("AMZN \t"+hashtable.get("AMZN"));
        System.out.println("BABA \t"+hashtable.get("BABA"));
        System.out.println("GOOG \t"+hashtable.get("GOOG"));
        System.out.println("META \t"+hashtable.get("META"));
        System.out.println("MSFT \t"+hashtable.get("MSFT"));
        System.out.println("NFLX \t"+hashtable.get("NFLX"));
        System.out.println("NVDA \t"+hashtable.get("NVDA"));
        System.out.println("QQQ \t"+hashtable.get("QQQ"));
        System.out.println("SPY \t"+hashtable.get("SPY"));
        System.out.println("TSLA \t"+hashtable.get("TSLA"));

    }
}
