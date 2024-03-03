package org.acme;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ApplicationScoped
@ClientEndpoint
public class PlayerStart {
    private static final org.jboss.logging.Logger LOGGER = Logger.getLogger("PlayerStart");
    private Session session;

    @ConfigProperty(name = "player.name")
    String playerName;

    @ConfigProperty(name = "music_folder")
    String musicFolder;

    @RestClient
    ConductorRestClient conductorRestClient;

    @Inject
    DrumPatternRepository drumPatternRepository;

    private Musician musician;

    void onStart(@Observes StartupEvent ev) throws DeploymentException, IOException, URISyntaxException {
        var instrumentToPlay = conductorRestClient.hello(playerName);
        LOGGER.info("Got instrument " + instrumentToPlay);
        DrumPattern drumPattern = drumPatternRepository.findByName(instrumentToPlay);
        LOGGER.info("Will play pattern " + drumPattern.getPattern());
        musician = new Musician(new File(musicFolder), instrumentToPlay, drumPattern.getPattern());
        URI uri = new URI("ws://localhost:8080/chat/" + playerName);
        session = ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
    }

    @OnMessage
    void message(String msg) {
        musician.beat(Short.parseShort(msg));
    }
}
