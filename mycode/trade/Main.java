package mycode.trade;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.ib.client.*;
import mycode.data.OptionChain;
import mycode.help.LinearEquation;
import mycode.help.MyMath;
import mycode.help.Tools;
import mycode.object.Option;
import mycode.object.Pair;
import mycode.strategy_.*;
import mycode.technical_indicator.Indicator;
import mycode.technical_indicator.VWAP;
import org.json.simple.parser.ParseException;


public class Main {
//	public static final String ACCOUNT ="DU6863447";//day trade demo=U10320468
//	public static final String ACCOUNT ="DU6156906";//demo=U10320468

	//	public static final String ACCOUNT="U10320468";//real=U10320468
	public static final String ACCOUNT="U10302374";//real new=U10302374
	public  static Hashtable<String, Pair<Double,Double>> symbols_prices_and_vwap_list = new Hashtable<>();
	public  static Hashtable<String,LinearEquation> linearList=new Hashtable<>();
	public static ArrayList<Option> create2(ArrayList<String> company_list) throws FileNotFoundException {

		ArrayList<OptionChain>option_chain_list=new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(100);
		for(int i=0;i<company_list.size();i++) {
			if(!Tools.haveExDividend(company_list.get(i))) {//not have a dividend  until the expration
				symbols_prices_and_vwap_list.put(company_list.get(i), new Pair<>(0.0, 0.0));
				OptionChain option_chain = new OptionChain(company_list.get(i));
				option_chain
						.Limit("250")
						.Expiriation_date_gt(Tools.DATE_START)
						.Expiriation_date_lt(Tools.DATE_END)
						.endPoint();

				option_chain_list.add(option_chain);
				pool.execute(option_chain);
			}
///////////////////////////////////////////////////////////////////////////////
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//			Calendar calendar = Calendar.getInstance();
//			calendar.add(Calendar.DAY_OF_YEAR, 1);
//			Date tomorrow = calendar.getTime();
//			calendar.add(Calendar.DAY_OF_YEAR, -2);
//			Date yesterday = calendar.getTime();
///////////////////////////////////////////////////////////////////////////////////
//			OptionChain option_chain2=new OptionChain(company_list.get(i));
//			option_chain2
//					.Limit("250")
//					.Expiriation_date_gt(formatter.format(yesterday))
//					.Expiriation_date_lt(formatter.format(tomorrow))
//					.endPoint();
//
//			option_chain_list.add(option_chain2);
//			pool.execute(option_chain2);
		}

		pool.shutdown();
		while(!pool.isTerminated()) {}

		ArrayList<Option> options_list=new ArrayList<>();

		for(int i=0;i<option_chain_list.size();i++){
			options_list.addAll(option_chain_list.get(i).option_list);
			option_chain_list.get(i).updateProcess();
		}

		return options_list;

	}
//	public static ArrayList<OptionContract> create(ArrayList<String> company_list){
//
//		ArrayList<OptionContract>allCompanyOptions=new ArrayList<>();
//		ExecutorService pool = Executors.newFixedThreadPool(250);
//		for(int i=0;i<company_list.size();i++) {
//			OptionContract contract=new OptionContract(company_list.get(i));
//			contract
//					.Limit("1000")
//					.Expiriation_date_lt(Tools.DATE_END)
//					.Expiriation_date_gt(Tools.DATE_START)
//					.endPoint();
//			allCompanyOptions.add(contract);
//			pool.execute(allCompanyOptions.get(i));
//		}
//		pool.shutdown();
//		while(!pool.isTerminated()) {}
//
//		return allCompanyOptions;
//
//	}

	public static void loadProgram(Program program) throws  IOException, ParseException, InterruptedException {

		EClientSocket client=program.m_s;
		OrdersManagement ordersManagement=program.ordersManagement;
		ArrayList<String> companyList=Tools.readCompanyFromFile();
		ArrayList<Strategy> strategys=new ArrayList<>();
		ArrayList<Option> optionList=create2(companyList);
		System.out.println("updateProccess linear");
		Tools.updateLinearList();
//		updateVWAP(program);
//		System.out.println("sleep  2 minute");
//		Thread.sleep(1000*60*2);
		System.out.println("option size befor filter  " +optionList.size());
		optionList=Tools.filterOpt(optionList);
		System.out.println("option size after filter  "+optionList.size());

		//////////////////////build strategy///////////////////////////////////////////
		System.out.println("start to build strategy");
		ArrayList<BearSpread> bearList=BuildStrategy.bearSpread(optionList,40);
		ArrayList<BullSpread> bullList=BuildStrategy.bullSpread(optionList,40);

		Thread.sleep(3000);
		System.out.println("start to loops over :"+bearList.size());
		System.out.println("start to loops over :"+bullList.size());
		strategys.addAll(bearList);
		strategys.addAll(bullList);

		//	strategys.addAll(BuildStrategy.ironCondor(bullList,bearList));
		//	strategys.addAll(BuildStrategy.shortBoxSpread(bullList,bearList));
		//strategys.addAll(BuildStrategy.longBoxSpread(bullList,bearList));

		//	strategys_0dte.addAll(BuildStrategy.ironCondor(bullList_Odte,bearList_0dte));

		strategys.addAll(BuildStrategy.shortBoxSpread(bullList,bearList));
	//	strategys.addAll(BuildStrategy.ironCondor(bullList,bearList));
		System.out.println("the strategys size : "+strategys.size());

		//////////////////////////////////////////////////////////////////////////////

		cancelTimer(client);


	//	runLongBox(client,strategys,ordersManagement);
		runShortBox(client,strategys,ordersManagement);
	//	runIronIronButterFly(client,strategys,ordersManagement);
//		runAtomicArbitrage(client,strategys_0dte,ordersManagement);
	//	runAtomicStrategy(client,strategys,ordersManagement);
	}
	public static void runAtomicArbitrage(EClientSocket client , ArrayList<Strategy> strategys,OrdersManagement ordersManagement){

		ArrayList<Strategy> dte0= (ArrayList<Strategy>) strategys.stream().filter(strategy -> strategy.daysToExpiration()==0).collect(Collectors.toList());
		if(dte0.size()<100){
			System.out.println("0 DTE");
			return;
		}
		Thread t=new Thread(new Runnable() {
			public void run() {
				System.out.println("start arbitrage algo...");
				int counter=0;
				while(true) {
					for(int i=0;i<strategys.size();i++) {
						Strategy copy=strategys.get(i).deepCopy();
						if( isArbitrage(copy) && !ordersManagement.isFilled(copy.getCompanySymbol())){
							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy_OCA(copy));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter>100){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		});
		t.start();



	}

	public static void runAtomicStrategy(EClientSocket client , ArrayList<Strategy> strategys,OrdersManagement ordersManagement){
		ArrayList<Strategy> newStrategy= (ArrayList<Strategy>) strategys.stream().filter(strategy -> (strategy instanceof BearSpread || strategy instanceof BullSpread) && strategy.daysToExpiration()==0).collect(Collectors.toList());
		System.out.println("start normal algo...");

		int cancel_index=0;
		while(true) {

			for(int i=0;i<newStrategy.size();i++) {
				Strategy copy=newStrategy.get(i).deepCopy();
				if(copy.getCompanySymbol().equals("SPY")){continue;}
				if(
						Tools.isValidData(copy)
								&& linearList.get(copy.getCompanySymbol()).isGood(copy)
								//	copy.isCreditSpread()
						//		&& Tools.isTimeToBuy(copy)
								&&  A(copy)
									&& Tools.isUnderValue(copy)

								&& !ordersManagement.isFilled(copy.getCompanySymbol()))
				{
					int next_order_id=Program.getNextOrderId();
					client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy_OCA(copy));
					Tools.sendedOrder.put(next_order_id,copy.toString());
					try {
						System.out.println(copy);
						System.out.println("sleep 3 second");
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					if(cancel_index++>15){
						cancel_index=0;
						client.reqGlobalCancel();
						Tools.sendedOrder.clear();
					}

				}
			}
		}
	}
	public static void runShortBox(EClientSocket client,ArrayList<Strategy> strategies,OrdersManagement ordersManagement){
		ArrayList<Strategy> shortBoxList= (ArrayList<Strategy>) strategies.stream().filter(s -> s instanceof ShortBoxSpread && s.daysToExpiration()<13).collect(Collectors.toList());

		new Thread(new Runnable() {
			public void run() {
				System.out.println("start short box ...");
				int counter=0;
				while (true){
					for(Strategy l :shortBoxList){
						ShortBoxSpread copy= (ShortBoxSpread) l.deepCopy();
						if(Tools.isValidData(copy) && isArbitrage(copy)   && !ordersManagement.isFilled(copy.getCompanySymbol())){
							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy_OCA(copy));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter>15){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}

						}
					}
				}

			}
		}).start();

	}
	public static void runLongBox(EClientSocket client,ArrayList<Strategy> strategies,OrdersManagement ordersManagement){
		ArrayList<Strategy> longBoxList= (ArrayList<Strategy>) strategies.stream().filter(s -> s instanceof LongBoxSpread && s.daysToExpiration()<1).collect(Collectors.toList());
		new Thread(new Runnable() {
			public void run() {
				System.out.println("start long box ");
				int counter=0;
				while (true){
					for(Strategy l :longBoxList){
						LongBoxSpread copy= (LongBoxSpread) l.deepCopy();

						if(isArbitrage(copy)  &&  (copy.percentage()>2 && (copy.probabilityITM()>0.9)) && !ordersManagement.isFilled(copy.getCompanySymbol())){
							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy_OCA(copy));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter>15){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}

						}
					}
				}
			}
		}).start();

	}
	public static void runIronIronButterFly(EClientSocket client,ArrayList<Strategy> strategies,OrdersManagement ordersManagement){

		ArrayList<Strategy> ironButterFly= (ArrayList<Strategy>) strategies.stream().filter(s -> s instanceof IronCondor
				&& ((IronCondor) s).isButterfly() && s.daysToExpiration()<1 ).collect(Collectors.toList());
		new Thread(new Runnable() {
			public void run() {
				System.out.println("start iron butterfly "+ironButterFly.size());

				int counter=0;
				while (true){
					for(Strategy l :ironButterFly){

						IronCondor copy= (IronCondor) l.deepCopy();
						if(copy.probabilityOfMaxProfit()>0.4
								&& copy.averageOfReturn()>30
								&& copy.maxLoss()>-20
								&& !ordersManagement.isFilled(copy.getCompanySymbol())){

							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy_OCA(copy));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter>15){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}

						}
					}
				}
			}
		}).start();
	}
	public static void all(EClientSocket client , ArrayList<Strategy> strategies,OrdersManagement ordersManagement) throws InterruptedException {
/////////////filter the dont match /////////////////////////////////////////////////////
		strategies= (ArrayList<Strategy>) strategies
				.stream()
				.filter(strategy -> (strategy instanceof BearSpread || strategy instanceof BullSpread))
				.collect(Collectors.toList());

///////////////////add the indicator //////////////////////////////////////////////////
		for(Strategy strategy: strategies){
			Option opt=null;
			if(strategy instanceof  BearSpread){
				opt=((BearSpread) strategy).sell.getOpt();
				opt.setIndicator(new Indicator(opt.getTicker()));
				opt=((BearSpread) strategy).buy.getOpt();
				opt.setIndicator(new Indicator(opt.getTicker()));
			}
			if(strategy instanceof  BullSpread){
				opt=((BullSpread) strategy).sell.getOpt();
				opt.setIndicator(new Indicator(opt.getTicker()));
				opt=((BullSpread) strategy).buy.getOpt();
				opt.setIndicator(new Indicator(opt.getTicker()));
			}
		}
		while (true){

			for(Strategy strategy: strategies){
				if(ordersManagement.isFilled(strategy.getCompanySymbol())){continue;}

				if(strategy instanceof  BearSpread){
					BearSpread copy= (BearSpread) strategy.deepCopy();
					if(copy.isTimeToBuy()){
						client.placeOrder(Program.getNextOrderId(),Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
						System.out.println(copy);
						Thread.sleep(3000);
					}
				}
				else {
					BullSpread copy= (BullSpread) strategy.deepCopy();
					if(copy.isTimeToBuy()){
						client.placeOrder(Program.getNextOrderId(),Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
						System.out.println(copy);
						Thread.sleep(3000);
					}
				}

			}

		}
	}
	public static void bullIndicator(EClientSocket client , ArrayList<BullSpread> bullSpreads,OrdersManagement ordersManagement) throws InterruptedException {

/////////////start to ---->//// init and add the indicator to all  option //////////////////////////////////////////////
		for(BullSpread bull: bullSpreads){
			Option opt=bull.sell.getOpt();
			if(opt.getIndicator()==null){opt.setIndicator(new Indicator(opt.getTicker()));}
			opt=bull.buy.getOpt();
			if(opt.getIndicator()==null){opt.setIndicator(new Indicator(opt.getTicker()));}
			Indicator.runAll(opt.getIndicator());
		}
//////////////end to ---->//// init and add the indicator to all  option /////////////////////////////////////

		while (true){
			for(BullSpread bull_value: bullSpreads){
				if(ordersManagement.isFilled(bull_value.getCompanySymbol())){continue;}
				BullSpread copy= (BullSpread) bull_value.deepCopy();
				if(copy.isTimeToBuy()){
					client.placeOrder(Program.getNextOrderId(),Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
					System.out.println(copy);
					Thread.sleep(3000);
				}
			}
		}

	}
	public static void bearIndicator(EClientSocket client , ArrayList<BearSpread> bearSpreads,OrdersManagement ordersManagement) throws InterruptedException {

		bearSpreads= (ArrayList<BearSpread>) bearSpreads
				.stream()
				.filter(bear-> bear.isCreditSpread() && bear.maxLoss()>-50 && bear.averageOfReturn()>5)
				.collect(Collectors.toList());

		List<Option>optionList=bearSpreads
				.stream()
				.map(bearSpread -> bearSpread.sell.getOpt())
				.collect(Collectors.toList());

		optionList.addAll(bearSpreads.stream()
				.map(bearSpread -> bearSpread.buy.getOpt())
				.collect(Collectors.toList()));
		System.out.println("option list size "+optionList.size());

		optionList.
				forEach(opt -> {if (opt.getIndicator()==null) opt.setIndicator(new Indicator(opt.getTicker()));});


		ArrayList<String> strings=new ArrayList<>();
		optionList
				.forEach(opt -> {if (!strings.contains(opt.getTicker())) strings.add(opt.getTicker());});

		System.out.println(optionList.size());
		optionList.forEach(opt -> Indicator.runAll(opt.getIndicator()));
		Thread.sleep(1000*60);
		System.out.println("while....");
		while (true){
//			bearSpreads.forEach(b -> {if(b.isTimeToBuy()
//					&& ordersManagement.isFilled(b.getCompanySymbol()))
//				client.placeOrder(Program.getNextOrderId(),Transaction.comboContract(b),Transaction.createOrderBuy(b.price()));
//				;});

			for(BearSpread bear_value: bearSpreads){
				ordersManagement.printFilledSymbol();
				if(ordersManagement.isFilled(bear_value.getCompanySymbol())){continue;}
				BearSpread copy= (BearSpread) bear_value.deepCopy();
				if(copy.isTimeToBuy()){
					client.placeOrder(Program.getNextOrderId(),Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
					System.out.println(copy);
					Thread.sleep(3000);
				}
			}
		}



	}
	public static void optionIndicator(EClientSocket client , ArrayList<Option> optionsList, int next_order_id,OrdersManagement ordersManagement) throws InterruptedException {

		System.out.println("start indicator algo...");
		ArrayList<Indicator> indicatorList=new ArrayList<>();

		int index=0;
		System.out.println("option list size "+optionsList.size());
		ArrayList<Option>  new_list=new ArrayList<>();
		ArrayList<String>  symbols=new ArrayList<>();

		for(Option opt:optionsList){
			if(opt.type().equals("call") && opt.getAsk()>0.8 && opt.getAsk()>0.4 && !symbols.contains(opt.getUnderlying_ticker())){
				symbols.add(opt.getUnderlying_ticker());
				new_list.add(opt);
				Indicator indicator=new Indicator(opt.getTicker());
				indicatorList.add(indicator);
				Indicator.runAll(indicator);
			}
		}
		symbols.clear();
		for(Option opt:optionsList){
			if(opt.type().equals("put") && opt.getAsk()>0.8 && opt.getAsk()>0.4 && !symbols.contains(opt.getUnderlying_ticker())){
				symbols.add(opt.getUnderlying_ticker());
				new_list.add(opt);
				Indicator indicator=new Indicator(opt.getTicker());
				indicatorList.add(indicator);
				Indicator.runAll(indicator);
			}
		}




		while (Indicator.created<index-1){
			System.out.println("sleep");
			Thread.sleep(2000);
		}

		System.out.println("__________________________");
		while (!Program.flag) {


			for (int i = 0; i < indicatorList.size(); i++) {
				Indicator ind = indicatorList.get(i);
				boolean rsi_flag_low = ind.getRsiObject().getValue() < 20 && ind.getRsiObject().getValue() > 1;
				boolean rsi_flag_high = ind.getRsiObject().getValue() > 80 && ind.getRsiObject().getValue() < 99;
				boolean histogram_flag_negative = ind.getMacdObject().getHistogram() < 0 && ind.getMacdObject().getValue() > 3;
				boolean histogram_flag_positive = ind.getMacdObject().getHistogram() > 0 && ind.getMacdObject().getValue() < -3;


				if (rsi_flag_low && histogram_flag_positive) {// time to up
					for (int j = 0; j < new_list.size(); j++) {
						Option opt = new_list.get(j);
						if(ordersManagement.isFilled(opt.getUnderlying_ticker())){continue;}
						if (opt.getTicker().equals(ind.getOptions_ticker()) && opt.getVwap() > opt.getAsk() * 1.2 && opt.type().equals("call")) {
							client.placeOrder(Program.getNextOrderId(), Transaction.singleContract(opt), Transaction.createOrderBuy(opt.getAsk()));
							next_order_id+=3;
							Thread.sleep(3000);
						}
					}
				} else if (rsi_flag_high && histogram_flag_negative) {// time to down
					for (int j = 0; j < optionsList.size(); j++) {
						Option opt = new_list.get(j);
						if(ordersManagement.isFilled(opt.getUnderlying_ticker())){continue;}
						if (opt.getTicker().equals(ind.getOptions_ticker()) && opt.getVwap() * 1.2 < opt.getAsk() && opt.type().equals("put")) {
							client.placeOrder(Program.getNextOrderId(), Transaction.singleContract(opt), Transaction.createOrderBuy(opt.getAsk()));
							Thread.sleep(3000);
						}
					}
				}
			}

		}




	}
	public static void updateVWAP(Program p){
		new Thread(new Runnable() {

			public void run() {
				while (true){
					for (String key : symbols_prices_and_vwap_list.keySet()) {
						p.reqHistoricalData_(key);
						while (!Program.historicalFlag){
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						}
						VWAP.calculateVWAP(Program.historical);
						//	System.out.println(key);
//						VWAP.print();
//						System.out.println();
						symbols_prices_and_vwap_list.put(key,new Pair<>(VWAP.lastPrice,VWAP.vwap));
						Program.historical="";
						Program.historicalFlag=false;
					}
					try {
						Thread.sleep(60*1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

			}
		}).start();

	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	private  synchronized static boolean A(Strategy strategy){


		if(
				strategy.maxProfit()+strategy.maxLoss()>0
						&&	strategy.probabilityOfMaxProfit()>0.5
						&& strategy.probabilityOfMaxProfit()>strategy.probabilityOfMaxLoss()
						&& strategy.averageOfReturn()>5
						&& strategy.maxLoss()>-200
		) {

			return true;

		}
		else{

			return  false;
		}
	}
	private synchronized static boolean isArbitrage(Strategy strategy){
		if(strategy.maxLoss()>50){
			return true;
		}
		return false;
	}
	private static void cancelTimer(EClientSocket client){
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					client.reqGlobalCancel();
					try {
						Thread.sleep(1000*60*5);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}).start();
	}
	public static void main(String[] args) throws Exception {
		Hashtable<String,Integer> d=new Hashtable<>();
		d.put("1",4);
		d.put("4",4);
		d.put("5",4);
		d.put("6",4);
		System.out.println(d.containsKey("5"));
	}



}
