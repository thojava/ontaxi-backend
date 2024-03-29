package vn.ontaxi.hub.utils;

import org.omnifaces.servlet.HttpServletResponseOutputWrapper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

public class HttpUtils extends vn.ontaxi.common.utils.HttpUtils {
    public static HttpServletResponse setupHttpResponseForMIMEContent(final String minetype, final String fileName) {
        final FacesContext context = FacesContext.getCurrentInstance();
        // Mark response complete to clean/up things that have been processed in this JSF life-cycle
        // avoid an exception
        context.responseComplete();
        // Set passThrough to be true so that can response OutputStream without exception
        final HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        if (HttpServletResponseOutputWrapper.class.isAssignableFrom(response.getClass())) {
            ((HttpServletResponseOutputWrapper) response).setPassThrough(true);
        }

        HttpUtils.setupHttpResponseHeaderForMIMEContent(response, minetype, fileName, false);

        return response;
    }

    private static void setupHttpResponseHeaderForMIMEContent(final HttpServletResponse response, final String minetype, final String fileName, final boolean asAttachment) {
        response.setHeader("Content-Type", minetype);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=0");
        if (asAttachment) {
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } else {
            response.setHeader("Content-Disposition", "filename=" + fileName);
        }
    }
}
