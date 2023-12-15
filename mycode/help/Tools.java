package mycode.help;

import mycode.data.LoadData;
import mycode.data.OptionChain;
import mycode.object.Option;
import mycode.strategy_.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Tools {



    public static Hashtable<String,Integer> contract_id_list=load();

    //contain  the  strategy info  that sended
    public  static Hashtable<Integer,String> sendedOrder=new Hashtable<>();
    private static final Map<String, String> tickerMap = new HashMap<>();

    static {
        tickerMap.put("AAP", "4027");
        tickerMap.put("AAPL", "265598");
        tickerMap.put("ABNB", "459530964");
        tickerMap.put("ADBE", "265768");
        tickerMap.put("AMD", "4391");
        tickerMap.put("AMZN", "3691937");
        tickerMap.put("BA", "4762");
        tickerMap.put("BABA", "166090175");
        tickerMap.put("BAC", "10098");
        tickerMap.put("BOIL", "635944944");
        tickerMap.put("CAT", "5437");
        tickerMap.put("CCL", "5437");
        tickerMap.put("COIN", "481691285");
        tickerMap.put("DIA", "73128548");
        tickerMap.put("DIS", "6459");
        tickerMap.put("ENPH", "105368327");
        tickerMap.put("F", "9599491");
        tickerMap.put("FDX", "5100583");
        tickerMap.put("GLD", "51529211");
        tickerMap.put("GME", "36285627");
        tickerMap.put("GOOG", "208813720");
        tickerMap.put("GOOGL", "208813719");
        tickerMap.put("HYG", "43652089");
        tickerMap.put("IWM", "9579970");
        tickerMap.put("JPM", "1520593");
        tickerMap.put("LCID", "504716446");
        tickerMap.put("META", "107113386");
        tickerMap.put("MO", "9769");
        tickerMap.put("MRNA", "344809106");
        tickerMap.put("MSFT", "272093");
        tickerMap.put("NFLX", "15124833");
        tickerMap.put("NIO", "332794741");
        tickerMap.put("NKE", "10291");
        tickerMap.put("NVDA", "4815747");
        tickerMap.put("PLTR", "444857009");
        tickerMap.put("PYPL", "199169591");
        tickerMap.put("QQQ", "320227571");
        tickerMap.put("ROKU", "290651477");
        tickerMap.put("SHOP", "195014116");
        tickerMap.put("SLV", "39039301");
        tickerMap.put("SNAP", "268060148");
        tickerMap.put("SPY", "756733");
        tickerMap.put("SQ", "212671971");
        tickerMap.put("SQQQ", "537765515");
        tickerMap.put("TLT", "15547841");
        tickerMap.put("TSLA", "76792991");
        tickerMap.put("UNG", "300917700");
        tickerMap.put("WFC", "10375");
        tickerMap.put("WMT", "13824");
        tickerMap.put("XOM", "13977");
        tickerMap.put("SPX", "416904");
    }

    public static final String PATH ="read_file/liquid/";
    public static  String  DATE_START = "2023-12-13";
    public static  String  DATE_END = "2024-03-30";


    public static ArrayList<String> readCompanyFromFile(){
        ArrayList<String>companyList=new ArrayList<>();
        File file =new File(PATH);

        String symbols[]=file.list();

        for(int i=0;i<symbols.length;i++){
            String symbol = symbols[i].substring(0, symbols[i].indexOf('.'));


            companyList.add(symbol);

        }
        return companyList;
    }


    /**
     * Retrieves option data for a list of companies while checking for ex-dividend dates.
     *
     * @param company_list List of company names for which option data is retrieved.
     * @return ArrayList of Option objects containing the retrieved option data.
     * @throws FileNotFoundException If option data files are not found or an error occurs during data retrieval.
     */
    public static ArrayList<Option> getOptions(ArrayList<String> company_list) throws FileNotFoundException {
        // Create a list to store Indicator instances for each company.
        ArrayList<OptionChain> option_chain_list = new ArrayList<>();

        // Create a thread pool for concurrent data retrieval.
        ExecutorService pool = Executors.newFixedThreadPool(100);

        // Iterate through the list of company names.
        for (int i = 0; i < company_list.size(); i++) {
            // Check if the company has ex-dividend dates within the specified date range.
            if (!Tools.haveExDividend(company_list.get(i))) {
                // Create an Indicator instance for the current company.
                OptionChain option_chain = new OptionChain(company_list.get(i));

                // Configure the Indicator with limit, expiration date range, and other options.

                option_chain
                        .Limit("250")
                        .Expiriation_date_gt(Tools.DATE_START)
                        .Expiriation_date_lt(Tools.DATE_END);
                if(company_list.get(i).equals("SPX")){
                    option_chain.Expiriation_date_gt("2024-12-12");
                    option_chain.Expiriation_date_lt("2029-12-30");
                }
                option_chain.endPoint();


                // Add the Indicator to the list for concurrent processing.
                option_chain_list.add(option_chain);

                // Execute the Indicator retrieval in a separate thread.



                pool.execute(option_chain);

            }
        }

        // Shutdown the thread pool and wait for all tasks to complete.
        pool.shutdown();
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Create a list to store retrieved Option objects.
        ArrayList<Option> options_list = new ArrayList<>();

        // Iterate through the Indicator instances and retrieve their Option data.
        for (int i = 0; i < option_chain_list.size(); i++) {
            options_list.addAll(option_chain_list.get(i).option_list);
            option_chain_list.get(i).updateProcess();

        }
//        option_chain_list.stream()
//                .peek(optionChain -> options_list.addAll(optionChain.option_list))
//                .forEach(Indicator::updateProcess);

        return options_list;
    }


    /**
     * option with the same symbol and the same expression date  considered same group
     * example option in the same group AAPL231006
     O:AAPL231006C00192500 --> AAPL231006
     O:AAPL231006C00167500 --> AAPL231006
     O:AAPL231006P00177500 --> AAPL231006
     * @param strategy
     * @return the group name
     */
    public  static String getGroup(Strategy strategy) {
        if(strategy instanceof  BearSpread){
            return ((BearSpread) strategy).sell.getOpt().getTicker().substring(0,((BearSpread) strategy).sell.getOpt().getTicker().length()-9);
        }
        if(strategy instanceof  BullSpread){
            return ((BullSpread) strategy).sell.getOpt().getTicker().substring(0,((BullSpread) strategy).sell.getOpt().getTicker().length()-9);
        }
        if(strategy instanceof  ShortBoxSpread){
            return getGroup(((ShortBoxSpread) strategy).bullSpread);
        }
        if(strategy instanceof  LongBoxSpread){
            return getGroup(((LongBoxSpread) strategy).bullSpread);
        }

        return null;
    }


    /**
     * every company have a  unique ticker id.
     *the function take company  ticker and  return the company id
     * @param tickerName
     * @return company id
     */
    public static String getTickerId(String tickerName) {
        return tickerMap.get(tickerName);
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
            if(strategy.price()>=0){return false;}
            return  isValidData(((ShortBoxSpread) strategy).bearSpread)
                    && isValidData(((ShortBoxSpread) strategy).bullSpread);
        }
        if(strategy instanceof  LongBoxSpread ){
            return  isValidData(((LongBoxSpread) strategy).bearSpread)
                    && isValidData(((LongBoxSpread) strategy).bullSpread);
        }
        if(strategy instanceof  Reversal ){
            return  isValidData(((Reversal) strategy).syntheticLong.buy.getOpt()) && isValidData(((Reversal) strategy).syntheticLong.sell.getOpt());
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




    /**
     * get the data from IBKR and extract the contract id and ticker information
     * @param symbol
     * @return string that contains the contract id and ticker information  like "O:SPX240920C05100000,637415894"
     */
    public static String extract_and_write_contractID(String symbol){
        String path=Tools.PATH+symbol+".txt";
        File file=new File(path);
        Hashtable<String,String> hashtable= new Hashtable<String, String>();
        Scanner in;
        in = new Scanner(LoadData.data);// LoadData.data is the data from IBKR
        String key="",value="";
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


    /**
     * the function read all the data in the given path
     * store it in hashtable
     * the structure of the file is like --> "O:ARKK230929C00039000,647350921"
     * in example  above in the hashTable the key is O:ARKK230929C00039000 and the value is 647350921
     * @return  the hash table
     */
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
            ;
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


    public static void main(String args[]) throws IOException {




        LocalDateTime l= LocalDateTime.now();
        String date=(l.toLocalDate()+"").split("-")[0]+(l.toLocalDate()+"").split("-")[1]+(l.toLocalDate()+"").split("-")[2];
        String time[]=(l.toLocalTime()+"").split(":");

        System.out.println(time[0]+time[1]+"00");

    }




}
