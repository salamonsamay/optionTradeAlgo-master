package mycode.junit_testing;

import mycode.object.*;
import mycode.strategy_.BearSpread;
import mycode.strategy_.BullSpread;
import mycode.strategy_.IronCondor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IronCondorTesting {

    @Test
    public void IronConder(){
        double delta=0.000001;
        double commission=8;
        Option sold_c=new OptionCall(550,7.89, 7.89, new Greeks(0.4,0.4,0.2,0.3));//sold
        Option bought_c=new OptionCall(600,1.94, 1.94, new Greeks(0.1,80.4,0.2,0.3));
        Option sold_p=new OptionPut(450,6.15, 6.15, new Greeks(0.4,0.4,0.2,0.3));
        Option bought_p=new OptionPut(400,0.72, 0.72, new Greeks(0.1,0.4,0.2,0.3));//bought

        Sell sell_call=new Sell(sold_c);
        Buy buy_call=new Buy(bought_c);

        Sell sell_put=new Sell(sold_p);
        Buy buy_put=new Buy(bought_p);

        BearSpread bearSpread =new BearSpread(sell_call,buy_call);
        BullSpread bullSpread =new BullSpread(buy_put,sell_put);

        IronCondor ironCondor=new IronCondor(bullSpread, bearSpread);

        //prime=buy Ask- sell Bid

        assertEquals(1.94-7.89, bearSpread.price(),delta);
        assertEquals(0.72-6.15, bullSpread.price(),delta);
        assertEquals((1.94-7.89)+(0.72-6.15),ironCondor.price(),delta);

        assertEquals(-3862-commission,ironCondor.maxLoss(),delta);
        assertEquals(1138-commission,ironCondor.maxProfit(),delta);

        assertEquals(0.20,ironCondor.probabilityOfMaxLoss(),delta);
        assertEquals(0.20,ironCondor.probabilityOfMaxProfit(),delta);
//        assertEquals(3,ironCondor.averageOfReturn());
//        assertEquals(0.20,ironCondor.averageOfReturn(),delta);

//        assertEquals();
//        assertEquals(3,bear2.price(),delta);
    }
}
