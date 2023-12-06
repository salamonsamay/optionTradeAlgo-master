package mycode.strategy_;

import mycode.help.MyMath;

public class ShortBoxSpread implements  Strategy{

    public BullSpread bullSpread;
    public BearSpread bearSpread;
    public static final double  COMMISSION=8;
    public ShortBoxSpread(BullSpread bullSpread, BearSpread bearSpread){
        if(!bullSpread.isCreditSpread() || !bearSpread.isCreditSpread()){
            throw new RuntimeException("not correct input ");
        }
        this.bullSpread = bullSpread;
        this.bearSpread = bearSpread;
    }

    public double maxProfit() {
        double spread= bearSpread.buy.getOpt().getStrike()- bearSpread.sell.getOpt().getStrike();
        return  (price()+spread)*-100 - ShortBoxSpread.COMMISSION;
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

        return new ShortBoxSpread((BullSpread) this.bullSpread.deepCopy(),(BearSpread) this.bearSpread.deepCopy());
    }
    public boolean isCreditSpread() {
        return true;
    }

    public static boolean inputIsCorrect(BullSpread bullCall, BearSpread bearPut){

        boolean isCredit= (bullCall.isCreditSpread() && bearPut.isCreditSpread());
        boolean sameDate=bullCall.sell.getOpt().getExpiration_date().equals(bearPut.sell.getOpt().getExpiration_date());
        boolean sameSymbol=bullCall.getCompanySymbol().equals(bearPut.getCompanySymbol());
        boolean equalStrike1=bullCall.buy.getOpt().getStrike()==bearPut.sell.getOpt().getStrike();
        boolean equalStrike2=bullCall.sell.getOpt().getStrike()==bearPut.buy.getOpt().getStrike();


        if(equalStrike1 && equalStrike2 && sameDate && sameSymbol && isCredit){
            return true;
        }
        return false;
    }
    public String toString(){
        String str="Box \n"
                +"credit "+(100*Math.abs(bullSpread.price()+ bearSpread.price())*100+"\n"
                +"day to expiration "+daysToExpiration()+"\n"
                + "expiration date "+ bullSpread.sell.getOpt().getExpiration_date()+"\n"
                +"undrline tiker call "+ bearSpread.sell.getOpt().getUnderlying_ticker()+"\n"
                + "Lower  Strike "+ bullSpread.buy.getOpt().getStrike()+"\n"
                + "Upper  Strike "+ bullSpread.sell.getOpt().getStrike()+"\n"
                + "max profit : "+ String.format("%.2f$", maxProfit())+"\n"
                + "max lost : "+ String.format("%.2f$", maxLoss())+"\n"
                + "intrest rate  : "+ String.format("%.2f%%", getInterestRate())+"\n"
                + "yearly intrest rate :"+ String.format("%.2f%%", yearlyInterestRate()))+"%\n";
        return str;
    }
    public int compareTo(Strategy s) {
        if(this.averageOfReturn()>s.averageOfReturn()) {return 1;}
        if(this.averageOfReturn()<s.averageOfReturn()) {return -1;}

        return 0;
    }
    public  double getInterestRate(){
        //if the result is positive it means that im get money
        double originalLoan=price()*-1;
        double returnLoan=Math.abs(bullSpread.buy.getOpt().getStrike()-bullSpread.sell.getOpt().getStrike());

        return  ((originalLoan-returnLoan)/originalLoan)*100;
    }
    public  double yearlyInterestRate(){
        //if the result is positive it means that im get money
        return (getInterestRate()/ bullSpread.sell.getOpt().daysToExpiration())*365;

    }

    public boolean isDeepInTheMoney(){
        if((bearSpread.sell.getOpt().getGreeks().getDelta()>0.7
                || bullSpread.sell.getOpt().getGreeks().getDelta()>0.7)){
            return  true;
        }
        return  false;
    }

    public static void main(String[] args) {
        BearSpread bearSpread1=new BearSpread("20,7,7,C","30,1,1,C");
        BullSpread bullSpread1=new BullSpread("20,3,3,P","30,8,8,P");
        String date="2023-09-27";
        bullSpread1.sell.getOpt().setExpiration_date(date);
        bearSpread1.buy.getOpt().setExpiration_date(date);
        bearSpread1.sell.getOpt().setExpiration_date(date);
        bearSpread1.buy.getOpt().setExpiration_date(date);

        ShortBoxSpread boxSpread=new ShortBoxSpread(bullSpread1,bearSpread1);


        System.out.println(boxSpread.price());
        System.out.println(boxSpread.maxLoss());
        System.out.println(boxSpread.getInterestRate());
        System.out.println(boxSpread.yearlyInterestRate());
        System.out.println();
    }
}
