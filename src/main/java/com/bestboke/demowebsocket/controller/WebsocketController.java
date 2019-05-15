package com.bestboke.demowebsocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.bestboke.demowebsocket.config.RedisMsg;
import com.bestboke.demowebsocket.utils.EmojiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/message")
@Component
public class WebsocketController implements RedisMsg {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketController.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebsocketController> webSocketSet = new CopyOnWriteArraySet<WebsocketController>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.info("新增连接！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }


    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws Exception {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public void sendInfo(String message){
        for (WebsocketController item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (Exception e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebsocketController.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebsocketController.onlineCount--;
    }

    @Override
    public void receiveMessage(String message) {
        message = message.substring(1, message.length()-1).replace("\\", "");
        JSONObject messageJson = JSONObject.parseObject(message);
        String msg = (String) messageJson.get("message");
        messageJson.put("message", EmojiUtils.emojiRecovery(msg));
        sendInfo(JSONObject.toJSONString(messageJson));
    }
}
