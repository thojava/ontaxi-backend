package vn.ontaxi.hub.jpa.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
            return Optional.of("System");
        }
    }
}
