package org.acme;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@Path("/hello")
public class GreetingResource {
    private static final Logger LOGGER = Logger.getLogger("GreetingResource");

    private static final List<String> ALL_INSTRUMENTS = List.of("ClHat-08", "Kick-08", "Flam-01");
    private static final List<String> INSTRUMENTS = new ArrayList<>();

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    @RunOnVirtualThread
    public String hello(@PathParam("name") String name) {
        String nextInstrument = getNextInstrument();
        LOGGER.info(name + " got " + nextInstrument);
        return nextInstrument;
    }

    private synchronized String getNextInstrument() {
        if (INSTRUMENTS.isEmpty()) {
            INSTRUMENTS.addAll(ALL_INSTRUMENTS);
        }
        return INSTRUMENTS.remove(0);
    }
}
