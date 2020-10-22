package org.someth2say.storm;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class ResponseData<T> {

    final public Instant startTime;
    final public Instant endTime;
    final public HttpResponse<String> response;
    final public Exception exception;
    final public HttpRequest request;

    public ResponseData(final HttpRequest request, final HttpResponse<String> response, final Instant startTime,
            final Instant endTime, final Exception exception) {
        this.request = request;
        this.response = response;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exception = exception;
    }
    
    public Duration getDuration(){
        return Duration.between(startTime, endTime);
    }


}
