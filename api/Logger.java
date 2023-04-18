package api;

import com.ib.controller.ApiConnection.ILogger;

public class Logger implements ILogger {

    @Override
    public void log(String valueOf) {
        System.out.print(valueOf);
    }

}