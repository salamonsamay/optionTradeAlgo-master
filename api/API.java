package api;

import apidemo.Test;
import com.ib.client.*;
import com.ib.controller.ApiController;
import samples.testbed.EWrapperImpl;
import samples.testbed.contracts.ContractSamples;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class API implements ApiController.IConnectionHandler {

    static API INSTANCE = new API();
    Logger m_inLogger = new Logger();
    Logger m_outLogger = new Logger();
    public  ApiController m_controller = new ApiController( this, m_inLogger, m_outLogger);

    public static void main(String[] args) {
     //   INSTANCE.run();

        API.INSTANCE.m_controller.connect( "127.0.0.1", 7497, 0,null);
        INSTANCE.test();

    }

    public void test(){
        Contract contract = new Contract();

        //Watch out for the spaces within the local symbol!
        contract.symbol("AAPL");
        contract.secType("OPT");
        contract.exchange("SMART");
        contract.lastTradeDateOrContractMonth("20221021");
        contract.currency("USD") ;
        contract.right("C");
        contract.strike(100);

        Order order=new Order();
        order.clientId(0);
        order.account("DU6156906");
        order.totalQuantity(Decimal.ONE_HUNDRED);
        order.action(Types.Action.BUY);
        order.orderType(OrderType.MKT);
        m_controller.placeOrModifyOrder(combo(),order,null);


    }

    void run() {
        // make initial connection to local host, port 7496, client id 0
        m_controller.connect( "127.0.0.1", 7497, 0,null);
        // Your implementation
        Contract contract = new Contract();

        //Watch out for the spaces within the local symbol!
        contract.symbol("PLTR");
        contract.secType("OPT");
        contract.exchange("SMART");

//        contract.primaryExch("ISLAND");
        contract.lastTradeDateOrContractMonth("20221021");
        contract.currency("USD") ;
        contract.right("C");
        contract.strike(10);
  //      contract.multiplier("100");

        Order order=new Order();
        order.clientId(0);
        order.account("DU6156906");
        order.totalQuantity(Decimal.ONE_HUNDRED);
        order.action(Types.Action.BUY);
        order.orderType(OrderType.MKT);


        m_controller.placeOrModifyOrder(contract,order,null);
       // m_controller.disconnect();


    }

    Contract combo() {

        Contract contract = new Contract();

        contract.symbol("AAPL");
        contract.secType("OPT");
        contract.currency("USD");
        contract.exchange("BOX");
        contract.lastTradeDateOrContractMonth("20221118");
        contract.right("C");
        contract.strike(100);
        contract.multiplier("100");
 //       contract.lastTradeDateOrContractMonth("20221118");

        ComboLeg leg1 = new ComboLeg();
        ComboLeg leg2 = new ComboLeg();

        List<ComboLeg> addAllLegs = new ArrayList<>();
        leg1.conid(536593638);//130
        leg1.ratio(1);
        leg1.action("BUY");
        leg1.exchange("BOX");
        leg2.conid(539222593);//135
        leg2.ratio(1);
        leg2.action("SELL");
        leg2.exchange("BOX");
        addAllLegs.add(leg1);
        addAllLegs.add(leg2);

        contract.comboLegs(addAllLegs);
        return contract;
    }

    // Abstract methods from IConnectionHandler implementation
    @Override
    public void connected() {

        m_controller.connect( "127.0.0.1", 7497, 0,null);
        System.out.println("connect to 127.0.0.1 port "+7497);
        try {
            Thread.sleep(333);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public void disconnected() {
        try {
            Thread.sleep(3323);

            m_controller.disconnect();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void accountList(List<String> list) {

    }


    public void accountList(ArrayList<String> list) { }

    @Override
    public void error(Exception e) { }

    @Override
    public void message(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {

    }


    public void message(int id, int errorCode, String errorMsg) { }

    @Override
    public void show(String string) { }

}