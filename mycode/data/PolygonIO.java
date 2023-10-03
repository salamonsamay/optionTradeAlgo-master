package mycode.data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class PolygonIO {

    public static void main(String[] args) {
        Function<String, String> messageHandler = e -> {
            System.out.println(e);
            return null;
        };
        WebSockets wsClient = new WebSockets("YLMlOppNufEGaHogyZpL2Bh9b3eC2v4U", messageHandler);

//           wsClient.connect("options");
           wsClient.connect("stocks");


//        wsClient.subscribe("Q.O:META230317C00165000");
        wsClient.subscribe("A.*");
        while (true){
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static class Rest {

        private String apiKey;
        private HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        public Rest(String apiKey) {
            this.apiKey = apiKey;
        }

        public HttpResponse<String> get(String endpoint) {
            String symbol = (endpoint.contains("?")) ? "&" : "?";
            endpoint = String.format("https://api.polygon.io%s%sapiKey=%s", endpoint, symbol, this.apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .build();
            HttpResponse<String> response = null;
            try {
                response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch(java.io.IOException | java.lang.InterruptedException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    public static class WebSockets{

        public static Queue<String> DATA=new LinkedList<>();
        private String apiKey;
//        private String endpoint = "wss://socket.polygon.io/";
        private String endpoint = "wss://delayed.polygon.io/";
        private WebSocket webSocket;
        Function<String, String> function;

        public WebSockets(String apiKey, Function<String, String> function) {
            this.apiKey = apiKey;
            this.function = function;
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
            System.out.println(json);
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
                webSocket.sendText(String.format("{\"action\":\"auth\",\"params\":\"%s\"}", this.apiKey), true);
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                this.function.apply(data.toString());
                WebSockets.DATA.add(data.toString());
                WebSockets.this.webSocket = webSocket;
                System.out.println("onText");
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                System.out.println("Error: " + webSocket.toString());
                WebSocket.Listener.super.onError(webSocket, error);
            }
        }
    }
}
