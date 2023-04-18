package mycode.object;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public  class EMAObject{

    private String optionsTicker;
    private long timestamp;
    private double value;

    public EMAObject(String optionsTicker,String timestamp, String value) {
        this.optionsTicker=optionsTicker;
        this.timestamp = Long.parseLong(timestamp);
        this.value = Double.parseDouble(value);
    }
    public EMAObject(String optionsTicker,JSONArray jsonArray) {
        try {
            this.optionsTicker=optionsTicker;
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

    public String getOptionsTicker() {
        return optionsTicker;
    }

    public void setOptionsTicker(String optionsTicker) {
        this.optionsTicker = optionsTicker;
    }

    @Override
    public String toString() {
        return "EMAObject{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}