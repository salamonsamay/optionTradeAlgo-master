package mycode.strategy_;

public class LongBoxSpread implements  Strategy{

    public BullSpread bullSpread;
    public BearSpread bearSpread;

    public static final double  COMMISSION=8;


    public LongBoxSpread(BullSpread bullSpread, BearSpread bearSpread){
        if(bullSpread.isCreditSpread() || bearSpread.isCreditSpread()){
            throw new RuntimeException("not correct input ");
        }
        this.bullSpread = bullSpread;
        this.bearSpread = bearSpread;
    }

    public double maxProfit() {
        double spread= bearSpread.buy.getOpt().getStrike()- bearSpread.sell.getOpt().getStrike();
        return (spread-(bearSpread.price()+ bullSpread.price()))*100 - LongBoxSpread.COMMISSION;
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
        return new LongBoxSpread((BullSpread) this.bullSpread.deepCopy(), (BearSpread) this.bearSpread.deepCopy());
    }

    public boolean isCreditSpread() {
        return false;
    }


    public  double probabilityITM(){
        return 1- (bearSpread.probabilityOfMaxLoss()+bullSpread.probabilityOfMaxLoss());
    }
    public double percentage(){
        return  (maxLoss())/price();
    }
    public static boolean inputIsCorrect(BullSpread bullCall, BearSpread bearPut){

        boolean isDebit= (!bullCall.isCreditSpread() && !bearPut.isCreditSpread());
        //   boolean flag=(bullSpread.sell.getOpt() instanceof OptionCall && bearSpread.sell.getOpt() instanceof OptionPut);
        boolean sameDate=bullCall.sell.getOpt().getExpiration_date().equals(bearPut.sell.getOpt().getExpiration_date());
        boolean sameSymbol=bullCall.sell.getOpt().getUnderlying_ticker().equals(bearPut.sell.getOpt().getUnderlying_ticker());
        boolean equalStrike1=bullCall.buy.getOpt().getStrike()==bearPut.sell.getOpt().getStrike();
        boolean equalStrike2=bullCall.sell.getOpt().getStrike()==bearPut.buy.getOpt().getStrike();

        if(equalStrike1 && equalStrike2 && sameDate && sameSymbol && isDebit){
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
                + "max profit : "+ maxProfit()+"$"+"\n"
                + "max lost : "+ maxLoss())+"$"+"\n"
                + "percentage :"+ percentage()+"%\n";


        return str;
    }


    public int compareTo(Strategy s) {
        if(this.averageOfReturn()>s.averageOfReturn()) {return 1;}
        if(this.averageOfReturn()<s.averageOfReturn()) {return -1;}

        return 0;
    }

    public static void main(String[] args) {
        BearSpread bearSpread1=new BearSpread("20,2,2,P","21,7,7,P");
        BullSpread bullSpread1=new BullSpread("20,8,8,C","21,3,3,C");

        LongBoxSpread boxSpread=new LongBoxSpread(bullSpread1,bearSpread1);
        System.out.println(boxSpread.percentage());
        System.out.println(boxSpread.price());
        System.out.println(boxSpread.maxLoss());
    }
}
