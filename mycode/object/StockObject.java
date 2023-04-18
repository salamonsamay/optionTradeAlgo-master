package mycode.object;

public class StockObject{

    private String optionsTicker;
    private double vwap;
    private double close_price;
    private long timestamp;
    private double volume;
    private double highest_price;
    private double  lowest_price;
    private int number_of_transactions;
    private double open_price;

    public StockObject(String ticker,long timestamp,double close_price){
        setOptionsTicker(ticker);
        setTimestamp(timestamp);
        setClose_price(close_price);
    }
    public StockObject(String optionsTicker,double vwap, double close_price, long timestamp, double volume, double highest_price, double lowest_price, int number_of_transactions, double open_price) {
        this.optionsTicker=optionsTicker;
        this.vwap = vwap;
        this.close_price = close_price;
        this.timestamp = timestamp;
        this.volume = volume;
        this.highest_price = highest_price;
        this.lowest_price = lowest_price;
        this.number_of_transactions = number_of_transactions;
        this.open_price = open_price;
    }

    public String getOptionsTicker() {
        return optionsTicker;
    }

    public void setOptionsTicker(String optionsTicker) {
        this.optionsTicker = optionsTicker;
    }

    public double getVwap() {
        return vwap;
    }

    public void setVwap(double vwap) {
        this.vwap = vwap;
    }

    public double getClose_price() {
        return close_price;
    }

    public void setClose_price(double close_price) {
        this.close_price = close_price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getHighest_price() {
        return highest_price;
    }

    public void setHighest_price(double highest_price) {
        this.highest_price = highest_price;
    }

    public double getLowest_price() {
        return lowest_price;
    }

    public void setLowest_price(double lowest_price) {
        this.lowest_price = lowest_price;
    }

    public int getNumber_of_transactions() {
        return number_of_transactions;
    }

    public void setNumber_of_transactions(int number_of_transactions) {
        this.number_of_transactions = number_of_transactions;
    }

    public double getOpen_price() {
        return open_price;
    }

    public void setOpen_price(double open_price) {
        this.open_price = open_price;
    }

    @Override
    public String toString() {
        return "StockObject{" +
                "optionsTicker='" + optionsTicker + '\'' +
                ", vwap=" + vwap +
                ", close_price=" + close_price +
                ", timestamp=" + timestamp +
                ", volume=" + volume +
                ", highest_price=" + highest_price +
                ", lowest_price=" + lowest_price +
                ", number_of_transactions=" + number_of_transactions +
                ", open_price=" + open_price +
                '}';
    }
}
