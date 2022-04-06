package mt;/*
 * mt.SignalTests.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import mt.LinearFilter;
import mt.Signal;

public class SignalTests {
    @Test
    void testAtIndex() {

        float[] kernelArray = new float[] { 0, 1, 2, 0.5f, 0.2f };
        Signal testSignal = new Signal(kernelArray, "Input");

        IntStream.range(0, kernelArray.length)
                .forEach(i -> Assertions.assertEquals(kernelArray[i], testSignal.atIndex(i)));

        Assertions.assertEquals(testSignal.size(), 5);
        Assertions.assertEquals(testSignal.maxIndex(), 4);
        Assertions.assertEquals(0.f, testSignal.atIndex(-4));
        Assertions.assertEquals(0.f, testSignal.atIndex(100));
    }

}