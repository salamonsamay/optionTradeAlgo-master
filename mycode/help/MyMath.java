package mycode.help;

import mycode.object.*;
import mycode.strategy_.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class MyMath {



	public static double marginRequirement(ShortBoxSpread shortBoxSpread){
		return Math.max(1.02* costToClose(shortBoxSpread)*100
				,shortBoxSpread.bearSpread.buy.getOpt().getStrike()
						-shortBoxSpread.bearSpread.sell.getOpt().getStrike());
	}

	/**
	 * calculate the loan rate
	 * if its arbitrage its return positive digit
	 * @param boxSpread
	 * @return percent the rate
	 */
	public static double rate(ShortBoxSpread boxSpread){
		double strike_1=boxSpread.bearSpread.buy.getOpt().getStrike();
		double strike_2=boxSpread.bearSpread.sell.getOpt().getStrike();
		double strike_diff=Math.abs(strike_1-strike_2);
		return (boxSpread.maxProfit()/strike_diff)*100;
	}
	public static double costToClose(Strategy strategy){

		if(strategy instanceof BearSpread){
			BearSpread bear=((BearSpread)(strategy));
			return (bidAskSpread(bear.sell.getOpt())+bidAskSpread(bear.buy.getOpt()))*100;
		}
		if(strategy instanceof BullSpread){
			BullSpread bull=((BullSpread)(strategy));
			return (bidAskSpread(bull.sell.getOpt())+bidAskSpread(bull.buy.getOpt()))*100;
		}
		if(strategy instanceof IronCondor){
			IronCondor ironCondor=(IronCondor)(strategy);
			return costToClose(ironCondor.bull_put) +costToClose(ironCondor.bear_call);
		}

		if(strategy instanceof ShortBoxSpread){
			ShortBoxSpread shortbox=(ShortBoxSpread)(strategy);
			return costToClose(shortbox.bullSpread) + costToClose(shortbox.bearSpread);
		}
		if(strategy instanceof LongBoxSpread){
			LongBoxSpread longbox=(LongBoxSpread)(strategy);
			return costToClose(longbox.bullSpread) +costToClose(longbox.bearSpread);
		}
		else {
			throw new RuntimeException();
		}

	}

	public static double bidAskSpread(Option opt){
		return opt.getAsk()-opt.getBid();
	}


	public static double midPrice(Strategy strategy){
		if(strategy instanceof BearSpread){
			BearSpread bearSpread= (BearSpread) strategy;
			if(bidAskSpread(((BearSpread) strategy).buy.getOpt())<0.05  ||bidAskSpread(((BearSpread) strategy).sell.getOpt())<0.05){
				return strategy.price();
			}
			return midPrice(bearSpread.buy.getOpt())-midPrice(bearSpread.sell.getOpt());
		}
		if(strategy instanceof  BullSpread){
			BullSpread bullSpread=(BullSpread) strategy;
			if(bidAskSpread(((BullSpread) strategy).buy.getOpt())<0.05  || bidAskSpread(((BullSpread) strategy).sell.getOpt())<0.05){
				return strategy.price();
			}
			return midPrice(bullSpread.buy.getOpt())-midPrice(bullSpread.sell.getOpt());
		}
		if(strategy instanceof IronCondor){
			IronCondor iron= (IronCondor) strategy;
			return midPrice(iron.bear_call)+midPrice(iron.bull_put);
		}
		if(strategy instanceof  ShortBoxSpread){
			ShortBoxSpread short_box=(ShortBoxSpread) strategy;
			return midPrice(short_box.bearSpread)+midPrice(short_box.bullSpread);
		}
		if(strategy instanceof  LongBoxSpread){
			LongBoxSpread long_box=(LongBoxSpread) strategy;
			return midPrice(long_box.bearSpread)+midPrice(long_box.bullSpread);
		}
		throw new NullPointerException();

	}
	public static double midPrice(Option opt){
		return (opt.getAsk()+opt.getBid())/2;
	}

	public static double timeDecay(Strategy strategy){
		if(strategy instanceof BearSpread){
			BearSpread bear=((BearSpread)(strategy));
			return timeDecay(bear.sell.getOpt())+timeDecay(bear.buy.getOpt());
		}
		if(strategy instanceof BullSpread){
			BullSpread bull=((BullSpread)(strategy));
			return timeDecay(bull.sell.getOpt())+timeDecay(bull.buy.getOpt());
		}
		if(strategy instanceof ShortBoxSpread){
			ShortBoxSpread shortbox=(ShortBoxSpread)(strategy);
			return timeDecay(shortbox.bullSpread.buy.getOpt())+timeDecay(shortbox.bullSpread.sell.getOpt())
					+timeDecay(shortbox.bearSpread.buy.getOpt())+timeDecay(shortbox.bearSpread.sell.getOpt());
		}
		if(strategy instanceof IronCondor){
			IronCondor ironCondor=(IronCondor)(strategy);
			return timeDecay(ironCondor.bull_put.buy.getOpt())+timeDecay(ironCondor.bull_put.sell.getOpt())
					+timeDecay(ironCondor.bear_call.buy.getOpt())+timeDecay(ironCondor.bear_call.sell.getOpt());
		}
		if(strategy instanceof LongBoxSpread){
			LongBoxSpread longbox=(LongBoxSpread)(strategy);
			return timeDecay(longbox.bullSpread.buy.getOpt())+timeDecay(longbox.bullSpread.sell.getOpt())
					+timeDecay(longbox.bearSpread.buy.getOpt())+timeDecay(longbox.bearSpread.sell.getOpt());
		}
		else {
			throw new RuntimeException();
		}
	}
	public static double timeDecay(Option opt){
		String str[]=opt.getExpiration_date().split("-");
		System.out.println(str[0]+str[1]+str[2]);
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(str[0]),Integer.parseInt(str[1]),Integer.parseInt(str[2]));
		int day= (int) (((c.getTime().getTime()-new Date().getTime())/(1000*60*60*24))-30);

		double timeDecay=opt.getGreeks().getTheta()*day;
		return timeDecay;
	}

	public static double binomialOptionPrice(double stockPrice, double strikePrice,
											 double riskFreeRate, double volatility,
											 int timeSteps, double timeToExpiration,
											 boolean isCallOption) {

		// Calculate parameters
		double deltaT = timeToExpiration / timeSteps;
		double upFactor = Math.exp(volatility * Math.sqrt(deltaT));
		double downFactor = 1 / upFactor;
		double discountFactor = Math.exp(-riskFreeRate * deltaT);

		// Initialize stock price tree and option value tree
		double[][] stockPriceTree = new double[timeSteps + 1][timeSteps + 1];
		double[][] optionValueTree = new double[timeSteps + 1][timeSteps + 1];

		// Calculate stock price tree
		for (int i = 0; i <= timeSteps; i++) {
			for (int j = 0; j <= i; j++) {
				stockPriceTree[i][j] = stockPrice * Math.pow(upFactor, j) * Math.pow(downFactor, i - j);
			}
		}

		// Calculate option value at expiration
		for (int j = 0; j <= timeSteps; j++) {
			if (isCallOption) {
				optionValueTree[timeSteps][j] = Math.max(stockPriceTree[timeSteps][j] - strikePrice, 0);
			} else {
				optionValueTree[timeSteps][j] = Math.max(strikePrice - stockPriceTree[timeSteps][j], 0);
			}
		}

		// Calculate option value at earlier time steps using backward induction
		for (int i = timeSteps - 1; i >= 0; i--) {
			for (int j = 0; j <= i; j++) {
				optionValueTree[i][j] = (discountFactor * (optionValueTree[i+1][j] + optionValueTree[i+1][j+1])) / 2;
				if (isCallOption) {
					optionValueTree[i][j] = Math.max(optionValueTree[i][j], stockPriceTree[i][j] - strikePrice);
				} else {
					optionValueTree[i][j] = Math.max(optionValueTree[i][j], strikePrice - stockPriceTree[i][j]);
				}
			}
		}

		// Return option value at time 0
		return optionValueTree[0][0];
	}

	public static void main(String[] args) {
		// Example usage
		double stockPrice = 158.8;
		double strikePrice = 160;
		double riskFreeRate = 5.5;
		double volatility = 0.2928077777806022;
		int timeSteps = 100;
		double timeToExpiration = 1;
		boolean isCallOption = true;

		double optionPrice = binomialOptionPrice(stockPrice, strikePrice, riskFreeRate, volatility, timeSteps, timeToExpiration, isCallOption);
		System.out.println("Option price: " + optionPrice);
	}



//
//	public static void main(String[] args) throws IOException {
//
//		System.out.println(	binomialOptionPrice(158.8,149,4,0.43456946455219864,5.0/365.0,100,true));
//
//	}



}
