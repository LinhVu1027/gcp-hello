package vn.cloud.gcphello.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.cloud.gcphello.entity.Record;
import vn.cloud.gcphello.repository.RecordRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class HelloController {

    private final RecordRepository recordRepository;

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

    @GetMapping("/records")
    public void saveRecord() {
        Record record = new Record(null,"a", "b");
        this.recordRepository.save(record);
    }
}
