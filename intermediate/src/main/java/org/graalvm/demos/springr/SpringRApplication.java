package org.graalvm.demos.springr;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Controller
@SpringBootApplication
public class SpringRApplication {

    @Value(value = "classpath:plot.R")
    private Resource rSource;

    @Autowired
    private BiFunction<DataHolder, DataHolder, String> plotFunction;

    @Bean
    BiFunction<DataHolder, DataHolder, String> getSource(@Autowired Context ctx)
            throws IOException {
        Source source =
                Source.newBuilder("R", rSource.getURL()).build();
        // we can interpret R code as a BiFunction
        return ctx.eval(source).as(BiFunction.class);
    }

    @RequestMapping(value = "/", produces = "image/svg+xml")
    public ResponseEntity<String> forecast() {
        String svg = "";
        List<Double> forecasts = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 48; i++) {
            forecasts.add(22.0 + i);
            timestamps.add(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
            now = now.plusHours(1);
        }
        // the Bean initialized earlier defines a BiFunction which maps two lists to a String
        // this is where the actual interop between Java and R happens
        svg = plotFunction.apply(new DataHolder<>(forecasts), new DataHolder<>(timestamps));
        return new ResponseEntity<String>(
                svg,
                HttpStatus.OK);
    }

    @Bean
    public Context getGraalVMContext() {
        return Context.newBuilder().allowAllAccess(true).build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringRApplication.class, args);
    }

}
