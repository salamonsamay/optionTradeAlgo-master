package mycode.strategy_;

import mycode.object.*;

public class PutCallParity implements Comparable<PutCallParity> {

    public Buy buyCall;
    public Buy buyPut;

    public static final  double RATE=0.05;

//    private OptionPut putOption;
//    private OptionCall callOption;


    public PutCallParity (Buy call, Buy put) {
        this.buyCall=call;
        this.buyPut=put;
    }

    public PutCallParity(OptionCall optionCall, OptionPut optionPut) {
        this(new Buy(optionCall) ,new  Buy(optionPut));
    }


    // Method to check put-call parity
    public double checkPutCallParity() {
        // Implement the put-call parity check logic using putOption and callOption
        // You may need to modify this based on your specific requirements
        Option putOption=this.buyPut.getOpt();
        Option callOption=this.buyCall.getOpt();


        double putPrice = putOption.getAsk();
        double callPrice = callOption.getAsk();
        double stockPrice = putOption.getUnderlying_price();
        double strikePrice = putOption.getStrike();
        double dayToExpires =(((double) callOption.daysToExpiration())+1)/365;

        // Put-Call Parity formula: C - P = S - X/(1+r)^t
        double leftSide = callPrice - putPrice;
        double rightSide = stockPrice - strikePrice/Math.pow(1+RATE,dayToExpires);

        if(leftSide < rightSide){
            putPrice=putOption.getBid();
        }
        else {
            callPrice=callOption.getBid();
        }
        leftSide = callPrice - putPrice;
        rightSide = stockPrice - strikePrice/Math.pow(1+RATE,dayToExpires);

        return leftSide - rightSide ; // Adjust the tolerance as needed
    }

    public boolean checkArbitrageOpportunity() {

        // Implement the arbitrage opportunity check logic using putOption and callOption
        // You may need to modify this based on your specific requirements
        Option putOption=this.buyPut.getOpt();
        Option callOption=this.buyCall.getOpt();

        if (putOption == null || callOption == null) {
            System.out.println("Options not set.");
            return false;
        }

        double putPrice = putOption.getAsk();
        double callPrice = callOption.getAsk();
        double stockPrice = putOption.getUnderlying_price();
        double strikePrice = putOption.getStrike();
        double riskFreeRate = 0.05; // 5% as per your assumption

        // Put-Call Parity formula: C - P = S - X
        double leftSide = callPrice - putPrice;
        double rightSide = stockPrice - strikePrice;

        // Check if the put-call parity holds
        if (Math.abs(leftSide - rightSide) >= 0.0001) {
            System.out.println("Put-Call Parity does not hold. No arbitrage opportunity.");
            return false;
        }

        // Calculate the risk-free profit
        double riskFreeProfit = (strikePrice / Math.pow(1 + riskFreeRate, putOption.daysToExpiration()))
                - stockPrice + callPrice - putPrice;

        // Calculate the arbitrage profit
        double arbitrageProfit = leftSide - rightSide;

        // Check if there is an arbitrage opportunity
        if (arbitrageProfit > riskFreeProfit) {
            System.out.println("Arbitrage opportunity detected!");
            System.out.println("Arbitrage Profit: " + arbitrageProfit);
            System.out.println("Risk-Free Profit: " + riskFreeProfit);
            return true;
        } else {
            System.out.println("No arbitrage opportunity.");
            return false;
        }
    }


    public  static  boolean inputIsCorrect(Option optCall , Option optPut){
        if(optCall instanceof  OptionCall  && optPut instanceof OptionPut
                && optCall.getUnderlying_ticker().equals(optPut.getUnderlying_ticker())
                && optCall.getStrike()==optPut.getStrike()
                && optCall.daysToExpiration()==optPut.daysToExpiration()){

            return true;
        }
        return false;
    }

    @Override
    public int compareTo(PutCallParity other) {
        // Compare instances based on the result of checkPutCallParity
        double result1 = this.checkPutCallParity();
        double result2 = other.checkPutCallParity();

        // You might want to adjust this based on your sorting criteria
        return Double.compare(result1, result2);
    }

    @Override
    public String toString() {
        return "Underlying Asset: " + this.buyCall.getOpt().getUnderlying_ticker() + "\n" +
                "Underlying Price: " + this.buyCall.getOpt().getUnderlying_price() + "\n" +
                "Strike: " + this.buyPut.getOpt().getStrike()+"\n" +
                "The Free Rate: "+0.5+"\n" +
                "Day To expires: " + this.buyCall.getOpt().daysToExpiration()+"\n";
    }

}
