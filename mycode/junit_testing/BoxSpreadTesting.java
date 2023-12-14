package mycode.junit_testing;

import mycode.strategy_.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoxSpreadTesting {
//    @Test
//    public void boxSpread(){
//        double delta=0.000001;
//        double commission=8;
//        BullSpread bullCall=new BullSpread("40,7,7,C","50,2,2,C");
//        BearSpread bearPut=new BearSpread("40,2,2,P","50,6.5,6.5,P");
//        BoxSpread box=new BoxSpread(bullCall,bearPut);
//        assertEquals(9.5,box.price(),delta);
//        assertEquals(50-commission,box.maxProfit(),delta);//(10-9.5)*100
//        assertEquals(50-commission,box.maxLoss(),delta);//(10-9.5)*100
//////////////////////////////////////////////////////////////////////////////////////////
//        BullSpread bullCall2=new BullSpread("20,6.5,6.5,C","30,1.5,1.5,C");
//        BearSpread bearPut2=new BearSpread("20,1.5,1.5,P","30,6.0,6.0,P");
//        BoxSpread box2=new BoxSpread(bullCall2,bearPut2);
//        assertEquals(50-commission,box2.maxProfit(),delta);
//        assertEquals(50-commission,box2.maxLoss(),delta);
//        assertEquals(9.5,box2.price(),delta);
//        System.out.println(box2.maxProfit());
//
//        ///////////////////////////short box spred////////////////////////////////////
//        BullSpread bullPut=new BullSpread("50,2,2,P","60,7,7,P");
//        BearSpread bearCall=new BearSpread("50,7,7,C","60,1.5,1.5,C");
//
//        BoxSpread box3=new BoxSpread(bullPut,bearCall);
//        assertEquals(50-commission,box3.maxProfit(),delta);
//        assertEquals(50-commission,box3.maxLoss(),delta);
//        assertEquals(-10.5,box3.price(),delta);
//
//
//    }

    @Test
    public void longBoxSpread(){
        double delta=0.0001;
        double commission=8;
        BullSpread bullCall=new BullSpread("40,7,7,C","50,2,2,C");
        bullCall.sell.getOpt().setExpiration_date("2024-03-02");
        bullCall.buy.getOpt().setExpiration_date("2024-03-02");
        BearSpread bearPut=new BearSpread("40,2,2,P","50,6.5,6.5,P");
        bearPut.sell.getOpt().setExpiration_date("2024-03-02");
        bearPut.buy.getOpt().setExpiration_date("2024-03-02");

        LongBoxSpread longBox=new LongBoxSpread(bullCall,bearPut);
        System.out.println(longBox.getInterestRate());
        assertEquals(9.5,longBox.price(),delta);
        assertEquals(50-commission,longBox.maxProfit(),delta);//(10-9.5)*100
        assertEquals(50-commission,longBox.maxLoss(),delta);//(10-9.5)*100
        assertEquals(4.384133,longBox.getInterestRate(),delta);//(10-9.5)*100
        System.out.println(longBox.maxProfit());



//        assertEquals(((10/9.5)/longBox.bearSpread.daysToExpiration())*365,longBox.yearlyInterestRate(),delta);//(10-9.5)*100

    }
    @Test
    public void shortBoxSpread(){
        double delta=0.0001;
        double commission=8;
        BullSpread bullPut=new BullSpread("50,2,2,P","60,7,7,P");
        BearSpread bearCall=new BearSpread("50,7,7,C","60,1.5,1.5,C");
        ShortBoxSpread shortBoxSpread=new ShortBoxSpread(bullPut,bearCall);
        System.out.println(shortBoxSpread.getInterestRate());
        assertEquals(50-commission,shortBoxSpread.maxProfit(),delta);
        assertEquals(50-commission,shortBoxSpread.maxLoss(),delta);
        assertEquals(-10.5,shortBoxSpread.price(),delta);
        assertEquals(-4.030710,shortBoxSpread.getInterestRate(),delta);
        System.out.println("-> "+shortBoxSpread.getInterestRate());


        ShortBoxSpread copy= (ShortBoxSpread) shortBoxSpread.deepCopy();
        assertEquals(true,copy instanceof ShortBoxSpread);

        //////////////////short box spread//////////////////////////////////////////////
        BullSpread bullPut22=new BullSpread("327,2.57,2.57,P","361,31.79,31.79,P");
        BearSpread bearCall2=new BearSpread("327,5.04,5.04,C","361,0.04,0.04,C");
        ShortBoxSpread shortBoxSpread2=new ShortBoxSpread(bullPut22,bearCall2);
        assertEquals(22-commission,shortBoxSpread2.maxProfit(),delta);
        assertEquals(22-commission,shortBoxSpread2.maxLoss(),delta);
        assertEquals(-34.22,shortBoxSpread2.price(),delta);
        assertEquals(-0.41,shortBoxSpread2.getInterestRate(),delta);
////////////////////////////////////////////////////////////////////////////////////////////////
        BullSpread bullPutSpread3 = new BullSpread("100,50,50,P", "200, 100,100,P");
        BearSpread bearCallSpread3 = new BearSpread("100,70,70,C", "200,10,10,C");
        ShortBoxSpread shortBoxSpread3 = new ShortBoxSpread(bullPutSpread3, bearCallSpread3);
        assertEquals(1000-commission, shortBoxSpread3.maxProfit(), delta);
        assertEquals(1000-commission, shortBoxSpread3.maxLoss(), delta);
        assertEquals(-110, shortBoxSpread3.price(), delta);
        assertEquals(-9.0247, shortBoxSpread3.getInterestRate(), delta);
        ///////////////////////////////////////////////////////////////////////////
        BullSpread bullPutSpread4 = new BullSpread("100,50,50,P", "200, 80,80,P");
        BearSpread bearCallSpread4 = new BearSpread("100,70,70,C", "200,10,10,C");
        ShortBoxSpread shortBoxSpread4 = new ShortBoxSpread(bullPutSpread4, bearCallSpread4);
        assertEquals(-1000-commission, shortBoxSpread4.maxProfit(), delta);
        assertEquals(-1000-commission, shortBoxSpread4.maxLoss(), delta);
        assertEquals(-90, shortBoxSpread4.price(), delta);
        assertEquals(11.2099, shortBoxSpread4.getInterestRate(), delta);


    }
}
