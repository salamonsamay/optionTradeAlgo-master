package mycode.data;

import mycode.object.Greeks;
import mycode.object.Option;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class OptionChain extends  Thread{

    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v3/snapshot/options/";

    private String endPoint="";

    public ArrayList<Option> option_list=new ArrayList<>();

    public  JSONObject jsonFormat=new JSONObject();

    private String ticker="";
    private String underlying_ticker="";
    private String contract_type="";
    private String expiriation_date_lt="";
    private String expiriation_date_gt="";

    private String expiriation_date_lte="";

    private String expiriation_date_gte="";
    private String strike_price="";
    private String expired="";
    private String order="";
    private String  limit="250";
    private String  sort="";

    public int failed_counter=0;


    public OptionChain(String symbol){
        this.underlying_ticker=symbol;
        //  url+=symbol;
        //  url+="?apikey="+API_kEY;
    }

    public OptionChain endPoint() {
        if(underlying_ticker!="") {endPoint+=""+underlying_ticker;}
        if(limit!="") {endPoint+="?limit="+limit;}
        if(ticker!="") {endPoint+="&ticker="+ticker;}
        if(contract_type!="") {endPoint+="&contract_type="+contract_type;}
        if(expiriation_date_lt!="") {endPoint+="&expiration_date.lt="+expiriation_date_lt;}
        if(expiriation_date_lte!="") {endPoint+="&expiration_date.lte="+expiriation_date_lte;}
        if(expiriation_date_gt!=""){endPoint+="&expiration_date.gt="+expiriation_date_gt;}
        if(expiriation_date_gte!=""){endPoint+="&expiration_date.gte="+expiriation_date_gte;}
        if(strike_price!="") {endPoint+="&strike_price="+strike_price;}
        if(expired!="") {endPoint+="&expired="+expired;}
        if(order!="") {endPoint+="&order="+order;}
        if(limit!="") {endPoint+="&limit="+limit;}
        if(sort!="") {endPoint+="&sort="+sort;}
        endPoint+="&apiKey="+API_kEY;

        return this;
    }

    public OptionChain Ticker(String ticker) {
        this.ticker=ticker;
        return this;
    }

    public OptionChain Underlying_ticker(String underlying_ticker) {
        this.underlying_ticker=underlying_ticker;
        return this;
    }
    public OptionChain Contract_type(String contract_type) {
        this.contract_type=contract_type;
        return this;
    }
    public OptionChain Expiriation_date_lt(String expiriation_date_lt) {
        this.expiriation_date_lt=expiriation_date_lt;
        return this;
    }
    public OptionChain Expiriation_date_lte(String expiriation_date_lte) {
        this.expiriation_date_lte=expiriation_date_lte;
        return this;
    }

    public OptionChain Expiriation_date_gt(String expiriation_date_gt) {
        this.expiriation_date_gt=expiriation_date_gt;
        return this;
    }
    public OptionChain Expiriation_date_gte(String expiriation_date_gte) {
        this.expiriation_date_gte=expiriation_date_gte;
        return this;
    }

    public OptionChain Strike_price(String strike_price) {
        this.strike_price=strike_price;
        return this;
    }
    public OptionChain Expired(String expired) {
        this.expired=expired;
        this.expiriation_date_gte=expired;
        this.expiriation_date_lte=expired;
        return this;
    }
    public OptionChain Order(String order) {
        this.order=order;
        return this;
    }
    public OptionChain Limit(String limit) {
        this.limit=limit;
        return this;
    }
    public OptionChain Sort(String sort) {
        this.sort=sort;
        return this;
    }

    public  void run (){
        try {
            System.out.println("start to build options for "+this.underlying_ticker );
            build();
            System.out.println("build "+this.option_list.size()+" contract for "+this.underlying_ticker);
        } catch (IOException e) {
            System.out.println("erorr with "+this.underlying_ticker);
            //	reqHistoricalData_.printStackTrace();
        } catch (ParseException e) {
            System.out.println("erorr with "+this.underlying_ticker);
            //	reqHistoricalData_.printStackTrace();
        }
    }

    public   ArrayList<Option> build() throws IOException, ParseException {

        JSONArray array;
        try {
            if(limit.equals("250")){array= requestWithNextUrl(url+endPoint);//to get the  max
            }else {
                Object obj=new JSONParser().parse(getRequest(url+endPoint));
                array= (JSONArray) ((JSONObject) obj).get("results");
            }
            failed_counter=0;
        } catch (NoSuchElementException e){
            System.out.println("exception "+failed_counter+" in build function");
            failed_counter++;
            return this.option_list;
        }

            ArrayList<Option> optionArray = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                Option option = null;
                if (((JSONObject) ((JSONObject) (array.get(i))).get("details")).get("contract_type").equals("call")) {
                    option = new OptionCall();
                } else {
                    option = new OptionPut();
                }

                //init the option
                JSONObject json = (JSONObject) array.get(i);
                JSONObject details = (JSONObject) json.get("details");
                JSONObject last_quote = (JSONObject) json.get("last_quote");
                JSONObject underlying_asset = (JSONObject) json.get("underlying_asset");
                JSONObject day = (JSONObject) json.get("day");

                option.setUnderlying_ticker(underlying_ticker);
                option.setTicker((details.get("ticker").toString()));
                option.setStrike(Double.parseDouble(details.get("strike_price") + ""));
                option.setAsk(Double.parseDouble(last_quote.get("ask") + ""));
                option.setBid(Double.parseDouble((last_quote.get("bid") + "")));
                option.setMid_point(Double.parseDouble((last_quote.get("midpoint") + "")));
                option.setExercise_style((details.get("exercise_style") + ""));
                option.setExpiration_date(details.get("expiration_date") + "");
                option.setLastUpdate(Long.parseLong((last_quote.get("last_updated") + "").substring(0, 13)));
                if (!day.isEmpty()) {
                    option.setVwap(Double.parseDouble(day.get("vwap") + ""));
                    option.setVolume(Double.parseDouble(day.get("volume") + ""));
                } else {
//                System.out.println("have no trade today");
                    continue;
                }

                try {
                    if (option.getExercise_style().equals("european")) {
                        option.setGreeks(new Greeks(0, 0, 0, 0));
                    } else {
                        option.setUnderlying_price(Double.parseDouble(underlying_asset.get("price") + ""));
                        option.setGreeks(new Greeks(json));
                    }
                } catch (NullPointerException e) {
                    continue;
                }
                optionArray.add(option);

            }
            if (this.option_list.size() == 0) {
                this.option_list = optionArray;
            } else {
                //add all the new element in this.option_list
                for (int i = 0; i < this.option_list.size(); i++) {
                    for (int j = 0; j < optionArray.size(); j++) {

                        if (this.option_list.get(i).equals(optionArray.get(j))) {
                            this.option_list.get(i).update(optionArray.get(j));
                            i++;
                            break;
                        }
                    }
                }
            }


            return optionArray;

    }

    private JSONArray requestWithNextUrl(String url) throws IOException, ParseException {
        String result= getRequest(url);
        Object obj=new JSONParser().parse(result);
        JSONObject json= (JSONObject) obj;
        JSONArray jsonArray=new JSONArray();

        if(json.get("next_url")==null){
            jsonArray= (JSONArray) json.get("results");
            return jsonArray;
        }
        while(json.get("next_url")!=null){

            JSONArray temp= (JSONArray) json.get("results");
            for(int i=0;i<temp.size();i++) {
                jsonArray.add(temp.get(i));
            }
            result= getRequest(json.get("next_url")+"&apiKey="+API_kEY);
            obj=new JSONParser().parse(result);
            json= (JSONObject) obj;

        }
        return jsonArray;
    }

    public   String getRequest(String url2) throws IOException ,NoSuchElementException{
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

    public void updateProcess()  {
        new Thread(new Runnable() {

            public void run() {

                while (failed_counter<3){
                    try {
                        build();
                    } catch (IOException e) {
//                        reqHistoricalData_.printStackTrace();
                        continue;
                    } catch (ParseException e) {
//                        reqHistoricalData_.printStackTrace();
                        continue;
                    }
                    try {
                        int r=(int)(Math.random()*5)+1;
                        Thread.sleep(5000*r);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                System.out.println("stop "+underlying_ticker+ " updateProccess loop because have more than " +3+" a error");
            }
        }).start();

    }




    public static void main(String[] args) throws IOException, ParseException, InterruptedException {

    }

}
