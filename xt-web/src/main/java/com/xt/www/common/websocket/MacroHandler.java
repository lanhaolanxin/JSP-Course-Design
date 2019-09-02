package com.xt.www.common.websocket;

import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.logging.Logger;

public class MacroHandler extends AbstractWebSocketHandler {
    private static final Logger logger =
            (Logger) LoggerFactory.getLogger(MacroHandler.class);

    protected void handlerTextMessage(
            WebSocketSession session, TextMessage textMessage)throws Exception{
        logger.info("received Message" +textMessage.getPayload());
        Thread.sleep(2000);
        session.sendMessage(new TextMessage("polo"));
    }

}
