package mycode.junit_testing;

import mycode.object.Greeks;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import mycode.object.Sell;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class OPT_Testing_ {
 @Test
    public void optionPut(){
     OptionPut opt=new OptionPut(222, 2.8, new Greeks(0.5, 0, 0, 0));

     double strike=opt.getStrike();
     double ask=opt.getAsk();
     double breackEven=opt.breakEven();

     assertEquals(222, strike,0);
     assertEquals(ask, 2.8,0);
     assertEquals((222-2.8), breackEven,0);

 }

    @Test
    public void optionCall() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        Date yesterday = calendar.getTime();

        OptionCall opt=new OptionCall(300, 2.3, null);
        opt.setExpiration_date(formatter.format(tomorrow));

        double strike=opt.getStrike();
        double ask=opt.getAsk();
        double breackEven=opt.breakEven();

        assertEquals(300, strike,0);
        assertEquals(ask, 2.3,0);
        assertEquals((300+2.3), breackEven,0);
        assertEquals(1,opt.daysToExpiration());


    }

}