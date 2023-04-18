package mycode.strategy_;

import mycode.object.*;
import mycode.trade.Main;

import java.util.ArrayList;


public class BuildStrategy {


	public static ArrayList<BoxSpread> boxSpread(ArrayList<Option> list){
		ArrayList<BearSpread>bearList=new ArrayList<BearSpread>();
		bearList.addAll(bearSpread(list));

		ArrayList<BullSpread> bullList=new ArrayList<BullSpread>();
		bullList.addAll(bullSpread(list));

		ArrayList<BoxSpread> boxSpredsList=new ArrayList<BoxSpread>();
		for(int i=0;i<bullList.size()-1;i++) {
			for(int j=bearList.size()-1;j>0;j--) {
				if(BoxSpread.inputIsCorrect(bullList.get(i), bearList.get(j))) {
					boxSpredsList.add(new BoxSpread(bullList.get(i), bearList.get(j)));
				}
				else {j=0;}
			}
		}
		return boxSpredsList;

	}

	public static ArrayList<BoxSpread> boxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList ){
		ArrayList<BoxSpread> boxSpredsList=new ArrayList<>();
		for(int i=0;i<bullList.size();i++) {
			for(int j=0;j<bearList.size();j++) {
				if(BoxSpread.inputIsCorrect(bullList.get(i),bearList.get(j))){
					boxSpredsList.add(new BoxSpread(bullList.get(i),bearList.get(j)));
				}
			}
		}
		return boxSpredsList;
	}

	public static ArrayList<ShortBoxSpread> shortBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList,double spread_diff ){
		ArrayList<ShortBoxSpread> shortBoxSpreadsList=new ArrayList<>();
		for(int i=0;i<bullList.size();i++) {

			for(int j=0;j<bearList.size();j++) {

				if(Math.abs(bearList.get(j).buy.getOpt().getStrike()-bearList.get(j).sell.getOpt().getStrike())>spread_diff){
					continue;
				}
				if(ShortBoxSpread.inputIsCorrect(bullList.get(i),bearList.get(j))) {

					shortBoxSpreadsList.add(new ShortBoxSpread(bullList.get(i),bearList.get(j)));
				}
			}
		}
		return shortBoxSpreadsList;
	}

	public static ArrayList<ShortBoxSpread> shortBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList){
		return  shortBoxSpread(bullList,bearList,100000);
	}

	public static ArrayList<LongBoxSpread> longBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList,double spread_diff ){
		ArrayList<LongBoxSpread> longBoxSpreadsList=new ArrayList<>();
		for(int i=0;i<bullList.size();i++) {
			for(int j=0;j<bearList.size();j++) {
				if(Math.abs(bearList.get(j).buy.getOpt().getStrike()-bearList.get(j).sell.getOpt().getStrike())>spread_diff){
					continue;
				}
				if(LongBoxSpread.inputIsCorrect(bullList.get(i),bearList.get(j)))
				{
					longBoxSpreadsList.add(new LongBoxSpread(bullList.get(i),bearList.get(j)));
				}
			}
		}
		return longBoxSpreadsList;
	}
	public static  ArrayList<LongBoxSpread> longBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList){
		return longBoxSpread(bullList,bearList, 100000);
	}



	/**
	 *
	 * @param list-call/put option list 
	 * @return list that every index represent iron condor strategy
	 */
	public static ArrayList<IronCondor> ironCondor(ArrayList<Option> list) {


		ArrayList<BearSpread>bearList=new ArrayList<BearSpread>();
		bearList.addAll(bearSpread(list));


		ArrayList<BullSpread> bullList=new ArrayList<BullSpread>();
		bullList.addAll(bullSpread(list));


		ArrayList<IronCondor> ironCondors=new ArrayList<IronCondor>();
		for(int i=0;i<bullList.size()-1;i++) {
			for(int j=bearList.size()-1;j>0;j--) {
				if(IronCondor.inputIsCorrect(bullList.get(i), bearList.get(j))) {
					ironCondors.add(new IronCondor(bullList.get(i), bearList.get(j)));
				}
				else {j=0;}

			}
		}

		return ironCondors;

	}

	public static ArrayList<IronCondor> ironCondor(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList){
		ArrayList<IronCondor> ironCondorsList=new ArrayList<>();
		for(int i=0;i<bullList.size();i++) {
			for(int j=0;j<bearList.size();j++) {
				if(IronCondor.inputIsCorrect(bullList.get(i),bearList.get(j))){
					ironCondorsList.add(new IronCondor(bullList.get(i),bearList.get(j)));
				}
			}
		}
		return ironCondorsList;
	}

	/**
	 *
	 * @param list-call/put option list 
	 * @return list that every index represent  bearSpread strategy
	 */
	public static ArrayList<BearSpread> bearSpread(ArrayList<Option> list,double num) {
		ArrayList<BearSpread> bear_list=new ArrayList<>();
		for (int i=0;i<list.size()-1;i++) {
			for(int j=i+1;j<list.size();j++) {
				// insure that the strategy spread is between the currrent price
//				if((list.get(i).getExercise_style().equals("american"))  && ((list.get(i).getStrike() > list.get(i).getUnderlying_price() && list.get(j).getStrike() > list.get(j).getUnderlying_price())
//						|| (list.get(i).getStrike() < list.get(i).getUnderlying_price() && list.get(j).getStrike() < list.get(j).getUnderlying_price()))){
//					continue;
//				}
				double spread=Math.abs(list.get(i).getStrike()-list.get(j).getStrike());
				if(spread<=num && BearSpread.inputIsCorrect(list.get(i),list.get(j)) ) {
					bear_list.add(new BearSpread(new Sell(list.get(i)), new Buy(list.get(j))));
				}
				else {j=list.size();}
			}
		}
		//		Collections.sort(bear_list);
		return bear_list;

	}

	public static ArrayList<BearSpread> bearSpread(ArrayList<Option> list){
		return bearSpread(list,100000);
	}

	/**
	 *
	 * @param list-call/put option list
	 * @return list that every index represent credit bearSpread strategy
	 */
	public static ArrayList<BearSpread> bearSpreadCall(ArrayList<Option> list) {
		ArrayList<BearSpread> bear_list=new ArrayList<>();
		for (int i=0;i<list.size()-1;i++) {
			for(int j=i+1;j<list.size();j++) {
				if(BearSpread.inputIsCorrect(list.get(i),list.get(j))
						&& list.get(i) instanceof OptionCall && list.get(j) instanceof OptionCall) {

					bear_list.add(new BearSpread(new Sell(list.get(i)), new Buy(list.get(j))));
				}
				else {j=list.size();}
			}
		}
		return bear_list;

	}

	/**
	 *
	 * @param list-call/put option list 
	 * @return list that every index represent bullSpread strategy
	 */
	public  static ArrayList<BullSpread> bullSpread(ArrayList<Option> list,double num) {
		ArrayList<BullSpread> bullList=new ArrayList<BullSpread>();

		for (int i=0;i<list.size()-1;i++) {
			for(int j=i+1;j<list.size();j++) {
				// insure that the strategy spread is between the currrent price
//				if((list.get(i).getExercise_style().equals("american"))   && ((list.get(i).getStrike() > list.get(i).getUnderlying_price() && list.get(j).getStrike() > list.get(j).getUnderlying_price())
//						|| (list.get(i).getStrike() < list.get(i).getUnderlying_price() && list.get(j).getStrike() < list.get(j).getUnderlying_price()))){
//					continue;
//				}
				double spread_=Math.abs(list.get(i).getStrike()-list.get(j).getStrike());
				if(spread_<=num && BullSpread.inputIsCorrect(list.get(i),list.get(j))  ) {
					bullList.add(new BullSpread(new Buy(list.get(i)), new Sell(list.get(j))));
				}
				else {j=list.size();}
			}
		}
		//	
		return bullList;
	}
	public  static ArrayList<BullSpread> bullSpread(ArrayList<Option> list) {
		return bullSpread(list,1000000);
	}

	public  static ArrayList<BullSpread> bullSpreadPut(ArrayList<Option> list) {
		ArrayList<BullSpread> bullList=new ArrayList<BullSpread>();

		for (int i=0;i<list.size()-1;i++) {
			for(int j=i+1;j<list.size();j++) {
				if(BullSpread.inputIsCorrect(list.get(i),list.get(j))
						&& list.get(i) instanceof OptionPut && list.get(j) instanceof OptionPut) {

					bullList.add(new BullSpread(new Buy(list.get(i)), new Sell(list.get(j))));
				}
				else {j=list.size();}
			}
		}
		//
		return bullList;

	}

}
