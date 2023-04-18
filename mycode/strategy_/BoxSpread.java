package mycode.strategy_;

public class BoxSpread implements Strategy,Comparable<Strategy>{

    public BullSpread bullSpread;
    public BearSpread bearSpread;

    public static final double  COMMISSION=8;

    public BoxSpread(BullSpread bullSpread, BearSpread bearSpread){
        this.bullSpread = bullSpread;
        this.bearSpread = bearSpread;
//        this.costOftheBox=(bullSpread.buy.getOpt().getAsk()-bullSpread.sell.getOpt().getBid())
//                +(bearSpread.buy.getOpt().getAsk()-bearSpread.sell.getOpt().getBid());
    }

    public double maxProfit() {
        double spread= bearSpread.buy.getOpt().getStrike()- bearSpread.sell.getOpt().getStrike();
        if(bullSpread.isCreditSpread()){
            return  (price()+spread)*-100 - BoxSpread.COMMISSION;
        }
        return (spread-(bearSpread.price()+ bullSpread.price()))*100 - BoxSpread.COMMISSION;
    }


    public double maxLoss() {
        return maxProfit();

    }

    @Override
    public double probabilityOfMaxProfit() {
        return 0;
    }

    @Override
    public double probabilityOfMaxLoss() {

        return 0;
    }
    public double averageOfReturn() {
       return maxProfit();
    }


    public String getCompanySymbol() {
        return this.bearSpread.getCompanySymbol();
    }


    public int daysToExpiration() {
        return this.bearSpread.daysToExpiration();
    }


    public double price() {
        return this.bullSpread.price()+this.bearSpread.price();
    }


    public double midPointPrice() {
        return this.bullSpread.midPointPrice()+this.bearSpread.midPointPrice();
    }


    public Strategy deepCopy() {
        return new BoxSpread((BullSpread) this.bullSpread.deepCopy(), (BearSpread) this.bearSpread.deepCopy());
    }


    public boolean isCreditSpread() {
        return (this.bearSpread.isCreditSpread() && bullSpread.isCreditSpread());
    }

    @Override
    public double percentage() {
        return 0;
    }

    public static boolean inputIsCorrect(BullSpread bullCall, BearSpread bearPut){

        boolean type=(bullCall.isCreditSpread() && bearPut.isCreditSpread())
                || (!bullCall.isCreditSpread() && !bearPut.isCreditSpread());
        //   boolean flag=(bullSpread.sell.getOpt() instanceof OptionCall && bearSpread.sell.getOpt() instanceof OptionPut);
        boolean sameDate=bullCall.sell.getOpt().getExpiration_date().equals(bearPut.sell.getOpt().getExpiration_date());
        boolean sameSymbol=bullCall.sell.getOpt().getUnderlying_ticker().equals(bearPut.sell.getOpt().getUnderlying_ticker());

        if(bullCall.buy.getOpt().getStrike()==bearPut.sell.getOpt().getStrike()
                && bullCall.sell.getOpt().getStrike()==bearPut.buy.getOpt().getStrike()
                && sameDate && sameSymbol
                && type){
            return true;
        }
        return false;
    }

    public String toString(){
        String str="Box \n"
                +"debit "+(Math.abs(bullSpread.price()+ bearSpread.price())+"\n"
                +"day to expiration "+daysToExpiration()+"\n"
                +"undrline tiker call "+ bearSpread.sell.getOpt().getUnderlying_ticker()+"\n"
                + "SELL/BUY Contract("+ bearSpread.sell.getOpt().getTicker()+","+ bearSpread.buy.getOpt().getTicker() + ")\n"
                + "BUY/SELL Contract("+ bullSpread.buy.getOpt().getTicker()+","+ bullSpread.sell.getOpt().getTicker() + ")\n"
                +"put date "+ bearSpread.sell.getOpt().getExpiration_date()+"\n"
                + "call date"+ bullSpread.sell.getOpt().getExpiration_date()+"\n"
                + "/////////////////////////////////////\n"
                + "SELL/BUY Strike("+ bearSpread.sell.getOpt().getStrike()+","+ bearSpread.buy.getOpt().getStrike()+")\n"
                + "SELL/BUY ASK("+ bearSpread.sell.getOpt().getAsk()+","+ bearSpread.buy.getOpt().getAsk()+")\n"
                + "SELL/BUY BID("+ bearSpread.sell.getOpt().getBid()+","+ bearSpread.buy.getOpt().getBid()+")\n"
                + "//////////////////////////////////////\n"
                + "BUY/SELL Strike("+ bullSpread.buy.getOpt().getStrike()+","+ bullSpread.sell.getOpt().getStrike()+")\n"
                + "BUY/SELL ASK("+ bullSpread.buy.getOpt().getAsk()+","+ bullSpread.sell.getOpt().getAsk()+")\n"
                + "BUY/SELL BID("+ bullSpread.buy.getOpt().getBid()+","+ bullSpread.sell.getOpt().getBid()+")\n"
                + "///////////////////////////////////////\n"

                + "max profit by worst case: "+ maxProfit()+"$"+"\n"
                + "max lost by worst case: "+ maxLoss()+"$"+"\n"
                + "probability of max success: "+Math.round(probabilityOfMaxProfit()*100)+"%\n"
                + "probability of max lost: "+Math.round(probabilityOfMaxLoss()*100)+"%\n"
                + "average of return worst case: "+ averageOfReturn())+"\n";
        //				+ put.toString()+" \n"
        //				+ call.toString();

        return str;
    }


    public int compareTo(Strategy s) {
        if(this.averageOfReturn()>s.averageOfReturn()) {return 1;}
        if(this.averageOfReturn()<s.averageOfReturn()) {return -1;}

        return 0;
    }

    public static void main(String[] args) {
        BearSpread bearSpread =new BearSpread("88,8,8,P","");
    }
}


