package mycode.help;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS {
    // Replace with your Twilio account SID and authentication token
    private static final String ACCOUNT_SID = "AC0bbe2020f38bc4a22e1d9cc66e7cff15";
    private static final String AUTH_TOKEN = "7b42e4d216928d8aa1f91aa3b03998ed";

    // Replace with your Twilio phone number and the recipient's phone number
    private static final String FROM_PHONE_NUMBER = "+14067294519";
    private static final String TO_PHONE_NUMBER = "+972528223498";

    public static  void sensSMS(String message_){
        try {
            // Initialize the Twilio client
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Send the SMS message
            Message message = Message.creator(new PhoneNumber(TO_PHONE_NUMBER), new PhoneNumber(FROM_PHONE_NUMBER), message_).create();

            // Print the message SID
            System.out.println("Message SID: " + message.getSid());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // Initialize the Twilio client
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Send the SMS message
        Message message = Message.creator(new PhoneNumber(TO_PHONE_NUMBER), new PhoneNumber(FROM_PHONE_NUMBER), "Hello, world!").create();

        // Print the message SID
        System.out.println("Message SID: " + message.getSid());
    }
}