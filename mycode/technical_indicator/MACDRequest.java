package mycode.technical_indicator;

import mycode.object.EMAObject;
import mycode.object.MACDObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

public class MACDRequest {


    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v1/indicators/macd/";
    private String options_ticker="";
    private String  timestamp_date=""; //defualt value
    private String  timestamp_date_gt="";
    private String  timestamp_date_lt="";
    private String timespan ="day";//The size of the aggregate time short_window.
    private String short_window ="12";
    private String long_window ="26";
    private String signal_window ="11";
    private String  series_type="close";
    private String  limit="250";
    private String endPoint="";

    public MACDRequest(String symbol){
        this.options_ticker=symbol;
//        url+=symbol;
//        url+="?apikey="+API_kEY;
    }
    public MACDRequest Options_ticker(String options_ticker) {
        this.options_ticker=options_ticker;
        return this;
    }
    public MACDRequest Timestamp_date(String timestamp_date) {
        this.timestamp_date=timestamp_date;
        return this;
    }
    public MACDRequest Timestamp_date_gt(String timestamp_date_gt) {
        this.timestamp_date_gt=timestamp_date_gt;
        return this;
    }
    public MACDRequest Timestamp_date_lt(String timestamp_date_lt) {
        this.timestamp_date_lt=timestamp_date_lt;
        return this;
    }
    public MACDRequest Timespan(String timespan) {
        this.timespan =timespan;
        return this;
    }

    public MACDRequest Short_window(String long_window) {
        this.short_window =short_window;
        return this;
    }

    public MACDRequest Long_window(String long_window) {
        this.long_window =long_window;
        return this;
    }
    public MACDRequest Signal_window(String signal_window){
        this.signal_window =signal_window;
        return this;
    }

    public MACDRequest Series_type(String series_type) {
        this.series_type=series_type;
        return this;
    }
    public MACDRequest Limit(String limit) {
        this.limit=limit;
        return this;
    }

    public MACDRequest endPoint() {
        if(options_ticker!="") {endPoint+=""+options_ticker;}
        if(timespan !="") {endPoint+="?timespan="+ timespan;}
        if(timestamp_date!="") {endPoint+="&timestamp_date="+timestamp_date;}
        if(timestamp_date_gt!="") {endPoint+="&timestamp_date.gt="+timestamp_date_gt;}
        if(timestamp_date_lt!="") {endPoint+="&timestamp_date.lt="+timestamp_date_lt;}
        if(short_window !="") {endPoint+="&short_window="+ short_window;}
        if(long_window !="") {endPoint+="&long_window="+ long_window;}
        if(signal_window !="") {endPoint+="&signal_window="+ signal_window;}
        if(series_type!="") {endPoint+="&series_type="+series_type;}
        if(limit!=""){endPoint+="&limit="+limit;}
      //  endPoint+="&expand_underlying=true";
        endPoint+="&apiKey="+API_kEY;

        return this;
    }

    private JSONArray build() throws IOException, ParseException {

        String data= null;
        if(this.limit.equals("5000")){
           return requestWithNextUrl(url+endPoint);
        }
        data = getRequest(url+endPoint);
        JSONParser parser=new JSONParser();
        Object obj= null;
        obj = parser.parse(data);

        JSONObject json=(JSONObject)obj;
        JSONArray array=(JSONArray) ((JSONObject)(json.get("results"))).get("values");
        return array;
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

    public MACDObject last(String options_ticker ) throws IOException, ParseException {

        return new MACDObject(options_ticker,new MACDRequest(options_ticker).Timespan("day").Limit("1").Series_type("close").endPoint().build());

    }


    public static void main(String[] args) throws IOException, ParseException {
        MACDRequest macd=new MACDRequest("O:SPY230303P00400000").Limit("5000");
        macd.endPoint();
        JSONArray array=macd.build();
        double max=0;
        int index=0;
        for(int i=0;i<array.size();i++){

            if(max<Double.parseDouble(((JSONObject)array.get(i)).get("value")+"")){
                max=Double.parseDouble(((JSONObject)array.get(i)).get("value")+"");
                index=i;
            }
            System.out.println(((JSONObject)array.get(i)).get("histogram"));
        }
        System.out.println(max);
        System.out.println(array.get(index));
        System.out.println(new Date((Long) ((JSONObject)array.get(index)).get("timestamp")));

    }


}
