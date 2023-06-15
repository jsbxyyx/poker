package io.github.jsbxyyx.poker;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicLong;

@Setter
@Getter
@ToString
@Accessors(chain = true)
public class WebSocketMessage<T> {

    private static final AtomicLong COUNTER = new AtomicLong();

    private String requestId;
    private String type;
    private T data;

    public WebSocketMessage() {
    }

    public WebSocketMessage(String type, T data) {
        this(null, type, data);
    }

    public WebSocketMessage(String requestId, String type, T data) {
        this.requestId = requestId == null ? COUNTER.getAndIncrement() + "" : requestId;
        this.type = type;
        this.data = data;
    }

    public static WebSocketMessage pong() {
        return new WebSocketMessage(Command.PING, "PONG");
    }

}
