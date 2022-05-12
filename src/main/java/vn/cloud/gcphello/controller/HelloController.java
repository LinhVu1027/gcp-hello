package vn.cloud.gcphello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping
public class HelloController {

    @GetMapping
    public String hello2() {
        return "Hello";
    }

    @GetMapping(value = "/hi")
    public String hi2() {
        return "Hi";
    }

    @GetMapping("/redirect")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("hi");
    }
}
