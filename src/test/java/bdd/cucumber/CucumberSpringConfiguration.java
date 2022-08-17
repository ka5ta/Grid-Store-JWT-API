package bdd.cucumber;

import com.shop.APIJWTStore.ApiJwtStoreApplication;
import com.shop.APIJWTStore.controller.AuthenticationController;
import com.shop.APIJWTStore.repository.AccountRepository;
import com.shop.APIJWTStore.service.JwtUserDetailsService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@CucumberContextConfiguration
@SpringBootTest(classes = {ApiJwtStoreApplication.class, CucumberSpringConfiguration.class})
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {

    @MockBean
    AccountRepository accountRepositoryMock;
}
