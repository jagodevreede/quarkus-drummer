package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.websocket.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
@ClientEndpoint
public class PlayerStart {
    private Session session;

    @ConfigProperty(name = "player.name")
    String playerName;

    @ConfigProperty(name = "music_folder")
    String musicFolder;

    private final Musician musician = new Musician(new File(musicFolder), "ClHat");

    void onStart(@Observes StartupEvent ev) throws DeploymentException, IOException, URISyntaxException {
        URI uri = new URI("ws://localhost:8080/chat/" + playerName);
        session = ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
    }

    @OnMessage
    void message(String msg) {
        musician.beat(Short.parseShort(msg));
    }
}
