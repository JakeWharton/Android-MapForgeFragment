/*
 * Copyright 2010, 2011 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jakewharton.android.mapsforge_fragment;

import android.util.Log;

/**
 * Class used for logging text to the console.
 */
final class Logger {
	private static final String LOG_TAG = "osm";

	/**
	 * Logs the given string message with debug level.
	 * 
	 * @param message
	 *            the message which should be logged.
	 */
	static void debug(String message) {
		Log.d(LOG_TAG, Thread.currentThread().getName() + ": " + message);
	}

	/**
	 * Logs the given exception, including its stack trace.
	 * 
	 * @param exception
	 *            the exception which should be logged.
	 */
	static void exception(Exception exception) {
		StringBuilder stringBuilder = new StringBuilder(512);
		stringBuilder.append("Exception in thread \"");
		stringBuilder.append(Thread.currentThread().getName());
		stringBuilder.append("\" ");
		stringBuilder.append(exception.toString());
		StackTraceElement[] stack = exception.getStackTrace();
		for (int i = 0; i < stack.length; ++i) {
			stringBuilder.append("\n\tat ");
			stringBuilder.append(stack[i].getMethodName());
			stringBuilder.append('(');
			stringBuilder.append(stack[i].getFileName());
			stringBuilder.append(':');
			stringBuilder.append(stack[i].getLineNumber());
			stringBuilder.append(')');
		}
		Log.e(LOG_TAG, stringBuilder.toString());
	}

	/**
	 * Empty private constructor to prevent object creation.
	 */
	private Logger() {
		// do nothing
	}
}