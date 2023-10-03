package mycode.data;

import mycode.object.AggregatesObject;
import mycode.object.Option;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AggregatesRequest {

    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v2/aggs/ticker/";


    private String endPoint="";

    public ArrayList<Option> option_list=new ArrayList<>();

    public JSONObject jsonFormat=new JSONObject();

    private String optionsTicker ="";
    private String multiplier="1";
    private String timespan ="minute";
    private String from ="";
    private String to ="";
    private String  sort="";
    private String  limit="50000";



    public AggregatesRequest(String optionsTicker){
        this.optionsTicker =optionsTicker;
    }
    public AggregatesRequest Timespan(String timespan){
        this.timespan =timespan;
        return this;
    }
    public AggregatesRequest endPoint() {
        if(optionsTicker !="") {endPoint+=""+ optionsTicker;}
        if(multiplier !="") {endPoint+="/range/"+ multiplier;}
        if(timespan !="") {endPoint+="/"+ timespan+"/";}
        if(from !="") {endPoint+= from+"/";}
        if(to !=""){endPoint+=to+"/";}
        if(limit!="") {endPoint+="?limit="+limit;}

        if(sort!="") {endPoint+="&sort="+sort;}
        endPoint+="&apiKey="+API_kEY;

        return this;
    }

    public AggregatesRequest Multiplier(String multiplier) {
        this.multiplier =multiplier;
        return this;
    }
    public AggregatesRequest From(String from) {
        this.from =from;
        return this;
    }
    public AggregatesRequest To(String to) {
        this.to =to;
        return this;
    }

    public AggregatesRequest Sort(String sort) {
        this.sort =sort;
        return this;
    }

    public AggregatesRequest Limit(String limit) {
        this.limit=limit;
        return this;
    }


    public   ArrayList<AggregatesObject> build() throws IOException, ParseException {
        String data= null;
        JSONArray array=null;
        if(this.limit.equals("50000")){
            array= requestWithNextUrl(url+endPoint);
        }
        else{
            data = getRequest(url+endPoint);
            JSONParser parser=new JSONParser();
            Object obj= null;
            obj = parser.parse(data);
            JSONObject json=(JSONObject)obj;
            array=(JSONArray) (json.get("results"));
        }


        ArrayList<AggregatesObject> aggList=new ArrayList<>();
        System.out.println(array.size());
        for(int i=0;i<array.size();i++){
            try {
                JSONObject index= (JSONObject) array.get(i);
                aggList.add(new AggregatesObject(optionsTicker,Double.parseDouble( index.get("vw")+""), (Double.parseDouble(index.get("c")+""))
                        , Long.parseLong( index.get("t")+""), Double.parseDouble( index.get("v")+"")
                        , Double.parseDouble( index.get("h")+""), Double.parseDouble(index.get("l")+"")
                        , Integer.parseInt(index.get("n")+""), Double.parseDouble( index.get("o")+"")));
                System.out.println(array.get(i));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }
        System.out.println(array.size());
        return aggList;

    }

    private JSONArray requestWithNextUrl(String url) throws IOException, ParseException {
        String new_response= getRequest(url);
        Object obj=new JSONParser().parse(new_response);
        JSONObject response= (JSONObject) obj;
        JSONArray all_response_results=new JSONArray();
        JSONArray results;
        while(response.get("next_url")!=null){//have more data
             results= (JSONArray) response.get("results");
            for(int i=0;i<results.size();i++) {
                all_response_results.add(results.get(i));
            }
            new_response= getRequest(response.get("next_url")+"&apiKey="+API_kEY);
            obj=new JSONParser().parse(new_response);
            response= (JSONObject) obj;

        }
        results= (JSONArray) response.get("results");
        for(int i=0;i<results.size();i++) {
            all_response_results.add(results.get(i));
        }
        System.out.println("size of results "+all_response_results.size());
        return all_response_results;
    }

    public   String getRequest(String url2) throws IOException ,NoSuchElementException{
        System.out.println(url2);
        URL url;
        url = new URL(url2);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);

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

    public static void main(String[] args) throws IOException, ParseException {
        AggregatesRequest aggregates=new AggregatesRequest("AAPL");
        aggregates.From("2022-02-26").To("2023-03-03").Timespan("day").endPoint().build();
        AggregatesObject object=aggregates.build().get(0);
        System.out.println(object.getVwap());


    }


}
