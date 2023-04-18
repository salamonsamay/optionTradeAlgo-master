package mycode.junit_testing;

import mycode.object.Greeks;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import mycode.object.Sell;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SellTesting {

    @Test
    public void test(){
        Sell sellCall1=new Sell(new OptionCall(200, 7,new Greeks(0.5, 0, 0, 0)));
        assertEquals(200+7, sellCall1.getBreckEven(),0);
        assertEquals(1-0.5, sellCall1.probabilityOfProfit(),0);
        assertEquals(100*7, sellCall1.getProfit(),0);
        /////////////////////////////////////////////////////////////////////////////
        Sell sellCall2=new Sell(new OptionCall(30, 2,new Greeks(0.2, 0.3, 0.02, 0.19)));
        assertEquals(30+2, sellCall2.getBreckEven(),0.0001);
        assertEquals(1-0.2, sellCall2.probabilityOfProfit(),0.0001);
        assertEquals(100*2, sellCall2.getProfit(),0.0001);

        /////////////////////////////////////////////////////////////////////////////////////////
        Sell sell_put=new Sell(new OptionPut(45.4, 0.5,new Greeks(0.2, 0.2, 0.3, 0.1)));
        assertEquals(45.4-0.5, sell_put.getBreckEven(),0);
        assertEquals(1-0.2, sell_put.probabilityOfProfit(),0.0001);
        assertEquals(100*0.5, sell_put.getProfit(),0.0001);
        /////////////////////////////////////////////////////////////////////////////////////////

        Sell sell_put2=new Sell(new OptionPut(200, 7,new Greeks(0.5, 0, 0, 0)));
        assertEquals(200-7, sell_put2.getBreckEven(),0);
        assertEquals(1-0.5, sell_put2.probabilityOfProfit(),0.0001);
        assertEquals(100*7, sell_put2.getProfit(),0.0001);
        ///////////////////////////////////////////////////////////////////////////////////////////

    }
}
