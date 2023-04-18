package mycode.junit_testing;

import mycode.object.Buy;
import mycode.object.Greeks;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BuyTestinh {
    @Test
    public void buy() {
        double delta=0.00001;
        Buy buy_call=new Buy(new OptionCall(130, 3.2,new Greeks(0.8, 0, 0, 0)));
        assertEquals(130+3.2, buy_call.getBreckEven(),0);
        assertEquals(0.8, buy_call.probabilityOfProfit(),delta);
  //      assertEquals(100*7, buy_call.getProfit(),delta);
        /////////////////////////////////////////////////////////////////////////////
        Buy buy_call2=new Buy(new OptionCall(10, 3.2,new Greeks(0.1, 0, 0, 0)));
        assertEquals(10+3.2, buy_call2.getBreckEven(),0);
        //	assertEquals(0.8, buy_call.probability());

        /////////////////////////////////////////////////////////////////////////////////////////
        Buy buy_put=new Buy(new OptionPut(30, 1.2,new Greeks(0.4, 0, 0, 0)));
        assertEquals(30-1.2, buy_put.getBreckEven(),0);
        assertEquals(0.4, buy_put.probabilityOfProfit(),delta);
        //		assertEquals(100*0.5, buy_put.getProfit());
        /////////////////////////////////////////////////////////////////////////////////////////

        Buy buy_put2=new Buy(new OptionPut(300, 2.2,new Greeks(0.72, 0, 0, 0)));
        assertEquals(300-2.2, buy_put2.getBreckEven(),0);
        assertEquals(0.72, buy_put2.probabilityOfProfit(),delta);
   //     assertEquals(100*7, buy_put2.getProfit(),0);
        ///////////////////////////////////////////////////////////////////////////////////////////

    }

}
