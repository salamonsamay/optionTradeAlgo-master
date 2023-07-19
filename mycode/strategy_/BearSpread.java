package mycode.strategy_;

import mycode.help.Tools;
import mycode.object.*;
import mycode.technical_indicator.Indicator;


/**
 *
 * @author salam
 *
 *the class calculate put and call strategy_
 *this strategy profit  when we think the market is on the way down
 */

public class BearSpread implements Strategy,Comparable<Strategy>{

	public Buy buy;
	public Sell sell;

	public static final double  COMMISSION=4;
	private long lastTimeUpdae;


	public BearSpread(){

	}
	public BearSpread(String sell, String buy){
		//buy option call 30 2.1,2 --> 30,2.1,2,C
		//sell option put 30 2.1,3 --> 30,2.1,3,P
		String str[]=sell.split(",");
		String str2[]=buy.split(",");
		if(str[3].equals("C")){
			double strike=Double.parseDouble(str[0]);
			double ask=Double.parseDouble(str[1]);
			double bid=Double.parseDouble(str[2]);
			this.sell=new Sell(new OptionCall(strike,ask,bid,new Greeks()));

			strike=Double.parseDouble(str2[0]);
			ask=Double.parseDouble(str2[1]);
			bid=Double.parseDouble(str2[2]);
			this.buy=new Buy(new OptionCall(strike,ask,bid,new Greeks()));
		}
		else {
			double strike=Double.parseDouble(str[0]);
			double ask=Double.parseDouble(str[1]);
			double bid=Double.parseDouble(str[2]);
			this.sell=new Sell(new OptionPut(strike,ask,bid,new Greeks()));

			strike=Double.parseDouble(str2[0]);
			ask=Double.parseDouble(str2[1]);
			bid=Double.parseDouble(str2[2]);
			this.buy=new Buy(new OptionPut(strike,ask,bid,new Greeks()));
		}


	}
	public BearSpread(Sell sell, Buy buy){

		this.buy = buy;
		this.sell = sell;

	}

	public BearSpread(BearSpread other){
		this.sell=new Sell(other.sell);
		this.buy=new Buy(other.buy);
		//

	}


	public double maxProfit() {
		double buyStrike=this.buy.opt.getStrike();
		double sellStrike=this.sell.opt.getStrike();
		double sellBid=this.sell.opt.getBid();
		double buyAsk=this.buy.opt.getAsk();

		boolean flag=this.buy.opt instanceof OptionPut && this.sell.opt instanceof OptionPut;
		if(flag) {
			return (buyStrike-sellStrike+sellBid-buyAsk)*100 - BearSpread.COMMISSION;
		}

		return (this.sell.opt.getBid()-this.buy.opt.getAsk())*100 - BearSpread.COMMISSION;

	}

	public double maxLoss() {
		double buyStrike=this.buy.opt.getStrike();
		double sellStrike=this.sell.opt.getStrike();
		double sellBid=this.sell.opt.getBid();
		double buyAsk=this.buy.opt.getAsk();
		boolean flag=this.buy.opt instanceof OptionPut && this.sell.opt instanceof OptionPut;
		if(flag) {

			return (buyAsk-sellBid)*100*-1 - BearSpread.COMMISSION;
		}

		return (buyStrike-sellStrike-sellBid+buyAsk)*100*-1 - BearSpread.COMMISSION;

	}

	public double probabilityOfMaxProfit() {
		if(buy.getOpt() instanceof OptionPut) {
			return 1-sell.probabilityOfProfit();
		}
		return sell.probabilityOfProfit();
	}

	public double probabilityOfMaxLoss() {
		if(buy.getOpt() instanceof OptionPut) {
			return 1-buy.probabilityOfProfit();
		}
		return buy.probabilityOfProfit();

	}

	public double averageOfReturn() {
		//double dif=sell.opt.getBid()/(buy.opt.getStrike()-sell.opt.getStrike());
		//	return maxProfit()*probabilityOfMaxSuccess()+maxlose()*(1-probabilityOfMaxSuccess()-dif);
		//return maxProfit()*probabilityOfMaxSuccess()+maxLoss()*(1-probabilityOfMaxSuccess());
		return maxProfit()*probabilityOfMaxProfit()
				+ maxLoss()*(probabilityOfMaxLoss()+ probabilityAboveBreakEven());
	}

	public String getCompanySymbol() {
		return this.buy.getOpt().getUnderlying_ticker();
	}

	public int daysToExpiration() {
		return this.sell.getOpt().daysToExpiration();
	}

	public double price(){
		return this.buy.getOpt().getAsk()-this.sell.getOpt().getBid();
	}


	public double midPointPrice() {
		return this.buy.getOpt().getMid_point()-this.sell.getOpt().getMid_point();
	}

	public Strategy deepCopy() {
		BearSpread bearSpread =new BearSpread();
		bearSpread.sell=this.sell.deepCopy();
		bearSpread.buy=this.buy.deepCopy();
		bearSpread.lastTimeUpdae=this.lastTimeUpdae;

		return bearSpread;
	}
	public boolean isCreditSpread(){
		if(this.sell.getOpt() instanceof OptionCall && this.buy.getOpt() instanceof OptionCall){
			return true;
		}
		return false;
	}

	@Override
	public double percentage() {
		return 0;
	}


	public double probabilityAboveBreakEven() {
		Option leg1=sell.getOpt();
		Option leg2=buy.getOpt();
		double breakEven;
		if(leg1 instanceof OptionCall) {
			breakEven=leg1.getStrike()+(leg1.getBid()-leg2.getAsk());
		}
		else {
			breakEven=leg2.getStrike()+leg2.getAsk()-leg1.getBid();
		}

		double spredProbability=1-(probabilityOfMaxProfit()+probabilityOfMaxLoss());
		double spred=Math.abs(sell.getOpt().getStrike()-buy.getOpt().getStrike());
		return Math.abs((spredProbability*(((leg2.getStrike()-breakEven)/spred))));
	}

	public double probabilityBelowBreakEven() {

		return 1-(probabilityAboveBreakEven()+probabilityOfMaxLoss()+probabilityOfMaxProfit());
	}

	public static boolean inputIsCorrect(Option opt1,Option opt2) {


//		if(!(opt1.getGreeks().getDelta()>0.1 && opt1.getGreeks().getDelta()<0.95
//				&& opt2.getGreeks().getDelta()>0.1 && opt2.getGreeks().getDelta()<0.95)) {
//			return false;
//		}

		boolean same_prefix=opt1.getTicker().substring(0,opt1.getTicker().length()-15).equals(opt2.getTicker().substring(0,opt2.getTicker().length()-15));

		if(opt1.getStrike()<=opt2.getStrike()
				&& opt1.getExpiration_date().equals(opt2.getExpiration_date())
				&& opt1.type().equals(opt2.type())
				&& opt1.getUnderlying_ticker().equals(opt2.getUnderlying_ticker())
				&& same_prefix) {
			return true;
		}
		return false;

	}


	public int compareTo(Strategy s) {

		if(this.averageOfReturn()>s.averageOfReturn()) {return 1;}
		if(this.averageOfReturn()<s.averageOfReturn()) {return -1;}

		return 0;
	}

	public boolean isTimeToBuy() {
		try {
			if (sell.getOpt().getIndicator().isGoingToDown()
					&& buy.getOpt().getIndicator().isGoingToUp()) {
				return true;
			}
		} catch (NullPointerException e) {
			return false;
		}
		return false;

	}





	public boolean equals(BearSpread other){
		return (this.buy.equals(other.buy)&&this.sell.equals(other.sell));
	}


	public String toString() {
		String s="PUT";

		if(this.sell.opt instanceof OptionCall) {
			s="CALL";
			//			cost="best credit be "+(sell.opt.getAsk()-buy.getOpt().getBid())+"\n"
			//					+ "worst credit be "+(sell.opt.getBid()-buy.getOpt().getAsk());
		}

		String info="///////////////BEAR SPRED OPTION TYPE "+s+"\n"

				+ "SELL/BUY Contract("+sell.getOpt().getTicker()+","+buy.getOpt().getTicker() + ")\n"
				+ "SELL/BUY Expiration date("+sell.getOpt().getExpiration_date()+","+buy.getOpt().getExpiration_date()+")\n"
				+ "SELL/BUY Strike("+sell.getOpt().getStrike()+","+buy.getOpt().getStrike()+")\n"
				+ "SELL/BUY ASK("+sell.getOpt().getAsk()+","+buy.getOpt().getAsk()+")\n"
				+ "SELL/BUY BID("+sell.getOpt().getBid()+","+buy.getOpt().getBid()+")\n"
				+ "max profit   : "+ maxProfit()+"$"+"\n"
				+ "max loss   : "+ maxLoss()+"$"+"\n"
				+ "probability of max success: "+Math.round(probabilityOfMaxProfit()*100)+"%\n"
				+ "probability of max loss: "+Math.round(probabilityOfMaxLoss()*100)+"%\n"
				+ "average of return : "+ averageOfReturn()+"\n"
				+ "day to expiration: "+daysToExpiration()+"\n"
				+"price is :"+ price()+"\n";


		;
		return info;
	}

	public static void main(String[] args) {
		String s="O:BAC230505C00050000";
		System.out.println(s.substring(0,s.length()-15));
	}












}
