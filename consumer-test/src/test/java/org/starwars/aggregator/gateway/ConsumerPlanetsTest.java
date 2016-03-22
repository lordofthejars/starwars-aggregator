package org.starwars.aggregator.gateway;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.johnzon.jaxrs.JsrProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.starwars.aggregator.gateway.gateway.PlanetsGateway;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConsumerPlanetsTest {

    @Rule public PactProviderRule rule = new PactProviderRule("planets_provider", "localhost", 8080, this);

    @Pact(provider="planets_provider", consumer="planets_consumer")
    public PactFragment createFragment(PactDslWithProvider builder) {
        Map<String, String> headersJson = new HashMap<>();
        headersJson.put("Content-Type", "application/json");

        Map<String, String> headersText = new HashMap<>();
        headersText.put("Content-Type", "text/plain");

        return builder
                .given("planets aggregator")
                .uponReceiving("Planets average calculation")
                    .path("/rest/planet/orbital/average")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .headers(headersText)
                    .body("1298.3")
                .uponReceiving("Planets with biggest orbital period")
                    .path("/rest/planet/orbital/biggest")
                    .method("GET")
                .willRespondWith()
                    .headers(headersJson)
                    .status(200)
                    .body("{\"planets\": [\"Bespin\", \"Yavin IV\", \"Hoth\"]}")
                .toFragment();
    }

    @BeforeClass
    public static void setPlanetsUrl() {
        System.setProperty("planets_url", "http://localhost:8080");
        final Bus bus = BusFactory.getDefaultBus();
        bus.setProperty("org.apache.cxf.jaxrs.bus.providers", Arrays.asList(new JsrProvider()));
    }

    @AfterClass
    public static void cleanPlanetsUrl() {
        System.clearProperty("planets_url");
    }

    @Test
    @PactVerification("planets_provider")
    public void shouldUseGatewayToCommunicateWithPlanetsService() {
        PlanetsGateway planetsGateway = new PlanetsGateway();
        planetsGateway.initializeClient();
        final String average = planetsGateway.getAverage();
        assertThat(average, is("1298.3"));

        final JsonObject planets = planetsGateway.getThreePlanetsWithMostOrbitalPeriod();
        final JsonArray planetsArray = planets.getJsonArray("planets");
        final List<JsonString> valuesAs = planetsArray.getValuesAs(JsonString.class);
        assertThat(valuesAs.get(0).getString(), is("Bespin"));
        assertThat(valuesAs.get(1).getString(), is("Yavin IV"));
        assertThat(valuesAs.get(2).getString(), is("Hoth"));
    }

}
