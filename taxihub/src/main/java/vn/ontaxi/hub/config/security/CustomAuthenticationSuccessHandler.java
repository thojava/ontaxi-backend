package vn.ontaxi.hub.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.entity.User;
import vn.ontaxi.common.jpa.repository.UserRepository;
import vn.ontaxi.common.utils.StringUtils;
import vn.ontaxi.hub.model.user.LoggedInUser;
import vn.ontaxi.hub.service.StringeeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${stringee.sid}")
    private String stringeeSid;

    @Value("${stringee.key_secret}")
    private String stringeeKeySecret;

    @Autowired
    private StringeeService stringeeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {

        LoggedInUser loggedInUser = (LoggedInUser) authentication.getPrincipal();
        if (loggedInUser.getUser().isHelpDesk()) {
            if (StringUtils.isEmpty(loggedInUser.getUser().getStringeeAccessToken())) {
                stringeeService.registerAgent(loggedInUser.getUser());
            }
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.sendRedirect("index.jsf");
    }

}
