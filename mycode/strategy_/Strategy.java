package mycode.strategy_;

public interface Strategy  {



	/**
	 * describe the max profit  the case i'm buy in ask price and sell  in bid price
	 * @return
	 */
	double maxProfit();


	/**
	 * describe the max lost if  the case i'm buy in ask price and sell  in bid price
	 * @return
	 */
	double maxLoss();


	/**
	 * calculate the average return by buy in ask price and sell in bid price
	 * @return
	 */
	double averageOfReturn();


	/**
	 * by using 'delta' the method 
	 * calculate the probability of maxProfit() in one single strategy
	 * @return x ---> 0<=x<=1
	 */
	double probabilityOfMaxProfit();

	double probabilityOfMaxLoss();


	String getCompanySymbol();

	int daysToExpiration();

	double price();

	double midPointPrice();

	Strategy deepCopy() ;
	boolean isCreditSpread();

	double percentage();

}
