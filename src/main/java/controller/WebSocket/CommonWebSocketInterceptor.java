package main.java.controller.WebSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ChaosWong
 * @date 2020/1/19 16:15
 * @title main.java.controller.DeviceServerController.APPWebSocket
 */
public class CommonWebSocketInterceptor implements HandshakeInterceptor {
    private Logger logger = LoggerFactory.getLogger( CommonWebSocketInterceptor.class);
    /**
     * 关联HeepSession和WebSocketSession，
     * beforeHandShake方法中的Map参数 就是对应websocketSession里的属性
     */
    @Override
    public boolean beforeHandshake( ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> map) throws Exception {

        if (request instanceof ServletServerHttpRequest ) {
            logger.info("*****beforeHandshake******");
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            HttpSession session = httpServletRequest.getSession(true);

            logger.info("mchNo：{}", httpServletRequest.getParameter("mchNo"));
            if (session != null) {

                map.put("sessionId",session.getId());
                map.put("mchNo", httpServletRequest.getParameter("mchNo"));
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        logger.info("******afterHandshake******");
    }
}
