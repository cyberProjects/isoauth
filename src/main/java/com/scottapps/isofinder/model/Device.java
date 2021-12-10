package com.scottapps.isofinder.model;

import java.util.List;
import java.util.Map;

public interface Device {

    void initialize();

    Map<Long, Long> prove(Boolean challenge);

    boolean verify(List<Long> h);

    boolean accept(Map<Long, Long> permutation);
}
