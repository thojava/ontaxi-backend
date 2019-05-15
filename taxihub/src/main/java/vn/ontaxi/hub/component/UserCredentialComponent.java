package vn.ontaxi.hub.component;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Role;

@Component
public class UserCredentialComponent {
    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


    public boolean hasViewAllPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(Role.ADMIN.toString())
                || a.getAuthority().equalsIgnoreCase(Role.MANAGER.toString()));
    }

    public boolean isManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(Role.MANAGER.toString()));
    }

    public boolean isCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equalsIgnoreCase(Role.CUSTOMER.toString()));
    }
}
