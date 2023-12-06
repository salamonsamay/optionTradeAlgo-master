package mycode.trade;

import com.ib.client.*;
import mycode.help.Configurations;
import mycode.help.Tools;
import mycode.object.Option;
import mycode.strategy_.*;
import samples.testbed.orders.OrderSamples;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Transaction {

    public static Order createOrderBuy_FOK(double limitPrice){
        Order order=new Order();
        order.tif("FOK");//fill or kill
//		order.clientId(0);

        order.orderId((int)(Math.random()*1000000)+10);
        order.account(Configurations.ACCOUNT);
        order.totalQuantity(Decimal.ONE);
        order.action(Types.Action.BUY);
        order.orderType(OrderType.LMT);
        order.lmtPrice(limitPrice);

        return order;
    }
    public static Order createOrderBuy(double limitPrice){
        Order order=new Order();
        //	order.clientId(0);

        order.account(Configurations.ACCOUNT);
        order.totalQuantity(Decimal.get(1));
        order.action(Types.Action.BUY);
        order.orderType(OrderType.LMT);
        order.lmtPrice(limitPrice);
        return order;
    }
    public static Order createOrderBuy_OCA(Strategy strategy){
        Order order=new Order();
        List<Order> o=new LinkedList<>();
        //	order.clientId(0);

        order.account(Configurations.ACCOUNT);
        order.totalQuantity(Decimal.get(1));
        order.action(Types.Action.BUY);
        order.orderType(OrderType.LMT);
        //   order.lmtPrice(MyMath.midPointPrice(strategy));
        order.lmtPrice(strategy.price());
        // order.lmtPrice(strategy.midPointPrice());
        o.add(order);
        OrderSamples.OneCancelsAll(strategy.getCompanySymbol(), o,1);
        return order;
    }

    public static Order createOrderBuyGTD(double limitPrice){
        Order order=new Order();
        //	order.clientId(0);

        order.account(Configurations.ACCOUNT);
        order.totalQuantity(Decimal.get(10));
        order.action(Types.Action.BUY);
        order.orderType(OrderType.LMT);
        order.lmtPrice(limitPrice);

        order.tif("GTD");//GTD=good till date/day/time
        LocalDateTime timer =LocalDateTime.now().plusHours(3);//order that will be valid for 2 minutes
        String year_=timer.getYear()+"";
        String month_=timer.getMonthValue()+"";
        String day_=timer.getDayOfMonth()+"";
        String hour_=timer.getHour()+":";
        String minute_=timer.getMinute()+":";
        String second_=timer.getSecond()+"";

        if(month_.length()==1){month_="0"+month_;}
        if(day_.length()==1){day_="0"+day_;}
        if(second_.length()==1){second_="0"+second_;}
        if(minute_.length()==2){minute_="0"+minute_;}
        if(hour_.length()==2){hour_="0"+hour_;}
//
//        String time=timer.getYear()+""+ timer.getMonthValue()+"" + timer.getDayOfMonth()+" "
//                + timer.toLocalTime().getHour()+":"+ timer.getMinute()+":"+ timer.getSecond();
        String s=year_+month_+day_+" "+hour_+minute_+second_;
        System.out.println("---->"+s);

        order.optOutSmartRouting(true);

        return order;
    }


    public static Order createOrderSell(double limitPrice){
        Order order=new Order();
//		order.clientId(0);
        order.account(Configurations.ACCOUNT);

        order.totalQuantity(Decimal.ONE);
        order.action(Types.Action.SELL);
        order.orderType(OrderType.LMT);
        order.lmtPrice(limitPrice);

//        order.tif("GTD");//GTD=good till date/day/time
//        LocalDateTime timer =LocalDateTime.now().plusHours(5);//order that will be valid for 2 minutes
//        String year_=timer.getYear()+"";
//        String month_=timer.getMonthValue()+"";
//        String day_=timer.getDayOfMonth()+"";
//        String hour_=timer.getHour()+":";
//        String minute_=timer.getMinute()+":";
//        String second_=timer.getSecond()+"";
//        if(second_.length()==1){second_="0"+second_;}
//        if(minute_.length()==2){minute_="0"+minute_;}
//        if(hour_.length()==2){hour_="0"+hour_;}
////
////        String time=timer.getYear()+""+ timer.getMonthValue()+"" + timer.getDayOfMonth()+" "
////                + timer.toLocalTime().getHour()+":"+ timer.getMinute()+":"+ timer.getSecond();
//        String s=year_+month_+day_+" "+hour_+minute_+second_;
//        System.out.println(s);
//        order.goodTillDate(s);
        return order;
    }

    /**
     * create standart option for testing
     * @param symbol
     * @return
     */
    public static Contract createContract(String symbol){
        Contract contract =new Contract();
        contract.symbol(symbol);
        if(symbol.equals("SPX") ){
            contract.secType("IND");
            contract.primaryExch("XSP");
            contract.exchange("SMART");
            contract.currency("USD");
            return contract;
        }
        //  contract.conid(609954644);
        contract.secType("STK");
        contract.exchange("SMART");
        contract.currency("USD");
        if(symbol.equals("META") || symbol.equals("ABNB")){//prevent ambiguous situation
            contract.primaryExch("NASDAQ");
        }

        return  contract;
    }
    public static Contract createOptContract(String symbol){
        Contract contract =new Contract();
        contract.symbol(symbol);
        //  contract.conid(609954644);
        contract.lastTradeDateOrContractMonth("202405");
        contract.secType("OPT");
        contract.exchange("SMART");
        contract.currency("USD");
        return  contract;
    }

    public  static Contract createStkContract(String symbol){
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        return contract;
    }

    public static Contract comboContract(Strategy strategy){

        if(strategy instanceof BearSpread){

            int sell_conid=((BearSpread) strategy).sell.getOpt().getContractId();
            int buy_conid=((BearSpread) strategy).buy.getOpt().getContractId();
            return comboBearSpread(strategy.getCompanySymbol(),sell_conid,buy_conid);
        }
        if(strategy instanceof BullSpread){
            int sell_conid=((BullSpread) strategy).sell.getOpt().getContractId();
            int buy_conid=((BullSpread) strategy).buy.getOpt().getContractId();
            return comboBullSpred(strategy.getCompanySymbol(),buy_conid,sell_conid);
        }

        if(strategy instanceof ShortBoxSpread ){
            int bull_buy_conid=((ShortBoxSpread) strategy).bullSpread.buy.getOpt().getContractId();
            int bull_sell_conid=((ShortBoxSpread) strategy).bullSpread.sell.getOpt().getContractId();
            int bear_sell_conid=((ShortBoxSpread) strategy).bearSpread.sell.getOpt().getContractId();
            int bear_buy_conid=((ShortBoxSpread) strategy).bearSpread.buy.getOpt().getContractId();

            return comboBoxSpred(strategy.getCompanySymbol(),bull_buy_conid,bull_sell_conid,bear_sell_conid,bear_buy_conid);
        }

        if(strategy instanceof LongBoxSpread ){
            int bull_buy_conid=((LongBoxSpread) strategy).bullSpread.buy.getOpt().getContractId();
            int bull_sell_conid=((LongBoxSpread) strategy).bullSpread.sell.getOpt().getContractId();

            int bear_sell_conid=((LongBoxSpread) strategy).bearSpread.sell.getOpt().getContractId();
            int bear_buy_conid=((LongBoxSpread) strategy).bearSpread.buy.getOpt().getContractId();

            return comboBoxSpred(strategy.getCompanySymbol(),bull_buy_conid,bull_sell_conid,bear_sell_conid,bear_buy_conid);
        }

        if(strategy instanceof IronCondor){
            int bull_buy_conid=((IronCondor) strategy).bull_put.buy.getOpt().getContractId();
            int bull_sell_conid=((IronCondor) strategy).bull_put.sell.getOpt().getContractId();

            int bear_sell_conid=((IronCondor) strategy).bear_call.sell.getOpt().getContractId();
            int bear_buy_conid=((IronCondor) strategy).bear_call.buy.getOpt().getContractId();

            return comboBoxSpred(strategy.getCompanySymbol(),bull_buy_conid,bull_sell_conid,bear_sell_conid,bear_buy_conid);
        }
        if(strategy instanceof Reversal){
            int buyCallConID=((Reversal) strategy).syntheticLong.buy.getOpt().getContractId();
            int sellPutConID=((Reversal) strategy).syntheticLong.sell.getOpt().getContractId();
            return comboSyntheticLong(strategy.getCompanySymbol(),buyCallConID,sellPutConID);
        }

        return null;
    }



    public static ArrayList<Contract> singleContract(Strategy strategy){

        ArrayList<Contract> contractsList=new ArrayList<>();
        if(strategy instanceof BearSpread){
            contractsList.add(singleContract(((BearSpread) strategy).sell.getOpt()));
            contractsList.add(singleContract(((BearSpread) strategy).buy.getOpt()));;
        }
        if(strategy instanceof BullSpread){
            contractsList.add(singleContract(((BullSpread) strategy).sell.getOpt()));
            contractsList.add(singleContract(((BullSpread) strategy).buy.getOpt()));;
        }
        if(strategy instanceof IronCondor ){
            contractsList.add(singleContract(((IronCondor) strategy).bull_put.buy.getOpt()));
            contractsList.add(singleContract(((IronCondor) strategy).bear_call.sell.getOpt()));
            contractsList.add(singleContract(((IronCondor) strategy).bear_call.sell.getOpt()));
            contractsList.add(singleContract(((IronCondor) strategy).bear_call.buy.getOpt()));

        }
        if(strategy instanceof BoxSpread){
            contractsList.add(singleContract(((BoxSpread) strategy).bearSpread.sell.getOpt()));
            contractsList.add(singleContract(((BoxSpread) strategy).bearSpread.buy.getOpt()));
            contractsList.add(singleContract(((BoxSpread) strategy).bullSpread.buy.getOpt()));
            contractsList.add(singleContract(((BoxSpread) strategy).bullSpread.sell.getOpt()));

        }
        return contractsList;
    }
    public static Contract singleContract(Option opt){

        Contract contract = new Contract();
        contract.symbol(opt.getUnderlying_ticker());
        contract.conid(opt.getContractId());
        contract.secType("opt");
        contract.currency("USD");
        contract.exchange("SMART");

        return contract;
    }

    private static Contract comboBoxSpred(String symbol,int bull_buy_conId,int bull_sell_conId,
                                          int bear_sell_conId,int bear_buy_conId){

        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("BAG");
        contract.currency("USD");
        contract.exchange("SMART");
        ComboLeg leg1 = new ComboLeg();
        ComboLeg leg2 = new ComboLeg();
        ComboLeg leg3 = new ComboLeg();
        ComboLeg leg4 = new ComboLeg();
        List<ComboLeg> addAllLegs = new ArrayList<>();

        leg1.conid(bull_buy_conId);
        leg1.ratio(1);
        leg1.action("BUY");
        leg1.exchange("SMART");

        leg2.conid(bull_sell_conId);
        leg2.ratio(1);
        leg2.action("SELL");
        leg2.exchange("SMART");

        leg3.conid(bear_sell_conId);
        leg3.ratio(1);
        leg3.action("SELL");
        leg3.exchange("SMART");

        leg4.conid(bear_buy_conId);
        leg4.ratio(1);
        leg4.action("BUY");
        leg4.exchange("SMART");

        addAllLegs.add(leg1);
        addAllLegs.add(leg2);
        addAllLegs.add(leg3);
        addAllLegs.add(leg4);
        contract.comboLegs(addAllLegs);
        return contract;
    }
    private   static Contract comboBearSpread(String symbol, int sellConId,int buyConId){
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("BAG");
        contract.currency("USD");
        contract.exchange("SMART");
        ComboLeg leg1 = new ComboLeg();
        ComboLeg leg2 = new ComboLeg();
        List<ComboLeg> addAllLegs = new ArrayList<>();

        leg1.conid(sellConId);
        leg1.ratio(1);
        leg1.action("SELL");
        leg1.exchange("SMART");

        leg2.conid(buyConId);
        leg2.ratio(1);
        leg2.action("BUY");
        leg2.exchange("SMART");

        addAllLegs.add(leg1);
        addAllLegs.add(leg2);
        contract.comboLegs(addAllLegs);
        return contract;
    }

    private   static Contract comboBullSpred(String symbol, int buyConId,int sellConId){
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("BAG");
        contract.currency("USD");
        contract.exchange("SMART");

        ComboLeg leg1 = new ComboLeg();
        ComboLeg leg2 = new ComboLeg();
        List<ComboLeg> addAllLegs = new ArrayList<>();

        leg1.conid(buyConId);
        leg1.ratio(1);
        leg1.action("BUY");
        leg1.exchange("SMART");

        leg2.conid(sellConId);
        leg2.ratio(1);
        leg2.action("SELL");
        leg2.exchange("SMART");

        addAllLegs.add(leg1);
        addAllLegs.add(leg2);
        contract.comboLegs(addAllLegs);
        return contract;
    }

    public static Contract comboSyntheticLong(String symbol, int buyCallConID, int sellPutConID) {
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("BAG");
        contract.currency("USD");
        contract.exchange("SMART");

        ComboLeg leg1 = new ComboLeg();
        ComboLeg leg2 = new ComboLeg();
        ComboLeg leg3 = new ComboLeg();
        List<ComboLeg> addAllLegs = new ArrayList<>();

        leg1.conid(buyCallConID);
        leg1.ratio(1);
        leg1.action("BUY");
        leg1.exchange("SMART");

        leg2.conid(sellPutConID);
        leg2.ratio(1);
        leg2.action("SELL");
        leg2.exchange("SMART");


        leg3.conid(Integer.parseInt(Tools.getTickerId(symbol)));
        leg3.ratio(100);
        leg3.action("SELL");
        leg3.exchange("SMART");

        addAllLegs.add(leg1);
        addAllLegs.add(leg2);
        addAllLegs.add(leg3);
        contract.comboLegs(addAllLegs);
        return contract;
    }
}
