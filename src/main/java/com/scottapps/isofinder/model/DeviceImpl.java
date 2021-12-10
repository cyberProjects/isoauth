package com.scottapps.isofinder.model;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

public class DeviceImpl implements Device {

    private Map<Long, Long> pi;
    private Map<Long, Long> piInv;
    private Map<Long, Long> sigma;
    private Map<Long, Long> sigmaInv;
    private List<Long> g1;
    private List<Long> g2;
    private List<Long> h;
    private Long n;
    private Boolean challenge;
    private Long seed;

    public DeviceImpl(Long n) {
        assert n % 2 == 0;
        assert n >= 8;
        this.n = n;
        this.pi = new HashMap<>();
        this.piInv = new HashMap<>();
        this.sigma = new HashMap<>();
        this.sigmaInv = new HashMap<>();
        this.g1 = new ArrayList<>();
        this.g2 = new ArrayList<>();
        this.h = new ArrayList<>();
        this.seed = 1L;
    }

    @Override
    public void initialize() {
        // seeding Random for determinism while testing
        var random = new SecureRandom("SECRET_PASSWORD".getBytes(StandardCharsets.UTF_8));
        for (int i = 0; i < this.n; i++) {
            g1.add(random.nextLong());
        }
        this.loadPiAndPiInv();
        this.generateCommitment();
        System.out.println(this.g1);
        System.out.println(this.g2);
    }

    @Override
    public Map<Long, Long> prove(Boolean challenge) {
        this.challenge = challenge;
        System.out.println("Received Challenge: " + this.challenge);
        if (this.challenge == true) {
            return this.sigmaInv;
        } else {
            var composition = new HashMap<Long, Long>();
            for (int i = 0; i < this.n; i++) {
                composition.put(this.h.get(i), this.pi.get(this.sigmaInv.get(this.h.get(i))));
            }
            return composition;
        }
    }

    @Override
    public boolean verify(List<Long> h) {
        this.h = h;
        System.out.println("Received commitment graph: " + this.h);
        var random = new SecureRandom();
        this.challenge = random.nextBoolean();
        return this.challenge;
    }

    @Override
    public boolean accept(Map<Long, Long> permutation) {
        System.out.println("Received permutation: " + permutation);
        var flag = false;
        var solution = new ArrayList<>();
        for (int i = 0; i < this.n; i++) {
            solution.add(permutation.get(this.h.get(i)));
        }
        if (this.challenge == true) {
            // compare solution to g1
            if (solution.equals(this.g1)) {
                flag = true;
            }
        } else {
            // compare solution to g2, BUT NEED OTHER DEVICES g2
            if (solution.equals(this.g2)) {
                flag = true;
            }
        }
        System.out.println("Permuted graph: " + solution);
        return flag;
    }

    private List<Long> randomizeGraph(List<Long> graph) {
        var copy = new ArrayList<Long>(graph);
        Collections.shuffle(copy);
        return copy;
    }

    private void loadPiAndPiInv() {
        this.g2 = this.randomizeGraph(this.g1);
        for (int i = 0; i < this.n; i++) {
            var v1 = this.g1.get(i);
            var v2 = this.g2.get(i);
            this.pi.put(v1, v2);
            this.piInv.put(v2, v1);
        }
    }

    private void generateCommitment() {
        this.h = this.randomizeGraph(this.g1);
        for (int i = 0; i < this.n; i++) {
            var v1 = this.g1.get(i);
            var v2 = this.h.get(i);
            this.sigma.put(v1, v2);
            this.sigmaInv.put(v2, v1);
        }
    }

    private void applyPi() {

    }

    public Map<Long, Long> getPi() {
        return pi;
    }

    public void setPi(Map<Long, Long> pi) {
        this.pi = pi;
    }

    public Map<Long, Long> getPiInv() {
        return piInv;
    }

    public void setPiInv(Map<Long, Long> piInv) {
        this.piInv = piInv;
    }

    public Map<Long, Long> getSigma() {
        return sigma;
    }

    public void setSigma(Map<Long, Long> sigma) {
        this.sigma = sigma;
    }

    public Map<Long, Long> getSigmaInv() {
        return sigmaInv;
    }

    public void setSigmaInv(Map<Long, Long> sigmaInv) {
        this.sigmaInv = sigmaInv;
    }

    public List<Long> getG1() {
        return g1;
    }

    public void setG1(List<Long> g1) {
        this.g1 = g1;
    }

    public List<Long> getG2() {
        return g2;
    }

    public void setG2(List<Long> g2) {
        this.g2 = g2;
    }

    public List<Long> getH() {
        return h;
    }

    public void setH(List<Long> h) {
        this.h = h;
    }

    public Long getN() {
        return n;
    }

    public void setN(Long n) {
        this.n = n;
    }

    public Boolean getChallenge() {
        return challenge;
    }

    public void setChallenge(Boolean challenge) {
        this.challenge = challenge;
    }
}
