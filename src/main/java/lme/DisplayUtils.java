/*
 * DisplayUtils.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

package lme;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Plot;
import ij.measure.Calibration;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageConverter;
import ij.plugin.FFT;
import ij.process.ImageProcessor;
//import mt.Image;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.converter.Converters;
import net.imglib2.converter.RealARGBConverter;
import net.imglib2.converter.RealTypeConverters;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


public class DisplayUtils {

    static List<Integer> randomColors;


    public static void showArray(float[] yValues, String title, double origin, double spacing) {
        showArray(yValues, new Plot(title, "X", "Y"), origin, spacing);
    }

    public static void showArray(float[] yValues, Plot plot, double origin, double spacing) {
        double[] yValuesDouble = new double[yValues.length];
        double[] xValues = new double[yValues.length];

        for (int i = 0; i < xValues.length; i++) {
            xValues[i] = origin + i * spacing;
            yValuesDouble[i] = (double) yValues[i];
        }

        // plot.setColor("red");
        plot.add("lines", xValues, yValuesDouble);
        plot.show();
    }




}


