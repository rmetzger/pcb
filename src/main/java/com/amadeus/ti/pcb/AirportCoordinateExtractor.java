package com.amadeus.ti.pcb;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple7;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

public class AirportCoordinateExtractor implements
        FlatMapFunction<String, Tuple7<String, String, String, String, String, Double, Double>> {

    private static final String DELIM = "\\^";
    private static final String HEADER = "#";

    private String[] tmp = null;
    private String from = null;
    private String until = null;

    private final static ArrayList<String> countriesWithStates = new ArrayList<String>() {{
        add("AR");
        add("AU");
        add("BR");
        add("CA");
        add("US");
    }};

    @Override
    public void flatMap(String value, Collector<Tuple7<String, String, String, String, String, Double, Double>> out) throws Exception {
        tmp = value.split(DELIM);
        if (tmp[0].trim().startsWith(HEADER)) {
            // header
            return;
        }
        if (!tmp[41].trim().equals("A")) {
            // not an airport
            return;
        }
        from = tmp[13].trim();
        until = tmp[14].trim();
        if (!until.equals("")) {
            // expired
            return;
        }
        Double latitude;
        Double longitude;
        try {
            latitude = new Double(Double.parseDouble(tmp[8].trim()));
            longitude = new Double(Double.parseDouble(tmp[9].trim()));
        } catch (Exception e) {
            // invalid coordinates
            return;
        }
        String iataCode = tmp[0].trim();
        String cityCode = tmp[36].trim();
        String countryCode = tmp[16].trim();
        String stateCode;
        if (countriesWithStates.contains(countryCode)) {
            stateCode = tmp[40].trim();
        } else {
            stateCode = "";
        }
        out.collect(new Tuple7<String, String, String, String, String, Double, Double>(iataCode, cityCode, stateCode, countryCode, "", latitude, longitude));
    }
}