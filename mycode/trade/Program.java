package mycode.trade;




import static apidemo.util.Util.sleep;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import com.ib.client.*;
import mycode.object.Order_info;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class Program implements EWrapper {
    public EJavaSignal m_signal = new EJavaSignal();
    public EClientSocket m_s = new EClientSocket(this, m_signal);
    public static int NextOrderId = -1;
    public  OrdersManagement ordersManagement=new OrdersManagement(m_s);

    public String data="";

    public  static String historical ="";
    public  static  boolean historicalFlag=false;
    public  static boolean flag=false;

    public static void main(String[] args) throws InterruptedException {

        Program p=new Program();

        p.run();

    }

    public void run() throws InterruptedException {
        // m_s.eConnect("127.0.0.1", 4001, 0);//ib getway real
        // m_s.eConnect("127.0.0.1", 4002, 0);//ib getway demo
           m_s.eConnect("127.0.0.1", 7497, 0);//tws demo ACCOUNT
    //    m_s.eConnect("127.0.0.1", 7496, 0);//tws real ACCOUNT


        final EReader reader = new EReader(m_s, m_signal);

        reader.start();

        new Thread(() -> {
            while (m_s.isConnected()) {
                m_signal.waitForSignal();
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        try {
                            reader.processMsgs();
                        } catch (IOException e) {
                            error(e);
                        }
                    });
                } catch (Exception e) {
                    error(e);
                }
            }
        }).start();

        if (NextOrderId < 0) {
            sleep(1000);
        }
        Thread.sleep(2000);


        m_s.reqGlobalCancel();

        //    m_s.reqMktData(1002, Transaction.createContract("XLF"), "", true, true, null);
        //        m_s.reqSecDefOptParams(1, "IBM", "", "STK", 8314);

//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        cal.add(Calendar.MONTH, -6);
//        SimpleDateFormat form = new SimpleDateFormat("20230326 -11:30:00");
//        String formatted = form.format(cal.getTime());
//        m_s.reqHistoricalData(4001, Transaction.createContract("META"), formatted, "1 D", "1 min", "TRADES", 1, 1, false, null);
//        reqHistoricalData_("SPY");
        try {
            Main.loadProgram(this);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

///////////////////////////////////////////////////////////////////////////
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        m_s.eDisconnect();


    }


    public void reqHistoricalData_(String symbol){
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        cal.add(Calendar.MONTH, -6);
//        SimpleDateFormat form = new SimpleDateFormat("20230326 -17:30:00");
//        String formatted = form.format(cal.getTime());

        LocalDateTime l= LocalDateTime.now();
        String date[]=(l.toLocalDate()+"").split("-");
        String time[]=(l.toLocalTime()+"").split(":");
        m_s.reqHistoricalData(4001, Transaction.createContract(symbol), date[0]+date[1]+date[2]+" "+time[0]+":"+time[1]+":00", "1 D", "1 min", "TRADES", 1, 1, false, null);

    }

    public static synchronized int getNextOrderId(){
        return NextOrderId++;
    }
    @Override public void nextValidId(int orderId) {
        NextOrderId = orderId;
        System.out.println("nextValidId --> "+EWrapperMsgGenerator.nextValidId(orderId));
    }

    @Override public void error(Exception e) {
        System.out.println(EWrapperMsgGenerator.error(e));
    }

    @Override public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        System.out.println(EWrapperMsgGenerator.error(id, errorCode, errorMsg, advancedOrderRejectJson));
    }

    @Override public void connectionClosed() {
        System.out.println(EWrapperMsgGenerator.connectionClosed());
    }

    @Override public void error(String str) {
        System.out.println(EWrapperMsgGenerator.error(str));
    }

    @Override public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
        System.out.println("tickPrice --> "+EWrapperMsgGenerator.tickPrice(tickerId, field, price, attribs));
    }

    @Override public void tickSize(int tickerId, int field, Decimal size) {
        System.out.println("tickSize --> "+EWrapperMsgGenerator.tickSize(tickerId, field, size));
    }

    @Override public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        System.out.println("tickOptionComputation --> "+EWrapperMsgGenerator.tickOptionComputation(tickerId, field, tickAttrib, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice));
    }

    @Override public void tickGeneric(int tickerId, int tickType, double value) {
        System.out.println("tickGeneric --> "+EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value));
    }

    @Override public void tickString(int tickerId, int tickType, String value) {
        System.out.println("tickString --> "+EWrapperMsgGenerator.tickString(tickerId, tickType, value));
    }

    @Override public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {
        System.out.println("tickEFP --> "+EWrapperMsgGenerator.tickEFP( tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureLastTradeDate, dividendImpact, dividendsToLastTradeDate));
    }

    @Override public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        String order_info=EWrapperMsgGenerator.orderStatus( orderId,  status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice);
        System.out.println("orderStatus --> "+order_info);
        //   this.ordersManagement.updateProccess(order_info);
    }

    @Override public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        String order_info=EWrapperMsgGenerator.openOrder( orderId, contract, order, orderState);
        System.out.println("openOrder --> "+order_info);
        this.ordersManagement.add(new Order_info(order_info));
    }

    @Override public void openOrderEnd() {
        System.out.println(EWrapperMsgGenerator.openOrderEnd());
    }

    @Override public void updateAccountValue(String key, String value, String currency, String accountName) {
        System.out.println(EWrapperMsgGenerator.updateAccountValue( key, value, currency, accountName));
    }

    @Override public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        System.out.println(EWrapperMsgGenerator.updatePortfolio( contract, position, marketPrice, marketValue, averageCost, unrealizedPNL, realizedPNL, accountName));

    }

    @Override public void updateAccountTime(String timeStamp) {
        System.out.println(EWrapperMsgGenerator.updateAccountTime( timeStamp));
    }

    @Override public void accountDownloadEnd(String accountName) {
        System.out.println(EWrapperMsgGenerator.accountDownloadEnd(accountName));
    }

    @Override public void contractDetails(int reqId, ContractDetails contractDetails) {
        System.out.println(EWrapperMsgGenerator.contractDetails( reqId, contractDetails));
        data+=EWrapperMsgGenerator.contractDetails( reqId, contractDetails)+"\n";

    }

    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        System.out.println(EWrapperMsgGenerator.bondContractDetails( reqId, contractDetails));
    }

    @Override public void contractDetailsEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.contractDetailsEnd(reqId));
    }

    @Override public void execDetails(int reqId, Contract contract, Execution execution) {
        String info=EWrapperMsgGenerator.execDetails( reqId, contract, execution);
        System.out.println(info);
        ordersManagement.add(new Order_info(info));
    }

    @Override public void execDetailsEnd(int reqId) {

        System.out.println(EWrapperMsgGenerator.execDetailsEnd( reqId));
        flag=true;

    }

    @Override public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {
        System.out.println(EWrapperMsgGenerator.updateMktDepth(tickerId, position, operation, side, price, size));
    }

    @Override public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, Decimal size, boolean isSmartDepth) {
        System.out.println(EWrapperMsgGenerator.updateMktDepthL2( tickerId, position, marketMaker, operation, side, price, size, isSmartDepth));
    }

    @Override public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        System.out.println(EWrapperMsgGenerator.updateNewsBulletin( msgId, msgType, message, origExchange));
    }

    @Override public void managedAccounts(String accountsList) {
        System.out.println(EWrapperMsgGenerator.managedAccounts( accountsList));
    }

    @Override public void receiveFA(int faDataType, String xml) {
        System.out.println(EWrapperMsgGenerator.receiveFA( faDataType, xml));
    }

    @Override public void historicalData(int reqId, Bar bar) {
        String info=EWrapperMsgGenerator.historicalData( reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap());
        historical += info;
//        System.out.println(info);

    }

    @Override public void scannerParameters(String xml) {
        System.out.println(EWrapperMsgGenerator.scannerParameters(xml));
    }

    @Override public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {
        System.out.println(EWrapperMsgGenerator.scannerData( reqId, rank, contractDetails, distance, benchmark, projection, legsStr));
    }

    @Override public void scannerDataEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.scannerDataEnd(reqId));
    }

    @Override public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume, Decimal wap, int count) {
        System.out.println(EWrapperMsgGenerator.realtimeBar( reqId, time, open, high, low, close, volume, wap, count));
    }

    @Override public void currentTime(long time) {
        System.out.println(EWrapperMsgGenerator.currentTime( time));
    }

    @Override public void fundamentalData(int reqId, String data) {
        System.out.println(EWrapperMsgGenerator.fundamentalData( reqId,  data));
    }

    @Override public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
        System.out.println(EWrapperMsgGenerator.deltaNeutralValidation( reqId, deltaNeutralContract));
    }

    @Override public void tickSnapshotEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.tickSnapshotEnd( reqId));
    }

    @Override public void marketDataType(int reqId, int marketDataType) {
        System.out.println(EWrapperMsgGenerator.marketDataType( reqId, marketDataType));
    }

    @Override public void commissionReport(CommissionReport commissionReport) {
        System.out.println(EWrapperMsgGenerator.commissionReport( commissionReport));
    }

    @Override public void position(String account, Contract contract, Decimal pos, double avgCost) {
        String info=EWrapperMsgGenerator.position( account,  contract,  pos,  avgCost);
//        ordersManagement.add(info);

    }

    @Override public void positionEnd() {
        System.out.println(EWrapperMsgGenerator.positionEnd());
    }

    @Override public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        System.out.println(EWrapperMsgGenerator.accountSummary( reqId, account, tag, value, currency));
    }

    @Override public void accountSummaryEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.accountSummaryEnd( reqId));
    }

    @Override public void verifyMessageAPI( String apiData) {
    }

    @Override public void verifyCompleted( boolean isSuccessful, String errorText){
    }

    @Override public void verifyAndAuthMessageAPI( String apiData, String xyzChallenge) {
    }

    @Override public void verifyAndAuthCompleted( boolean isSuccessful, String errorText){
    }

    @Override public void displayGroupList( int reqId, String groups){
    }

    @Override public void displayGroupUpdated( int reqId, String contractInfo){
    }

    @Override public void positionMulti( int reqId, String account, String modelCode, Contract contract, Decimal pos, double avgCost) {
        System.out.println(EWrapperMsgGenerator.positionMulti( reqId, account, modelCode, contract, pos, avgCost));
    }

    @Override public void positionMultiEnd( int reqId) {
        System.out.println(EWrapperMsgGenerator.positionMultiEnd( reqId));
    }

    @Override public void accountUpdateMulti( int reqId, String account, String modelCode, String key, String value, String currency) {
        System.out.println(EWrapperMsgGenerator.accountUpdateMulti( reqId, account, modelCode, key, value, currency));
    }

    @Override public void accountUpdateMultiEnd( int reqId) {
        System.out.println(EWrapperMsgGenerator.accountUpdateMultiEnd( reqId));
    }

    public void connectAck() {
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass,
                                                    String multiplier, Set<String> expirations, Set<Double> strikes) {
        System.out.println(EWrapperMsgGenerator.securityDefinitionOptionalParameter( reqId, exchange, underlyingConId, tradingClass, multiplier, expirations, strikes));
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.securityDefinitionOptionalParameterEnd( reqId));
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        System.out.println(EWrapperMsgGenerator.softDollarTiers( reqId,tiers));
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        System.out.println(EWrapperMsgGenerator.familyCodes(familyCodes));
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        System.out.println(EWrapperMsgGenerator.symbolSamples( reqId, contractDescriptions));
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        // System.out.println(EWrapperMsgGenerator.historicalDataEnd( reqId, startDateStr, endDateStr));
        historicalFlag=true;
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        System.out.println(EWrapperMsgGenerator.mktDepthExchanges(depthMktDataDescriptions));
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline,
                         String extraData) {
        System.out.println(EWrapperMsgGenerator.tickNews(tickerId, timeStamp, providerCode, articleId, headline, extraData));
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {
        System.out.println(EWrapperMsgGenerator.smartComponents(reqId, theMap));
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
        System.out.println(EWrapperMsgGenerator.tickReqParams(tickerId, minTick, bboExchange, snapshotPermissions));
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        System.out.println(EWrapperMsgGenerator.newsProviders(newsProviders));
    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
        System.out.println(EWrapperMsgGenerator.newsArticle(requestId, articleType, articleText));
    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
        System.out.println(EWrapperMsgGenerator.historicalNews(requestId, time, providerCode, articleId, headline));
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
        System.out.println(EWrapperMsgGenerator.historicalNewsEnd(requestId, hasMore));
    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        System.out.println(EWrapperMsgGenerator.headTimestamp(reqId, headTimestamp));
    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {
        System.out.println(EWrapperMsgGenerator.histogramData(reqId, items));
    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        historicalData(reqId, bar);
    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
        System.out.println(EWrapperMsgGenerator.rerouteMktDataReq(reqId, conId, exchange));
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
        System.out.println(EWrapperMsgGenerator.rerouteMktDepthReq(reqId, conId, exchange));
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
        System.out.println(EWrapperMsgGenerator.marketRule(marketRuleId, priceIncrements));
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        System.out.println(EWrapperMsgGenerator.pnl(reqId, dailyPnL, unrealizedPnL, realizedPnL));
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {
        System.out.println(EWrapperMsgGenerator.pnlSingle(reqId, pos, dailyPnL, unrealizedPnL, realizedPnL, value));
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {
        for (HistoricalTick tick : ticks) {
            System.out.println(EWrapperMsgGenerator.historicalTick(reqId, tick.time(), tick.price(), tick.size()));
        }
    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
        for (HistoricalTickBidAsk tick : ticks) {
            System.out.println(EWrapperMsgGenerator.historicalTickBidAsk(reqId, tick.time(), tick.tickAttribBidAsk(), tick.priceBid(), tick.priceAsk(), tick.sizeBid(),
                    tick.sizeAsk()));
        }
    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
        for (HistoricalTickLast tick : ticks) {
            System.out.println(EWrapperMsgGenerator.historicalTickLast(reqId, tick.time(), tick.tickAttribLast(), tick.price(), tick.size(), tick.exchange(),
                    tick.specialConditions()));
        }
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size, TickAttribLast tickAttribLast,
                                  String exchange, String specialConditions) {
        System.out.println(EWrapperMsgGenerator.tickByTickAllLast(reqId, tickType, time, price, size, tickAttribLast, exchange, specialConditions));
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize, Decimal askSize,
                                 TickAttribBidAsk tickAttribBidAsk) {
        System.out.println(EWrapperMsgGenerator.tickByTickBidAsk(reqId, time, bidPrice, askPrice, bidSize, askSize, tickAttribBidAsk));
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        System.out.println(EWrapperMsgGenerator.tickByTickMidPoint(reqId, time, midPoint));
    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {
        System.out.println(EWrapperMsgGenerator.orderBound(orderId, apiClientId, apiOrderId));
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        System.out.println(EWrapperMsgGenerator.completedOrder(contract, order, orderState));
        System.out.println("completeOrder function ... need to overide");
    }

    @Override
    public void completedOrdersEnd() {
        System.out.println(EWrapperMsgGenerator.completedOrdersEnd());
        System.out.println("completedOrdersEnd function ... need to overide");
    }

    @Override
    public void replaceFAEnd(int reqId, String text) {
        System.out.println(EWrapperMsgGenerator.replaceFAEnd(reqId, text));
    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {
        System.out.println(EWrapperMsgGenerator.wshMetaData(reqId, dataJson));
    }

    @Override
    public void wshEventData(int reqId, String dataJson) {
        System.out.println(EWrapperMsgGenerator.wshEventData(reqId, dataJson));
    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {
        System.out.println(EWrapperMsgGenerator.historicalSchedule(reqId, startDateTime, endDateTime, timeZone, sessions));
    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {
        System.out.println(EWrapperMsgGenerator.userInfo(reqId, whiteBrandingId));
    }
}


