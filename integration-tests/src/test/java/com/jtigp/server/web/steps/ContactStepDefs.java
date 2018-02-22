package com.jtigp.server.web.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Java6Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContactStepDefs {

    private RestTemplate restTemplate;
    private ResponseEntity response;
    private JSONObject contact;


    @Before
    public void setUp() {
        restTemplate = new RestTemplateBuilder().rootUri("http://localhost:8080").build();
        response = null;
        contact = null;
    }

    @When("^I create a contact named (\\w+) (\\w+)$")
    public void createContactNamed(String firstName, String lastName) throws JSONException {
        contact = new JSONObject();
        contact.put("firstName", firstName);
        contact.put("lastName", lastName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(contact.toString(), headers);
        response = restTemplate.postForEntity("/api/contacts", requestEntity, ResponseEntity.class);
    }

    @Then("^I should be able to find the contact by id named (\\w+) (\\w+)$")
    public void findContactById(String firstName, String lastName) throws JSONException {
        assertThat(response.getHeaders().containsKey("location"), is(true));
        String location = response.getHeaders().get("location").get(0);

        ResponseEntity<String> getById = restTemplate.getForEntity(location, String.class);
        assertThat(getById.getStatusCode(), is(HttpStatus.OK));
        JSONObject body = new JSONObject(getById.getBody());
        assertThat(body.get("firstName"), is(firstName));
        assertThat(body.get("lastName"), is(lastName));
    }


    @Given("^a contact already created named (\\w+) (\\w+)$")
    public void aContactAlreadyCreated(String firstName, String lastName) throws JSONException {
        String findAllUri = String.format("/api/contacts?firstName=%s&lastName=%s", firstName, lastName);
        ResponseEntity<String> getAllByName = restTemplate.getForEntity(findAllUri, String.class);
        JSONArray arr = new JSONArray(getAllByName.getBody());
        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);
            restTemplate.delete("/api/contacts/" + json.get("id"));
        }

        contact = new JSONObject();
        contact.put("firstName", firstName);
        contact.put("lastName", lastName);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(contact.toString(), headers);
        response = restTemplate.postForEntity("/api/contacts", requestEntity, ResponseEntity.class);
        ResponseEntity<String> getById = restTemplate.getForEntity(response.getHeaders().get("location").get(0), String.class);
        contact = new JSONObject(getById.getBody());
    }

    @When("^I delete the contact$")
    public void iDeleteTheContact() throws JSONException {
        restTemplate.delete("/api/contacts/" + contact.get("id"));
    }

    @Then("^I should not be able to find the contact by id")
    public void shouldNotFindById() {
        String location = response.getHeaders().get("location").get(0);
        try {
            ResponseEntity<String> getById = restTemplate.getForEntity(location, String.class);
            fail("Should have thrown a 404");
        } catch (HttpClientErrorException e) {
            assertThat(e.getRawStatusCode(), is(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Then("^I should not be able to find the contact named (\\w+) (\\w+)$")
    public void shouldNotFindByName(String firstName, String lastName) throws JSONException {
        String findAllUri = String.format("/api/contacts?firstName=%s&lastName=%s", firstName, lastName);
        ResponseEntity<String> getAllByName = restTemplate.getForEntity(findAllUri, String.class);
        JSONArray arr = new JSONArray(getAllByName.getBody());

        assertThat(arr.length(), is(0));
    }


}
