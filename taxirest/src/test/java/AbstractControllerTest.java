import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.NamingException;
import java.io.IOException;

public abstract class AbstractControllerTest {

    public AbstractControllerTest() {
        MockitoAnnotations.initMocks(this);
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/ontaxi?autoReconnect=true");
        dataSource.setUsername("root");
        dataSource.setPassword("admin");
        builder.bind("java:comp/env/jdbc/onTaxiDataSource", dataSource);
        try {
            builder.activate();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

}
