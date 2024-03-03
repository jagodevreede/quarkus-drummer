package org.acme;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import org.jboss.logging.Logger;

@ClientEndpoint
public class Player {
    private static final Logger LOGGER = Logger.getLogger("Player");

    public Player(PlayerStart playerStart) {

    }

    @OnOpen
    public void open(Session session) {
        LOGGER.info("connected");
    }

    @OnMessage
    void message(String msg) {
        LOGGER.info(msg);
    }

}
