package mycode.trade;

import mycode.data.StockRequest;
import mycode.object.StockObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class BackTest2 {


    public  static double between(ArrayList<StockObject> list,int first,int second,int therd) {
        double success=0;
        double faild=0;
        list = StockObject.filter(list);
        for (int i = 0; i < list.size(); i = i + 390) {//itertion is one day
            double max = list.get(i).getHighest_price();
            double min = list.get(i).getLowest_price();

            for (int j = i+first; j < i + second; j++) {
                if (list.get(j).getHighest_price() > max) {
                    max = list.get(j).getHighest_price();
                }
                if (list.get(j).getLowest_price() < min) {
                    min = list.get(j).getLowest_price();
                }
            }

//            if (max - min < list.get(i).getHighest_price() * 0.002 || (max-min<1)) {
//                System.out.println("try next day");
//                System.out.println();
//                continue;
//            }
            double diff=(max+min)/2;
            boolean flag=false;

            for(int j=i+330;j<i+390;j++){
                if(Math.abs(list.get(j).getClose_price()-diff)<0.1){
                    success++;
                    System.out.println("close price "+list.get(i+390-1).getClose_price());
                    System.out.println("strike is "+diff);
                    flag=true;
                    break;
                }
            }
            if(flag==false){
                faild++;
            }



        }

        System.out.println("["+success+","+faild+"]");
        return ((success)/(faild+success))*100;
    }


    public static  void closeGap(ArrayList<StockObject> list,double gap,int day_gap){
        double success=0;
        double failed=0;
        list=StockObject.filter(list);
        double min_max_average=0;
        int counter=0;
        for (int i = 390; i < list.size(); i = i + 390*day_gap) {//itraion is one day

            double day_before=list.get(i-1).getClose_price();
            double today=list.get(i).getOpen_price();
            boolean flag=false;
            double min=1000000;
            double max=0;
            long min_max_time=0;

            if(day_before-today>gap){//if gap down
                for(int j=i+1;j<(i+390*day_gap);j++){
                    if(list.get(j).getLowest_price()<min){
                        min=list.get(j).getLowest_price();
                        min_max_time=list.get(j).getTimestamp();
                    }
                    if(list.get(j).getHighest_price()>day_before){
                        success++;
                        System.out.println(new Date(list.get(i-1).getTimestamp()) +" THE CLOSE PRICE AT DAY BEFOR IS "+ day_before);
                        System.out.println(new Date(list.get(i).getTimestamp()) +" THE OPEN PRICE TODAY IS "+ today);
                        System.out.println(new Date(min_max_time) +" THE MIN  WAS "+ min);
                        System.out.println(new Date(list.get(j).getTimestamp()) +" THE GAP CLOSE WHEN PRICE  IS "+ list.get(j).getHighest_price());
                        System.out.println();
                        min_max_average+=(list.get(j).getHighest_price()-min);
                        counter++;
                        flag=true;
                        break;
                    }
                }
            }
            else if(day_before-today<-gap){
                for(int j=i+1;j<(i+390*day_gap);j++){
                    if(list.get(j).getHighest_price()>max){
                        max=list.get(j).getHighest_price();
                        min_max_time=list.get(j).getTimestamp();
                    }
                    if(list.get(j).getLowest_price()<day_before ){
                        success++;
                        System.out.println(new Date(list.get(i-1).getTimestamp()) + " THE CLOSE PRICE AT DAY BEFOR IS "+ day_before);
                        System.out.println(new Date(list.get(i).getTimestamp())+" THE OPEN PRICE TODAY IS "+today);
                        System.out.println(new Date(min_max_time) +" THE MAX  WAS "+ max);
                        System.out.println(new Date(list.get(j).getTimestamp())+" THE GAP CLOSE WHEN PRICE  IS "+list.get(j).getLowest_price());
                        System.out.println();
                        min_max_average+=(max-list.get(j).getLowest_price());
                        counter++;
                        flag=true;
                        break;
                    }
                }
            }
            else {
                continue;
            }

            if(!flag){
                failed++;
            }


        }
        min_max_average/=counter;
        System.out.println("failed "+failed);
        System.out.println("success "+success);
        System.out.println("precent  "+(success)/(failed+success)*100+"%");
        System.out.println("average "+min_max_average);
    }
    public  static double M15(ArrayList<StockObject> list,int first,int second,int therd) {
        double balance =0;
        double success=0;
        double faild=0;
        list = StockObject.filter(list);
        for (int i = 0; i < list.size(); i = i + 390) {//itertion is one day
            double max = list.get(i).getHighest_price();
            double min = list.get(i).getLowest_price();

            for (int j = i+first; j < i + second; j++) {
                if (list.get(j).getHighest_price() > max) {
                    max = list.get(j).getHighest_price();
                }
                if (list.get(j).getLowest_price() < min) {
                    min = list.get(j).getLowest_price();
                }
            }

            if (max - min < list.get(i).getHighest_price() * 0.002 || (max-min<0.8)) {
                System.out.println("try next day");
                System.out.println();
                continue;
            }
            double long_position = 0;
            double short_position = 0;

            for (int j = i + second; j < i + therd; j++) {//define the long/short point
                if (list.get(j).getLowest_price() < min ) {
                    long_position = list.get(j).getLowest_price();
                    System.out.println("long position at " + long_position+" in time "+new Date(list.get(j).getTimestamp()));
                    break;
                }
                if (list.get(j).getHighest_price() > max  ) {
                    short_position = list.get(j).getClose_price();
                    System.out.println("short position at " + short_position+" in time "+new Date(list.get(j).getTimestamp()));
                    break;
                }
            }
            if (long_position == 0 && short_position == 0) {
                System.out.println("try next day");
                System.out.println();
                continue;

            }//check if have opportunity to buy or sellf
            boolean flag=false;//check if get in to position
            for (int j = i + therd; j < i + 390 && j<list.size(); j++) {
                if (long_position != 0) {

                    if (list.get(j).getHighest_price() > max ) {
                        System.out.println("sell back at " + list.get(j).getHighest_price()+" in time "+new Date(list.get(j).getTimestamp()));
                        balance=balance+(list.get(j).getHighest_price()-long_position);
                        System.out.println();
                        success++;
                        flag=true;
                        break;
                    }
                }
                if (short_position != 0) {

                    if (list.get(j).getLowest_price() < min ) {
                        System.out.println("buy back at " + list.get(j).getLowest_price()+" in time "+new Date(list.get(j).getTimestamp()));
                        balance=balance+(short_position-list.get(j).getLowest_price());
                        System.out.println();
                        success++;
                        flag=true;
                        break;
                    }
                }
            }

            if(!flag){
                if(short_position!=0){
                    System.out.println("befor "+balance);
                    balance=balance+(short_position-list.get(i + 390-1).getLowest_price());
                    System.out.println("after "+balance);
                }
                if(long_position!=0){
                    balance=balance+(list.get(i + 390-1).getHighest_price()-long_position);
                }
                //      balance=balance+(long_position+short_position);
                System.out.println("the function field");
                faild++;
                System.out.println();
            }

        }

        System.out.println("balance "+balance);
        System.out.println("["+success+","+faild+"]");
        return ((success)/(faild+success))*100;
    }

    public  static  double check(ArrayList<StockObject> list,int first,int second,int therd){
        double balance =0;
        double success=0;
        double faild=0;

        StockObject[][]  array=StockObject.filter_(list);
        for (int i = 0; i < array.length; i++) {//itertion is one day
            double max = array[i][0].getHighest_price();
            double min = array[i][0].getLowest_price();

            for (int j = first ;j<second;j++) {
                if (array[i][j].getHighest_price() > max) {
                    max = array[i][j].getHighest_price();
                }
                if (array[i][j].getLowest_price() < min) {
                    min = array[i][j].getLowest_price();
                }
            }

            if (max - min < array[i][0].getHighest_price() * 0.002 || (max-min<1)) {
                System.out.println("try next day");
                System.out.println();
                continue;
            }
            double long_position = 0;
            double short_position = 0;

            for (int j = second ;j < therd; j++) {//define the long/short point
                if (array[i][j].getLowest_price() < min ) {
                    long_position = array[i][j].getLowest_price();
                    System.out.println("long position at " + long_position+" in time "+new Date(array[i][j].getTimestamp()));
                    break;
                }
                if (array[i][j].getHighest_price() > max  ) {
                    short_position = array[i][j].getClose_price();
                    System.out.println("short position at " + short_position+" in time "+new Date(array[i][j].getTimestamp()));
                    break;
                }
            }
            if (long_position == 0 && short_position == 0) {
                System.out.println("try next day");
                System.out.println();
                continue;

            }//check if have opportunity to buy or sellf
            boolean flag=false;//check if get in to position
            for (int j = 30; j <array[i].length ; j++) {
                if (long_position != 0) {

                    if (array[i][j].getHighest_price() > max ) {
                        System.out.println("sell back at " + array[i][j].getHighest_price()+" in time "+new Date(array[i][j].getTimestamp()));
                        balance=balance+(array[i][j].getHighest_price()-long_position);
                        System.out.println();
                        success++;
                        flag=true;
                        break;
                    }
                }
                if (short_position != 0) {

                    if (array[i][j].getLowest_price() < min ) {
                        System.out.println("buy back at " + array[i][j].getLowest_price()+" in time "+new Date(array[i][j].getTimestamp()));
                        balance=balance+(short_position-array[i][j].getLowest_price());
                        System.out.println();
                        success++;
                        flag=true;
                        break;
                    }
                }
            }

            if(!flag){
                if(short_position!=0){
                    System.out.println("befor "+balance);
                    balance=balance+(short_position-array[i][389].getLowest_price());
                    System.out.println("after "+balance);
                }
                if(long_position!=0){
                    balance=balance+(array[i][389].getHighest_price()-long_position);
                }
                //      balance=balance+(long_position+short_position);
                System.out.println("the function field");
                faild++;
                System.out.println();
            }

        }

        System.out.println("balance "+balance);
        System.out.println("["+success+","+faild+"]");
        System.out.println(((success)/(faild+success))*100+"%");
        return ((success)/(faild+success))*100;

    }
    public static void read(double gap) throws FileNotFoundException {
        File file=new File("C:\\Users\\salam\\OneDrive\\שולחן העבודה\\TSLA.csv");
        Scanner in=new Scanner(file);
        in.nextLine();
        ArrayList<Double> list=new ArrayList<>();
        while (in.hasNextLine()){
            String str=in.nextLine();
            double open= Double.parseDouble(str.split(",")[1]);
            double high= Double.parseDouble(str.split(",")[2]);
            double low= Double.parseDouble(str.split(",")[3]);
            double close= Double.parseDouble(str.split(",")[4]);
            list.add(open);
            list.add(high);
            list.add(low);
            list.add(close);
        }
        double close=0;
        double not_close=0;
        int sequens=0;
        int max_sequens=0;

        for(int i=4;i<list.size();i=i+4 ){
            if(list.get(i-1)>list.get(i) +gap && list.get(i+1)>list.get(i-1)){//if gap down close
                close++;
               sequens++;

            }
           else if(list.get(i-1)<list.get(i)-gap && list.get(i+2)<list.get(i-1)){//if gap down close
                close++;
                sequens++;

            }
            else if(list.get(i-1)<list.get(i)-gap || list.get(i-1)>list.get(i)+gap){
                not_close++;
                sequens=0;
            }
            if(max_sequens<sequens){
                max_sequens=sequens;
            }
        }
        System.out.println("not close "+not_close);
        System.out.println("close "+close);
        System.out.println("max sequens "+max_sequens);
        System.out.println("the gap that close in percent "+close/(not_close+close)*100+"%");
    }
    public static void main(String[] args) throws IOException, ParseException {
     //   read(0.001);
        StockRequest request=new StockRequest("PLTR");
        ArrayList<StockObject> stockObjects=request.From("2023-02-08").To("2023-05-02").endPoint().build();
//
//      //  StockObject.filter_(stockObjects);
//        //  BackTest2.check(stockObjects,0,10,20);
////        double r=BackTest2.M15(stockObjects,0,15,30);
////        System.out.println(r);
            closeGap(stockObjects,0.2,2);
    }
}

