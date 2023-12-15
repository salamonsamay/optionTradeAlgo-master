package mycode.trade;

import com.ib.client.EClientSocket;
import mycode.help.Tools;
import mycode.object.Option;
import mycode.strategy_.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;


public class Main {


	public static void loadProgram(Program program) throws  IOException, ParseException, InterruptedException {

		EClientSocket client=program.m_s;
		OrdersManagement ordersManagement=program.ordersManagement;
		ArrayList<String> companyList=Tools.readCompanyFromFile();
		ArrayList<Option> optionList=Tools.getOptions(companyList);
//		System.out.println("updateProccess linear");
//		Tools.updateLinearList();
//		updateVWAP(program);
//		System.out.println("sleep  2 minute");
//		Thread.sleep(1000*60*2);
		System.out.println("option size befor filter  " +optionList.size());
		optionList=Tools.filterOpt(optionList);
		System.out.println("option size after filter  "+optionList.size());

		//////////////////////build strategy///////////////////////////////////////////
		System.out.println("start to build strategy");
		ArrayList<BearSpread> bearList=BuildStrategy.bearSpread(optionList,500);
		ArrayList<BullSpread> bullList=BuildStrategy.bullSpread(optionList,500);
		//	ArrayList<PutCallParity> putCallParityArrayList=BuildStrategy.putCallParities(optionList);
		//	runPutCallParity(client,putCallParityArrayList);

//		for(PutCallParity value:putCallParityArrayList){
//			System.out.println(value.checkPutCallParity());
//			System.out.println(value);
//
//
//		}

		System.out.println("start to loops over :"+bearList.size());
		System.out.println("start to loops over :"+bullList.size());

		//strategys.addAll(reversal);

		//	strategys.addAll(BuildStrategy.ironCondor(bullList,bearList));

		ArrayList<Strategy> strategys=new ArrayList<>();
		strategys.addAll(bearList);
		strategys.addAll(bullList);
		strategys.addAll(BuildStrategy.longBoxSpread2(bullList,bearList,100));
		strategys.addAll(BuildStrategy.shortBoxSpread2(bullList,bearList,500));
		strategys.addAll( BuildStrategy.reversal(optionList));
		//	strategys.addAll(BuildStrategy.shortBoxSpread(bullList,bearList,100));


		//	strategys.addAll(BuildStrategy.ironCondor2(bullList,bearList,1000));

		//strategys.addAll(BuildStrategy.shortBoxSpread(bullList,bearList,100));
		//	strategys.addAll(BuildStrategy.ironCondor(bullList,bearList));
		//	strategys.addAll(BuildStrategy.longBoxSpread2(bullList,bearList,100));
		System.out.println("the strategys size : "+strategys.size());

		//////////////////////////////////////////////////////////////////////////////

		cancelTimer(client);

		//	runLongBox(client,strategys,ordersManagement);
		//		runShortBox(client,strategys,ordersManagement);
//		runIronIronButterFly(client,strategys,ordersManagement);
		runAtomicArbitrage(client,strategys,ordersManagement);
		//	runAtomicStrategy(client,strategys,ordersManagement);
	}
	public static void runAtomicArbitrage(EClientSocket client , ArrayList<Strategy> strategys,OrdersManagement ordersManagement){

		Thread t=new Thread(new Runnable() {
			public void run() {
				System.out.println("start arbitrage algo...");
				int counter=0;
				while(true) {
					int random=(int)(Math.random()*strategys.size());
					Strategy copy=strategys.get(random).deepCopy();
					if( Tools.isValidData(copy) && isArbitrage(copy) ){
						int next_order_id=Program.getNextOrderId();
						client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
						Tools.sendedOrder.put(next_order_id,copy.toString());
						System.out.println("send order for "+copy);


						if(counter++>20){
							client.reqGlobalCancel();
							Tools.sendedOrder.clear();
							counter=0;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}

				}
			}

		});
		t.start();



	}

	public  static void runPutCallParity(EClientSocket client, ArrayList<PutCallParity> list){
		Thread t=new Thread(()->{
			while (true){
				Collections.sort(list);

				try {
					System.out.println("_______________________________");
					System.out.println(list.get(0).checkPutCallParity());
					System.out.println(list.get(0));
					System.out.println(list.get(list.size()-1).checkPutCallParity());
					System.out.println(list.get(list.size()-1));
					System.out.println("_______________________________");
					Thread.sleep(60*1000*5);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}


		});
		t.start();
	}
	public static void runAtomicStrategy(EClientSocket client , ArrayList<Strategy> strategys,OrdersManagement ordersManagement){
		System.out.println("start normal algo...");

		int cancel_index=0;
		while(true) {

			for(int i=0;i<strategys.size();i++) {
				Strategy copy=strategys.get(i).deepCopy();
				if(
						Tools.isValidData(copy)
								//	copy.isCreditSpread()
								//		&& Tools.isTimeToBuy(copy)
								&&  A(copy)
								&& Tools.isUnderValue(copy)

								&& !ordersManagement.isFilled(copy.getCompanySymbol()) )

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

		new Thread(new Runnable() {
			public void run() {
				System.out.println("start short box ...");
				int counter=0;
				while (true){
					for(Strategy l :strategies){
						ShortBoxSpread copy= (ShortBoxSpread) l.deepCopy();
//						double stock_price=copy.bearSpread.sell.getOpt().getUnderlying_price();
//						if(Math.abs(stock_price-copy.bearSpread.sell.getOpt().getStrike())<
//								Math.abs(stock_price-copy.bullSpread.sell.getOpt().getStrike())){
//							continue;
//						}
						if( Tools.isValidData(copy) && isArbitrage(copy)
								&& !ordersManagement.isFilled(copy.getCompanySymbol())
						){
							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter++>50){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(5000);
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
		ArrayList<Strategy> longBoxList= (ArrayList<Strategy>) strategies.stream().filter(s -> s instanceof LongBoxSpread && s.daysToExpiration()<20).collect(Collectors.toList());
		new Thread(new Runnable() {
			public void run() {
				System.out.println("start long box ");
				int counter=0;
				while (true){
					for(Strategy l :longBoxList){
						LongBoxSpread copy= (LongBoxSpread) l.deepCopy();

						if(copy.isDeepInTheMoney() && isArbitrage(copy)  &&   !ordersManagement.isFilled(copy.getCompanySymbol())){
							int next_order_id=Program.getNextOrderId();
							client.placeOrder(next_order_id, Transaction.comboContract(copy),Transaction.createOrderBuy(copy.price()));
							Tools.sendedOrder.put(next_order_id,copy.toString());
							System.out.println("send order for "+copy);
							if(counter>50){
								client.reqGlobalCancel();
								Tools.sendedOrder.clear();
								counter=0;
							}
							try {
								Thread.sleep(5000);
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
								&& copy.averageOfReturn()>5
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
		if(strategy.maxLoss()>0 && strategy instanceof  LongBoxSpread){
			if( ((LongBoxSpread) strategy).yearlyInterestRate()>20){
				return  true;
			}
			return false;
		}
		if(strategy instanceof  Reversal){
			if(strategy.maxProfit()>50 && ((Reversal) strategy).syntheticLong.buy.getOpt().getAsk()>((Reversal) strategy).syntheticLong.sell.getOpt().getAsk()){
				return true;
			}
			return false;
		}
		if(strategy instanceof  ShortBoxSpread){

			if(((ShortBoxSpread) strategy).maxLoss()>0
			){
				return  true;
			}
			return false;
		}
		if(strategy instanceof  IronCondor){
			if(strategy.maxLoss()>0 && strategy.price()*100<-10000){
				return  true;
			}
		}
		if(strategy instanceof  BearSpread || strategy instanceof  BullSpread){
			if(strategy.maxLoss()>0)
				return true;
			return false;
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
						Thread.sleep(1000*60*50);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}).start();
	}
	public static void main(String[] args) throws Exception {
		File file=new File(Tools.PATH);
		File list[]=file.listFiles();

		for(int i=0; i<list.length; i++) {
			File file1=new File(Tools.PATH+list[i].getName());
			PrintWriter printWriter=new PrintWriter(file1);

			printWriter.print("");
			printWriter.close();
		}

	}



}
