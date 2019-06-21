package vn.ontaxi.rest.controller;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ontaxi.rest.payload.stringee.From;
import vn.ontaxi.rest.payload.stringee.SCCO;
import vn.ontaxi.rest.payload.stringee.To;

import java.util.Map;

@RestController
@RequestMapping(path = "/scco")
public class SCCOController {

    @RequestMapping(path = "/data")
    public String test(@RequestParam Map<String,String> allParams) {

        SCCO scco = new SCCO();
        scco.setAction("connect");
        scco.setCustomData("test-custom-data");

        From from = new From();
        boolean isFromInternal = allParams.get("fromInternal").equalsIgnoreCase("true");
        from.setType(isFromInternal ? "internal" : "external");
        from.setNumber(allParams.get("from"));
        from.setAlias(allParams.get("from"));

        scco.setFrom(from);

        To to = new To();
        if (isFromInternal)
            to.setNumber(allParams.get("to"));
        else
            to.setNumber("ontaxi");
        to.setAlias(allParams.get("to"));
        to.setType(isFromInternal ? "external" : "internal");

        scco.setTo(to);

        return new Gson().toJson(new SCCO[] {scco});
    }

}
