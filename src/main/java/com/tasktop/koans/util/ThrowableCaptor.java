/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Tasktop EULA
 * which accompanies this distribution, and is available at
 * http://tasktop.com/legal
 *******************************************************************************/
package com.tasktop.koans.util;

public class ThrowableCaptor {

	public interface Actor {

		void act() throws Throwable;
	}

	public static Throwable capture(Actor actor) {
		Throwable result = null;
		try {
			actor.act();
		} catch (Throwable thrown) {
			result = thrown;
		}
		return result;
	}
}