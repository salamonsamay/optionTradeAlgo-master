package mycode.object;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public  class SMAObject{

    private String optionTicker;
    private long timestamp;
    private double value;

    public SMAObject(String optionTicker,String timestamp, String value) {
        this.optionTicker=optionTicker;
        this.timestamp = Long.parseLong(timestamp);
        this.value = Double.parseDouble(value);
    }

    public SMAObject(String optionTicker,JSONArray jsonArray) {
        try {
            this.optionTicker=optionTicker;
            this.timestamp = Long.parseLong(((JSONObject)jsonArray.get(0)).get("timestamp")+"");
            this.value = Double.parseDouble(((JSONObject)jsonArray.get(0)).get("value")+"");
        }catch (NullPointerException e){
            this.timestamp=0;
            this.value=0;
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

    public String getOptionTicker() {
        return optionTicker;
    }

    public void setOptionTicker(String optionTicker) {
        this.optionTicker = optionTicker;
    }

    @Override
    public String toString() {
        return "SMAObject{" +
                "optionTicker='" + optionTicker + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}