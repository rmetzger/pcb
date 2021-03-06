package de.tuberlin.dima.ti.pcb;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.java.tuple.Tuple8;

/**
 * add geographical information to Flight class
 */
public class OriginCoordinateJoiner implements JoinFunction<Flight, Tuple8<String, String, String, String, String, Double, Double, String>, Flight> {

    @Override
    public Flight join(Flight first, Tuple8<String, String, String, String, String, Double, Double, String> second)
            throws Exception {
        if (!second.f1.isEmpty()) {
            first.setOriginCity(second.f1);
        } else {
            // use airport code as city code if city code unavailable
            first.setOriginCity(second.f0);
        }
        first.setOriginState(second.f2);
        first.setOriginCountry(second.f3);
        first.setOriginRegion(second.f4);
        first.setOriginLatitude(second.f5);
        first.setOriginLongitude(second.f6);
        first.setOriginICAO(second.f7);

        if (!second.f1.isEmpty()) {
            first.setLastCity(second.f1);
        } else {
            // use airport code as city code if city code unavailable
            first.setLastCity(second.f0);
        }
        first.setLastState(second.f2);
        first.setLastCountry(second.f3);
        first.setLastRegion(second.f4);
        first.setLastLatitude(second.f5);
        first.setLastLongitude(second.f6);
        first.setLastICAO(second.f7);

        return first;
    }
}