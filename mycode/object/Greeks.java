package mycode.object;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;

public class Greeks {
	private double delta;
	private double gamma;
	private double theta;
	private double vega;



	public Greeks(){
		this.delta = 0;
		this.gamma = 0;
		this.vega = 0;
		this.theta = 0;
	}
	public Greeks(double delta, double gama, double theta,double vega) {

		this.delta = delta;
		this.gamma = gama;
		this.vega = vega;
		this.theta = theta;
	}

	public Greeks(String delta, String gama,String theta, String vega) {
		this.delta = Double.parseDouble(delta);
		this.gamma = Double.parseDouble(gama);
		this.theta = Double.parseDouble(theta);
		this.vega=Double.parseDouble(vega);
	}

	public Greeks(Greeks other) {
		super();
		if(other!=null){
			this.delta = other.delta;
			this.gamma = other.gamma;
			this.vega = other.vega;
			this.theta = other.theta;
		}

	}

	public  Greeks(JsonNode json) throws NullPointerException{
		JsonNode greeks= json.get("greeks");
		if(greeks.isEmpty()){
			throw new NullPointerException();
		}

		String delta=(greeks.get("delta").asText());
		String gamma= (greeks.get("gamma").asText());
		String theta= (greeks.get("theta").asText());
		String vega= (greeks.get("vega").asText());

		if(Double.parseDouble(delta)<0) {//if it put option
			setDelta(Double.parseDouble(delta)*-1);
			setGamma(Double.parseDouble(gamma));
			setTheta(Double.parseDouble(theta));
			setVega(Double.parseDouble(vega));

		}
		else {//if it call option
			setDelta(Double.parseDouble(delta));
			setGamma(Double.parseDouble(gamma));
			setTheta(Double.parseDouble(theta));
			setVega(Double.parseDouble(vega));
		}


	};
	public double getDelta() {
		return delta;
	}

	public void setDelta(double dleta) {
		this.delta = dleta;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gama) {
		this.gamma = gama;
	}

	public double getVega() {
		return vega;
	}

	public void setVega(double vega) {
		this.vega = vega;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public boolean equals(Greeks other) {
		if(getDelta()==other.getDelta() && getGamma()==other.getGamma() &&
				getVega()==other.getVega() && getTheta()==other.getTheta()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Greeks [dleta=" + delta + ", gama=" + gamma + ", vega=" + vega + ", theta=" + theta + "]";
	}







}
