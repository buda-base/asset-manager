package io.bdrc.assetmanager.employee;

import io.bdrc.assetmanager.Greeting.GreetingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/*
This code uses Spring @RestController annotation, which marks the class as a controller where every method returns a domain object instead of a view. It is shorthand for including both
@Controller and @ResponseBody
 */

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public GreetingModel greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new GreetingModel(counter.incrementAndGet(), String.format(template, name));
    }
}