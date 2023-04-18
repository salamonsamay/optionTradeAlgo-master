package mycode.junit_testing;

import mycode.help.MyMath;
import mycode.object.*;
import mycode.strategy_.BullSpread;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BullSpreadTesting {
    @Test
    public void BullSpredPut() {
        double delta=0.000001;
        double commission=4;
        //assume underlying_asset is 370
        Sell sell=new Sell(new OptionPut(350,6,6,new Greeks(0.35,1,2,1)));
        Buy buy=new Buy(new OptionPut(300,4,4,new Greeks(0.05,1,2,1)));

        BullSpread bull=new BullSpread(buy,sell);
        assertEquals( -4800-commission,bull.maxLoss(),delta);
        assertEquals(200-commission,bull.maxProfit(),delta);
//		System.out.println(bull.probabilityOfLost());
        assertEquals(0.05,bull.probabilityOfMaxLoss(),delta);
        assertEquals(((200-commission)*0.65+((-4800-commission)*(0.05+0.288))),bull.averageOfReturn(),delta);
   //     assertEquals(((200-commission)*0.65+((-4800-commission)*(0.05+0.288))),bull.averageOfReturn(),delta);

///////////////////////////////////////////////////////////////////////////////////////////
        BullSpread bull2=new BullSpread("145,8.8,8.8,P","155,3.99,3.99,P");
        assertEquals(-1481-commission,bull2.maxLoss(),delta);
        assertEquals(-481-commission,bull2.maxProfit(),delta);
        assertEquals(4.81,bull2.price(),delta);
//////////////////////////////////////////////////////////////////////////////////////////
    }

    @Test
    public void BullSpredCall(){
        double delta=0.000001;
        double commission=4;
        BullSpread bull=new BullSpread("145,8.8,8.8,C","155,3.99,3.99,C");
        assertEquals(-481-commission,bull.maxLoss(),delta);
        assertEquals(519-commission,bull.maxProfit(),delta);
        assertEquals(4.81,bull.price(),delta);


        //assume underlying_asset is 140
        Sell sell=new Sell(new OptionCall(120,5,5,new Greeks(0.80,1,2,1)));
        Buy buy=new Buy(new OptionCall(100,7,7,new Greeks(0.95,1,2,1)));
        BullSpread bullCall=new BullSpread(buy,sell);
        assertEquals(-200-commission,bullCall.maxLoss(),delta);
        assertEquals(1800-commission,bullCall.maxProfit(),delta);
        assertEquals(0.80,bullCall.probabilityOfMaxProfit(),delta);
        assertEquals(0.05,bullCall.probabilityOfMaxLoss(),delta);
        assertEquals((1800-commission)*0.80+((-200-commission)*(0.05+0.015)),bullCall.averageOfReturn(),delta);
        assertEquals(2,bullCall.price(),delta);

        //assertEquals(4.81,bull.price(),delta);
    }

}
