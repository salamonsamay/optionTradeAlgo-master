package mycode.trade;


public class BlackScholes {

    public static double blackScholesModel(double stock,double strike,double risk_free_rate,
                                         double implied_volatility,double time_to_expiration){
        double d1 = (Math.log(stock / strike) + (risk_free_rate + 0.5 * implied_volatility * implied_volatility) * time_to_expiration) / (implied_volatility * Math.sqrt(time_to_expiration));
        double d2 = d1 - implied_volatility * Math.sqrt(time_to_expiration);

        // Calculate option value
        double callValue = stock * cdf(d1) - strike * Math.exp(-risk_free_rate * time_to_expiration) * cdf(d2);
        double putValue = strike * Math.exp(-risk_free_rate * time_to_expiration) * cdf(-d2) - stock * cdf(-d1);

        // Print results
        System.out.println("Call option value: " + callValue);
        System.out.println("Put option value: " + putValue);
        return 0;
    }

    // Cumulative normal distribution function
    public static double cdf(double x) {
        double k = 1.0 / (1.0 + 0.2316419 * Math.abs(x));
        double a1 = 0.319381530;
        double a2 = -0.356563782;
        double a3 = 1.781477937;
        double a4 = -1.821255978;
        double a5 = 1.330274429;
        double y = 1.0 / (Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * x * x);
        double z = y * (a1 * k + a2 * k * k + a3 * Math.pow(k, 3) + a4 * Math.pow(k, 4) + a5 * Math.pow(k, 5));
        if (x > 0) {
            return 1 - z;
        } else {
            return z;
        }
    }
    public static void main(String[] args) {

        // Input parameters
        double S = 158.93;  // Current stock price
        double K = 149.0;  // Strike price
        double r = 0.0438;   // Risk-free interest rate
        double sigma = 0.43456946455219864; // Volatility
        double T = 5.0/365.0;    // Time to expiration in years

        System.out.println(blackScholesModel(S,K,r,sigma,T));

    }

}