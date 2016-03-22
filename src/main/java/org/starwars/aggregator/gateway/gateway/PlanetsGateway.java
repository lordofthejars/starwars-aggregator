package org.starwars.aggregator.gateway.gateway;


import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static java.lang.System.*;
import static java.util.Optional.*;

public class PlanetsGateway {

    String planetsUrl = "http://localhost:8080/starwars/";

    Client client;

    @PostConstruct
    public void initializeClient() {
        client = ClientBuilder.newClient();
        planetsUrl = ofNullable(getenv("planets_url"))
                             .orElse(ofNullable(getProperty("planets_url"))
                             .orElse("http://localhost:8080/starwars/"));

    }

    public String getAverage() {
        WebTarget peopleTarget = client.target(planetsUrl).path("rest/planet/orbital/average");
        String average = peopleTarget.request(MediaType.TEXT_PLAIN).get(String.class);
        return average;
    }

    public JsonObject getThreePlanetsWithMostOrbitalPeriod() {
        WebTarget peopleTarget = client.target(planetsUrl).path("rest/planet/orbital/biggest");
        JsonObject planets = peopleTarget.request(MediaType.APPLICATION_JSON_TYPE).get(JsonObject.class);
        return planets;
    }

}
