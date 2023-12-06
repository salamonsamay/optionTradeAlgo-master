package mycode.data;





import static apidemo.util.Util.sleep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.ib.client.*;
import mycode.help.Tools;
import mycode.object.Order_info;

import mycode.trade.OrdersManagement;
import mycode.trade.Transaction;
import org.json.simple.parser.ParseException;

import javax.swing.*;

public class LoadData implements EWrapper {
    public EJavaSignal m_signal = new EJavaSignal();
    public EClientSocket m_s = new EClientSocket(this, m_signal);
    public int NextOrderId = -1;
    public OrdersManagement ordersManagement=new OrdersManagement(m_s);

    public static String data="";
    public  static boolean isEnd =false;
    public  static boolean isValid =true;

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {



        //   ArrayList<String > list=Init.read2();
//
        LoadData loadData=new LoadData();
//        ArrayList<String> symbols=Tools.readCompanyFromFile();
        ArrayList<String> symbols=new  ArrayList<String>();
        symbols.add("SPX");
       // loadData.run2("SPX");
        loadData.run(symbols);
    }

    public void run(ArrayList<String> symbols) throws InterruptedException {
        // Connect to Interactive Brokers Gateway
        m_s.eConnect("127.0.0.1", 4002, 0);

        // Uncomment the line below to connect to TWS Demo ACCOUNT or TWS Real ACCOUNT
       //  m_s.eConnect("127.0.0.1", 7497, 0);

        // Uncomment the line below to connect to TWS Real ACCOUNT
        // m_s.eConnect("127.0.0.1", 7496, 0);

        // Uncomment the lines below if you need to perform additional actions after connecting
        // Thread.sleep(2000);
        // m_s.reqGlobalCancel();

        final EReader reader = new EReader(m_s, m_signal);
        reader.start();

        // Start a new thread to process messages from Interactive Brokers
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

        // Process each symbol in the list
        for (String symbol : symbols) {
            // Request contract details
            m_s.reqContractDetails(251, Transaction.createOptContract(symbol));
            System.out.println("Start: " + symbol);

            // Wait for the processing to finish
            while (!isEnd) {
                Thread.sleep(100);
            }

            isEnd = false;

            // Continue to the next symbol if the current one is not valid
            if (!isValid) {
                isValid = true;
                System.out.println("Continue");
                continue;
            }

            System.out.println("-----");

            // Handle file operations
            File file = new File(Tools.PATH + symbol + ".txt");

            try {
                System.out.println("Start writing: " + symbol);
                System.out.println();

                // Read existing information from the file
                Scanner scanner = new Scanner(file);
                StringBuilder oldInfo = new StringBuilder();
                boolean oldInfoExists = false;

                while (scanner.hasNextLine()) {
                    oldInfo.append(scanner.nextLine()).append("\n");
                    oldInfoExists = true;
                }

                scanner.close();

                if (oldInfoExists) {
                    System.out.println("End writing old info: " + symbol);
                }

                // Write new information to the file
                PrintWriter printWriter = new PrintWriter(file);
                String newInfo = Tools.extract_and_write_contractID(symbol);
                printWriter.print(oldInfo + newInfo);
                data = "";
                System.out.println("End converting: " + symbol);

                printWriter.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // Disconnect from Interactive Brokers
        m_s.eDisconnect();
    }


    public void run2(String symbol){
         // m_s.eConnect("127.0.0.1", 4002, 0);//ib getway demo
        m_s.eConnect("127.0.0.1", 7497, 0);//tws getway demo

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

        m_s.reqContractDetails(251, Transaction.createOptContract(symbol));
        while(!isEnd){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(data);

    }


    public  void clear() throws FileNotFoundException {
        File file=new File(Tools.PATH);

        for(int i=0;i<file.list().length;i++){
            File file2= new File(Tools.PATH+file.list()[i]);

            PrintWriter pw =new PrintWriter(file2);
            pw.println();
            pw.close();
        }

    }



    @Override public void nextValidId(int orderId) {
        NextOrderId = orderId;
        System.out.println(EWrapperMsgGenerator.nextValidId(orderId));
    }

    @Override public void error(Exception e) {
        System.out.println(EWrapperMsgGenerator.error(e));

    }

    @Override public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        System.out.println(EWrapperMsgGenerator.error(id, errorCode, errorMsg, advancedOrderRejectJson));
        String value=EWrapperMsgGenerator.error(id, errorCode, errorMsg, advancedOrderRejectJson);
        String error1="-1 | 2104 | Market data farm connection is OK:usfarm";
        String error2="-1 | 2106 | HMDS data farm connection is OK:ushmds";
        String error3="-1 | 2107 | HMDS data farm connection is inactive but should be available upon demand.ushmds";
        String error4="-1 | 2158 | Sec-def data farm connection is OK:secdefil";
        String error5="-1 | 1102 | Connectivity between IB and TWS has been restored - data maintained. The following farms are connected: usfarm; secdefil. The following farms are not connected: ushmds.";
        String error6="-1 | 1100 | Connectivity between IB and TWS has been lost.";
        String error7="-1 | 2157 | Sec-def data farm connection is broken:secdefil";
        String error8="-1 | 2103 | Market data farm connection is broken:usfarm";
        String error9="-1 | 1100 | Connectivity between IB and TWS has been lost.";
        String error10="-1 | 2105 | HMDS data farm connection is broken:ushmds";
        // String error11="-1 | 2172 | The version of the application you are running, 1018.1, needs to be upgraded, as it will be desupported on 20230718. The minimum supported version at that time will be 1019.2.";

        if(!value.equals(error1) && !value.equals(error2) && !value.equals(error3) && !value.equals(error4)
                && !value.equals(error5) && !value.equals(error6) && !value.equals(error7)
                && !value.equals(error8) && !value.equals(error9) && !value.equals(error10))
        {

            isValid=false;
            isEnd=true;
        }


    }

    @Override public void connectionClosed() {
        System.out.println(EWrapperMsgGenerator.connectionClosed());
    }

    @Override public void error(String str) {
        System.out.println(EWrapperMsgGenerator.error(str));
    }

    @Override public void tickPrice(int tickerId, int field, double price, TickAttrib attribs) {
        System.out.println(EWrapperMsgGenerator.tickPrice(tickerId, field, price, attribs));
    }

    @Override public void tickSize(int tickerId, int field, Decimal size) {
        System.out.println(EWrapperMsgGenerator.tickSize(tickerId, field, size));
    }

    @Override public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        System.out.println(EWrapperMsgGenerator.tickOptionComputation(tickerId, field, tickAttrib, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice));
    }

    @Override public void tickGeneric(int tickerId, int tickType, double value) {
        System.out.println(EWrapperMsgGenerator.tickGeneric(tickerId, tickType, value));
    }

    @Override public void tickString(int tickerId, int tickType, String value) {
        System.out.println(EWrapperMsgGenerator.tickString(tickerId, tickType, value));
    }

    @Override public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {
        System.out.println(EWrapperMsgGenerator.tickEFP( tickerId, tickType, basisPoints, formattedBasisPoints, impliedFuture, holdDays, futureLastTradeDate, dividendImpact, dividendsToLastTradeDate));
    }

    @Override public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        String order_info=EWrapperMsgGenerator.orderStatus( orderId,  status, filled, remaining, avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld, mktCapPrice);
        System.out.println(order_info);
        System.out.println("orderStatus");

    }

    @Override public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        String order_info=EWrapperMsgGenerator.openOrder( orderId, contract, order, orderState);
        System.out.println(order_info);
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
        System.out.println("accountDownloadEnd");
    }

    @Override public void contractDetails(int reqId, ContractDetails contractDetails) {
        System.out.println(EWrapperMsgGenerator.contractDetails( reqId, contractDetails));
        data+=EWrapperMsgGenerator.contractDetails( reqId, contractDetails)+"\n";

    }

    @Override public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        System.out.println(EWrapperMsgGenerator.bondContractDetails( reqId, contractDetails));
        System.out.println("bondContractDetails");
    }

    @Override public void contractDetailsEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.contractDetailsEnd(reqId));
        System.out.println("------>contractDetailsEnd");
        isEnd=true;
    }

    @Override public void execDetails(int reqId, Contract contract, Execution execution) {
        String info=EWrapperMsgGenerator.execDetails( reqId, contract, execution);
        System.out.println(info);

    }

    @Override public void execDetailsEnd(int reqId) {
        System.out.println(EWrapperMsgGenerator.execDetailsEnd( reqId));
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
        System.out.println(EWrapperMsgGenerator.historicalData( reqId, bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume(), bar.count(), bar.wap()));
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
        System.out.println(EWrapperMsgGenerator.position( account,  contract,  pos,  avgCost));

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
        System.out.println(EWrapperMsgGenerator.historicalDataEnd( reqId, startDateStr, endDateStr));
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


