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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;

import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;


public class KoanRunner extends BlockJUnit4ClassRunner {

	public KoanRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> computedMethods = super.computeTestMethods();
		Class<?> testClass = getTestClass().getJavaClass();
		return sortBySourceOrder(testClass, computedMethods);
	}

	private List<FrameworkMethod> sortBySourceOrder(Class<?> testClass, List<FrameworkMethod> methods) {
		try {
			List<Method> sortedMethods = sortMethodsBySourceOrder(testClass,
					methods.stream().map(method -> method.getMethod()).collect(Collectors.toList()));
			return sortedMethods.stream().map(method -> new FrameworkMethod(method)).collect(Collectors.toList());
		} catch (Exception shouldNotHappen) {
			throw new IllegalStateException(shouldNotHappen);
		}
	}

	private List<Method> sortMethodsBySourceOrder(Class<?> testClass, List<Method> methods) throws Exception {
		List<String> classMethods = getClassMethods(testClass);
		Collections.sort(methods, new Comparator<Method>() {

			@Override
			public int compare(Method methodA, Method methodB) {
				return Ints.compare(classMethods.indexOf(methodA.getName()),
						classMethods.indexOf(methodB.getName()));
			}
		});
		return methods;
	}

	@SuppressWarnings("unchecked")
	private List<String> getClassMethods(Class<?> testClass) throws Exception {
		File tempFile = writeClassToFile(testClass);
		try (DataInputStream in = new DataInputStream(new FileInputStream(tempFile))) {
			ClassFile w = new ClassFile(in);
			tempFile.delete();
			List<MethodInfo> methods = w.getMethods();
			List<String> methodNames = new ArrayList<>();
			for (MethodInfo methodInfo : methods) {
				methodNames.add(methodInfo.getName());
			}
			return methodNames;
		}
	}

	private File writeClassToFile(Class<?> testClass) throws IOException, URISyntaxException {
		String classFileName = testClass.getSimpleName() + ".class";
		URL resource = getClassAsResource(testClass, classFileName);
		File tempFile = File.createTempFile(classFileName, "koanclass");
		Files.copy(new File(resource.toURI()), tempFile);
		return tempFile;
	}

	private URL getClassAsResource(Class<?> testClass, String classFileName) {
		URL resource = testClass.getResource(classFileName);
		if (resource == null) {
			throw new IllegalStateException("Works only for concreate classes!");
		}
		return resource;
	}

	@Override
	protected void validateZeroArgConstructor(List<Throwable> errors) {
		// needed for KoanFixtureRunner
	}

}
