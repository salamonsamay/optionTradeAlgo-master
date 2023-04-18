package mycode.object;

public class Sell  {

	public Option opt;
	double currentPrice;
	public static  final double  commission=2;

	public Sell(){}
	public Sell(Option opt) {
		this.opt = opt;
		this.currentPrice=currentPrice;	
	}

	public Sell(Sell other) {
		if(other.opt instanceof OptionCall) {
			this.opt=new OptionCall((OptionCall) other.opt);
		}
		else {
			this.opt=new OptionPut((OptionPut) other.opt);
		}
		this.currentPrice=other.currentPrice;


	}


	public double probabilityOfProfit() {
		return 1- opt.getGreeks().getDelta();
	}

	public double getProfit() {
		return opt.getAsk()*100;
	}

	public double getBreckEven() {
		if(opt instanceof OptionCall) {
			return opt.getStrike()+opt.getAsk();
		}
		else return  opt.getStrike()-opt.getAsk();
	}
	
	public boolean equals(Sell other) {
		if(this.opt.equals(other.opt))
			return true;
		else return false;
	}

	@Override
	public String toString() {
		return "Sell [opt=" + opt + ", currentPrice=" + currentPrice + ", commission=" + commission + "]";
	}

	public Option getOpt() {
		return opt;
	}

	public void setOpt(Option opt) {
		this.opt = opt;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getCommission() {
		return commission;
	}


	public Sell deepCopy(){
		Sell sell=new Sell();
		sell.opt=this.getOpt().deepCopy();
		sell.currentPrice=this.currentPrice;
		return  sell;
	}
	

}
