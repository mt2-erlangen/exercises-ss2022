package mt;/*
 * mt.SignalTests.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class ImageTests {
    @Test
    void testAtIndex() {

        Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/pacemaker.png", ".png");

        Image testImage = new Image(image.width(),image.height(), "Input",image.buffer);

        for(int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                Assertions.assertEquals(image.buffer[(y- image.minIndexY)*image.width + (x- image.minIndexX)],
                        testImage.atIndex(x,y));
            }
        }
        Assertions.assertEquals(testImage.maxIndexX(), 549);
        Assertions.assertEquals(testImage.maxIndexY(), 487);
        Assertions.assertEquals(0.f, testImage.atIndex(-4,-4));
        Assertions.assertEquals(0.f, testImage.atIndex(600,600));
    }

}