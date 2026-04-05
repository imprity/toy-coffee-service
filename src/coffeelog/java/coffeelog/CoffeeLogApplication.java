package coffeelog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CoffeeLogApplication {
    @RestController
    @Slf4j
    public static class SimpleController {
        @PostMapping("/api/coffee-log")
        public void logBody(@RequestBody String toLog) {
            log.info(toLog);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(CoffeeLogApplication.class, args);
    }
}
