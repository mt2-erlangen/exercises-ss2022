package exercises;

import mt.*;

public class Exercise04 {
    public static void main(String[] args) {
        (new ij.ImageJ()).exitWhenQuitting(true);

        Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/pacemaker.png", ".png");
        image.show();
        Image image_hand = lme.DisplayUtils.openImage("data/hand.png");
        image_hand.show();

        GaussFilter2d filter = new mt.GaussFilter2d(9, 1.0f);
        DerivativeFilter2d filter2 = new mt.DerivativeFilter2d(false);
        DerivativeFilter2d filter3 = new mt.DerivativeFilter2d(true);
        AverageFilter2d filter4 = new mt.AverageFilter2d(9);
        SharpeningFilter2d filter5 = new mt.SharpeningFilter2d(9);

        Image gausImage = filter.apply(image);
        gausImage.show();
        Image derivImage = filter2.apply(image);
        derivImage.show();
        Image derivImage2 = filter3.apply(image);
        derivImage2.show();
        Image averageImage = filter4.apply(image);
        averageImage.show();
        Image sharpeningImage = filter5.apply(image);
        sharpeningImage.show();
    }
}
