package mycode.strategy_;

import mycode.help.Tools;
import mycode.object.*;


public class BullSpread implements Strategy,Comparable<Strategy>{

	public Buy buy;

	public Sell sell;

	public static final double  COMMISSION=4;
	private long lastTimeUpdae;

	public BullSpread(){

	}

	public BullSpread(String buy, String sell){
		//buy option call 30 2.1,2 --> 30,2.1,2,C
		//sell option put 30 2.1,2 --> 30,2.1,2,P
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
	public BullSpread(Buy buy, Sell sell) {
		this.buy = buy;
		this.sell = sell;

	}

	public BullSpread(BullSpread other){
		this.buy=new Buy(other.buy);
		this.sell=new Sell(other.sell);

	}

	public double maxProfit() {
		boolean isPut=this.buy.opt instanceof OptionPut && this.sell.opt instanceof OptionPut;
		if(isPut) {
			return (sell.getOpt().getBid()-buy.getOpt().getAsk())*100 - BullSpread.COMMISSION;
		}

		double strikeDiff=sell.getOpt().getStrike()-buy.getOpt().getStrike();
		return (strikeDiff-(buy.getOpt().getAsk()-sell.getOpt().getBid()))*100 - BullSpread.COMMISSION;
	}


	public double maxLoss() {
		double strikeDiff=sell.getOpt().getStrike()-buy.getOpt().getStrike();
		double diffPrice=sell.getOpt().getBid()-buy.getOpt().getAsk();
		boolean isPut=this.buy.opt instanceof OptionPut && this.sell.opt instanceof OptionPut;
		if(isPut) {

			return (strikeDiff-diffPrice)*-100 - BullSpread.COMMISSION;
		}

		return diffPrice*100 - BullSpread.COMMISSION;

	}

	public double probabilityOfMaxProfit() {
		if(sell.getOpt() instanceof OptionPut) {
			return sell.probabilityOfProfit();
		}
		///not shore need to check
		return 1-sell.probabilityOfProfit();
	}

	public double probabilityOfMaxLoss() {
		if(buy.getOpt() instanceof OptionPut) {
			return buy.probabilityOfProfit();
		}
		///not shure need to check
		return 1-buy.probabilityOfProfit();
	}


	public double averageOfReturn() {

		return maxProfit()*probabilityOfMaxProfit()
				+ maxLoss()*(probabilityOfMaxLoss()+ probabilityBelowBreakEven());
	}


	public String getCompanySymbol() {
		return this.buy.getOpt().getUnderlying_ticker();
	}


	public int daysToExpiration() {
		return  this.sell.getOpt().daysToExpiration();
	}

	public double price(){
		return this.buy.getOpt().getAsk()-this.sell.getOpt().getBid();
	}


	public double midPointPrice() {
		return this.buy.getOpt().getMid_point()-this.sell.getOpt().getMid_point();
	}

	public Strategy deepCopy() {
		BullSpread bullSpread =new BullSpread();
		bullSpread.sell=this.sell.deepCopy();
		bullSpread.buy=this.buy.deepCopy();
		bullSpread.lastTimeUpdae=this.lastTimeUpdae;

		return bullSpread;
	}

	public boolean isCreditSpread(){
		if(this.sell.getOpt() instanceof OptionPut && this.buy.getOpt() instanceof OptionPut){
			return true;
		}
		return false;
	}



	public double probabilityAboveBreakEven() {
		Option leg1=buy.getOpt();
		Option leg2=sell.getOpt();
		double breakEven;
		if(leg1 instanceof OptionCall) {
			breakEven=leg1.getStrike()+leg1.getAsk()-leg2.getBid();
		}
		else {
			breakEven=leg2.getStrike()+leg2.getBid()-leg1.getAsk();
		}

		double spredProbability=1-(probabilityOfMaxProfit()+probabilityOfMaxLoss());
		double spred=Math.abs(sell.getOpt().getStrike()-buy.getOpt().getStrike());
		return Math.abs((spredProbability*(((leg2.getStrike())-breakEven/spred))));
		//		return 1-(probabilityBelowBreakEven()+probabilityOfMaxLoss()+probabilityOfMaxSuccess());
	}


	public double probabilityBelowBreakEven() {
		Option leg1=buy.getOpt();
		Option leg2=sell.getOpt();
		double breakEven;
		if(leg1 instanceof OptionCall) {
			breakEven=leg1.getStrike()+(leg1.getAsk()-leg2.getBid());
		}
		else {
			breakEven=leg2.getStrike()-(leg2.getBid()-leg1.getAsk());
		}



		double spredProbability=1-(probabilityOfMaxProfit()+probabilityOfMaxLoss());
		double spred=Math.abs(sell.getOpt().getStrike()-buy.getOpt().getStrike());
		return Math.abs((spredProbability*(((breakEven-leg1.getStrike())/spred))));
	}





	public static boolean inputIsCorrect(Option opt1,Option opt2) {


//		if(!(opt1.getGreeks().getDelta()>0.1 && opt1.getGreeks().getDelta()<0.95
//				&& opt2.getGreeks().getDelta()>0.1 && opt2.getGreeks().getDelta()<0.95)) {
//			return false;
//		}

		boolean same_prefix=opt1.getTicker().substring(0,opt1.getTicker().length()-15).equals(opt2.getTicker().substring(0,opt2.getTicker().length()-15));
		if(opt1.getStrike()<=opt2.getStrike()&& opt1.getExpiration_date().equals(opt2.getExpiration_date())
				&& opt1.type().equals(opt2.type()) && opt1.getUnderlying_ticker().equals(opt2.getUnderlying_ticker()) && same_prefix) {
			return true;
		}
		return false;

	}

	public int compareTo(Strategy s) {
		//		if(this.maxLoss()>s.maxLoss()) {return 1;}
		//		if(this.maxLoss()<s.maxLoss()) {return -1;}
		//		return 0;
		if(this.averageOfReturn()>s.averageOfReturn()) {return 1;}
		if(this.averageOfReturn()<s.averageOfReturn()) {return -1;}

		return 0;
	}

	public String toString() {
		String s="PUT";
		if(this.sell.opt instanceof OptionCall) {
			s="CALL";
		}

		String info="///////////////BULL SPRED OPTION TYPE "+s+"\n"
				+ "BUY/SELL Contract("+buy.getOpt().getTicker()+","+sell.getOpt().getTicker() + ")\n"
				+ "BUY/SELL Expiration date("+buy.getOpt().getExpiration_date()+","+sell.getOpt().getExpiration_date()+")\n"
				+ "BUY/SELL Strike("+buy.getOpt().getStrike()+","+sell.getOpt().getStrike()+")\n"
				+ "BUY/SELL ASK("+buy.getOpt().getAsk()+","+sell.getOpt().getAsk()+")\n"
				+ "BUY/SELL BID("+buy.getOpt().getBid()+","+sell.getOpt().getBid()+")\n"
				+ "max profit : "+ maxProfit()+"$"+"\n"
				+ "max loss : "+ maxLoss()+"$"+"\n"
				+ "probability of max success: "+Math.round(probabilityOfMaxProfit()*100)+"%\n"
				+ "probability of max loss: "+Math.round(probabilityOfMaxLoss()*100)+"%\n"
				+ "average of return  : "+ averageOfReturn()+"\n"
				+"day to expiration: "+daysToExpiration()+"\n"
				+"price is: "+ price()+"\n";
		return info;
	}







}
