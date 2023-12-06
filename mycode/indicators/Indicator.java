package mycode.indicators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import mycode.object.AggregatesObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;


import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.*;

public class Indicator  {
    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v1/indicators/";
    private  String type="";
    private String endPoint="";
    private String stockTicker="";
    private String timespan="";
    private String timestamp="";
    private String timestamp_lt ="";
    private String timestamp_lte ="";
    private String timestamp_gt ="";
    private String timestamp_gte ="";
    private String adjusted="true";
    private String window="";
    private String series_type="";
    private String expand_underlying="";
    private String order="";
    private String limit="5000";//max is 5000
    public ArrayNode indicatorList =new ObjectMapper().createArrayNode();
    public ArrayNode aggregatesList=new ObjectMapper().createArrayNode();






    public Indicator(String symbol,String type){
        this.stockTicker=symbol;
        this.type=type;
        url+=type+"/";
    }
    public Indicator EndPoint() {
        if (!stockTicker.isEmpty()) {
            endPoint += stockTicker;
        }
        if (!limit.isEmpty()) {
            endPoint += "?limit=" + limit;
        }
        if (!timespan.isEmpty()) {
            endPoint += "&timespan=" + timespan;
        }
        if(!timestamp.isEmpty()){
            endPoint+="&timestamp=" + timestamp;
        }
        if (!timestamp_lt.isEmpty()) {
            endPoint += "&timestamp.lt=" + timestamp_lt;
        }
        if(!expand_underlying.isEmpty()){
            endPoint+="&expand_underlying="+expand_underlying;
        }
        if (!timestamp_lte.isEmpty()) {
            endPoint += "&timestamp.lte=" + timestamp_lte;
        }
        if (!timestamp_gt.isEmpty()) {
            endPoint += "&timestamp.gt=" + timestamp_gt;
        }
        if (!timestamp_gte.isEmpty()) {
            endPoint += "&timestamp.gte=" + timestamp_gte;
        }
        if (!adjusted.isEmpty()) {
            endPoint += "&Adjusted=" + adjusted;
        }
        if (!window.isEmpty()) {
            endPoint += "&Window=" + window;
        }
        if (!series_type.isEmpty()) {
            endPoint += "&series_type=" + series_type;
        }
        if (!order.isEmpty()) {
            endPoint += "&order=" + order;
        }
        endPoint += "&apiKey=" + API_kEY;

        return this;
    }

    public Indicator StockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
        return this;
    }

    public Indicator Timespan(String timespan){
        this.timespan=timespan;
        return this;
    }

    public Indicator Timestamp(String timestamp){
        this.timestamp = timestamp;
        return this;
    }

    public Indicator Timestamp_lt(String timespan_lt) {
        this.timestamp_lt = timespan_lt;
        return this;
    }

    public Indicator Timestamp_lte(String timestamp_lte) {
        this.timestamp_lte = timestamp_lte;
        return this;
    }

    public Indicator Timestamp_gt(String timespan_gt) {
        this.timestamp_gt = timespan_gt;
        return this;
    }

    public Indicator Timestamp_gte(String timespan_gte) {
        this.timestamp_gte = timespan_gte;
        return this;
    }

    public Indicator Adjusted(boolean adjusted) {
        this.adjusted = Boolean.toString(adjusted);
        return this;
    }

    public Indicator Window(String window) {
        this.window = window;
        return this;
    }

    public Indicator SeriesType(String series_type) {
        this.series_type = series_type;
        return this;
    }

    public Indicator ExpandUnderlying(boolean expand_underlying) {
        this.expand_underlying = Boolean.toString(expand_underlying);
        return this;
    }

    public Indicator Order(String order) {
        this.order = order;
        return this;
    }

    public Indicator Limit(int limit) {
        this.limit = Integer.toString(limit);
        return this;
    }



    public Indicator fetchData() throws IOException {
        String url = Indicator.url+ endPoint;
        return fetchData(url);
    }

    private Indicator fetchData(String url) throws IOException {

        while (url != null) {
            String result = getRequest(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(result);

            if (json.has("results")) {
                JsonNode resultsNode = json.get("results");


                for (JsonNode valueNode : resultsNode.get("underlying").get("aggregates")) {
                    aggregatesList.add(valueNode);
                }
                for(JsonNode valueNode : resultsNode.get("values")){
                    indicatorList.add(valueNode);
                }

            }
            url = json.has("next_url") ? json.get("next_url").asText() : null;
        }

        return this;
    }



    private String getRequest(String url) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + "&apiKey=" + API_kEY);
        System.out.println(url + "&apiKey=" + API_kEY);

        HttpResponse response = httpClient.execute(httpGet);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            if(statusCode==429){
                try {
                    System.out.println("sleep 1 minute");
                    Thread.sleep(1000*60);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else throw new RuntimeException("HttpResponseCode: " + statusCode);
        }

        try (Scanner scanner = new Scanner(new InputStreamReader(response.getEntity().getContent()))) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private void sortAggregatesByTimestamp() {
        int n = aggregatesList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                JsonNode now = aggregatesList.get(i);
                JsonNode next = aggregatesList.get(j);

                // Compare timestamps and swap if necessary
                if (now.get("t").asLong() <= next.get("t").asLong()) {
                    // Swap elements if they are in the wrong order
                    aggregatesList.set(i, next);
                    aggregatesList.set(j, now);

                    // Update references to now and next after the swap
                    now = aggregatesList.get(i);
                    next = aggregatesList.get(j);
                }
            }
        }
    }
    private void sortIndicatorByTimestamp() {
        int n = indicatorList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                JsonNode now = indicatorList.get(i);
                JsonNode next = indicatorList.get(j);

                // Compare timestamps and swap if necessary
                if (now.get("timestamp").asLong() <= next.get("timestamp").asLong()) {
                    // Swap elements if they are in the wrong order
                    indicatorList.set(i, next);
                    indicatorList.set(j, now);

                    // Update references to now and next after the swap
                    now = indicatorList.get(i);
                    next = indicatorList.get(j);
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // Example usage
        Indicator indicator = new Indicator("TSLA","sma");
        indicator.Timestamp_gte("2010-08-01")
                .Timespan("minute")
                .Window("50")
                .ExpandUnderlying(true)
                .EndPoint().fetchData();

        System.out.println(indicator.indicatorList.size());
        System.out.println(indicator.aggregatesList.size());

        indicator.sortIndicatorByTimestamp();
        indicator.sortAggregatesByTimestamp();
        int n=Math.min(indicator.indicatorList.size(),indicator.aggregatesList.size());
       for(int i=0;i<n-1;i++) {
           JsonNode agg= indicator.aggregatesList.get(i);
           JsonNode indi= indicator.indicatorList.get(i);
           System.out.println(i);
           System.out.println("______________________");
           System.out.println(agg.get("t").asLong());
           System.out.println(indi.get("timestamp").asLong());
           System.out.println("_________________");
           if(agg.get("t").asLong()!=indi.get("timestamp").asLong()){
               throw new RuntimeException();
           }

       }




    }




}
