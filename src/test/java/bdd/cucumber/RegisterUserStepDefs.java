package bdd.cucumber;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shop.apistore.constraint.Role;
import com.shop.apistore.dto.RegisterAccountDTO;
import com.shop.apistore.model.Account;
import com.shop.apistore.repository.AccountRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Slf4j
public class RegisterUserStepDefs {

    private static final String PUBLIC_REGISTER_LINK = "/shop/api/auth/register";
    static String registerDetailsForNewUserJson;
    MvcResult mvcResult;
    private static RegisterAccountDTO USER_REGISTER_ACCOUNT_DTO;
    private static Account accountMock;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepositoryMock;

    @Before
    public static void setUp() {
        // Json String to send post request
        USER_REGISTER_ACCOUNT_DTO = new RegisterAccountDTO("example@gmail.com", "pass", Role.USER.name());
        registerDetailsForNewUserJson = new Gson().toJson(USER_REGISTER_ACCOUNT_DTO);

        // Mock account from DTO
        accountMock = new Account(USER_REGISTER_ACCOUNT_DTO.getEmail(), USER_REGISTER_ACCOUNT_DTO.getPassword());
        accountMock.setId(1L);
    }

    @Given("Account do not exists in database")
    public void emailAndPassword() {
        when(accountRepositoryMock.save(any(Account.class))).thenReturn(accountMock);
    }

    @Given("Account already exists in database")
    public void accountAlreadyExistsInDatabase() {
        Optional<Account> accountOptionalMock = Optional.of(accountMock);
        when(accountRepositoryMock.findByEmailIgnoreCase(any(String.class))).thenReturn(accountOptionalMock);
    }

    @When("I sign up with email and password")
    public void iSignUpWithEmailAndPassword() throws Exception {
        mvcResult = mockMvc.perform(post(PUBLIC_REGISTER_LINK).content(registerDetailsForNewUserJson)
                .characterEncoding("utf-8").contentType(MediaType.APPLICATION_JSON)).andDo(print()).andReturn();
    }

    @Then("I should receive {int} status")
    public void iShouldReceiveSuccessMessage(int expectedStatus) {
        int actualStatus = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, actualStatus);
    }

    @And("Response message is {string}.")
    public void responseMessage(String expectedMessage) throws UnsupportedEncodingException {
        // String http response body
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // Convert response to JSONObject to get a message property
        JsonObject jsonObject = new Gson().fromJson(contentAsString, JsonObject.class);
        String responseMessage = jsonObject.get("message").getAsString();

        assertEquals(expectedMessage, responseMessage);
        // assertEquals(expectedMessage, "Success !!!");
    }

    @And("Response error is {string}.")
    public void responseError(String expectedMessage) throws UnsupportedEncodingException {
        // String http response body
        String contentAsString = mvcResult.getResponse().getContentAsString();
        // Convert response to JSONObject to get a message property
        JsonObject jsonObject = new Gson().fromJson(contentAsString, JsonObject.class);
        String responseMessage = jsonObject.get("error").getAsString();

        assertEquals(expectedMessage, responseMessage);
        // assertEquals(expectedMessage, "Account exists");
    }

}
