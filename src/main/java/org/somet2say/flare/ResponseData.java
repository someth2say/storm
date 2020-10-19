package org.somet2say.flare;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class ResponseData<T> {

    final public Instant startTime;
    final public Instant endTime;
    final public HttpResponse<String> response;
    final public Exception exception;

    public ResponseData(final HttpResponse<String> response, final Instant startTime, final Instant endTime,
            final Exception exception) {
        this.response = response;
        this.startTime = startTime;
        this.endTime = endTime;
        this.exception = exception;
    }
    
    public Duration getDuration(){
        return Duration.between(startTime, endTime);
    }


}
