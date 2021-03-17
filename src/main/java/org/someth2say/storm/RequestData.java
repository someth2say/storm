package org.someth2say.storm;

import java.net.http.HttpRequest;

public class RequestData {

    public final int count;
    public final HttpRequest httpRequest;

    public RequestData(int count, HttpRequest httpRequest) {
        this.count = count;
        this.httpRequest = httpRequest;
    }

}
