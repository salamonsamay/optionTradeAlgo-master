package mycode.data;

import mycode.help.LinearEquation;
import mycode.help.LinearRegression;
import mycode.help.Tools;
import mycode.object.Option;
import mycode.object.StockObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class StockRequest {


    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v2/aggs/ticker/";

    private String endPoint="";

    public ArrayList<Option> option_list=new ArrayList<>();

    private String stockTicker ="";
    private String multiplier="1";
    private String timespan ="minute";
    private String from ="";
    private String to ="";
    private String  sort="";
    private String  limit="50000";



    public StockRequest(String stockTicker){
        this.stockTicker =stockTicker;
    }
    public StockRequest Timespan(String timespan){
        this.timespan =timespan;
        return this;
    }
    public StockRequest endPoint() {
        if(stockTicker !="") {endPoint+=""+ stockTicker;}
        if(multiplier !="") {endPoint+="/range/"+ multiplier;}
        if(timespan !="") {endPoint+="/"+ timespan+"/";}
        if(from !="") {endPoint+= from+"/";}
        if(to !=""){endPoint+=to+"/";}
        if(limit!="") {endPoint+="?limit="+limit;}

        if(sort!="") {endPoint+="&sort="+sort;}
        endPoint+="&apiKey="+API_kEY;

        return this;
    }

    public StockRequest Multiplier(String multiplier) {
        this.multiplier =multiplier;
        return this;
    }
    public StockRequest From(String from) {
        this.from =from;
        return this;
    }
    public StockRequest To(String to) {
        this.to =to;
        return this;
    }

    public StockRequest Sort(String sort) {
        this.sort =sort;
        return this;
    }

    public StockRequest Limit(String limit) {
        this.limit=limit;
        return this;
    }


    public   ArrayList<StockObject> build() throws IOException, ParseException {
        JSONArray array=null;
        if(limit.equals("50000")){
            array= requestWithNextUrl(url+endPoint);
        }
        else {
            String   data = getRequest(url+endPoint);
            JSONParser parser=new JSONParser();
            Object obj= null;
            obj = parser.parse(data);
            JSONObject json=(JSONObject)obj;
            array=(JSONArray) (json.get("results"));
        }


        ArrayList<StockObject> stockList=new ArrayList<>();

        for(int i=0;i<array.size();i++){
            try {
                JSONObject index= (JSONObject) array.get(i);
                StockObject stockObject=new StockObject(stockTicker,Double.parseDouble( index.get("vw")+""), (Double.parseDouble(index.get("c")+""))
                        , Long.parseLong( index.get("t")+""), Double.parseDouble( index.get("v")+"")
                        , Double.parseDouble( index.get("h")+""), Double.parseDouble(index.get("l")+"")
                        , Integer.parseInt(index.get("n")+""), Double.parseDouble( index.get("o")+""));

                stockList.add(stockObject);
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        return stockList;

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
            System.out.println(url);
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

    public   String getRequest(String url2) throws IOException , NoSuchElementException {
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


    public static void main(String[] args) throws IOException, ParseException {
//        StockRequest stock_request=new StockRequest("SPY");
//        stock_request.From("2023-02-01").To("2023-04-10").endPoint();
//        ArrayList<StockObject> stockObject=stock_request.build();
//        LinearEquation linearEquation=new LinearEquation();
//        linearEquation.buildLinearRegression(stockObject);
//
//        System.out.println(linearEquation.predict(new Date().getTime()+1000*60*60*24*10));

       Date d=new Date();
        ArrayList<String> arr=Tools.readCompanyFromFile();
        ArrayList<LinearEquation> linearList=new ArrayList<>();

        File file=new File("read_file/output/linear.txt");
        PrintWriter  pw=new PrintWriter(file);

        for(String a:arr){
            System.out.println(a);
            StockRequest stock_request=new StockRequest(a);
            stock_request.From("2023-02-01").To("2023-04-10").endPoint();
            ArrayList<StockObject> stockList=stock_request.build();
            LinearEquation linearEquation=new LinearEquation();
            linearEquation.buildLinearRegression(stockList);
            linearList.add(linearEquation);
            pw.println(linearEquation.getSymbol()+","+linearEquation.getSlope()+","+linearEquation.getYIntercept());
        }
        pw.close();

        for(LinearEquation linear:linearList){
            System.out.println(linear.getSymbol()+"  "+linear.predict(new Date().getTime()));
        }
    }


}
