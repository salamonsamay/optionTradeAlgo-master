package mycode.strategy_;

import mycode.object.*;

import java.util.HashMap;
import java.util.Hashtable;

public class Reversal implements Strategy{
    public SyntheticLong syntheticLong;
    public   double underlyingValue;


    public Reversal(SyntheticLong syntheticLong) {
        this.syntheticLong = syntheticLong;
        this.underlyingValue = syntheticLong.buy.getOpt().getStrike();
        ;
    }
    public Reversal(Option call,Option put){
        this(new SyntheticLong(new Buy(call),new Sell(put)));
    }


    public double maxProfit() {
        return  (price()*-1-this.syntheticLong.buy.getOpt().getStrike())*100;
    }

    @Override
    public double maxLoss() {
        return maxProfit();
    }

    @Override
    public double averageOfReturn() {
        return 0;
    }

    @Override
    public double probabilityOfMaxProfit() {
        return 0;
    }

    @Override
    public double probabilityOfMaxLoss() {
        return 0;
    }


    public String getCompanySymbol() {
        return this.syntheticLong.buy.getOpt().getUnderlying_ticker();
    }

    @Override
    public int daysToExpiration() {
        return 0;
    }


    public double price() {
        return this.syntheticLong.price()-this.underlyingValue;
    }

    @Override
    public double midPointPrice() {
        return 0;
    }


    public Strategy deepCopy() {
        Reversal reversal=new Reversal(this.syntheticLong.deepCopy());
        return reversal;
    }

    @Override
    public boolean isCreditSpread() {
        return false;
    }

    public static boolean inputIsCorrect(Option call, Option put){
        return SyntheticLong.inputIsCorrect(call,put);
    }
    public static class SyntheticLong {
        public Buy buy;
        public Sell sell;

        public SyntheticLong(){

        }

        public SyntheticLong(Buy buy, Sell sell){
            if(!SyntheticLong.inputIsCorrect(buy.opt,sell.opt)){
                throw new RuntimeException("the input is not correct");
            }

            this.buy=buy;
            this.sell=sell;
        }

        public int daysToExpiration() {
            return buy.getOpt().daysToExpiration();
        }

        public double price() {
            return buy.getOpt().getAsk()-sell.getOpt().getBid();
        }

        public  static  boolean inputIsCorrect(Option optCall, Option optPut ){
            if(!(optCall instanceof OptionCall && optPut instanceof OptionPut)){
                return  false;
            }
            boolean isSameSymbol=optCall.getUnderlying_ticker().equals(optPut.getUnderlying_ticker());
            boolean isSameStrike=optCall.getStrike()-optPut.getStrike()==0;
            boolean isSameDate= optCall.getExpiration_date().equals(optPut.getExpiration_date());


            if(isSameSymbol && isSameStrike && isSameDate){
                return  true;
            }
            return false;
        }

        public SyntheticLong deepCopy() {
            SyntheticLong syntheticLong=new SyntheticLong();
            syntheticLong.buy=this.buy.deepCopy();
            syntheticLong.sell=this.sell.deepCopy();
            return syntheticLong;
        }

    }

    public static void main(String[] args) {
        Reversal aaa=new Reversal(new SyntheticLong(new Buy(new OptionCall(140,3.6,3.6,null)),new Sell(new OptionPut(140,4.25,4.25,null))));
        System.out.println(aaa.maxProfit());
        System.out.println(aaa.price());
    }
}
