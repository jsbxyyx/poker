package io.github.jsbxyyx.poker;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoomService {

    private static final Map<String/*roomid*/, List<String>/*userid*/> ROOM_USER_MAP = new ConcurrentHashMap<>();
    private static final Map<String/*userid*/, String/*roomid*/> USER_ROOM_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*userid*/, ChannelId> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static final Map<ChannelId, String/*userid*/> CHANNEL_USER_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*roomid*/, Integer/*win index*/> ROOM_WIN_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*roomid*/, Integer/*score*/> ROOM_SCORE_MAP = new ConcurrentHashMap<>();
    private static final Map<String/*roomid*/, Integer/*user index*/> ROOM_NEXT_USER_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*userid*/, Integer/*score*/> USER_SCORE_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*userid*/, List<String>/*cards*/> USER_CARDS_MAP = new ConcurrentHashMap<>();

    private static final Map<String/*command*/, Command> COMMAND_MAP = new HashMap<>();

    private static final CardService cardService = new CardService();

    private static final JsonMapper json = new JsonMapper();

    static {
        COMMAND_MAP.put(Command.JOIN_ROOM, new JoinRoomCommand());
        COMMAND_MAP.put(Command.ROOM_JIAO, new RoomJiaoCommand());
        COMMAND_MAP.put(Command.ROOM_GEN, new RoomGenCommand());
        COMMAND_MAP.put(Command.ROOM_BU_YAO, new RoomBuYaoCommand());
        COMMAND_MAP.put(Command.ROOM_KAI, new RoomKaiCommand());
        COMMAND_MAP.put(Command.ROOM_AGAIN, new RoomAgainCommand());
    }

    public void invoke(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) {
        try {
            Command command = COMMAND_MAP.get(wsm.getType());
            if (command != null)
                command.run(wsm, channelGroup, channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String clearChannel(ChannelGroup channelGroup, Channel channel) {
        channelGroup.remove(channel);
        String userid = CHANNEL_USER_MAP.remove(channel.id());
        if (userid != null && !"".equals(userid)) {
            USER_CHANNEL_MAP.remove(userid);
            String roomid = USER_ROOM_MAP.remove(userid);
            if (roomid != null && !"".equals(roomid)) {
                ROOM_USER_MAP.get(roomid).remove(userid);
            }
        }
        return userid;
    }

    static class JoinRoomCommand implements Command {

        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            if (!ROOM_USER_MAP.containsKey(roomid)) {
                ROOM_USER_MAP.put(roomid, new ArrayList<>());
                ROOM_WIN_MAP.put(roomid, 0);
            }
            String userid = wsm.getData().get("userid").toString();
            ROOM_USER_MAP.get(roomid).add(userid);
            USER_ROOM_MAP.put(userid, roomid);
            USER_CHANNEL_MAP.put(userid, channel.id());
            CHANNEL_USER_MAP.put(channel.id(), userid);

            int size = ROOM_USER_MAP.get(roomid).size();
            if (size <= 1) {
                channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage<>(Command.JOIN_ROOM_RESP, size)));
            } else if (size == 2) {
                takeCard(wsm, channelGroup, channel);
            } else {
                log.info("user limit 2");
            }
        }
    }

    static class RoomJiaoCommand implements Command {
        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            String userid = wsm.getData().get("userid").toString();
            int score = Integer.parseInt(wsm.getData().get("score").toString());
            ROOM_SCORE_MAP.put(roomid, ROOM_SCORE_MAP.get(roomid) + score);
            channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_SCORE_RESP, ROOM_SCORE_MAP.get(roomid))));
            List<String> users = ROOM_USER_MAP.get(roomid);
            int idx = 0;
            for (int i = 0, len = users.size(); i < len; i++) {
                String useridTmp = users.get(i);
                if (useridTmp.equals(userid)) {
                    idx = i + 1;
                    if (idx >= len) {
                        idx = 0;
                    }
                    break;
                }
            }
            channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_JIAO_RESP, idx)));
        }
    }

    static class RoomGenCommand implements Command {
        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            String userid = wsm.getData().get("userid").toString();
            List<String> users = ROOM_USER_MAP.get(roomid);
            int idx = 0;
            for (int i = 0, len = users.size(); i < len; i++) {
                String useridTmp = users.get(i);
                if (useridTmp.equals(userid)) {
                    idx = i + 1;
                    if (idx >= len) {
                        idx = 0;
                    }
                    break;
                }
            }
            channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_JIAO_RESP, idx)));
        }
    }

    static class RoomBuYaoCommand implements Command {
        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            String userid = wsm.getData().get("userid").toString();
            Integer score = ROOM_SCORE_MAP.get(roomid);
            if (score > 0) {
                List<String> users = ROOM_USER_MAP.get(roomid);
                int idx = 0;
                for (int i = 0, len = users.size(); i < len; i++) {
                    String useridTmp = users.get(i);
                    if (useridTmp.equals(userid)) {
                        idx = i;
                        break;
                    }
                }
                Integer winIndex = ROOM_WIN_MAP.get(roomid);
                if (winIndex != idx) {
                    ROOM_WIN_MAP.put(roomid, idx);
                    USER_SCORE_MAP.put(userid, USER_SCORE_MAP.get(userid) + score);
                    for (int i = 0, len = users.size(); i < len; i++) {
                        String useridTmp = users.get(i);
                        if (!useridTmp.equals(userid)) {
                            USER_SCORE_MAP.put(useridTmp, USER_SCORE_MAP.get(useridTmp) + (-score));
                        }
                    }
                    List<String> userids = ROOM_USER_MAP.get(roomid);
                    String user0 = userids.get(0);
                    String user1 = userids.get(1);
                    List<String> card0 = USER_CARDS_MAP.get(user0);
                    List<String> card1 = USER_CARDS_MAP.get(user1);
                    channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_WIN, maps(
                            "winIdx", idx, "card0", card0, "card1", card1
                    ))));
                } else {
                    List<String> userids = ROOM_USER_MAP.get(roomid);
                    String user0 = userids.get(0);
                    String user1 = userids.get(1);
                    int winIdx = cardService.compare(USER_CARDS_MAP.get(user0), USER_CARDS_MAP.get(user1));
                    if (winIdx == 0) {
                        USER_SCORE_MAP.put(user0, USER_SCORE_MAP.get(user0) + score);
                        USER_SCORE_MAP.put(user1, USER_SCORE_MAP.get(user0) + (-score));
                    } else if (winIdx == 1) {
                        USER_SCORE_MAP.put(user0, USER_SCORE_MAP.get(user0) + (-score));
                        USER_SCORE_MAP.put(user1, USER_SCORE_MAP.get(user0) + (score));
                    }
                    if (winIdx != -1) {
                        ROOM_WIN_MAP.put(roomid, winIdx);
                        USER_CARDS_MAP.remove(user0);
                        USER_CARDS_MAP.remove(user1);
                        ROOM_NEXT_USER_MAP.put(roomid, winIdx);
                        ROOM_SCORE_MAP.put(roomid, 0);
                        channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_SCORE_RESP, ROOM_SCORE_MAP.get(roomid))));
                    }
                    List<String> card0 = USER_CARDS_MAP.get(user0);
                    List<String> card1 = USER_CARDS_MAP.get(user1);
                    channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_WIN, maps(
                            "winIdx", winIdx, "card0", card0, "card1", card1
                    ))));
                }
            } else {
                channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_AGAIN_RESP, null)));
            }
        }
    }

    static class RoomKaiCommand implements Command {

        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            String userid = wsm.getData().get("userid").toString();
            Integer score = ROOM_SCORE_MAP.get(roomid);
            if (score > 0) {
                List<String> userids = ROOM_USER_MAP.get(roomid);
                String user0 = userids.get(0);
                String user1 = userids.get(1);
                List<String> card0 = USER_CARDS_MAP.get(user0);
                List<String> card1 = USER_CARDS_MAP.get(user1);
                int winIdx = cardService.compare(card0, card1);
                log.info("compare card0 : {} {} card1 : {} {} : {}", user0, card0, user1, card1, winIdx);
                if (winIdx == 0) {
                    USER_SCORE_MAP.put(user0, USER_SCORE_MAP.get(user0) + score);
                    USER_SCORE_MAP.put(user1, USER_SCORE_MAP.get(user0) + (-score));
                } else if (winIdx == 1) {
                    USER_SCORE_MAP.put(user0, USER_SCORE_MAP.get(user0) + (-score));
                    USER_SCORE_MAP.put(user1, USER_SCORE_MAP.get(user0) + (score));
                }
                if (winIdx != -1) {
                    ROOM_WIN_MAP.put(roomid, winIdx);
                    USER_CARDS_MAP.remove(user0);
                    USER_CARDS_MAP.remove(user1);
                    ROOM_NEXT_USER_MAP.put(roomid, winIdx);
                    ROOM_SCORE_MAP.put(roomid, 0);
                    channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_SCORE_RESP, ROOM_SCORE_MAP.get(roomid))));
                }
                channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_WIN, maps(
                        "winIdx", winIdx, "card0", card0, "card1", card1
                ))));
            } else {
                channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_AGAIN_RESP, null)));
            }
        }
    }

    static class RoomAgainCommand implements Command {

        @Override
        public void run(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
            String roomid = wsm.getData().get("roomid").toString();
            channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_AGAIN_RESP, null)));
            channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_FIRST, ROOM_WIN_MAP.get(roomid))));
            takeCard(wsm, channelGroup, channel);
        }

    }

    private static void takeCard(WebSocketMessage<Map<String, Object>> wsm, ChannelGroup channelGroup, Channel channel) throws Exception {
        String roomid = wsm.getData().get("roomid").toString();
        Stack<String> stack = cardService.shuffleCard();
        Map<String/*userid*/, List<String>/*cards*/> userCard = new HashMap<>();
        List<String> users = ROOM_USER_MAP.get(roomid);
        int idx = 0;
        for (String user : users) {
            userCard.put(user, new ArrayList<>());
            channelGroup.find(USER_CHANNEL_MAP.get(user))
                    .writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_START, idx)));
            idx++;
            USER_SCORE_MAP.put(user, 100);
        }
        channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_FIRST, ROOM_WIN_MAP.get(roomid))));
        ROOM_SCORE_MAP.put(roomid, 0);
        ROOM_NEXT_USER_MAP.put(roomid, ROOM_WIN_MAP.get(roomid));
        channelGroup.find(USER_CHANNEL_MAP.get(users.get(0)))
                .writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_JIAO_RESP, ROOM_NEXT_USER_MAP.get(roomid))));
        channelGroup.writeAndFlush(wrapMsg(new WebSocketMessage(Command.ROOM_SCORE_RESP, ROOM_SCORE_MAP.get(roomid))));
        int nums = 0;
        while (!stack.isEmpty()) {
            if (nums >= 3) {
                break;
            }
            for (Map.Entry<String, List<String>> entry : userCard.entrySet()) {
                entry.getValue().add(stack.pop());
            }
            nums++;
        }
        for (Map.Entry<String, ChannelId> entry : USER_CHANNEL_MAP.entrySet()) {
            String key = entry.getKey();
            List<String> cards = userCard.get(key);
            USER_CARDS_MAP.put(key, cards);
            ChannelId channelId = entry.getValue();
            channelGroup.find(channelId).writeAndFlush(wrapMsg(
                    new WebSocketMessage(Command.TAKE_CARD, cards)
            ));
        }
    }

    private static TextWebSocketFrame wrapMsg(WebSocketMessage msg) throws Exception {
        log.info("output : {}", msg);
        return new TextWebSocketFrame(json.writeValueAsString(msg));
    }

    private static Map<String, Object> maps(Object... objects) {
        if (objects == null) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < objects.length; ) {
            map.put(objects[i++].toString(), objects[i++]);
        }
        return map;
    }

}
