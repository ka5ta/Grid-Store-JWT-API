package bdd.cucumber;

import com.shop.apistore.ApiJwtStoreApplication;
import com.shop.apistore.repository.AccountRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CucumberContextConfiguration
@SpringBootTest(classes = { ApiJwtStoreApplication.class, CucumberSpringConfiguration.class })
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {

    @MockBean
    AccountRepository accountRepositoryMock;
}
