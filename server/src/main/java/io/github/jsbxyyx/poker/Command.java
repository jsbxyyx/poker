package io.github.jsbxyyx.poker;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;

interface Command {

    String PING = "PING";
    String JOIN_ROOM = "JOIN_ROOM";
    String JOIN_ROOM_RESP = "JOIN_ROOM_RESP";
    String ROOM_START = "ROOM_START";
    String TAKE_CARD = "TAKE_CARD";
    String ROOM_FIRST = "ROOM_FIRST";
    String ROOM_JIAO = "ROOM_JIAO";
    String ROOM_GEN = "ROOM_GEN";
    String ROOM_KAI = "ROOM_KAI";
    String ROOM_BU_YAO = "ROOM_BU_YAO";
    String ROOM_SCORE_RESP = "ROOM_SCORE_RESP";
    String ROOM_JIAO_RESP = "ROOM_JIAO_RESP";
    String ROOM_WIN = "ROOM_WIN";
    String ROOM_AGAIN = "ROOM_AGAIN";
    String ROOM_AGAIN_RESP = "ROOM_AGAIN_RESP";

    void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception;

}
