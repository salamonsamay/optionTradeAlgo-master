package mycode.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Order_info {
    private int order_id;
    private String action;
    private int quantity;
    private int conid;
    private String symbol;
    private double  lmtPrice;
    private String status;
    private long time;

    public Order_info(String info){
        String data[]=info.split(" ");
        for(int i=2;i<data.length;i++){
            try {
                if(data[i].substring(0,data[i].indexOf('=')).equals("orderId")){
                    setOrder_id(Integer.parseInt(data[i].substring(data[i].indexOf('=')+1)));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("action")){
                    setAction((data[i].substring(data[i].indexOf('=')+1)));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("quantity")){
                    setQuantity(Integer.parseInt((data[i].substring(data[i].indexOf('=')+1))));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("conid")){
                    setConid(Integer.parseInt((data[i].substring(data[i].indexOf('=')+1))));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("symbol")){
                    setSymbol(data[i].substring(data[i].indexOf('=')+1));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("lmtPrice")){
                    setLmtPrice(Double.parseDouble(data[i].substring(data[i].indexOf('=')+1)));
                }
                else if(data[i].substring(0,data[i].indexOf('=')).equals("status")){
                    setStatus(data[i].substring(data[i].indexOf('=')+1));
                }


            }catch (Exception e){

            }
            setTime(new Date().getTime());

        }
    }

    public Order_info(int order_id, String symbol, String status) {
        setOrder_id(order_id);
        setSymbol(symbol);
        setStatus(status);
        setTime(new Date().getTime());
    }


    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getConid() {
        return conid;
    }

    public void setConid(int conid) {
        this.conid = conid;
    }

    public double getLmtPrice() {
        return lmtPrice;
    }

    public void setLmtPrice(double lmtPrice) {
        this.lmtPrice = lmtPrice;
    }

    @Override
    public boolean equals(Object order_info){

        if(this.order_id==((Order_info)order_info).order_id){
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "Order_info{" +
                "order_id=" + order_id +
                ", action='" + action + '\'' +
                ", quantity=" + quantity +
                ", conid=" + conid +
                ", symbol='" + symbol + '\'' +
                ", lmtPrice=" + lmtPrice +
                ", status='" + status + '\'' +
                ", time=" + time +
                '}';
    }

    public static void main(String[] args) {

        String status="order status: orderId=25700 clientId=0 permId=2076246514 status=PreSubmitted filled=0 remaining=1 avgFillPrice=0 lastFillPrice=0 parent Id=0 whyHeld=null mktCapPrice=0";
        String data[]=status.split(" ");
        for(int i=2;i<data.length;i++){
            System.out.println(data[i]);
        }

    }
}
