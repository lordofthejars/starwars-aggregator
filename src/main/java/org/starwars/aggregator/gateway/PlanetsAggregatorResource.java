package org.starwars.aggregator.gateway;

import org.starwars.aggregator.gateway.gateway.PlanetsGateway;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/planetaggregator")
@Singleton
@Lock(LockType.READ)
public class PlanetsAggregatorResource {

    @Inject
    PlanetsGateway planetsGateway;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAggregatedInformation() {
        String average = planetsGateway.getAverage();
        JsonObject planets = planetsGateway.getThreePlanetsWithMostOrbitalPeriod();

        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("average", average);
        objectBuilder.add("planets", planets.get("planets"));

        return Response.accepted(objectBuilder.build()).build();
    }

}
