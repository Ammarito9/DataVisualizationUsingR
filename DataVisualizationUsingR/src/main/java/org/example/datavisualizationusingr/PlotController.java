package org.example.datavisualizationusingr;

import jakarta.annotation.PostConstruct;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import org.graalvm.polyglot.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class PlotController {
    @org.springframework.beans.factory.annotation.Value(value = "classpath:Plot.R")
    private Resource rCode;
    Value plotData;
    Context context;

    @PostConstruct
    public void initialize() throws IOException {
        context = Context.newBuilder("R").allowAllAccess(true).build();
        plotData = getPlotFunction(context);
    }

    Value getPlotFunction(Context context) throws IOException {
        Source source = Source.newBuilder("R", rCode.getURL()).build();
        return context.eval(source);
    }

    List<Double> values = new ArrayList<Double>();
    int index = 0;
    @RequestMapping(value ="/plot", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> plot() throws IOException {
        byte[] image = Files.readAllBytes(Path.of("/tmp/test-plot.png"));
        return ResponseEntity.ok(image);
    }

    @Scheduled(fixedRate = 1000)
    public void updatePlot() {
        index = index % 100 + 1;

        if (values.size() < 100) {
            values.add(MongoService.getValue(index));
        } else {
            Collections.rotate(values, -1);
        }

        if (values.size() >= 2) {
            plotData.execute(
                    (Object) values.stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray()
            );
        }
    }
}
