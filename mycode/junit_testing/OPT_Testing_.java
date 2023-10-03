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
    public void optionPut2(){
        double epsilon =0.00001;
        OptionPut opt=new OptionPut(130, 2.8,2.6, new Greeks(0.5, 0.003, -0.001, 0.0073));
        opt.setTicker("O:AAPL20240203P000130");

        assertEquals(130, opt.getStrike(),epsilon);
        assertEquals(2.8, opt.getAsk(),epsilon);
        assertEquals(2.6,opt.getBid(),0);
        assertEquals(0.5,opt.getGreeks().getDelta(),epsilon);
        assertEquals(0.003,opt.getGreeks().getGamma(),epsilon);
        assertEquals(-0.001, opt.getGreeks().getTheta(),epsilon);
        assertEquals(0.0073,opt.getGreeks().getVega(),epsilon);



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