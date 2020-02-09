package main.java.utils;

/**
 * @author ChaosWong
 * @date 2020/1/22 18:24
 * @title main.java.utils
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

@ClientEndpoint
public class WebSocketUtils {
    private static Logger logger = LoggerFactory.getLogger(WebSocketUtils.class);
    private Session session;
    @OnOpen
    public void open(Session session){
        logger.info("Client WebSocket is opening...");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message){
        logger.info("Server send message: " + message);
    }

    @OnClose
    public void onClose(){
        logger.info("Websocket closed");
    }

    /**
     * 发送客户端消息到服务端
     * @param message 消息内容
     */
    public void send(String message){
        this.session.getAsyncRemote().sendText(message);
    }
}
