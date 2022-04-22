package exercises;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageConverter;
import mt.Image;
import mt.ImageTransformer;
import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.command.Previewable;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.widget.NumberWidget;

/**
 * An ImageJ2 command with preview capabilities.
 */
@Plugin(type = Command.class, menuPath = "Tutorials>Command with Preview")
public class Exercise05 implements Command, Previewable {
    // -- Parameters --

    @Parameter(initializer = "initImagePlus")
    public ImagePlus imp;

    @Parameter(style = NumberWidget.SLIDER_STYLE,
            min = "-200", max = "200", stepSize = "0.5")
    private float shiftX;

    @Parameter(style = NumberWidget.SLIDER_STYLE,
            min = "-200", max = "200", stepSize = "0.5")
    private float shiftY;

    @Parameter(style = NumberWidget.SLIDER_STYLE,
            min = "0.1", max = "5", stepSize = "0.1")
    private float scale;

    @Parameter(style = NumberWidget.SLIDER_STYLE,
            min = "-6", max = "6", stepSize = "0.1")
    private float rotation;

    // -- Other fields --

    /**
     * The original title of the image.
     */
    private FloatProcessor originalProcessor;
    private ImageTransformer transformer = new ImageTransformer();

    // -- Command methods --

    @Override
    public void run() {
        transformer.shiftX = shiftX;
        transformer.shiftY = shiftY;
        transformer.scale = scale;
        transformer.rotation = rotation;

        Image input = lme.DisplayUtils.floatProcessorToImage(originalProcessor, "Original");
        Image output = lme.DisplayUtils.plusToImage(imp, "Transformed");
        input.centerOrigin();
        output.centerOrigin();
        transformer.apply(input, output);

        imp.updateAndDraw();
    }

    // -- Previewable methods --

    @Override
    public void preview() {
        run();
    }

    @Override
    public void cancel() {
        // Set the image's title back to the original value.
        imp.setProcessor(originalProcessor);
    }

    protected void initImagePlus() {
        ImageConverter converter = new ImageConverter(imp);
        converter.convertToGray32();
        originalProcessor = (FloatProcessor) imp.getProcessor();
        imp.setProcessor(new FloatProcessor(originalProcessor.getWidth(), originalProcessor.getHeight()));
        imp.setDisplayRange(0, 255);
    }

    // -- Main method --

    /**
     * Tests our command.
     */
    public static void main(final String... args) throws Exception {
        // Launch ImageJ as usual.
        final ImageJ ij = new ImageJ();
        PluginInfo<Command> rampInfo = new PluginInfo<Command>(Exercise05.class, Command.class);
        ij.plugin().addPlugin(rampInfo);
        ij.launch(args);

        Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/x_ray.png", ".png");
        image.show();

        // Launch the "CommandWithPreview" command.
        ij.command().run(Exercise05.class, true);
    }

}
