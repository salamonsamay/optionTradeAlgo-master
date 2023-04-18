package mycode.trade;

import com.ib.client.EClientSocket;
import com.twilio.Twilio;
import mycode.help.Tools;
import mycode.help.TwilioSMS;
import mycode.object.Order_info;

import java.time.LocalDateTime;
import java.util.*;

public class OrdersManagement {
   public static int counter=0;

   ArrayList<Order_info> submit_order =new ArrayList<>();
   ArrayList<Order_info> ordersFilled =new ArrayList<>();
   Queue<Order_info> queue=new LinkedList<>();

   ArrayList<String> symbolsFilled =new ArrayList<>();
   EClientSocket client;

   public OrdersManagement(EClientSocket m_s){
      this.client=m_s;

   }


   /**
    *  get call back info from 'openOrder method' from class  Program
    * if the order already done  add it in 'ordersFilled' list than remove all the other
    * order with same company symbolsFilled.
    * otherwise add in 'submit_order'
    * @param orderinfo
    */
   public synchronized void add(Order_info orderinfo){
      if(symbolsFilled.contains(orderinfo.getSymbol())){
         cancelSubmittedOrder(orderinfo.getSymbol());
         return;
      }

      if(orderinfo.getStatus().equals("Submitted") && !symbolsFilled.contains(orderinfo.getSymbol())){
         submit_order.add(orderinfo);
         return;
      }
      if(orderinfo.getStatus().equals("Filled")){
         client.reqGlobalCancel();
         counter++;
         TwilioSMS.sensSMS(Tools.sendedOrder.get((orderinfo.getOrder_id())));

       //  cancelSubmittedOrder(orderinfo.getSymbol());//cancel all the order that have same symbol
         ordersFilled.add(orderinfo);
         symbolsFilled.add(orderinfo.getSymbol());

      }
   }
//   public void add(String position){
//      String symbol_output="";
//      String position_output = "";
//      Scanner in=new Scanner(position);
//      while (in.hasNextLine()){
//         String str=in.nextLine();
//          String arr[]=str.split(" ");
//          if(arr[0].equals("symbol")){
//             symbol_output=arr[2];
//          }
//          else if(arr[0].equals("position")){
//             position_output=arr[2];
//          }
//
//      }
//      if(!position_output.equals("0")){
//         this.symbolsFilled.add(symbol_output);
//
//      }
//
//   }

   public  void cancelSubmittedOrder(String symbol){
      for(int i=submit_order.size()-1;i>=0;i--){
         if(submit_order.get(i).getSymbol().equals(symbol)){
            client.cancelOrder(submit_order.get(i).getOrder_id(),"");
            submit_order.remove(i);
         }

      }

   }


   /**
    * check if the symbol was filled once
    * @param symbol
    * @return
    */
   public boolean isFilled(String symbol){
      for(String str: symbolsFilled){
         if(str.equals(symbol)){return  true;}
      }
      return false;
   }


   public void run(){
      new Thread(new Runnable() {
         @Override
         public void run() {
            while (true){
               try {
                  System.out.println("2 min from "+LocalDateTime.now());
                  Thread.sleep(1000*60*2);
                  client.reqPositions();
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }).start();
   }


    public void printFilledSymbol(){
      for(int i=0;i<this.symbolsFilled.size();i++){
         System.out.print(symbolsFilled.get(i)+",");
      }
    }

   public static void main(String[] args) {
      ArrayList<Integer> integers=new ArrayList<>();
      integers.add(1);integers.add(2);integers.add(3);integers.add(4);integers.add(5);
      for(int i=0;i<integers.size();i++) {
         if(i%2==0){
            integers.remove(i);
         }
      }


   }
}
