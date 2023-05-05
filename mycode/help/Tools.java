package mycode.help;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


import mycode.data.LoadData;
import mycode.object.Option;
import mycode.strategy_.*;
import mycode.trade.Main;


public class Tools {



    public static Hashtable<String,Integer> contract_id_list=load();

    //contain  the  strategy info  that sended
    public  static Hashtable<Integer,String> sendedOrder=new Hashtable<>();

    public static final String PATH ="read_file/all_symbol/";
    public static  String  DATE_START = "2023-05-03";
    public static  String  DATE_END = "2023-06-03";


    public static ArrayList<String> readCompanyFromFile(){
        ArrayList<String>companyList=new ArrayList<>();
        File file =new File(PATH);

        String symbols[]=file.list();
        for(int i=0;i<symbols.length;i++){
            String symbol=symbols[i].substring(0,symbols[i].indexOf('.'));
//            if(
//                    symbol.equals("SQQQ") ||symbol.equals("GME")
//                    ||  symbol.equals("SPY")){
//
////                    ||symbol.equals("IWM") ||symbol.equals("COIN"))
//
//                continue;
//            }

            companyList.add(symbols[i].substring(0,symbols[i].indexOf('.')));
        }

        return companyList;

    }


    public  static  void updateLinearList(){
        File file=new File("read_file/output/linear.txt");
        try {
            Scanner in =new Scanner(file);
            while (in.hasNextLine()){
                String str[]=in.nextLine().split(",");
                LinearEquation l=new LinearEquation();
                l.setSymbol(str[0]);
                l.setSlope(Double.parseDouble(str[1]));
                l.setyIntercept(Double.parseDouble(str[2]));
                Main.linearList.put(str[0],l);
            }
            in.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public static boolean isValidData(Strategy strategy){
        if(strategy instanceof  BearSpread ){
            return isValidData(((BearSpread) strategy).sell.getOpt())
                    &&  isValidData(((BearSpread) strategy).buy.getOpt());
        }
        if(strategy instanceof  BullSpread){
            return isValidData(((BullSpread) strategy).sell.getOpt())
                    && isValidData(((BullSpread) strategy).buy.getOpt());
        }
        if(strategy instanceof  IronCondor){
            return isValidData(((IronCondor) strategy).bear_call)
                    && isValidData(((IronCondor) strategy).bull_put);
        }
        if(strategy instanceof  ShortBoxSpread ){
            return  isValidData(((ShortBoxSpread) strategy).bearSpread)
                    && isValidData(((ShortBoxSpread) strategy).bullSpread);
        }
        if(strategy instanceof  LongBoxSpread ){
            return  isValidData(((LongBoxSpread) strategy).bearSpread)
                    && isValidData(((LongBoxSpread) strategy).bullSpread);
        }
        throw new RuntimeException();

    }
    public static boolean isValidData(Option opt){
        if(opt.getAsk()==0 || opt.getBid()==0){
            return false;
        }
        return true;
    }

    public static boolean haveExDividend(String symbol) throws FileNotFoundException {
        File file=new File("read_file/ex_dividend/ex_dividend.txt");
        Scanner in=new Scanner(file);
        while(in.hasNextLine()){
            String ex_dividend_date[]=in.nextLine().split(",");
            if(ex_dividend_date[0].equals(symbol)){
                for(int i=1;i<ex_dividend_date.length;i++){
                    String date[]=ex_dividend_date[i].split("-");
                    int day= Integer.parseInt(date[0]);
                    int month= Integer.parseInt(date[1]);
                    int year= Integer.parseInt(date[2]);
                    LocalDate ex_date=LocalDate.of(year,month,day);
                    LocalDate current_date=LocalDate.now();

                    int end_day= Integer.parseInt(Tools.DATE_END.substring(8,10));
                    int end_year= Integer.parseInt(Tools.DATE_END.substring(0,4));
                    int end_month= Integer.parseInt(Tools.DATE_END.substring(5,7));
                    LocalDate end_date=LocalDate.of(end_year,end_month,end_day);

                    if( ex_date.isAfter(current_date)  && ex_date.isBefore(end_date) ){
                        System.out.println(symbol +" have ex dividend day on "+ex_date);
                        return true;
                    }
                }
            }
        }
        return false;

    }

    /**
     * check if the all strategy is under price
     * @param strategy
     * @return
     */
    public static boolean isUnderValue(Strategy strategy){
        if(strategy instanceof  BearSpread){
            Option option=((BearSpread) strategy).sell.getOpt();
            Option option2=((BearSpread) strategy).buy.getOpt();
            return (option.getBid()>option.getVwap())
                    && (option2.getAsk()<option.getVwap());
        }
        if(strategy instanceof BullSpread){
            Option option=((BullSpread) strategy).sell.getOpt();
            Option option2=((BullSpread) strategy).buy.getOpt();
            return (option.getBid()>option.getVwap())
                    && (option2.getAsk()<option.getVwap());
        }
        if(strategy instanceof  IronCondor){
            return isUnderValue(((IronCondor) strategy).bear_call)
                    && isUnderValue(((IronCondor) strategy).bull_put);
        }
        if(strategy instanceof  ShortBoxSpread){
            return isUnderValue(((ShortBoxSpread) strategy).bearSpread)
                    && isValidData(((ShortBoxSpread) strategy).bullSpread);
        }
        if(strategy instanceof LongBoxSpread){
            return   isUnderValue(((LongBoxSpread) strategy).bearSpread)
                    && isUnderValue(((LongBoxSpread) strategy).bullSpread);
        }
        throw new RuntimeException();
    }
    public static boolean isTimeToBuy(Strategy strategy){
        if(strategy instanceof BullSpread){
            String symbol=strategy.getCompanySymbol();
            double price=Main.symbols_prices_and_vwap_list.get(symbol).getFirst();
            double vwap=Main.symbols_prices_and_vwap_list.get(symbol).getSecond();
            if(price*1.05<vwap || price+2<vwap){
                //       System.out.println("at isTimetoBuy function " +true);
                return true;
            }else {
                //         System.out.println("at isTimetoBuy function " +false);
                return false;
            }
        }
        if(strategy instanceof BearSpread){
            String symbol=strategy.getCompanySymbol();
            double price=Main.symbols_prices_and_vwap_list.get(symbol).getFirst();
            double vwap=Main.symbols_prices_and_vwap_list.get(symbol).getSecond();
            if(price<1.05*vwap || price>vwap+2){
                //         System.out.println("at isTimetoBuy function " +true);
                return true;
            }else {
                //           System.out.println("at isTimetoBuy function " +false);
                return false;
            }
        }
        else throw new RuntimeException();

    }


    public static void print(ArrayList<Strategy> list){
        for(Strategy value:list){
            System.out.println(value);
        }
    }


    public static boolean contain1(Strategy strategy) {
        String s1=null,s2=null;
        if(strategy instanceof BearSpread) {

            s1=((BearSpread)(strategy)).buy.getOpt().getTicker();
            s2=((BearSpread)(strategy)).sell.getOpt().getTicker();

            return (contain1(s1) || contain1(s2));
        }
        else if(strategy instanceof BullSpread) {

            s1=((BullSpread)(strategy)).buy.getOpt().getTicker();
            s2=((BullSpread)(strategy)).sell.getOpt().getTicker();
            return (contain1(s1) || contain1(s2));
        }
        else if(strategy instanceof IronCondor) {
            return (contain1(((IronCondor)(strategy)).bear_call))
                    ||(contain1(((IronCondor)(strategy)).bull_put));

        }

        return true;
    }

    private static boolean contain1(String ticker) {

        for(int i=0;i<ticker.length();i++) {
            try {
                Integer.parseInt(ticker.charAt(i)+"");//check if the char is a number

                if(ticker.substring(i).length()==16) {
                    return true;
                }
                else {return false;}

            }catch (NumberFormatException e) {

            }
        }

        return false;
    }

    public static void write(ArrayList<Strategy> shortBoxSpreads) throws FileNotFoundException {
        File file=new File("read_file/output/short_box_data.txt");
        PrintWriter pw=new PrintWriter(file);
        for(Strategy  value :shortBoxSpreads){
            ShortBoxSpread box=(ShortBoxSpread)(value);
            pw.println(box.bearSpread.sell.getOpt().getUnderlying_ticker());
            pw.println(box.bearSpread.sell.getOpt().getUnderlying_price());
            pw.println(box.bearSpread.sell.getOpt().getTicker());
            pw.println(box.bearSpread.sell.getOpt().getStrike());
            pw.println(box.bearSpread.sell.getOpt().getAsk());
            pw.println(box.bearSpread.sell.getOpt().getBid());
            pw.println(box.bearSpread.sell.getOpt().getExpiration_date());
            pw.println(box.bearSpread.sell.getOpt().getExercise_style());
            pw.println(box.bearSpread.sell.getOpt().getContractId());
            ////////////////////////////////////////////////////////
            pw.println(box.bearSpread.buy.getOpt().getUnderlying_ticker());
            pw.println(box.bearSpread.buy.getOpt().getUnderlying_price());
            pw.println(box.bearSpread.buy.getOpt().getTicker());
            pw.println(box.bearSpread.buy.getOpt().getStrike());
            pw.println(box.bearSpread.buy.getOpt().getAsk());
            pw.println(box.bearSpread.buy.getOpt().getBid());
            pw.println(box.bearSpread.buy.getOpt().getExpiration_date());
            pw.println(box.bearSpread.buy.getOpt().getExercise_style());
            pw.println(box.bearSpread.buy.getOpt().getContractId());
            ////////////////////////////////////////////////////////
            pw.println(box.bullSpread.buy.getOpt().getUnderlying_ticker());
            pw.println(box.bullSpread.buy.getOpt().getUnderlying_price());
            pw.println(box.bullSpread.buy.getOpt().getTicker());
            pw.println(box.bullSpread.buy.getOpt().getStrike());
            pw.println(box.bullSpread.buy.getOpt().getAsk());
            pw.println(box.bullSpread.buy.getOpt().getBid());
            pw.println(box.bullSpread.buy.getOpt().getExpiration_date());
            pw.println(box.bullSpread.buy.getOpt().getExercise_style());
            pw.println(box.bullSpread.buy.getOpt().getContractId());
            //////////////////////////////////////////////////////////
            pw.println(box.bullSpread.sell.getOpt().getUnderlying_ticker());
            pw.println(box.bullSpread.sell.getOpt().getUnderlying_price());
            pw.println(box.bullSpread.sell.getOpt().getTicker());
            pw.println(box.bullSpread.sell.getOpt().getStrike());
            pw.println(box.bullSpread.sell.getOpt().getAsk());
            pw.println(box.bullSpread.sell.getOpt().getBid());
            pw.println(box.bullSpread.sell.getOpt().getExpiration_date());
            pw.println(box.bullSpread.sell.getOpt().getExercise_style());
            pw.println(box.bullSpread.sell.getOpt().getContractId());

        }
        pw.close();


    }





    /**
     * remove the option that  have no contract id
     * and remove  ticker that lengh above 16
     * @param optionList
     * @return
     */
    public static ArrayList<Option> filterOpt(ArrayList<Option> optionList){
        ArrayList<Option> newList=new ArrayList<>();
        for(Option opt:optionList){
            try {

                //    opt.setContractId(Tools.getContractId(opt.getUnderlying_ticker(),opt.getTicker()));
                opt.setContractId(Tools.contract_id_list.get(opt.getTicker()));

            }catch (NullPointerException nullPointerException){
                opt.setContractId(-1);
                continue;
            }
            if(!contain1(opt.getTicker()))//correct option and not null
            {
                newList.add(opt);
            }
        }

        return newList;

    }
    public static ArrayList<Strategy> filter(ArrayList<Strategy> list) {


        ArrayList<Strategy>filter=new ArrayList<Strategy>();

        for(Strategy strategy:list) {
            if(
//					&& strategy.daysToExpiration()<=4
//					&& strategy.maxLoss()>-80
//					&& strategy.probabilityOfMaxSuccess()>0.25
//					&& strategy.probabilityOfMaxSuccess()>strategy.probabilityOfMaxLoss()

                    strategy.daysToExpiration()<=5
                            && !contain1(strategy)

            )
            {
                filter.add(strategy);
            }

        }

        return filter;
    }


    public static void updateAll(ArrayList<Option> list) {

        for(Option l:list) {
            new Thread(new Runnable() {

                public void run() {
                    //long t=new Date().getTime();
                    while(true) {
                        l.update();
//						long t2=new Date().getTime();
//						System.out.println((t2-t)/1000);
//						t=t2;
                        //		System.out.println((new Date().getTime()-l.getLastUpdate())/1000);
                        try {
                            Thread.sleep(20);
//						System.out.println("updateProccess "+l.getTicker());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            //	reqHistoricalData_.printStackTrace();
                            System.out.println(e);
                        }
                    }

                }
            }).start();
        }
    }


    public static void updateRSI(ArrayList<Option> list ){
        for(Option l:list) {
            new Thread(new Runnable() {

                public void run() {
                    //long t=new Date().getTime();
                    while(true) {
                        l.update();
                        try {
                            Thread.sleep(1000);
//						System.out.println("updateProccess "+l.getTicker());
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            //	reqHistoricalData_.printStackTrace();
                            System.out.println(e);
                        }
                    }

                }
            }).start();
        }
    }
    public static String extract_and_write_contractID(String symbol){
        String path=Tools.PATH+symbol+".txt";
        File file=new File(path);
        Hashtable<String,String> hashtable= new Hashtable<String, String>();
        Scanner in= null;
        in = new Scanner(LoadData.data);
        String key="",value="";
        String localSymbolName="";
        while (in.hasNextLine()){
            String line[]=in.nextLine().split(" ");

            if(line[0].equals("conid")){
                value=line[2];
            }
            else if(line[0].equals("localSymbol")){
                key=line[2]+line[line.length-1];
                hashtable.put(key,value);

//				System.out.println(line[4]+","+value);
            }

        }
        in.close();

        Set<String> keys = hashtable.keySet();
        String info="";//will contain the new info
        if(keys.isEmpty()){return null;}

        //    PrintWriter pw=new PrintWriter(file);
        for(String k: keys){

//            if(!k.startsWith("SPXW")){
                info+="O:"+k+","+hashtable.get(k)+"\n";
//            }
        }


        return info;
    }

    public static Hashtable load()  {

        Hashtable<String,Integer> temp=new Hashtable<>();
        System.out.println("load  contract id list");
        File file=new File(Tools.PATH);
        String list[]= file.list();
        for(int i=0;i<list.length;i++){
            String symbol=list[i];
            File f=new File(Tools.PATH+symbol);
            Scanner in= null;
            try {
                in = new Scanner(f);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while(in.hasNextLine()){
                try {

                    String str=in.nextLine();
                    String key=str.split(",")[0];
                    String value=str.split(",")[1];
                    temp.put(key,Integer.parseInt(value));
                }catch (Exception e){
                }

            }
            in.close();
        }
        return temp;

    }
    public static int getContractId(String symbol,String ticker){
        //	String PATH="read_file\\indices\\"+symbol+".txt";

        File file=new File(PATH +symbol+".txt");

        try {
            Scanner in=new Scanner(file);
            while (in.hasNextLine()){
                String line[]=in.nextLine().split(",");
                if(line[0].equals(ticker)){
                    return Integer.parseInt(line[1]);
                }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return -1;

    }


    public static void main(String args[]) {


        LocalDateTime l= LocalDateTime.now();
        String date=(l.toLocalDate()+"").split("-")[0]+(l.toLocalDate()+"").split("-")[1]+(l.toLocalDate()+"").split("-")[2];
        String time[]=(l.toLocalTime()+"").split(":");

        System.out.println(time[0]+time[1]+"00");

    }




}
