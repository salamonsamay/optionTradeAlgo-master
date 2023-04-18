package mycode.object;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MACDObject {
    private String optionTicker;
    private long timestamp;
    private double value;
    private double  signal;
    private double histogram;

    public MACDObject(String timestamp, String value, String signal, String histogram) {
        this.timestamp = Long.parseLong(timestamp);
        this.value = Double.parseDouble(value);
        this.signal = Double.parseDouble(signal);
        this.histogram = Double.parseDouble(histogram);
    }

    public MACDObject(String optionTicker,JSONArray jsonArray){

        try {
            this.optionTicker=optionTicker;
            this.timestamp = Long.parseLong(((JSONObject)jsonArray.get(0)).get("timestamp")+"");
            this.value = Double.parseDouble(((JSONObject)jsonArray.get(0)).get("value")+"");
            this.signal= Double.parseDouble(((JSONObject)jsonArray.get(0)).get("signal")+"");
            this.histogram= Double.parseDouble(((JSONObject)jsonArray.get(0)).get("histogram")+"");

        }catch (NullPointerException e){
            this.timestamp=0;
            this.value=0;
            this.signal=0;
            this.histogram=0;
        }

    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = Long.parseLong(timestamp);
    }

    public double getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Double.parseDouble(value);
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = Double.parseDouble(signal);
    }

    public double getHistogram() {
        return histogram;
    }

    public void setHistogram(String histogram) {
        this.histogram = Double.parseDouble(histogram);
    }

    public String getOptionTicker() {
        return optionTicker;
    }

    public void setOptionTicker(String optionTicker) {
        this.optionTicker = optionTicker;
    }

    @Override
    public String toString() {
        return "MACDObject{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                ", signal=" + signal +
                ", histogram=" + histogram +
                '}';
    }
}
