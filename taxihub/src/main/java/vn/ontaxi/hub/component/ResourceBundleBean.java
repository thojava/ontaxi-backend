package vn.ontaxi.hub.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import java.util.HashMap;

@Component(value = "msg")
public class ResourceBundleBean extends HashMap {

    private final MessageSource messageSource;

    @Autowired
    public ResourceBundleBean(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String get(Object key) {
        ServletRequest request = (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String message;
        try {
            message = messageSource.getMessage((String) key, null, request.getLocale());
        }
        catch (NoSuchMessageException e) {
            message = "???" + key + "???";
        }
        return message;
    }
}