package mycode.strategy_;

import mycode.help.Tools;
import mycode.object.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class BuildStrategy {


	public static List<Reversal> reversal(List<Option> list) {
		return list.stream()
				.flatMap(option1 -> list.stream()
						.filter(option2 -> Reversal.inputIsCorrect(option1, option2))
						.map(option2 -> new Reversal(option1, option2)))
				.collect(Collectors.toList());
	}

	public static ArrayList<ShortBoxSpread> shortBoxSpread2(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList,double spread_diff ){
		Map<Object, List<BullSpread>> bullMap=bullList.stream().collect(Collectors.groupingBy(Tools::getGroup));
		Map<Object, List<BearSpread>> bearMap=bearList.stream().collect(Collectors.groupingBy(Tools::getGroup));
		ArrayList<ShortBoxSpread> shortBoxSpreadsList=new ArrayList<>();
		System.out.println("the key size  in bull map " + bullMap.keySet().size());
		System.out.println("the key size in bear map " + bearMap.keySet().size());
		for (Object key : bullMap.keySet()) {
			if (bearMap.containsKey(key)) {
				List<BullSpread> bullSpreads = bullMap.get(key);
				List<BearSpread> bearSpreads = bearMap.get(key);
				System.out.println(key);
				// Perform the comparison logic between 'bullSpreads' and 'bearSpreads'

				for (BullSpread bullSpread : bullSpreads) {
					if(!bullSpread.getCompanySymbol().equals("SPX")){continue;}
					for (BearSpread bearSpread : bearSpreads) {

						if (Math.abs(bearSpread.buy.getOpt().getStrike() - bearSpread.sell.getOpt().getStrike()) <= spread_diff) {
							if (ShortBoxSpread.inputIsCorrect(bullSpread, bearSpread)) {
								shortBoxSpreadsList.add(new ShortBoxSpread(bullSpread, bearSpread));
							}
						}
					}
				}
			}
		}

		return shortBoxSpreadsList;
	}

	public static ArrayList<ShortBoxSpread> shortBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList,double spread_diff ){

		ArrayList<ShortBoxSpread> shortBoxSpreadsList=new ArrayList<>();


		for(int i=0;i<bullList.size();i++) {

			for(int j=0;j<bearList.size();j++) {
//                if(bullList.get(i).buy.getOpt().getStrike()<bearList.get(j).sell.getOpt().getStrike()){
//					i++;
//				break;
//				}
				if(Math.abs(bearList.get(j).buy.getOpt().getStrike()-bearList.get(j).sell.getOpt().getStrike())>spread_diff){
					continue;
				}
				if(ShortBoxSpread.inputIsCorrect(bullList.get(i),bearList.get(j))) {

					shortBoxSpreadsList.add(new ShortBoxSpread(bullList.get(i),bearList.get(j)));
				}
			}
		}
		System.out.println("build "+shortBoxSpreadsList.size()+" short boxSpread");
		return shortBoxSpreadsList;
	}
	public static ArrayList<ShortBoxSpread> shortBoxSpread(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList){
		return  shortBoxSpread(bullList,bearList,100000);
	}


	public static ArrayList<LongBoxSpread> longBoxSpread2(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList, double spread_diff) {
		Map<String, List<BullSpread>> bullMap = bullList.stream().collect(Collectors.groupingBy(Tools::getGroup));
		Map<String, List<BearSpread>> bearMap = bearList.stream().collect(Collectors.groupingBy(Tools::getGroup));

		ArrayList<LongBoxSpread> longBoxSpreadsList = new ArrayList<>();

		for (String key : bullMap.keySet()) {
			if (bearMap.containsKey(key)) {
				List<BullSpread> bullSpreads = bullMap.get(key);
				List<BearSpread> bearSpreads = bearMap.get(key);

				for (BullSpread bullSpread : bullSpreads) {
					for (BearSpread bearSpread : bearSpreads) {
						if (Math.abs(bearSpread.buy.getOpt().getStrike() - bearSpread.sell.getOpt().getStrike()) <= spread_diff) {
							if (LongBoxSpread.inputIsCorrect(bullSpread, bearSpread)) {
								longBoxSpreadsList.add(new LongBoxSpread(bullSpread, bearSpread));

							}
						}
					}
				}

			}
		}

		return longBoxSpreadsList;
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
		System.out.println("build "+longBoxSpreadsList.size()+" long boxSpread");
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

	public static ArrayList<IronCondor> ironCondor2(ArrayList<BullSpread> bullList, ArrayList<BearSpread> bearList,double spread_diff){
		Map<Object, List<BullSpread>> bullMap=bullList.stream().collect(Collectors.groupingBy(Tools::getGroup));
		Map<Object, List<BearSpread>> bearMap=bearList.stream().collect(Collectors.groupingBy(Tools::getGroup));
		ArrayList<IronCondor> ironCondorsList=new ArrayList<>();
		System.out.println("the key size  in bull map " + bullMap.keySet().size());
		System.out.println("the key size in bear map " + bearMap.keySet().size());

		for (Object key : bullMap.keySet()) {
			System.out.println("iron condor size "+ironCondorsList.size());
			if (bearMap.containsKey(key)) {
				List<BullSpread> bullSpreads = bullMap.get(key);
				List<BearSpread> bearSpreads = bearMap.get(key);
				System.out.println(key);
				// Perform the comparison logic between 'bullSpreads' and 'bearSpreads'

				for (BullSpread bullSpread : bullSpreads) {
					for (BearSpread bearSpread : bearSpreads) {

						if (Math.abs(bearSpread.buy.getOpt().getStrike() - bearSpread.sell.getOpt().getStrike()) <= spread_diff) {
							if (IronCondor.inputIsCorrect(bullSpread, bearSpread)) {
								ironCondorsList.add(new IronCondor(bullSpread, bearSpread));
							}
						}
					}
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



}
