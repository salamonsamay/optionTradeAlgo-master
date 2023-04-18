package mycode.technical_indicator;

import mycode.object.EMAObject;
import mycode.object.MACDObject;
import mycode.object.RSIObject;
import mycode.object.SMAObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Indicator extends Thread{
    public String options_ticker;
    private RSIRequest rsi;
    private SMARequest sma;
    private EMARequest ema;
    private MACDRequest macd;
    public RSIObject rsiObject;
    public SMAObject smaObject;
    public EMAObject emaObject;
    public MACDObject macdObject;

    public  static ExecutorService pool = Executors.newFixedThreadPool(5);
    public  static  int created=0;
    private  boolean flag=false;


    public Indicator(String options_ticker) {
        this.options_ticker = options_ticker;
        this.rsi=new RSIRequest(options_ticker);
        this.sma=new SMARequest(options_ticker);
        this.ema=new EMARequest(options_ticker);
        this.macd=new MACDRequest(options_ticker);
//        smaObject=sma.last20SMA(this.options_ticker);
//        emaObject=ema.last20EMA(this.options_ticker);
//        macdObject=macd.last(this.options_ticker);
//        rsiObject=rsi.last14(this.options_ticker);
    }

    public void update(){

        try {
            smaObject=sma.last20SMA(this.options_ticker);
            emaObject=ema.last20EMA(this.options_ticker);
            macdObject=macd.last(this.options_ticker);
            rsiObject=rsi.last14(this.options_ticker);
            if(!flag){
                flag=true;
                synchronized (this){
                    created++;
                    System.out.println("created "+created);
                }
            }

        } catch (ParseException e) {
//            reqHistoricalData_.printStackTrace();
            System.out.println("eror");
        } catch (IOException e) {
//            reqHistoricalData_.printStackTrace();
            System.out.println("eror");

        }
        catch (RuntimeException e){
            System.out.println("eror");
        }




    }


    public  static  void runAll(Indicator indicator){

        new Thread(new Runnable() {

            public void run() {
                while (true){
                    pool.execute(indicator);
                    try {

                        Thread.sleep(1000*50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }

    public static void sleep(int num){
        while (created<=num){
            try {
                System.out.println("sleep ....");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public  void run(){
        update();
    }


    public boolean isGoingToDown(){
        boolean high_rsi=getRsiObject().getValue()>75 && getRsiObject().getValue()<99;//gone to down
  //      boolean high_macd=getMacdObject().getValue()>0.5 && getMacdObject().getHistogram()<0;//gone to down

        if(high_rsi ){
            return true;
        }
        return false;

    }
    public boolean isGoingToUp(){

        boolean low_rsi=getRsiObject().getValue()<25 && getRsiObject().getValue()>1;//gone to up
      //  boolean low_macd=getMacdObject().getValue()<-0.5 && getMacdObject().getHistogram()>0;//gone to up

        if(low_rsi ){
            return true;
        }
        return false;

    }
    public String getOptions_ticker() {
        return options_ticker;
    }

    public RSIObject getRsiObject() {
        return rsiObject;
    }

    public SMAObject getSmaObject() {
        return smaObject;
    }

    public EMAObject getEmaObject() {
        return emaObject;
    }

    public MACDObject getMacdObject() {
        return macdObject;
    }






    @Override
    public boolean equals(Object o) {
        String ticker= (String) o;
        if(ticker.equals(getOptions_ticker())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Indicator{" +
                "options_ticker='" + options_ticker + '\'' +
                ", rsiObject=" + rsiObject +
                ", smaObject=" + smaObject +
                ", emaObject=" + emaObject +
                ", macdObject=" + macdObject +
                '}';
    }

    public static void main(String[] args) throws InterruptedException {
        Indicator indicator=new Indicator("O:NKE230303C00065000");


        indicator.run();

        Thread.sleep(30000);
        while (true){
            System.out.println(indicator.getRsiObject().getValue());

        }
    }


}
