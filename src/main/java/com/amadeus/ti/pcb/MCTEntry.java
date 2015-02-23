package com.amadeus.ti.pcb;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple25;

public class MCTEntry extends Tuple25<String, String, String, String, String, String,
        String, String, String, String, String, String,
        String, String, String, Integer, Integer, Integer,
        Integer, String, String, String, String, Tuple2<Long, Long>, Integer> {

    public MCTEntry() {
        super();
        this.f23 = new Tuple2<Long, Long>(0L, Long.MAX_VALUE);
    }

    public MCTEntry(String arrival, String stat, String departure,
                    String arrivalCarrier, String departureCarrier, String arrivalAircraft, String departureAircraft, String arrivalTerminal , String departureTerminal,
                    String previousCountry, String previousCity, String previousAP, String nextCountry, String nextCity, String nextAP,
                    Integer inFlightNo, Integer inFlightNoEOR, Integer outFlightNo, Integer outFlightNoEOR,
                    String previousState, String nextState, String previousRegion, String nextRegion,
                    Long validFrom, Long validUntil,
                    Integer mct) {
        this.f0 = arrival;
        this.f1 = stat;
        this.f2 = departure;
        this.f3 = arrivalCarrier;
        this.f4 = departureCarrier;
        this.f5 = arrivalAircraft;
        this.f6 = departureAircraft;
        this.f7 = arrivalTerminal;
        this.f8 = departureTerminal;
        this.f9 = previousCountry;
        this.f10 = previousCity;
        this.f11 = previousAP;
        this.f12 = nextCountry;
        this.f13 = nextCity;
        this.f14 = nextAP;
        this.f15 = inFlightNo;
        this.f16 = inFlightNoEOR;
        this.f17 = outFlightNo;
        this.f18 = outFlightNoEOR;
        this.f19 = previousState;
        this.f20 = nextState;
        this.f21 = previousRegion;
        this.f22 = nextRegion;
        this.f23 = new Tuple2<Long, Long>(validFrom, validUntil);
        this.f24 = mct;
    }


    public String getArrival() {
        return f0;
    }

    public String getStat() {
        return f1;
    }

    public String getDeparture() {
        return f2;
    }

    public String getArrCarrier() {
        return f3;
    }

    public String getDepCarrier() {
        return f4;
    }

    public String getArrAircraft() {
        return f5;
    }

    public String getDepAircraft() {
        return f6;
    }

    public String getArrTerminal() {
        return f7;
    }

    public String getDepTerminal() {
        return f8;
    }

    public String getPrevCountry() {
        return f9;
    }

    public String getPrevCity() {
        return f10;
    }

    public String getPrevAirport() {
        return f11;
    }

    public String getNextCountry() {
        return f12;
    }

    public String getNextCity() {
        return f13;
    }

    public String getNextAirport() {
        return f14;
    }

    public Integer getInFlightNumber() {
        return f15;
    }

    public Integer getInFlightEOR() {
        return f16;
    }

    public Integer getOutFlightNumber() {
        return f17;
    }

    public Integer getOutFlightEOR() {
        return f18;
    }

    public String getPrevState() {
        return f19;
    }

    public String getNextState() {
        return f20;
    }

    public String getPrevRegion() {
        return f21;
    }

    public String getNextRegion() {
        return f22;
    }

    public Long getValidFrom() {
        return f23.f0;
    }

    public Long getValidUntil() {
        return f23.f1;
    }

    public Integer getMCT() {
        return f24;
    }

}
