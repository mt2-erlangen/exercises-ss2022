/*
 * Excercise03.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

package exercises;

import mt.Image;
import mt.Vector2d;

public class Exercise03 {
	public static void main(String[] args) {
		(new ij.ImageJ()).exitWhenQuitting(true);

		Image image = lme.DisplayUtils.openImageFromInternet("https://mt2-erlangen.github.io/pacemaker.png", ".png");
		image.show();
		image.fft();

		//FFT der Beispiele
		var wave_1_0 = new mt.CosineWave2d(new Vector2d(1, 0), 256, 256, "Cosine 1 0");
		wave_1_0.show();
		wave_1_0.fft();
		var wave_0_1 = new mt.CosineWave2d(new Vector2d(0, 1), 256, 256, "Cosine 0 1");
		wave_0_1.show();
		wave_0_1.fft();


		//Checkboard patterns
		var plus = new mt.SineWave2d(new Vector2d(4, 0), 512, 512, "Cosine 1 0")
				.add(new mt.SineWave2d(new Vector2d(0, 4), 512, 512, "Cosine 1 0"))
				.add(new mt.CosineWave2d(new Vector2d(0, 4), 512, 512, "Cosine 1 0"))
		.add(new mt.CosineWave2d(new Vector2d(4, 0), 512, 512, "Cosine 1 0"));

		plus.show();
		plus.fft();



	}
}
