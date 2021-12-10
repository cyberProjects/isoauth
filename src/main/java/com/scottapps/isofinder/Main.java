package com.scottapps.isofinder;

import com.scottapps.isofinder.model.DeviceImpl;

public class Main {

    public static void main(String[] args) {
        // SIMULATION 1
        var d1 = new DeviceImpl(8L);
        d1.initialize();
        var d2 = new DeviceImpl(8L);
        d2.initialize();
        // Because the devices are not bootstrapped with
        // previously generated graphs, in this simulation
        // device 2 doesn't know devices 1's public graphs.
        // as such, most testing was done by manually observing
        // graphs after the permutation was applied on the verifier's side
        var result = d2.accept(d1.prove(d2.verify(d1.getH())));
        System.out.println("Result of attestation round: " + result);
    }
}
