package mt;

import net.imglib2.algorithm.convolution.Convolution;
import net.imglib2.algorithm.convolution.kernel.Kernel1D;
import net.imglib2.algorithm.convolution.kernel.SeparableKernelConvolution;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;


public class LinearImageFilterTests {

    @Test
    void testCopyFilter() {
        boolean interactiveExecution = false;
        (new ij.ImageJ()).exitWhenQuitting(true);

        Image smallImage = new Image(8, 8, "Small image");
        smallImage.addNoise(0.f, 1.0f);
        smallImage.show();

        LinearImageFilter copyFilter = new LinearImageFilter(3, 3, "Copy Filter");
        copyFilter.setBuffer(new float[]{0, 0, 0,
                0, 1, 0,
                0, 0, 0});

        Image output = copyFilter.apply(smallImage);
        output.show();

        if (interactiveExecution) {
            for (; ; ) ;
        } else {
            Assertions.assertArrayEquals(smallImage.buffer(), output.buffer());
        }
    }

    @Test
    void testShiftFilter() {
        boolean interactiveExecution = false;
        (new ij.ImageJ()).exitWhenQuitting(true);

        Image smallImage = new Image(8, 8, "Small image");
        smallImage.addNoise(0.f, 1.0f);
        smallImage.show();

        LinearImageFilter shiftFilter = new LinearImageFilter(3, 3, "Shift Filter");
        shiftFilter.setBuffer(new float[]{1, 0, 0,
                0, 0, 0,
                0, 0, 0});

        Image output = shiftFilter.apply(smallImage);
        output.show();

        if (interactiveExecution) {
            for (; ; ) ;
        } else {
            Assertions.assertEquals(output.atIndex(output.maxIndexX(), output.maxIndexY()), 0.f, 0.001f);
        }
    }

    @Test
    void testMinMaxIndex() {
        LinearImageFilter filter = new LinearImageFilter(3, 9, "Foo");
        Assertions.assertEquals(-1, filter.minIndexX());
        Assertions.assertEquals(-4, filter.minIndexY());

        Assertions.assertEquals(+1, filter.maxIndexX());
        Assertions.assertEquals(+4, filter.maxIndexY());
    }

    @Test
    void testRandomFilter() {
        boolean interactiveExecution = false;
        (new ij.ImageJ()).exitWhenQuitting(true);

        Image smallImage = new Image(8, 8, "Small image");
        smallImage.addNoise(0.f, 1.0f);
        smallImage.show();

        float[] kernelArray = new float[]{1, 2, 3};

        LinearImageFilter filter = new LinearImageFilter(1, kernelArray.length, "Filter");
        filter.show();
        filter.setBuffer(kernelArray);
        LinearImageFilter filter2 = new LinearImageFilter(kernelArray.length, 1, "Filter");
        filter2.show();
        filter2.setBuffer(kernelArray);

        Image output = filter2.apply(filter.apply(smallImage));

        Kernel1D kernel = Kernel1D.asymmetric(IntStream.range(0, kernelArray.length).mapToDouble(i -> kernelArray[i]).toArray(), kernelArray.length / 2);
        Convolution<NumericType<?>> convolution = SeparableKernelConvolution.convolution(kernel, kernel);

        ExtendedRandomAccessibleInterval<FloatType, ArrayImg<FloatType, FloatArray>> inputImage = Views.extendZero(ArrayImgs.floats(smallImage.buffer(), smallImage.height(), smallImage.width()));
        ArrayImg<FloatType, FloatArray> referenceImage = ArrayImgs.floats(smallImage.height(), smallImage.width());
        convolution.process(inputImage, referenceImage);
        ImageJFunctions.show(referenceImage);
        output.show();

        if (interactiveExecution) {
            for (; ; ) ;
        } else {
            IntStream.range(0, output.height()).forEach(y ->
                    IntStream.range(0, output.height()).forEach(x ->
                            Assertions.assertEquals(output.atIndex(x, y), referenceImage.getAt(x, y).get(), 0.01f)
                    ));
        }

    }

    @Test
    void testShiftBackAndForthFilter() {
        Image smallImage = new Image(8, 8, "Small image");
        smallImage.addNoise(0.f, 1.0f);
        smallImage.show();

        LinearImageFilter shiftFilter = new LinearImageFilter(3, 3, "Shift Filter");
        shiftFilter.setBuffer(new float[]{1, 0, 0,
                0, 0, 0,
                0, 0, 0});
        LinearImageFilter shiftFilter2 = new LinearImageFilter(3, 3, "Shift Filter2");
        shiftFilter2.setBuffer(new float[]{0, 0, 0,
                0, 0, 0,
                0, 0, 1});

        Image result1 = shiftFilter.apply(shiftFilter2.apply(shiftFilter2.apply(shiftFilter.apply(smallImage))));
        Image result2 = shiftFilter2.apply(shiftFilter.apply(shiftFilter.apply(shiftFilter2.apply(smallImage))));

        result1.show();
        result2.show();


        boolean interactiveExecution = false;
        if (interactiveExecution) {
            for (; ; ) ;
        } else {
            Assertions.assertArrayEquals(result1.buffer(), result2.buffer());
        }
    }

}
