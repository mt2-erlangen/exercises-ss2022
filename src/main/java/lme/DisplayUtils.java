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
import mt.Image;
import mt.Volume;
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

    public static void showImage(float[] buffer, String title, int width) {
        showImage(buffer, title, width, new float[]{0, 0}, 1.0, false);
    }

    public static void showImage(float[] buffer, String title, long width, float[] origin, double spacing, boolean replaceWindowWithSameName) {
        FloatProcessor processor = new FloatProcessor((int) width, buffer.length / (int) width, buffer);
        ImagePlus plus = new ImagePlus();
        if (replaceWindowWithSameName && ij.WindowManager.getImage(title) != null) {
            plus = ij.WindowManager.getImage(title);
        }
        plus.setProcessor(title, processor);

        Calibration calibration = new Calibration();
        calibration.setUnit("mm");
        calibration.xOrigin = origin[0];
        calibration.yOrigin = origin[1];
        calibration.pixelHeight = spacing;
        calibration.pixelWidth = spacing;

        plus.setCalibration(calibration);
        plus.show();
        ij.IJ.run("Tile");
    }

    public  static void FFT(float[] buffer, String title, long width, float[] origin, double spacing){
        FloatProcessor processor = new FloatProcessor((int) width, buffer.length / (int) width, buffer);
        ImagePlus plus = new ImagePlus();
        plus.setProcessor(title, processor);

        Calibration calibration = new Calibration();
        calibration.setUnit("mm");
        calibration.xOrigin = origin[0];
        calibration.yOrigin = origin[1];
        calibration.pixelHeight = spacing;
        calibration.pixelWidth = spacing;
        plus.setCalibration(calibration);

        FFT transform = new FFT();
        ImagePlus fft = transform.forward(plus);

        fft.show();
        ij.IJ.run("Tile");

    }

    public static mt.Image plusToImage(ImagePlus plus, String title) {
        ImageConverter converter = new ImageConverter(plus);
        converter.convertToGray32();
        FloatProcessor processor = (FloatProcessor) plus.getProcessor();
        return floatProcessorToImage(processor, title);
    }

    public static mt.Image floatProcessorToImage(FloatProcessor processor, String title) {
        Image image = new mt.Image(processor.getWidth(), processor.getHeight(), title);
        image.setBuffer((float[]) processor.getPixels());
        return image;
    }

    public static mt.Image openImageFromInternet(String url, String fileType) {
        if (new File(url).exists()) {
            return openImage(url);
        }
        try {
            Path tempFile = Files.createTempFile("mt2", fileType);
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile());
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            ImagePlus plus = ij.IJ.openImage(tempFile.toString());
            return plusToImage(plus, tempFile.toFile().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static mt.Image openImage(String path) {
        ImagePlus plus = ij.IJ.openImage(path);
        return plusToImage(plus, (new File(path)).getName());
    }

    public static void showSegmentedCells(mt.Image original, mt.Image segmented, boolean differentColors) {
        ArrayImg<FloatType, FloatArray> originalImg = toArrayImg(original);
        ArrayImg<FloatType, FloatArray> segmentedImg = toArrayImg(segmented);
        RandomAccessibleInterval<UnsignedByteType> segmentedBytes = RealTypeConverters.convert(segmentedImg, new UnsignedByteType());
        ArrayImg<IntType, IntArray> labeling = ArrayImgs.ints(original.width(), original.height());

        if (randomColors == null) {
            randomColors = new ArrayList<Integer>(10000);
            IntStream.range(0, 10000)
                    .forEach(i -> randomColors.add(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)));
        }

        IterableInterval<ARGBType> argb = Converters.convert((IterableInterval<FloatType>) originalImg,
                new RealARGBConverter<>(0, original.max()), new ARGBType());
        net.imglib2.algorithm.labeling.ConnectedComponents.labelAllConnectedComponents(segmentedBytes, labeling,
                ConnectedComponents.StructuringElement.EIGHT_CONNECTED);

        ColorProcessor processor = new ij.process.ColorProcessor(original.width(), original.height());

        ArrayCursor<IntType> cursor = labeling.cursor();
        Cursor<ARGBType> argbCursor = argb.cursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            argbCursor.fwd();
            int value = cursor.get().get();
            int x = cursor.getIntPosition(0);
            int y = cursor.getIntPosition(1);
            if (segmented.atIndex(x,y) > 0.5f) {
                if (differentColors) {
                    processor.set(x, y, mixRGB(randomColors.get(value % randomColors.size()), argbCursor.get().get(), 0.3f));
                } else {
                    processor.set(x, y, mixRGB(0xCC0000, argbCursor.get().get(), 0.3f));
                }
            } else {
                processor.set(x, y, argbCursor.get().get());
            }
        }

        ImagePlus plus = new ImagePlus();
        if(differentColors) {
            plus.setTitle("Segmentation on Image");
        } else {
            plus.setTitle("Segmentations on Image");
        }
        plus.setProcessor(processor);
        plus.show();
    }

    public static ArrayImg<FloatType, FloatArray> toArrayImg(mt.Image image) {
        return ArrayImgs.floats(image.buffer(), image.width(), image.height());
    }

    private static int lerp256(int a, int b, float f) {
        return (int) Math.min(a + f * (b - a), 255);
    }

    static int mixRGB(int a, int b, float alpha) {
        return lerp256((a >> 0) & ((1 << 8) - 1), (b >> 0) & ((1 << 8) - 1), alpha)
                + (lerp256((a >> 8) & ((1 << 8) - 1), (b >> 8) & ((1 << 8) - 1), alpha) << 8)
                + (lerp256((a >> 16) & ((1 << 8) - 1), (b >> 16) & ((1 << 8) - 1), alpha) << 16);
    }
    
    
    public static void showVolume(mt.Volume volume) {
        ImagePlus plus = new ImagePlus();
//        if (ij.WindowManager.getImage(volume.name()) != null) {
//            plus = ij.WindowManager.getImage(volume.name());
//        }
        ImageStack stack = new ij.ImageStack();
        for (int z = 0; z < volume.depth(); ++z) {

            if(volume.kspace()){
                ImageProcessor processor = volume.getkSlice(z).getProcessor();
                stack.addSlice(processor);
            }else{
                FloatProcessor processor = new FloatProcessor(volume.width(), volume.height(), volume.getSlice(z).buffer());
                stack.addSlice(processor);
            }

        }
        plus.setStack(volume.name(), stack);
        Calibration calibration = new Calibration();
        calibration.setUnit("mm");
        calibration.xOrigin = volume.origin()[0];
        calibration.yOrigin = volume.origin()[1];
        calibration.zOrigin = volume.origin()[2];
        calibration.pixelHeight = volume.spacing();
        calibration.pixelWidth = volume.spacing();
        calibration.pixelDepth = volume.spacing();

        plus.setCalibration(calibration);
        //ImageConverter ic = new ImageConverter(plus);
        //ic.convertToGray8();
        plus.show();
        ij.IJ.run("Tile");
    }
    public static mt.Volume openVolume(String path) {
        ImagePlus plus = ij.IJ.openImage(path);
        return plusToVolume(plus, (new File(path)).getName());
    }

    public static mt.Volume plusToVolume(ImagePlus plus, String title) {
        ImageConverter converter = new ImageConverter(plus);
        converter.convertToGray32();
        Volume volume = new mt.Volume(plus.getStack().getWidth(), plus.getStack().getHeight(), plus.getStackSize(), title);
        ImageStack stack = plus.getStack();

        IntStream.range(0, plus.getStackSize()).forEach(z -> {
            FloatProcessor processor = (FloatProcessor) stack.getProcessor(1 + z);
            volume.setSlice(z, floatProcessorToImage(processor, title));
        });
        return volume;
    }

    public static ImagePlus fft(float[] buffer, String title, long width, float[] origin, double spacing){
        FloatProcessor processor = new FloatProcessor((int) width, buffer.length / (int) width, buffer);
        ImagePlus plus = new ImagePlus();
        plus.setProcessor(title, processor);

        Calibration calibration = new Calibration();
        calibration.setUnit("mm");
        calibration.xOrigin = origin[0];
        calibration.yOrigin = origin[1];
        calibration.pixelHeight = spacing;
        calibration.pixelWidth = spacing;
        plus.setCalibration(calibration);

        FFT transform = new FFT();
        ImagePlus fft = transform.forward(plus);

        return fft;
    }

    public static Image inversefft(ImagePlus image){
        FFT transform = new FFT();
        ImagePlus inversefft = transform.inverse(image);

        return plusToImage(inversefft, "Inverse FFT");

    }


    public static void fftfilter(ImagePlus image, Image mask){
        var plusmask = ImageToPlus(mask);
        FloatProcessor processor = new FloatProcessor((int) mask.width(),
                mask.buffer().length / (int) mask.width(), mask.buffer());

        FFT.filter(image,plusmask);

    }

    public static ImagePlus ImageToPlus(Image image){
        FloatProcessor processor = new FloatProcessor((int) image.width(),
                image.buffer().length / (int) image.width(), image.buffer());
        ImagePlus plus = new ImagePlus();
        plus.setProcessor(image.name(), processor);

        Calibration calibration = new Calibration();
        calibration.setUnit("mm");
        calibration.xOrigin = image.origin()[0];
        calibration.yOrigin = image.origin()[1];
        calibration.pixelHeight = image.spacing();
        calibration.pixelWidth = image.spacing();
        plus.setCalibration(calibration);

        return plus;
    }


}


