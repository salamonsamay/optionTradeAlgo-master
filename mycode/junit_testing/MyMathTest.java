package mycode.junit_testing;
import mycode.help.MyMath;
import mycode.strategy_.BearSpread;
import mycode.strategy_.BullSpread;
import mycode.strategy_.IronCondor;
import mycode.strategy_.ShortBoxSpread;
import org.junit.Assert;
import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class MyMathTest {
    @Test
    public void test(){
        double delta=0.00000001;
        BearSpread bearSpread_c=new BearSpread("30,48.5,48.3,c","35,38,36.2,c");
        assertEquals(-11.3, MyMath.midPrice(bearSpread_c),delta);

        BearSpread bearSpread_p=new BearSpread("30,48.3,48.0,p","35,55.4,55,p");
        assertEquals(7.05, MyMath.midPrice(bearSpread_p),delta);

        BullSpread bullcall=new BullSpread("35,4.4,3.4,c","39,2.3,2,c");
        assertEquals(1.75, MyMath.midPrice(bullcall),delta);

        BullSpread bullput=new BullSpread("35,4.4,3.4,p","39,6.3,6,p");
        assertEquals(-2.25, MyMath.midPrice(bullput),delta);

        IronCondor ironCondor=new IronCondor(bullput,bearSpread_c);
        assertEquals(-2.25-11.3, MyMath.midPrice(ironCondor),delta);

    }
}
