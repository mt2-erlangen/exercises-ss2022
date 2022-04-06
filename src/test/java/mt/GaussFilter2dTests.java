package mt;

import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class GaussFilter2dTests {

    @Test
    void testGaussFiltering() {
        Image smallImage = new Image(16, 16, "Small image");
        smallImage.addNoise(0.f, 1.0f);
        smallImage.show();

        double[] sigma = new double[]{ 2., 2.};

        LinearImageFilter filter = new GaussFilter2d(11, (float) sigma[0]);
        filter.show();

        Image output = filter.apply(smallImage);
        output.show();


        float[] copy = Arrays.copyOf(smallImage.buffer(), smallImage.size());
        ArrayImg<FloatType, FloatArray> reference = ArrayImgs.floats(copy, smallImage.width(), smallImage.height());
        RandomAccessible<FloatType> infiniteImg = Views.extendZero(reference);

        Gauss3.gauss( sigma, infiniteImg, reference );
        ImageJFunctions.show(reference);

        boolean interactiveExecution = false;
        if (interactiveExecution) {
            for(;;);
        } else {
            Assertions.assertArrayEquals(copy, output.buffer(), 0.01f);
        }
    }
}