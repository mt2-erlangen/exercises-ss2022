package lme;

import net.imglib2.algorithm.convolution.kernel.Kernel1D;
import net.imglib2.algorithm.convolution.kernel.KernelConvolverFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.view.Views;

import java.util.stream.IntStream;

public class Algorithms {

    public static mt.Signal convolution1d(mt.Signal input, mt.LinearFilter filter) {
        double[] kernelDoubleArray = IntStream.range(0, filter.buffer().length).mapToDouble(i -> filter.buffer()[i]).toArray();
        Kernel1D kernel = net.imglib2.algorithm.convolution.kernel.Kernel1D.centralAsymmetric(kernelDoubleArray);

        float[] inputPadded = new float[input.size() + kernel.size() - 1];
        IntStream.range(0, input.size()).forEach(i -> inputPadded[-(int)kernel.min() + i] = input.buffer()[i]);
        float[] output = new float[input.size()];

        // Now we have a problem factory...
        Runnable convolution = new KernelConvolverFactory(kernel).getConvolver(
                Views.extendZero(ArrayImgs.floats(inputPadded, inputPadded.length)).randomAccess(),
                ArrayImgs.floats(output, output.length).randomAccess(),
                0,
                input.size());
        convolution.run();

        return new mt.Signal(output, input.name() + " filtered with reference implementation.");
    }
}