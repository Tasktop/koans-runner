/**
 * (C) Copyright (c) 2015 Tasktop Technologies and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Holger Staudacher - initial implementation
 */
package com.tasktop.koans;

import static java.math.MathContext.DECIMAL32;
import static org.fusesource.jansi.Ansi.Color.BLACK;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharSink;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

public class PathToEnlightment {

	private final Result result;

	private final Failure firstFailure;

	public PathToEnlightment(Result result, Failure firstFailure) {
		this.result = result;
		this.firstFailure = firstFailure;
	}

	public List<String> getPath() {
		if (result.wasSuccessful()) {
			return getEnlightment();
		}
		return producePathToEnlightment();
	}

	private List<String> getEnlightment() {
		try (InputStream in = PathToEnlightment.class.getResourceAsStream("/enlightment.txt")) {
			List<String> lines = CharStreams.readLines(new InputStreamReader(in, Charsets.UTF_8));
			return lines.stream().map(line -> color(CYAN, line)).collect(Collectors.toList());
		} catch (IOException shouldnotHappen) {
			throw new IllegalStateException(shouldnotHappen);
		}
	}

	private List<String> producePathToEnlightment() {
		List<String> path = new ArrayList<>();
		observate(path);
		encourage(path);
		guideThroughError(path);
		aZenlikeStatement(path);
		showProgress(path);
		storeFailures();
		return path;
	}

	private void observate(List<String> path) {
		path.add(color(RED, firstFailure.getDescription().getTestClass().getSimpleName() + "#"
				+ firstFailure.getDescription().getMethodName() + " has damaged your karma."));
	}

	private void encourage(List<String> path) {
		path.add(color(BLACK, ""));
		path.add("The Master says:");
		path.add(color(CYAN, "  You have not yet reached enlightenment."));
		List<String> lastFailures = getLastFailures();
		if (!lastFailures.isEmpty()) {
			int lastEqualFails = countEqualLastEntries(lastFailures, firstFailure.getDescription().getDisplayName());
			if (lastEqualFails >= 5) {
				path.add(color(CYAN, "  I sense frustration. Do not be afraid to ask for help."));
			} else if (lastEqualFails >= 2) {
				path.add(color(CYAN, "  Do not lose hope."));
			} else if (lastEqualFails == 0) {
				int success = result.getRunCount() - result.getFailureCount();
				path.add(color(CYAN, "  You are progressing. Excellent. " + success + " koans completed."));
			}
		}
	}

	private int countEqualLastEntries(List<String> failues, String failureName) {
		List<String> reverseFailures = Lists.reverse(failues);
		int count = 0;
		for (int i = 0; i < reverseFailures.size(); i++) {
			if (reverseFailures.get(i).equals(failureName)) {
				count++;
			} else {
				return count;
			}
		}
		return count;
	}

	private void guideThroughError(List<String> path) {
		path.add(color(BLACK, ""));
		path.add("The answers you seek...");
		path.add(color(RED, "  " + firstFailure.getException()));
		path.add(color(BLACK, ""));
		path.add("Please meditate on the following code:");
		path.add(color(RED, "  " + firstFailure.getDescription()));
	}

	private void aZenlikeStatement(List<String> path) {
		path.add(color(GREEN, ""));
		switch (result.getFailureCount() % 10) {
		case 0:
			path.add("mountains are merely mountains");
			break;
		case 1:
			path.add("learn the rules so you know how to break them properly");
			break;
		case 2:
			path.add("learn the rules so you know how to break them properly");
			break;
		case 3:
			path.add("remember that silence is sometimes the best answer");
			break;
		case 4:
			path.add("remember that silence is sometimes the best answer");
			break;
		case 5:
			path.add("sleep is the best meditation");
			break;
		case 6:
			path.add("sleep is the best meditation");
			break;
		case 7:
			path.add("when you lose, don't lose the lesson");
			break;
		case 8:
			path.add("when you lose, don't lose the lesson");
			break;
		default:
			path.add("things are not what they appear to be: nor are they otherwise");
			break;
		}
	}

	private void showProgress(List<String> path) {
		StringBuilder pathSoFar = new StringBuilder();
		pathSoFar.append(color(GREEN, "your path thus far ["));
		appendPathSoFar(pathSoFar);
		pathSoFar.append(color(GREEN, "]"));
		pathSoFar.append(color(BLACK, " "));
		pathSoFar.append(result.getRunCount() - result.getFailureCount() + "/" + result.getRunCount());
		path.add(pathSoFar.toString());
	}

	private void appendPathSoFar(StringBuilder pathSoFar) {
		int barWidth = 50;
		BigDecimal scale = BigDecimal.valueOf(barWidth).divide(BigDecimal.valueOf(result.getRunCount()), DECIMAL32);
		int passCount = result.getRunCount() - result.getFailureCount();
		int happySteps = scale.multiply(BigDecimal.valueOf(passCount)).intValue();
		if (happySteps == 0 && passCount > 0) {
			happySteps = 1;
		}
		for (int i = 0; i < happySteps; i++) {
			pathSoFar.append(".");
		}
		if (!result.wasSuccessful()) {
			pathSoFar.append(color(RED, "X"));
			int dashes = barWidth - 1 - happySteps;
			for (int i = 0; i < dashes; i++) {
				pathSoFar.append(color(CYAN, "_"));
			}
		}
	}

	private String color(Color color, String toColor) {
		if (Boolean.getBoolean("enable.ansi")) {
			return Ansi.ansi().eraseScreen().fg(color).a(toColor).toString();
		}
		return toColor;
	}

	private List<String> getLastFailures() {
		try {
			File file = getTmpFile();
			return Files.readLines(file, Charsets.UTF_8);
		} catch (IOException shouldNotHappen) {
			throw new IllegalStateException(shouldNotHappen);
		}
	}

	private void storeFailures() {
		List<String> failures = getLastFailures();
		failures.add(firstFailure.getDescription().getDisplayName());
		try {
			File file = getTmpFile();
			CharSink sink = Files.asCharSink(file, Charsets.UTF_8);
			sink.writeLines(Lists.reverse(Lists.reverse(failures).stream().limit(10).collect(Collectors.toList())),
					"\n");
		} catch (IOException shouldNotHappen) {
			throw new IllegalStateException(shouldNotHappen);
		}
	}

	private File getTmpFile() throws IOException {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		if (!tmpFolder.endsWith(File.separator)) {
			tmpFolder = tmpFolder + File.separator;
		}
		File file = new File(tmpFolder, "com.tasktop.koans.progress.txt");
		file.createNewFile();
		return file;
	}

}
