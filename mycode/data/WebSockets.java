package mycode.data;

import mycode.object.Option;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public  class WebSockets{
    private String apiKey;
    private String endpoint = "wss://socket.polygon.io/";
    private WebSocket webSocket;
    Function<String, String> function;
    static  Hashtable<String,Option> hashtable=new Hashtable<>();

    public WebSockets(String apiKey, Function<String, String> function,ArrayList<Option> optionList) {
        this.apiKey = apiKey;
        this.function = function;
        connect("options");
        if(optionList!=null){
            for(Option opt:optionList){
                hashtable.put(opt.getTicker(),opt);
                subscribe("Q."+opt.getTicker());
            }
        }
    }

    public void connect(String cluster) {

        this.endpoint = this.endpoint + cluster;
        this.webSocket = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(this.endpoint), new WebSocketClient(this.apiKey, this.function))
                .join();


    }

    public void close() {
        this.webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "");
    }

    public void subscribe(String ... params) {
        this.sendAction("subscribe", params);
    }

    public void unsubscribe(String ... params) {
        this.sendAction("unsubscribe", params);
    }

    private void sendAction(String action, String[] params) {
        StringBuilder parsedParams = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            parsedParams.append(params[i]);
            if (params.length > 1 && i != params.length - 1) parsedParams.append(",");
        }
        String json = String.format("{\"action\":\"%s\",\"params\":\"%s\"}", action, parsedParams.toString());
        this.webSocket.sendText(json, true);
    }

    private class WebSocketClient implements WebSocket.Listener {
        private String apiKey;
        private Function<String, String> function;

        public WebSocketClient(String apiKey, Function<String, String> function) {
            this.apiKey = apiKey;
            this.function = function;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("onOpen");
            webSocket.sendText(String.format("{\"action\":\"auth\",\"params\":\"%s\"}", this.apiKey), true);

            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

            this.function.apply(data.toString());

            WebSockets.this.webSocket = webSocket;
            JSONParser parser=new JSONParser();
            try {
                Object obj = parser.parse(data.toString());
                JSONArray jsonArray= (JSONArray) obj;
                for(int i=0;i<jsonArray.size();i++){

                    if(((JSONObject) jsonArray.get(i)).get("ev").equals("status")){continue;}
                    //  System.out.println(jsonArray.get(i));
                    double bidPrice=Double.parseDouble(((JSONObject) jsonArray.get(i)).get("bp").toString());
                    double askPrice=Double.parseDouble(((JSONObject) jsonArray.get(i)).get("ap").toString());
                    String sym=((JSONObject) jsonArray.get(i)).get("sym").toString();
                    hashtable.get(sym).setBid(bidPrice);
                    hashtable.get(sym).setAsk(askPrice);
                    System.out.println((jsonArray.get(i)));
                }
                System.out.println();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
//
//            JSONArray jsonArray= (JSONArray) data;
//            String symbol,bidPrice,askPrice;
//            for (int i = 0; i < jsonArray.size(); i++) {
//                symbol = ((JSONObject) jsonArray.get(i)).get("sym").toString();
//                bidPrice = ((JSONObject) jsonArray.get(i)).get("bp").toString();
//                askPrice= ((JSONObject) jsonArray.get(i)).get("ap").toString();
//                hashtable.get(symbol).setBid(Double.parseDouble(bidPrice));
//                hashtable.get(symbol).setAsk(Double.parseDouble(askPrice));
//
//            }

            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("onError");
            System.out.println("Error: " + webSocket.toString());
            WebSocket.Listener.super.onError(webSocket, error);
        }
    }



    public  static  void run(ArrayList<Option> list){
        Thread t=new Thread(new Runnable() {

            public  void run() {
                Function<String, String> messageHandler = e -> {

                    return null;
                };
                WebSockets wsClient = new WebSockets("ijXpwSusNPap5U2vjqWLiGgPaR2CkgDe", messageHandler,list);


                while (true){}
            }
        });
        t.start();

    }







    public static void main(String[] args) throws Exception {

        // File f=new File("C:\\Users\\salam\\OneDrive\\שולחן העבודה\\data\\output.txt");
//        String PATH="C:\\Users\\salam\\OneDrive\\שולחן העבודה\\data\\output.txt";
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse(new FileReader(PATH));
//        JSONArray jsonArray= (JSONArray) obj;
//        Hashtable<String,Option> hashtable=new Hashtable<>();
//        String symbol,bidPrice,askPrice;
//        for (int i = 0; i < jsonArray.size(); i++) {
//            symbol = ((JSONObject) jsonArray.get(i)).get("sym").toString();
//            bidPrice = ((JSONObject) jsonArray.get(i)).get("bp").toString();
//            askPrice= ((JSONObject) jsonArray.get(i)).get("ap").toString();
//
//            hashtable.get(symbol).setBid(Double.parseDouble(bidPrice));
//            hashtable.get(symbol).setAsk(Double.parseDouble(askPrice));
//
//            //  System.out.println(  jsonArray.get(i));
//        }

        Function<String, String> messageHandler = e -> {
            System.out.println(e);
            return null;
        };
        WebSockets wsClient = new WebSockets("YLMlOppNufEGaHogyZpL2Bh9b3eC2v4U", messageHandler,null);

     //   wsClient.connect("options");


        wsClient.subscribe("Q.O:META230317C00165000");


        while (true){}



    }
}