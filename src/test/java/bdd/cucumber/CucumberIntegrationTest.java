package bdd.cucumber;

import com.shop.APIJWTStore.controller.AuthenticationController;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;


import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("bdd/cucumber")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "bdd.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class CucumberIntegrationTest {



}
