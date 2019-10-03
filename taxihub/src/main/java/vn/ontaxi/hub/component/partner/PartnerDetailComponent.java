package vn.ontaxi.hub.component.partner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Partner;
import vn.ontaxi.common.jpa.entity.Role;
import vn.ontaxi.common.jpa.repository.PartnerRepository;
import vn.ontaxi.common.service.JwtTokenProvider;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Component
@Scope("view")
public class PartnerDetailComponent {
    private Partner partner;
    private final PartnerRepository partnerRepository;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public PartnerDetailComponent(PartnerRepository partnerRepository, JwtTokenProvider tokenProvider) {
        this.partnerRepository = partnerRepository;
        this.tokenProvider = tokenProvider;
    }

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if (params.containsKey("id")) {
            String parameterOne = params.get("id");
            partner = partnerRepository.findById(Long.parseLong(parameterOne)).get();
        } else {
            partner = new Partner();
        }
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public void savePartner() {
        partner = partnerRepository.save(partner);
    }

    public void generateToken() {
        Authentication authentication = new Authentication() {

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_PARTER.name()));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return partner;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        };
        String jwt = tokenProvider.generateToken(authentication);
        partner.setApiToken(jwt);
        partner = partnerRepository.save(partner);
    }
}
