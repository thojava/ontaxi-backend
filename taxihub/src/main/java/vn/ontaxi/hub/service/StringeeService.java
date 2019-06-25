package vn.ontaxi.hub.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.ontaxi.common.jpa.entity.User;
import vn.ontaxi.common.jpa.repository.UserRepository;
import vn.ontaxi.hub.model.CreateAgent;
import vn.ontaxi.hub.model.CreateAgentResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StringeeService {

    String accessToken = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTS3hXRVVWeUZZeWVQaEtvdlRNNHRNRGFHWDM1ZDZVdkItMTU2MTQ0ODgwMCIsImlzcyI6IlNLeFdFVVZ5Rll5ZVBoS292VE00dE1EYUdYMzVkNlV2QiIsImV4cCI6MTU2NDA0MDgwMCwicmVzdF9hcGkiOnRydWV9.tT5XpTtbrOrbrFQVxyrUgE3fZU29IcAlfiIHLBulxHw";

    @Value("${stringee.sid}")
    private String stringeeSid;

    @Value("${stringee.key_secret}")
    private String stringeeKeySecret;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

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
        } else {
            throw new RuntimeException("Cannot register agent");
        }

    }

    private String generateAccessToken(String userId) {

        try {
            Algorithm algorithmHS = Algorithm.HMAC256(stringeeKeySecret);

            Map<String, Object> headerClaims = new HashMap<>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = System.currentTimeMillis() + 3600L * 24 * 30 * 12 * 1000 * 99; //99 years

            String token = JWT.create().withHeader(headerClaims)
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

}
