package vn.ontaxi.rest.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.ontaxi.common.jpa.entity.Customer;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.jpa.entity.Partner;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.CustomerRepository;
import vn.ontaxi.common.jpa.repository.DriverRepository;
import vn.ontaxi.common.jpa.repository.PartnerRepository;
import vn.ontaxi.common.service.JwtTokenProvider;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String accountType = tokenProvider.getAccountTypeFromJWT(jwt);
            String email = tokenProvider.getEmailFromJWT(jwt);

            if (JwtTokenProvider.DRIVER.equalsIgnoreCase(accountType)) {
                Driver driver = driverRepository.findByEmailAndDeletedFalse(email);
                if (driver != null && driver.getStatus() == Driver.Status.ACTIVATED) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(driver, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_DRIVER.name())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else if (JwtTokenProvider.CUSTOMER.equalsIgnoreCase(accountType)) {
                Customer customer = customerRepository.findByPhoneOrEmail(email, email);
                if (customer != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customer, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_CUSTOMER.name())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else if(JwtTokenProvider.PARTNER.equalsIgnoreCase(accountType)) {
                Partner partner = partnerRepository.findByEmail(email);
                if (partner != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(partner, null, Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_PARTER.name())));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
