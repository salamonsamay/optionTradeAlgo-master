package mycode.junit_testing;

import mycode.object.*;
import mycode.strategy_.BearSpread;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BearSpreadTesting {
    @Test
    public  void bearSpredCAll() {
        double delta=0.000001;
        double commission=4;
        //asuume underlying_asset is 190
        Sell sell=new Sell(new OptionCall(200,8.3,8.3,new Greeks(0.2,1,2,1)));
        Buy buy=new Buy(new OptionCall(210,3.2,3.2,new Greeks(0.05,1,2,1)));

        BearSpread bear=new BearSpread(sell,buy);
        assertEquals(bear.maxLoss(), -490-commission,delta);
        assertEquals(bear.maxProfit(), 510-commission,delta);
		assertEquals(0.8,bear.probabilityOfMaxProfit(),delta);
        assertEquals(0.05,bear.probabilityOfMaxLoss(),delta);
        assertEquals(0.8*(510-commission)+(0.05+0.0735)*(-490-commission),bear.averageOfReturn(),delta);
        assertEquals((3.2-8.3),bear.price(),delta);

     //   assertEquals(bear.averageOfReturn(), 510*0.2+(-490)*0.8,delta);

        BearSpread bear2=new BearSpread("310,8,8,C","320,5,5,C");
        assertEquals( -700-commission,bear2.maxLoss(),delta);
        assertEquals(300-commission,bear2.maxProfit(),delta);

        assertEquals(-3,bear2.price(),delta);


    }

    @Test
    public  void bearSpredPut() {
        double delta=0.00001;
        double commission=4;
        Sell sell=new Sell(new OptionPut(200,3.2,new Greeks(0.8,1,2,1)));
        Buy buy=new Buy(new OptionPut(210,8.3,new Greeks(0.3,1,2,1)));
        sell.getOpt().setTicker("O:CRH221021P00035000");
        sell.getOpt().setUnderlying_ticker("CRH");
        sell.getOpt().setExpiration_date("2022-10-21");
        buy.getOpt().setTicker("O:CRH221021P00060000");
        buy.getOpt().setUnderlying_ticker("CRH");
        buy.getOpt().setExpiration_date("2022-10-21");

        BearSpread bear=new BearSpread(sell,buy);
        assertEquals( -510-commission,bear.maxLoss(),delta);
        assertEquals( 490-commission,bear.maxProfit(),delta);
        assertEquals(0.8,bear.probabilityOfMaxProfit(),0);
		assertEquals(bear.probabilityOfMaxLoss(),0.7,delta);

        BearSpread bear2=new BearSpread("120,2,2,P","130,5,5,P");
        assertEquals(700-commission,bear2.maxProfit(),delta);
        assertEquals(-300-commission,bear2.maxLoss(),delta);
        assertEquals(3,bear2.price(),delta);
    }

}
