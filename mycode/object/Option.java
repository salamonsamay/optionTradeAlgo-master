package mycode.object;

import mycode.technical_indicator.Indicator;

public interface Option {

	String getUnderlying_ticker();
	void setUnderlying_ticker(String underlying_ticker);

	String getTicker();
	void setTicker(String ticker);

	double getStrike();
	void setStrike(double strike);

	double getAsk();
	void setAsk(double ask);

	double getBid();
	void setBid(double ask);

	double getMid_point();

	void setMid_point(double mid_point);

	Greeks getGreeks();
	void setGreeks(Greeks greeks);

	String getExpiration_date();
	void setExpiration_date(String expiration_date);

	String getExercise_style();
	void setExercise_style(String exerciseStyle);

	long getLastUpdate();
	void setLastUpdate(long time);

	double breakEven();


	void setUnderlying_price(double underlying_price);
	double getUnderlying_price();
	String type();

	void update(Option opt);
	boolean update();

	int getContractId();

	void setContractId(int contractId);

	double getVwap();

	void setVwap(double vwap);

	double getVolume();
	void setVolume(double vwap);

	Indicator getIndicator();

	void setIndicator(Indicator indicator);


	Option deepCopy();



	int daysToExpiration();




}
