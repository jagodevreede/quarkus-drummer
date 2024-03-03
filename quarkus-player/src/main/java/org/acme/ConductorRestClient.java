package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/hello")
@RegisterRestClient(configKey="conductor-api")
public interface ConductorRestClient {

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    String hello(@PathParam("name") String name);
}
