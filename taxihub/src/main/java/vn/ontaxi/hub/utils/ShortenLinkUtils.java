package vn.ontaxi.hub.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ShortenLinkUtils {

    private static final String URL = "https://www.shorturl.at/url-shortener.php";

    public static String getShortenUrl(String url) {

        try {
            Document document = Jsoup.connect(URL).data("u", url).timeout(10000).post();
            Element shortenurl = document.getElementById("shortenurl");
            return shortenurl.val();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;

    }

}
