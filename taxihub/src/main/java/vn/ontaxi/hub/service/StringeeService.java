package vn.ontaxi.hub.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import vn.ontaxi.common.jpa.entity.User;
import vn.ontaxi.common.jpa.repository.UserRepository;
import vn.ontaxi.hub.model.AddAgentIntoGroup;
import vn.ontaxi.hub.model.CreateAgent;
import vn.ontaxi.hub.model.CreateAgentResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StringeeService {

    @Value("${stringee.access_token}")
    String accessToken;

    @Value("${stringee.group_id}")
    String groupId;

    @Value("${stringee.sid}")
    private String stringeeSid;

    @Value("${stringee.key_secret}")
    private String stringeeKeySecret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void registerAgent(User user) {

        CreateAgent createAgent = new CreateAgent(user.getUserName(), user.getUserName());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-STRINGEE-AUTH", accessToken);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAgent> entity = new HttpEntity<>(createAgent,headers);

        CreateAgentResponse exchange = restTemplate.exchange("https://icc-api.stringee.com/v1/agent", HttpMethod.POST, entity, CreateAgentResponse.class).getBody();

        if (exchange != null && exchange.isCreated()){
            user.setStringeeAccessToken(generateAccessToken(user.getUserName()));
            userRepository.save(user);

            AddAgentIntoGroup addAgentIntoGroup = new AddAgentIntoGroup(exchange.getAgentID(), groupId);
            restTemplate.exchange("https://icc-api.stringee.com/v1/manage-agents-in-group", HttpMethod.POST, new HttpEntity<>(addAgentIntoGroup,headers), String.class).getBody();

        } else {
            throw new RuntimeException("Cannot register agent");
        }

    }

    private String generateAccessToken(String userId) {

        try {
            Algorithm algorithmHS = Algorithm.HMAC256(stringeeKeySecret);
            long exp = System.currentTimeMillis() + 3600L * 24 * 30 * 12 * 1000 * 99; //99 years
            String token = JWT.create().withHeader(headerClaims())
                    .withClaim("jti", stringeeSid + "-" + System.currentTimeMillis())
                    .withClaim("iss", stringeeSid)
                    .withClaim("userId", userId)
                    .withClaim("icc_api", true)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private Map<String, Object> headerClaims() {
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", "JWT");
        headerClaims.put("alg", "HS256");
        headerClaims.put("cty", "stringee-api;v=1");

        return headerClaims;
    }

    private String generateAccessTokenForRest() {

        try {
            Algorithm algorithmHS = Algorithm.HMAC256(stringeeKeySecret);
            long exp = System.currentTimeMillis() + 3600L * 24 * 30 * 12 * 1000 * 99; //99 years
            String token = JWT.create().withHeader(headerClaims())
                    .withClaim("jti", stringeeSid + "-" + System.currentTimeMillis())
                    .withClaim("iss", stringeeSid)
                    .withClaim("rest_api", true)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void main(String [] ars) {
        try {
            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            Algorithm algorithmHS = Algorithm.HMAC256("SWZXdHZIMWY5R0ViVTdadUttVG5ISktxVktoMGx1b08=");
            long exp = System.currentTimeMillis() + 3600L * 24 * 30 * 12 * 1000 * 99; //99 years
            String token = JWT.create().withHeader(headerClaims)
                    .withClaim("jti", "SKxWEUVyFYyePhKovTM4tMDaGX35d6UvB" + "-" + System.currentTimeMillis())
                    .withClaim("iss", "SKxWEUVyFYyePhKovTM4tMDaGX35d6UvB")
                    .withClaim("rest_api", true)
                    .withExpiresAt(new Date(exp))
                    .sign(algorithmHS);

            System.out.println(token);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
