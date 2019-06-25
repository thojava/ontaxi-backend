package vn.ontaxi.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/stringee")
@PropertySource("classpath:application.properties")
public class StringeeController {

    @Value("${stringee.sid}")
    private String strinngeeSid;

    @Value("${stringee.key_secret}")
    private String strinngeeKeySecret;


}
