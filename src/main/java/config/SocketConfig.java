package main.java.config;

import main.java.controller.WebSocket.CommonWebSocketHandler;
import main.java.controller.WebSocket.CommonWebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author ChaosWong
 * @date 2020/1/19 16:16
 * @title main.java.controller.DeviceServerController.APPWebSocket
 */
@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers( WebSocketHandlerRegistry registry) {
        registry.addHandler(commonWebSocketHandler(), "/socket").addInterceptors(new CommonWebSocketInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler commonWebSocketHandler() {
        return new CommonWebSocketHandler();
    }

}
