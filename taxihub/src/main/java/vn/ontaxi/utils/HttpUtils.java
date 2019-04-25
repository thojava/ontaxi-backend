package vn.ontaxi.utils;

import org.omnifaces.servlet.HttpServletResponseOutputWrapper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HttpUtils {
    public static String readTextFromURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        }
        else {
            response.setHeader("Content-Disposition", "filename=" + fileName);
        }
    }

}
