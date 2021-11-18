package com.scottapps.isofinder.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Device {

    private byte[] g1;
    private byte[] g2;
    private int pi;

    public Device(String pi) {
        this.init(pi);
    }

    public byte[] getG1() {
        return g1;
    }

    public void setG1(byte[] g1) {
        this.g1 = g1;
    }

    public byte[] getG2() {
        return g2;
    }

    public void setG2(byte[] g2) {
        this.g2 = g2;
    }

    public int getPi() {
        return pi;
    }

    public void setPi(int pi) {
        this.pi = pi;
    }

    private void init(String pi) {
        try {
            this.pi = ByteBuffer.wrap(getSha256Digest(pi)).getInt();
            this.g1 = generateGraph();
            this.g2 = applyPi(g1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] generateAlphabet() {
        final var bytes = new byte[256];
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte) i;
        }
        return bytes;
    }

    public byte[] getSha256Digest(String password) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return hashBytes;
    }

    private byte[] generateGraph() throws Exception {
        final var random = new Random();
        random.setSeed(this.pi);
        final var alphabet = this.generateAlphabet();
        var randBytes = randomizeGraph(alphabet);
        final var set = new HashSet<Byte>();
        for (int i = 0; i < randBytes.length; i++) {
            set.add(randBytes[i]);
        }
        if (set.size() != randBytes.length) {
            throw new Exception("Permuted graph contains duplicates!");
        }
        return Arrays.copyOfRange(randBytes, 0, 32);
    }

    public byte[] randomizeGraph(byte[] bytes) {
        var copy = bytes.clone();
        final var random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            var randomIndexToSwap = random.nextInt(copy.length);
            var tmp = copy[randomIndexToSwap];
            copy[randomIndexToSwap] = copy[i];
            copy[i] = tmp;
        }
        return copy;
    }

    public byte[] applyPi(byte[] graph) {
        final var permutation = graph.clone();
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = (byte) ((permutation[i] + pi) % 256);
        }
        return permutation;
    }

    public byte[] applyInversePi(byte[] graph) {
        final var permutation = graph.clone();
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = (byte) ((permutation[i] - pi) % 256);
        }
        return permutation;
    }

    @Override
    public String toString() {
        return "Device{" +
                "g1=" + Arrays.toString(g1) +
                ", g2=" + Arrays.toString(g2) +
                ", pi=" + pi +
                '}';
    }
}
