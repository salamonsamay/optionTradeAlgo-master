package mycode.object;


public class Buy  {

	public Option opt;
	double currentPrice;
	public static  final double  commission=2;
	public Buy() {


	}
	public Buy(Option opt) {
		this.opt = opt;
		this.currentPrice=currentPrice;	

	}

	public Buy(Buy other) {
		if(other.opt instanceof OptionCall) {
			this.opt=new OptionCall((OptionCall) other.opt);
		}
		else {
			this.opt=new OptionPut((OptionPut) other.opt);
		}

		this.currentPrice=other.currentPrice;



	}

	public double probabilityOfProfit() {
		return opt.getGreeks().getDelta();
	}

	public double getProfit() {
		if(opt instanceof OptionCall) {
			if(this.currentPrice<=opt.getStrike()) {
				return 0;
			}
			return (this.currentPrice-opt.getStrike())*100-(opt.getAsk()*100);
		}
		else {
			if(this.currentPrice>opt.getStrike()) {
				return 0;
			}
			return (opt.getStrike()-currentPrice)*100-(opt.getAsk()*100);
		}
	}

	public double getBreckEven() {
		if(opt instanceof OptionCall) {
			return opt.getStrike()+opt.getAsk();
		}
		else return  opt.getStrike()-opt.getAsk();
	}
	
	
	public boolean equals(Buy other) {
		if(this.opt.equals(other.opt))
			return true;
		else return false;

	}

	@Override
	public String toString() {
		return "Buy [opt=" + opt + ", currentPrice=" + currentPrice + ", commission=" + commission + "]";
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

	public Buy deepCopy(){
		Buy buy=new Buy();
		buy.opt=this.getOpt().deepCopy();
		buy.currentPrice=this.currentPrice;
		return  buy;
	}
	
}
