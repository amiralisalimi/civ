package ir.ap.network;

import java.util.ArrayList;

public class Request {
    private final String methodName;
    private final String authToken;
    private final ArrayList<Object> params;

    public Request(String methodName, String authToken) {
        this.methodName = methodName;
        this.authToken = authToken;
        this.params = new ArrayList<>();
    }

    public String getMethodName() {
        return methodName;
    }

    public void addParam(Object value) {
        params.add(value);
    }

    public ArrayList<Object> getParams() {
        return params;
    }

    public String getAuthToken() {
        return authToken;
    }
}
