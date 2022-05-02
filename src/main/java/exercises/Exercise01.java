
/*
 * Exercise01.java
 * Copyright (C) 2020 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */
package exercises;


public class Exercise01 {
	public static void main(String[] args) {
		(new ij.ImageJ()).exitWhenQuitting(true);

		var cos = new mt.CosineWave(1, 1024);
		var sin = new mt.SineWave(1, 1024);

		cos.show();
		sin.show();

		var numWaves = 3;
		//TODO try to understand the code and play around with it! 

		var coolWave = new mt.SineWave(1 * numWaves, 1024)
			.plus(new mt.SineWave(2 * numWaves, 1024).times(-1.0f/2.0f))
			.plus(new mt.SineWave(3 * numWaves, 1024).times(+1.0f/3.0f))
			.plus(new mt.SineWave(4 * numWaves, 1024).times(-1.0f/4.0f))
			.plus(new mt.SineWave(5 * numWaves, 1024).times(+1.0f/5.0f))
			.plus(new mt.SineWave(6 * numWaves, 1024).times(-1.0f/6.0f))
			.plus(new mt.SineWave(7 * numWaves, 1024).times(+1.0f/7.0f))
			.plus(new mt.SineWave(8 * numWaves, 1024).times(-1.0f/8.0f))
			.plus(new mt.SineWave(9 * numWaves, 1024).times(+1.0f/9.0f))
			;

		coolWave.show();

	}
}
