package vn.cloud.gcphello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping(value = "/work")
@Slf4j
public class WorkController {
    Random r = new Random();

    public void meeting() {
        try {
            log.info("meeting...");
            // Delay for random number of milliseconds.
            Thread.sleep(r.nextInt(500));
        } catch (InterruptedException e) {
        }
    }

    @GetMapping("/")
    public String work() {
        // What is work? Meetings!
        // When you hit this URL, it'll call meetings() 5 times.
        // Each time will have a random delay.
        log.info("starting to work");
        for (int i = 0; i < 5; i++) {
            this.meeting();
        }
        log.info("finished!");
        return "finished work!";
    }
}