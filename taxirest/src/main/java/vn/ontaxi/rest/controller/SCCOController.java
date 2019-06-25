package vn.ontaxi.rest.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ontaxi.common.jpa.entity.User;
import vn.ontaxi.common.jpa.repository.UserRepository;
import vn.ontaxi.rest.payload.stringee.From;
import vn.ontaxi.rest.payload.stringee.SCCO;
import vn.ontaxi.rest.payload.stringee.To;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/scco")
public class SCCOController {

    @Autowired
    private UserRepository userRepository;

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
        //if (isFromInternal)
        //    to.setNumber(allParams.get("to"));
        //else

        List<User> users = userRepository.findAll();
        to.setNumber(users.stream().map(User::getUserName).collect(Collectors.joining(", ")));
        to.setAlias(allParams.get("to"));
        to.setType(isFromInternal ? "external" : "internal");

        scco.setTo(to);

        return new Gson().toJson(new SCCO[] {scco});
    }

}
