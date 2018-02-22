package com.jtigp.server.web;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"classpath:features/"}, glue = {"com.jtigp.server.web.steps"})
public class ContactServiceTest {
}
