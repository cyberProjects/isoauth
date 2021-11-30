package com.scottapps.isofinder;

import com.scottapps.isofinder.model.DeviceImpl;

public class Main {

    private static String sharedKey = "SomeSharedSecret";

    public static void main(String[] args) {
        // SIMULATION 1: Preshared key for Random seed
        var d1 = new DeviceImpl(100L);
        d1.initialize();
        var d2 = new DeviceImpl(100L);
        d2.initialize();
        var result = d2.accept(d1.prove(d2.verify(d1.getH())));
        System.out.println("Result of attestation round: " + result);
    }
}
