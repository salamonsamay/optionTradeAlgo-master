package mycode.data;



import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;


import java.util.Scanner;

import mycode.object.Greeks;
import mycode.object.Option;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class OptionContract extends Thread{

	private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
	private static String url="https://api.polygon.io/v3/reference/options/contracts?apiKey=Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
	private String endPoint="";
	static int index=0;

	public ArrayList<Option> option_list;


	//public static String url="https://api.polygon.io/v3/reference/options/";
	//https://api.polygon.io/v3/reference/options/contracts?underlying_ticker=PLTR&expiration_date.lt=2022-12-02&expiration_date.gt=2022-11-02&limit=1000&sort=strike_price&apiKey=YLMlOppNufEGaHogyZpL2Bh9b3eC2v4U
	private String ticker="";
	private String underlying_ticker="";
	private String contract_type="";
	private String expiriation_date_lt="";
	private String expiriation_date_gt="";
	private String strike_price="";
	private String expired="";
	private String order="";
	private String  limit="";
	private String  sort="";

	public OptionContract(){

	}
	public OptionContract(String symbol){
		this.underlying_ticker=symbol;
	}

	public OptionContract endPoint() {
		if(ticker!="") {endPoint+="&ticker="+ticker;}
		if(underlying_ticker!="") {endPoint+="&underlying_ticker="+underlying_ticker;}
		if(contract_type!="") {endPoint+="&contract_type="+contract_type;}
		if(expiriation_date_lt!="") {endPoint+="&expiration_date.lt="+expiriation_date_lt;}
		if(expiriation_date_gt!=""){endPoint+="&expiration_date.gt="+expiriation_date_gt;}
		if(strike_price!="") {endPoint+="&strike_price="+strike_price;}
		if(expired!="") {endPoint+="&expired="+expired;}
		if(order!="") {endPoint+="&order="+order;}
		if(limit!="") {endPoint+="&limit="+limit;}
		if(sort!="") {endPoint+="&sort="+sort;}

		return this;
	}

	public OptionContract Ticker(String ticker) {
		this.ticker=ticker;
		return this;
	}

	public OptionContract Underlying_ticker(String underlying_ticker) {
		this.underlying_ticker=underlying_ticker;
		return this;
	}
	public OptionContract Contract_type(String contract_type) {
		this.contract_type=contract_type;
		return this;
	}
	public OptionContract Expiriation_date_lt(String expiriation_date_lt) {
		this.expiriation_date_lt=expiriation_date_lt;
		return this;
	}

	public OptionContract Expiriation_date_gt(String expiriation_date_gt) {
		this.expiriation_date_gt=expiriation_date_gt;
		return this;
	}

	public OptionContract Strike_price(String strike_price) {
		this.strike_price=strike_price;
		return this;
	}
	public OptionContract Expired(String expired) {
		this.expired=expired;
		return this;
	}
	public OptionContract Order(String order) {
		this.order=order;
		return this;
	}

	public OptionContract Limit(String limit) {
		this.limit=limit;
		return this;
	}
	public OptionContract Sort(String sort) {
		this.sort=sort;
		return this;
	}




	public void run() {
		try {
			System.out.println("start to build options for "+this.underlying_ticker );
			this.option_list=build();
			System.out.println("build "+this.option_list.size()+" contract for "+this.underlying_ticker);
			System.out.println("theard "+ (++index)+" is finnish");
		} catch (IOException e) {
			System.out.println("erorr with "+this.underlying_ticker);
			//	reqHistoricalData_.printStackTrace();
		} catch (ParseException e) {
			System.out.println("erorr with "+this.underlying_ticker);
			//	reqHistoricalData_.printStackTrace();
		}


    }


	/**
	 * the function get company ticker like "AAPL" and return all the contracts for this ticker
	 * storage in array,the function use 2 another function "getRequest" and "create"
	 * @return array that contain put/call options
	 * @throws IOException
	 * @throws ParseException
	 */
	private   ArrayList<Option> build() throws IOException, ParseException {

		//company_ticker and underlying_ticker have the same meaning(company simbol)
		//ticker meaning is  the the options number

		String data=getRequset(url+endPoint);
//		System.out.println(data);
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(data);
		JSONObject jsobj=(JSONObject)obj;
		JSONArray array=(JSONArray) jsobj.get("results");

		ArrayList<Option> optionArray=new ArrayList<>();
		//		BearSpredList list=new BearSpredList();
		for(int i=0;i<array.size();i++) {
			jsobj=(JSONObject) array.get(i);
			String ticker=(String) jsobj.get("ticker");

			String url="https://api.polygon.io/v3/snapshot/options/"+ underlying_ticker +"/"+ticker+"?apiKey="+API_kEY;

			try {
				Option option=snapShot(url);
				if(option!=null ) {//if greeks values are existing
					optionArray.add(option);
				}
			}catch (NullPointerException e) {
				continue;
			}

		}
		//		System.out.println("second :"+((new Date()).getTime()-time.getTime())/(1000));
		//		System.out.println("mintue :"+((new Date()).getTime()-time.getTime())/(60*1000));
		return optionArray;
	}


	/**
	 * the function get url that represents the data about specific company
	 * by using "getRequest" function we get the data for create a single option object
	 * @param url
	 * @return single put/call option 
	 * @throws IOException
	 * @throws ParseException
	 */
	public  Option snapShot(String url) throws IOException, ParseException{
		String str[]=url.split("/");
		String u_t=str[6];
		//System.out.println(str[6]);
		String data=getRequset(url);
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(data);
		JSONObject json=(JSONObject)obj;

		JSONObject results= (JSONObject) json.get("results");
		Option option;
		if(((JSONObject)results.get("details")).get("contract_type").equals("call"))	 {
			option=new OptionCall();
		}else {
			option=new OptionPut();
		}

		//init the option

		option.setUnderlying_ticker(u_t);
		option.setUnderlying_price(Double.parseDouble(((JSONObject)results.get("underlying_asset")).get("price")+""));
		option.setTicker(((JSONObject)results.get("details")).get("ticker")+"");
		option.setStrike(Double.parseDouble((((JSONObject)results.get("details")).get("strike_price"))+""));
		option.setAsk(Double.parseDouble( ((JSONObject)results.get("last_quote")).get("ask")+""));
		option.setBid(Double.parseDouble( ((JSONObject)results.get("last_quote")).get("bid")+""));
		option.setExercise_style(((JSONObject)results.get("details")).get("exercise_style")+"");
		option.setExpiration_date((String) ((JSONObject)results.get("details")).get("expiration_date"));

		if(!((JSONObject)results.get("day")).isEmpty()){

			option.setVwap(Double.parseDouble(((JSONObject)results.get("day")).get("vwap")+""));
		}

		try {
			if(option.getExercise_style().equals("european"))
			{
				option.setGreeks(new Greeks(0,0,0,0));
			}
			else
			{
				option.setGreeks(new Greeks(results));
			}
		} catch (java.lang.NullPointerException e) {
			return null;
		}

		return option;
	}



	/**
	 * the function get url PATH and send to the Server HTTP Get Request
	 * than over the information that came back and storage that in a String object
	 * @param url2 the url PATH
	 * @return String  object  that contain the data that requested in order to go over the information after
	 * @throws IOException
	 */
	public   String getRequset(String url2) throws IOException {
		URL url;
		url = new URL(url2);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(500000);
		conn.setReadTimeout(500000);

		//Check if connect is made
		int responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			throw new RuntimeException("HttpResponseCode: " + responseCode);
		}
		Scanner in=new Scanner(new InputStreamReader(conn.getInputStream()));
		String result=in.next();
		in.close();
		conn.disconnect();
		return result;
	}


	public ArrayList<Option> getOption_list() {
		return option_list;
	}


	public static void main(String[] args) throws IOException, ParseException, InterruptedException {

		//		BuildOptions build=new BuildOptions("AAPL");
		//		build.run();
		//		build.getByType("call");
		//		for(int i=0;i<build.option_list.size();i++) {
		//             System.out.println(build.option_list.get(i));
		//		}
		OptionContract contract=new OptionContract("TSLA");
		contract
				.Limit("30").Expiriation_date_gt("2023-03-25").endPoint();
		System.out.println(contract.url);
		contract.run();

		for(int i=0;i<contract.option_list.size();i++) {
			System.out.println(contract.option_list.get(i));
		}

		System.out.println(contract.option_list.size());
		System.out.println(index);
		System.out.println(contract.endPoint);

	}

}

