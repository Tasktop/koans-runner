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

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class KoanSuiteRunner extends Suite {

	private final KoanRunListener runListener;

	public KoanSuiteRunner(Class<?> klass) throws InitializationError {
		super(klass, new KoanRunnerBuilder());
		runListener = new KoanRunListener();
	}

	private static class KoanRunnerBuilder extends RunnerBuilder {

		public KoanRunnerBuilder() {
		}

		@Override
		public Runner runnerForClass(Class<?> testClass) throws Throwable {
			RunWith annotation = testClass.getAnnotation(RunWith.class);
			Class<? extends Runner> runnerClass = annotation.value();
			if (runnerClass == KoanRunner.class) {
				return new KoanRunner(testClass);
			}
			throw new IllegalStateException(
					testClass.getName() + " should have a @RunWith annotation with supported Koan Runners");
		}

	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(runListener);
		super.run(notifier);
	}

}
