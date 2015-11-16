/*
 * Copyright (c) 2015 Tasktop Technologies.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
