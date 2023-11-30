package mycode.indicators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class SMA {
    private static final String API_kEY="Yb44MaLyneZsziOqLRcrwPjtlgpfXaFG";
    private static String url="https://api.polygon.io/v1/indicators/sma/";
    private String endPoint="";
    private String stockTicker="";

    private String timespan="";
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


    public SMA(String symbol){
        this.stockTicker=symbol;

    }

    public SMA EndPoint() {
        if (!stockTicker.isEmpty()) {
            endPoint += stockTicker;
        }
        if (!limit.isEmpty()) {
            endPoint += "?limit=" + limit;
        }
        if (!timespan.isEmpty()) {
            endPoint += "&timespan=" + timespan;
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

    public SMA stockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
        return this;
    }

    public SMA Timespan(String timespan){
        this.timespan=timespan;
        return this;
    }

    public SMA Timestamp_lt(String timespan_lt) {
        this.timestamp_lt = timespan_lt;
        return this;
    }

    public SMA Timestamp_lte(String timestamp_lte) {
        this.timestamp_lte = timestamp_lte;
        return this;
    }

    public SMA Timestamp_gt(String timespan_gt) {
        this.timestamp_gt = timespan_gt;
        return this;
    }

    public SMA Timestamp_gte(String timespan_gte) {
        this.timestamp_gte = timespan_gte;
        return this;
    }

    public SMA Adjusted(boolean adjusted) {
        this.adjusted = Boolean.toString(adjusted);
        return this;
    }

    public SMA Window(String window) {
        this.window = window;
        return this;
    }

    public SMA SeriesType(String series_type) {
        this.series_type = series_type;
        return this;
    }

    public SMA ExpandUnderlying(boolean expand_underlying) {
        this.expand_underlying = Boolean.toString(expand_underlying);
        return this;
    }

    public SMA order(String order) {
        this.order = order;
        return this;
    }

    public SMA limit(int limit) {
        this.limit = Integer.toString(limit);
        return this;
    }



    public List<Double> fetchData() throws IOException {
        String url = SMA.url+ endPoint;
        return fetchData(url);
    }

    private List<Double> fetchData(String url) throws IOException {
        List<Double> values = new ArrayList<>();

        while (url != null) {
            String result = getRequest(url);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(result);

            if (json.has("results")) {
                JsonNode resultsNode = json.get("results");
                for (JsonNode valueNode : resultsNode.get("underlying").get("aggregates")) {s
                    values.add(valueNode.asDouble());
                    System.out.println(valueNode);
                }
            }

            url = json.has("next_url") ? json.get("next_url").asText() : null;
        }

        return values;
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

    public static void main(String[] args) throws IOException {
        // Example usage
        SMA sma = new SMA("AAPL");
        sma.Timestamp_gte("2023-11-01")
                .Timespan("minute")
                .ExpandUnderlying(true)
                .Window("3").EndPoint().fetchData();


    }



}
