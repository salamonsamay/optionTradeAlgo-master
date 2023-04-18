package mycode.strategy_;


import mycode.help.MyMath;
import mycode.help.Tools;
import mycode.object.OptionCall;
import mycode.object.OptionPut;

import java.text.NumberFormat;


public class IronCondor implements Strategy,Comparable<Strategy>{

	public BullSpread bull_put;
	public BearSpread bear_call;

	public static final double  COMMISSION=8;

	private long lastTimeUpdae;


	public IronCondor(){}



	public IronCondor(BullSpread put, BearSpread call) {
		this.bull_put=put;
		this.bear_call=call;

	}

	public double maxProfit() {

		return bear_call.maxProfit()+bull_put.maxProfit();
	}

	public double maxLoss() {

		return Math.min(bull_put.maxProfit()+bear_call.maxLoss(), bull_put.maxLoss()+bear_call.maxProfit());
	}

	public double probabilityOfMaxProfit() {

		return 1-((1-bull_put.probabilityOfMaxProfit())+(1-bear_call.probabilityOfMaxProfit()));
	}

	public double probabilityOfMaxLoss() {
		return bull_put.probabilityOfMaxLoss()+bear_call.probabilityOfMaxLoss();
	}


	public double averageOfReturn()	{

		return bear_call.averageOfReturn()+bull_put.averageOfReturn();
//		return probabilityOfMaxProfit()*maxProfit()
//				+probabilityOfMaxLoss()*( maxLoss()
//						+bear_call.probabilityAboveBreakEven()
//						+bull_put.probabilityBelowBreakEven());

	}


	public String getCompanySymbol() {
		return this.bear_call.buy.getOpt().getUnderlying_ticker();
	}

	public int daysToExpiration() {
		return this.bear_call.daysToExpiration();
	}


	public double price() {
		return this.bear_call.price()+this.bull_put.price();
	}

	public double midPointPrice() {
		return this.bear_call.midPointPrice()+this.bull_put.midPointPrice();
	}

	public Strategy deepCopy() {
		return new IronCondor((BullSpread) this.bull_put.deepCopy(), (BearSpread) this.bear_call.deepCopy());
	}


	public boolean isCreditSpread() {
		return (this.bear_call.isCreditSpread() && this.bull_put.isCreditSpread());
	}

	@Override
	public double percentage() {
		return 0;
	}


	public static boolean inputIsCorrect(BullSpread bull_Put, BearSpread bear_call) {
		boolean compareType=bull_Put.sell.getOpt() instanceof OptionPut && bear_call.sell.getOpt() instanceof OptionCall;
		boolean compareStrike=bull_Put.sell.getOpt().getStrike()<=bear_call.sell.getOpt().getStrike();
		boolean sameDate=bull_Put.sell.getOpt().getExpiration_date().equals(bear_call.sell.getOpt().getExpiration_date());
		boolean sameSymbol=bull_Put.sell.getOpt().getUnderlying_ticker().equals(bear_call.sell.getOpt().getUnderlying_ticker());

		if(compareStrike && compareType && sameDate && sameSymbol) {return true;	}
		return false;

	}

	public  boolean isButterfly(){
		if(bear_call.sell.getOpt().getStrike()==bull_put.sell.getOpt().getStrike()){
			return true;
		}
		return false;
	}

	public double theta(){
		double sellTheta=(bear_call.sell.getOpt().getGreeks().getTheta()-bull_put.sell.getOpt().getGreeks().getTheta())*-1;
		double buyTheta=(bear_call.buy.getOpt().getGreeks().getTheta()-bull_put.buy.getOpt().getGreeks().getTheta())*-1;
		return sellTheta-buyTheta;
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


		String str="Iron Condor \n"
				+"net credit/debit "+(bear_call.sell.getOpt().getBid()+bull_put.sell.getOpt().getBid()-bear_call.buy.getOpt().getAsk()-bull_put.buy.getOpt().getAsk())+"\n"
				+"day to expiration "+daysToExpiration()+"\n"
				+"underlying ticker call "+bear_call.sell.getOpt().getUnderlying_ticker()+"\n"
				+""+bull_put.sell.getOpt().getExpiration_date()+"\n"
				+ "strike "+bear_call.sell.getOpt().getStrike()+"/"+bear_call.buy.getOpt().getStrike()+""
				+ "  "+bull_put.buy.getOpt().getStrike()+"/"+bull_put.sell.getOpt().getStrike()+"\n"
				+ "max profit : "+ maxProfit()+"$"+"\n"
				+ "max lost  : "+ maxLoss()+"$"+"\n"
				+ "probability of max profit :"+Math.round(probabilityOfMaxProfit()*100)+"%\n"
				+ "probability of max lost : "+Math.round(probabilityOfMaxLoss()*100)+"%\n"
				+ "average of return  : "+ averageOfReturn()+"\n"
				+"return/risk "+ Math.abs(maxProfit()/maxLoss())+"\n";
		return str;
	}















	public static void main(String[] args) {
		BearSpread bearSpread =new BearSpread("60,2.38,2.38,C","66,0.80,0.80,C");
		BullSpread bullSpread =new BullSpread("35,1.72,1.72,P","40,2.10,2.10,P");

		IronCondor ironCondor=new IronCondor(bullSpread, bearSpread);
		System.out.println(ironCondor.maxLoss());
		System.out.println(ironCondor.maxProfit());

	}

}
