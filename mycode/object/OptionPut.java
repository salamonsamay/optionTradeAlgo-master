package mycode.object;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import mycode.data.OptionContract;
import mycode.technical_indicator.Indicator;


public class OptionPut implements Option {
	private String underlying_ticker="";
	private double underlying_price;
	private String ticker="";
	private double strike;//
	private double ask;//the cost or prime for the option
	private double bid;

	private double mid_point;
	private Greeks greeks=null;
	private String expiration_date;
	private String exercise_style;
	private double vwap;
	private double volume;
	private Indicator indicator;
	private long lastUpdate;
	private int contractId;


	public OptionPut() {

	}

	public OptionPut(double strike,double ask, Greeks greek) {
		this.ask = ask;
		this.bid=ask;
		this.strike = strike;
		this.greeks = greek;
	}
	public OptionPut(double strike,double ask,double bid, Greeks greek) {
		this.strike = strike;
		this.ask = ask;
		this.bid=bid;
		this.greeks = greek;
	}
	public OptionPut(String underlying_ticker,String ticker ,String strike,String ask, String greek,String expiration_date) {
		this.underlying_ticker=underlying_ticker;
		this.ticker=ticker;
		this.strike = Double.parseDouble(strike);
		this.ask= (Math.random()*10);
		this.greeks=new Greeks(Math.random(),1,1,1);
		this.expiration_date=expiration_date;
	}

	public OptionPut(OptionPut other) {
		this.underlying_ticker=other.underlying_ticker;
		this.ticker=other.ticker;
		this.strike = other.strike;
		this.bid=other.bid;
		this.ask = other.ask;
		this.greeks = new Greeks(other.greeks);
		this.expiration_date=other.expiration_date;
	}


	public String type() {
		return "put";
	}

	public String getUnderlying_ticker() {

		return underlying_ticker;
	}

	public void setUnderlying_ticker(String underlying_ticker) {
		this.underlying_ticker=underlying_ticker;

	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker=ticker;

	}

	public double getStrike() {
		return strike;
	}

	public void setStrike(double strike) {
		this.strike=strike;

	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask=ask;

	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getMid_point() {
		return this.mid_point;
	}

	public void setMid_point(double mid_point) {
		this.mid_point = mid_point;
	}

	public Greeks getGreeks() {
		return this.greeks;
	}

	public void setGreeks(Greeks greeks) {
		this.greeks=greeks;

	}

	public String getExpiration_date() {
		return this.expiration_date;
	}

	public void setExpiration_date(String expiration_date) {
		this.expiration_date=expiration_date;
	}

	public String getExercise_style() {
		return this.exercise_style;
	}

	public void setExercise_style(String exerciseStyle) {
		this.exercise_style=exerciseStyle;
	}

	public double getVwap() {
		return vwap;
	}

	public void setVwap(double vwap) {
		this.vwap = vwap;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Indicator getIndicator() {
		return indicator;
	}
	public void setIndicator(Indicator indicator) {
		this.indicator=indicator;
	}
	public double breakEven() {
		return this.strike-this.ask;
	}






	public boolean equals(Object opt_) {
		Option opt= (Option) opt_;
		if(opt instanceof OptionCall)
			return false;
		boolean flag1=(getTicker().equals(opt.getTicker()));

		if(flag1 ) {
			return true;
		}

		return false;
	}


	public void setUnderlying_price(double underlying_price) {
		this.underlying_price=underlying_price;
	}
	public double getUnderlying_price() {
		return this.underlying_price;
	}


	@Override
	public String toString() {
		return "OptionPut{" +
				"underlying_ticker='" + underlying_ticker + '\'' +
				", underlying_price=" + underlying_price +
				", ticker='" + ticker + '\'' +
				", strike=" + strike +
				", ask=" + ask +
				", bid=" + bid +
				", greeks=" + greeks +
				", expiration_date='" + expiration_date + '\'' +
				", exercise_style='" + exercise_style + '\'' +
				", vwap=" + vwap +
				", lastUpdate=" + lastUpdate +
				", contractId=" + contractId +
				'}';
	}

	public  void update(Option opt) {
		if (opt instanceof OptionCall) throw new RuntimeException();

		setUnderlying_ticker(opt.getUnderlying_ticker());
		setUnderlying_price(opt.getUnderlying_price());
		setTicker(opt.getTicker());
		setStrike(opt.getStrike());
		setAsk(opt.getAsk());
		setBid(opt.getBid());
		setMid_point(opt.getMid_point());
		setGreeks(opt.getGreeks());
		setExpiration_date(opt.getExpiration_date());
		setExercise_style(opt.getExercise_style());
		setVolume(getVolume());
		setVwap(opt.getVwap());
		setLastUpdate(opt.getLastUpdate());

	}
	public  boolean update() {
		//if the last time was updateProccess less than 1 minute
		if((new Date()).getTime()- getLastUpdate()<1000) {return true;}

		String url="https://api.polygon.io/v3/snapshot/options/"+getUnderlying_ticker()+"/"+getTicker()+"?apiKey=Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
		OptionContract opt_=new OptionContract(getUnderlying_ticker());
		Option opt=null;
		try {
			opt = opt_.snapShot(url);
			setUnderlying_ticker(opt.getUnderlying_ticker());
			setUnderlying_price(opt.getUnderlying_price());
			setTicker(opt.getTicker());
			setStrike(opt.getStrike());
			setAsk(opt.getAsk());
			setBid(opt.getBid());
			setMid_point(opt.getMid_point());
			setGreeks(opt.getGreeks());
			setExpiration_date(opt.getExpiration_date());
			setVwap(opt.getVwap());
			setLastUpdate((new Date()).getTime());
		} catch (Exception e) {
//			reqHistoricalData_.printStackTrace();
			return false;
		}

		return true;
	}



	public int getContractId() {
		return this.contractId;
	}

	public void setContractId(int contractId) {
		this.contractId=contractId;
	}


	public Option deepCopy() {
		OptionPut opt=new OptionPut();
		opt.setContractId(getContractId());
		opt.setExpiration_date(getExpiration_date());
		opt.setExercise_style(getExercise_style());
		opt.setUnderlying_ticker(getUnderlying_ticker());
		opt.setUnderlying_price(getUnderlying_price());
		opt.setTicker(getTicker());
		opt.setAsk(getAsk());
		opt.setBid(getBid());
		opt.setMid_point(getMid_point());
		opt.setStrike(getStrike());
		opt.setGreeks(new Greeks(getGreeks()));
		opt.setVwap(getVwap());
		opt.setVolume(getVolume());
		opt.setIndicator(getIndicator());//not deep copy
		return opt;
	}


	public long getLastUpdate() {

		return this.lastUpdate;
	}


	public void setLastUpdate(long time) {

		this.lastUpdate =time;
	}

	public int daysToExpiration() {

		String str[]=getExpiration_date().split("-");
		String newDateFormat=str[0]+"/"+str[1]+"/"+str[2];
		LocalDate endDate=LocalDate.of(Integer.parseInt(str[0]),Integer.parseInt(str[1]),Integer.parseInt(str[2]));
		LocalDate startDate=LocalDate.now();
		Date current=new Date();

		int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate);
		return  daysBetween;
	}






}
