package mycode.junit_testing;

import mycode.strategy_.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoxSpreadTesting {
    @Test
    public void boxSpread(){
        double delta=0.000001;
        double commission=8;
        BullSpread bullCall=new BullSpread("40,7,7,C","50,2,2,C");
        BearSpread bearPut=new BearSpread("40,2,2,P","50,6.5,6.5,P");
        BoxSpread box=new BoxSpread(bullCall,bearPut);
        assertEquals(9.5,box.price(),delta);
        assertEquals(50-commission,box.maxProfit(),delta);//(10-9.5)*100
        assertEquals(50-commission,box.maxLoss(),delta);//(10-9.5)*100
////////////////////////////////////////////////////////////////////////////////////////
        BullSpread bullCall2=new BullSpread("20,6.5,6.5,C","30,1.5,1.5,C");
        BearSpread bearPut2=new BearSpread("20,1.5,1.5,P","30,6.0,6.0,P");
        BoxSpread box2=new BoxSpread(bullCall2,bearPut2);
        assertEquals(50-commission,box2.maxProfit(),delta);
        assertEquals(50-commission,box2.maxLoss(),delta);
        assertEquals(9.5,box2.price(),delta);
        System.out.println(box2.maxProfit());

        ///////////////////////////short box spred////////////////////////////////////
        BullSpread bullPut=new BullSpread("50,2,2,P","60,7,7,P");
        BearSpread bearCall=new BearSpread("50,7,7,C","60,1.5,1.5,C");

        BoxSpread box3=new BoxSpread(bullPut,bearCall);
        assertEquals(50-commission,box3.maxProfit(),delta);
        assertEquals(50-commission,box3.maxLoss(),delta);
        assertEquals(-10.5,box3.price(),delta);


    }

    @Test
    public void longBoxSpread(){
        double delta=0.000001;
        double commission=8;
        BullSpread bullCall=new BullSpread("40,7,7,C","50,2,2,C");
        BearSpread bearPut=new BearSpread("40,2,2,P","50,6.5,6.5,P");
        LongBoxSpread longBox=new LongBoxSpread(bullCall,bearPut);

        assertEquals(9.5,longBox.price(),delta);
        assertEquals(50-commission,longBox.maxProfit(),delta);//(10-9.5)*100
        assertEquals(50-commission,longBox.maxLoss(),delta);//(10-9.5)*100
        assertEquals(4.42105263157,longBox.percentage(),delta);
        System.out.println(longBox.price());
        System.out.println(longBox.percentage());




    }
    @Test
    public void shortBoxSpread(){
        double delta=0.000001;
        double commission=8;
        BullSpread bullPut=new BullSpread("50,2,2,P","60,7,7,P");
        BearSpread bearCall=new BearSpread("50,7,7,C","60,1.5,1.5,C");
        ShortBoxSpread shortBoxSpread=new ShortBoxSpread(bullPut,bearCall);
        assertEquals(50-commission,shortBoxSpread.maxProfit(),delta);
        assertEquals(50-commission,shortBoxSpread.maxLoss(),delta);
        assertEquals(-10.5,shortBoxSpread.price(),delta);

        ShortBoxSpread copy= (ShortBoxSpread) shortBoxSpread.deepCopy();
        assertEquals(true,copy instanceof ShortBoxSpread);


        //////////////////short box spread//////////////////////////////////////////////
        BullSpread bullPut22=new BullSpread("327,2.57,2.57,P","361,31.79,31.79,P");
        BearSpread bearCall2=new BearSpread("327,5.04,5.04,C","361,0.04,0.04,C");
        ShortBoxSpread shortBoxSpread2=new ShortBoxSpread(bullPut22,bearCall2);
        System.out.println(shortBoxSpread2.price());
        System.out.println(34/(shortBoxSpread2.price()*-100));
        System.out.println(shortBoxSpread2.percentage());

        BullSpread bullPut3=new BullSpread("50,30,30,P","90,40,40,P");
        BearSpread bearCall3=new BearSpread("50,42,42,C","90,10,10,C");
        ShortBoxSpread shortBoxSpread3=new ShortBoxSpread(bullPut3,bearCall3);
        assertEquals(400,shortBoxSpread3.percentage(),delta);



    }
}
