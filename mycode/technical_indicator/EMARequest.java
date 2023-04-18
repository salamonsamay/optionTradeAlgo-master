package mycode.technical_indicator;

import mycode.object.EMAObject;
import mycode.object.SMAObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


public class EMARequest {

    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v1/indicators/ema/";
    private String options_ticker="";
    private String  timestamp_date=""; //defualt value
    private String  timestamp_date_gt="";
    private String  timestamp_date_lt="";
    private String timespan ="minute";//The size of the aggregate time window.

    private String window ="10";

    private String  series_type="";
    private String  limit="500";
    private String endPoint="";

    public EMARequest(String symbol){
        this.options_ticker=symbol;
//        url+=symbol;
//        url+="?apikey="+API_kEY;
    }
    public EMARequest Options_ticker(String options_ticker) {
        this.options_ticker=options_ticker;
        return this;
    }
    public EMARequest Timestamp_date(String timestamp_date) {
        this.timestamp_date=timestamp_date;
        return this;
    }
    public EMARequest Timestamp_date_gt(String timestamp_date_gt) {
        this.timestamp_date_gt=timestamp_date_gt;
        return this;
    }
    public EMARequest Timestamp_date_lt(String timestamp_date_lt) {
        this.timestamp_date_lt=timestamp_date_lt;
        return this;
    }
    public EMARequest Timespan(String timespan) {
        this.timespan =timespan;
        return this;
    }
    public EMARequest Window(String window) {
        this.window =window;
        return this;
    }
    public EMARequest Series_type(String series_type) {
        this.series_type=series_type;
        return this;
    }
    public EMARequest Limit(String limit) {
        this.limit=limit;
        return this;
    }

    public EMARequest endPoint() {
        if(options_ticker!="") {endPoint+=""+options_ticker;}
        if(timespan !="") {endPoint+="?timespan="+ timespan;}
        if(timestamp_date!="") {endPoint+="&timestamp_date="+timestamp_date;}
        if(timestamp_date_gt!="") {endPoint+="&timestamp_date.gt="+timestamp_date_gt;}
        if(timestamp_date_lt!="") {endPoint+="&timestamp_date.lt="+timestamp_date_lt;}
        if(window !="") {endPoint+="&window="+ window;}
        if(series_type!="") {endPoint+="&series_type="+series_type;}
        if(limit!=""){endPoint+="&limit="+limit;}
        endPoint+="&apiKey="+API_kEY;

        return this;
    }

    private JSONArray build() throws IOException, ParseException {
        if(this.limit.equals("5000")){
            return requestWithNextUrl(url+endPoint);
        }
        String data= null;

        data = getRequest(url+endPoint);

        JSONParser parser=new JSONParser();
        Object obj= null;
        obj = parser.parse(data);

        JSONObject json=(JSONObject)obj;
        JSONArray array=(JSONArray) ((JSONObject)(json.get("results"))).get("values");

        return array;
    }

    public   String getRequest(String url2) throws IOException {
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
    private JSONArray requestWithNextUrl(String url) throws IOException, ParseException {
        String result= getRequest(url);
        Object obj=new JSONParser().parse(result);
        JSONObject json= (JSONObject) obj;
        // JSONObject jsonResult= (JSONObject) json.get("results");
        JSONArray jsonArray=new JSONArray();

        if(json.get("next_url")==null){
            jsonArray= (JSONArray) ((JSONObject)json.get("results")).get("values");
            return jsonArray;
        }

        while(json.get("next_url")!=null){

            JSONArray temp= (JSONArray) ((JSONObject)json.get("results")).get("values");
            for(int i=0;i<temp.size();i++) {
                jsonArray.add(temp.get(i));
            }
            result= getRequest(json.get("next_url")+"&apiKey="+API_kEY);
            obj=new JSONParser().parse(result);
            json= (JSONObject) obj;

        }

        return jsonArray;
    }
    public  EMAObject last20EMA(String options_ticker) throws IOException, ParseException {

            return new EMAObject(options_ticker,new EMARequest(options_ticker).Timespan("minute").Window("20").Limit("1").Series_type("close").endPoint().build());


    }
    public  EMAObject last50EMA(String options_ticker) throws IOException, ParseException {

            return new EMAObject(options_ticker,new EMARequest(options_ticker).Timespan("minute").Window("50").Limit("1").Series_type("close").endPoint().build());

    }
    public  EMAObject last200EMA(String options_ticker) throws IOException, ParseException {

            return new EMAObject(options_ticker,new EMARequest(options_ticker).Timespan("minute").Window("200").Limit("1").Series_type("close").endPoint().build());

    }
    public ArrayList<EMAObject> ema_list(){
        try {
            ArrayList<EMAObject> list=new ArrayList<>();
            JSONArray array=build();
            for(int i=0;i<array.size();i++){
                JSONObject json= (JSONObject) array.get(i);
                list.add(new EMAObject(options_ticker,json.get("timestamp")+"",json.get("value")+""));
            }
            return list;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static ArrayList<EMAObject> create_ema_list(){
        EMARequest ema=new EMARequest("O:META230224C00175000");
        ema.Timespan("minute").Window("20").Limit("5000").Series_type("close").endPoint();
        return ema.ema_list();
    }

    public static void main(String[] args) throws IOException, ParseException {
        EMARequest ema=new EMARequest("O:META230303C00175000");
        ema.Timespan("minute").Window("20").Limit("5000").Series_type("close");
        ema.endPoint();
        JSONArray array=ema.build();
        System.out.println(array.size());




    }

}
