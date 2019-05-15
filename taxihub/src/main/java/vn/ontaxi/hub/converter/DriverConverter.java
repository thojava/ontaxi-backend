package vn.ontaxi.hub.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.ontaxi.common.jpa.entity.Driver;
import vn.ontaxi.common.service.DriverService;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

@Component("driverConverter")
public class DriverConverter implements Converter {
    private final DriverService driverService;

    @Autowired
    public DriverConverter(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        return driverService.findByEmail(value);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object != null && object instanceof Driver) {
            return String.valueOf(((Driver) object).getEmail());
        } else {
            return null;
        }
    }
}
