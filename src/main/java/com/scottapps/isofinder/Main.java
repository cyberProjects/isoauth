package com.scottapps.isofinder;

import com.scottapps.isofinder.model.Device;

public class Main {

    public static void main(String[] args) {
        var device1 = new Device("SomeSecret2");
        System.out.println(Device.bytesToHex(device1.getG1()));
        System.out.println(Device.bytesToHex(device1.getG2()));
        System.out.println(device1.getPi());
        System.out.println(Device.bytesToHex(device1.applyInversePi(device1.getG2())));
    }
}
