package mycode.data;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import mycode.help.Tools;
import mycode.object.Greeks;
import mycode.object.Option;
import mycode.object.OptionCall;
import mycode.object.OptionPut;
import mycode.strategy_.Strategy;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
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
            System.out.println("IOException erorr with "+this.underlying_ticker);
            //	reqHistoricalData_.printStackTrace();
        } catch (ParseException e) {
            System.out.println("ParseException erorr with "+this.underlying_ticker);
            //	reqHistoricalData_.printStackTrace();
        }
    }

    public ArrayList<Option> build() throws IOException, ParseException {
        JSONArray array;

        if (limit.equals("250")) {
            array = requestWithNextUrl(url + endPoint); // Get the max
        } else {
            Object obj = new JSONParser().parse(getRequest(url + endPoint));
            array = (JSONArray) ((JSONObject) obj).get("results");
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<Option> optionArray = new ArrayList<>();

        for (Object item : array) {
            JSONObject json = (JSONObject) item;
            String contractType = null;
            Option option;
            try{
                contractType = (String) ((JSONObject) json.get("details")).get("contract_type");
                option = contractType.equals("call") ? new OptionCall() : new OptionPut();
                option.update(json);
                optionArray.add(option);
            }catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

        if (this.option_list.isEmpty()) {
            this.option_list = optionArray;
        } else {
            // Update existing elements in this.option_list with new data
            for (Option existingOption : this.option_list) {
                for (Option newOption : optionArray) {
                    if (existingOption.equals(newOption)) {
                        existingOption.update(newOption);
                    }
                }
            }
        }

        return optionArray;
    }



    public JSONArray requestWithNextUrl(String url) throws IOException, ParseException {
        JSONArray jsonArray = new JSONArray();

        while (url != null) {
            String result = getRequest(url);
            JSONObject json = (JSONObject) new JSONParser().parse(result);

            if (json.containsKey("results")) {
                jsonArray.addAll((JSONArray) json.get("results"));
            }

            url = (String) json.get("next_url");
        }

        return jsonArray;
    }

    public String getRequest(String url) throws IOException,RuntimeException{
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + "&apiKey=" + API_kEY);

        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + statusCode);
        }

        try (Scanner scanner = new Scanner(new InputStreamReader(response.getEntity().getContent()))) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    public void updateProcess()  {
        new Thread(new Runnable() {
            public void run() {

                while (failed_counter<3){
                    try {
                        build();
                        failed_counter=0;
                    } catch (IOException | ParseException e) {
                        failed_counter++;
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

    public static void main(String[] args){

    }

}
