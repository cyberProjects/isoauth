package com.scottapps.isofinder.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class DeviceOld {

    private byte[] g1;
    private byte[] g2;
    private byte[] pi;
    private byte[] h;
    private byte[] sigma;

    public DeviceOld(String pi) {
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

    public byte[] getPi() {
        return pi;
    }

    public void setPi(byte[] pi) {
        this.pi = pi;
    }

    public byte[] getH() {
        return h;
    }

    public void setH(byte[] h) {
        this.h = h;
    }

    public byte[] getSigma() {
        return sigma;
    }

    public void setSigma(byte[] sigma) {
        this.sigma = sigma;
    }

    private void init(String pi) {
        try {
            this.pi = getSha256Digest(pi);
            this.g1 = generateGraph();
            this.g2 = applyIsomorphism(this.g1, this.pi);
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
        final var bytes = new byte[32];
        for (int i = 0; i < 32; i++) {
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
        random.setSeed(ByteBuffer.wrap(this.pi).getInt());
        final var alphabet = this.generateAlphabet();
        System.out.println("Vertex Labels: " + DeviceOld.bytesToHex(alphabet));
        var randBytes = randomizeGraph(alphabet);
        final var set = new HashSet<Byte>();
        for (int i = 0; i < randBytes.length; i++) {
            set.add(randBytes[i]);
        }
        if (set.size() != randBytes.length) {
            throw new Exception("Permuted graph contains duplicates!");
        }
        return randBytes;
    }

    private byte[] randomizeGraph(byte[] bytes) {
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

    private void createIsomorphism(byte[] bytes) {
        var copy = bytes.clone();
        final var random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            var randomIndexToSwap = random.nextInt(copy.length);
            var tmp = copy[randomIndexToSwap];
            copy[randomIndexToSwap] = copy[i];
            copy[i] = tmp;
        }
        this.sigma = copy;
    }

    public byte[] generateCommitment(byte[] bytes) {
        this.h = applyIsomorphism(bytes, this.pi);
        return this.h;
    }

    public byte[] applyIsomorphism(byte[] graph, byte[] isomorphism) {
        final var permutation = new byte[32];
        final var isomorphismAsInteger = ByteBuffer.wrap(isomorphism).getInt();
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = (byte) ((graph[i] + isomorphismAsInteger) % graph.length);
        }
        return permutation;
    }

    public byte[] applyInverseIsomorphism(byte[] graph, byte[] isomorphism) {
        final var permutation = new byte[32];
        final var isomorphismAsInteger = ByteBuffer.wrap(isomorphism).getInt();
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = (byte) ((graph[i] - isomorphismAsInteger) % graph.length);
        }
        return permutation;
    }

    public boolean accept() {
        var string1 = DeviceOld.bytesToHex(this.g1);
        System.out.println("G1: " + string1);
        var decrypted = this.applyInverseIsomorphism(this.h, this.pi);
        var string2 = DeviceOld.bytesToHex(decrypted);
        System.out.println("Apply Isomorphism: " + string2);
        if (string1.compareTo(string2) == 0) {
            return true;
        }
        return false;
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
