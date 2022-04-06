/*
 * DebugExample.java
 * Copyright (C) 2021 Stephan Seitz <stephan.seitz@fau.de>
 *
 * Distributed under terms of the GPLv3 license.
 */

package exercises;


/**
 * This program does meaningless stuff, so you can check whether your debugger works.
 */
public class DebugExample {
	static int foo = 1;

	static class Foo {
		public int x, y;
	}

	public static void main(String[] args) {
		(new ij.ImageJ()).exitWhenQuitting(true);

		System.out.println("Executing this program should show an ImageJ window.");
		
		// This is just some non-sense to check whether your debugger is working.
		Foo a = new Foo();
		a.x = 5;
		a.y = 3;

		final int MAX_COUNT = 4;
		for (int i = 0; i < MAX_COUNT; i++) {
			System.out.println("I'm counting: " + i);	
			foo++;
		}
		// Let's break things!
		a = null;
		// Does your debugger stop here when the NullPointerException is triggered?
		a.x = 6;

		System.out.println("The end of the program.");
	}
}
