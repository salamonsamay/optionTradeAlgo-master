package mycode.object;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public  class RSIObject{

    private String optionTicker;
    private long timestamp;
    private double value;

    public RSIObject(String optionTicker,String timestamp, String value) {
        this.optionTicker=optionTicker;
        this.timestamp = Long.parseLong(timestamp);
        this.value = Double.parseDouble(value);
    }

    public RSIObject(String optionTicker,JSONArray jsonArray) {
        try {
            this.optionTicker=optionTicker;
            this.timestamp = Long.parseLong(((JSONObject)jsonArray.get(0)).get("timestamp")+"");
            this.value = Double.parseDouble(((JSONObject)jsonArray.get(0)).get("value")+"");
        }catch (NullPointerException e){
            this.timestamp=0;
            this.value=0;
        }
    }


    public String getOptionTicker() {
        return optionTicker;
    }

    public void setOptionTicker(String optionTicker) {
        this.optionTicker = optionTicker;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(double value) {
        this.value = value;
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


    public String toString() {
        return "RSIObject{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}