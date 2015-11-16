# JUnit koans-runner
[![Build Status](https://travis-ci.org/Tasktop/koans-runner.svg)](https://travis-ci.org/Tasktop/koans-runner) [![Maven Status](https://maven-badges.herokuapp.com/maven-central/com.tasktop.koans/koans-runner/badge.png)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.tasktop.koans%22) [![License](http://img.shields.io/badge/license-MIT-blue.svg)](https://en.wikipedia.org/wiki/MIT_License) 

A JUnit extension to learn through unit tests. Inspired by the famous [Ruby Koans](http://rubykoans.com/) this project provides generic JUnit runners to write koans in Java.

## What are Koans?
Koans are just unit tests! A Koan Suite is a set of failing test classes that explain a certain technology or framework to the user. The task for the user is to get all tests green and "reach enlightment". To achieve this the koans-runner provides continuous feedback and reproducible test execution.

![koans](https://cloud.githubusercontent.com/assets/289648/11192046/167b4b42-8c9f-11e5-94b9-2441161ebd0d.png)

## Installation
Download the koans-runner from Maven Central or add the dependency to your pom.
```xml
<dependency>
	<groupId>com.tasktop.koans</groupId>
	<artifactId>koans-runner</artifactId>
</dependency>
```

## Usage
To create your own Koans you need to provide **two** things:

1) One or more Test classes. They need to use `@RunWith( KoanRunner.class )` to be executed in the same order as the methods are defined in the source code.

```java
@RunWith( KoanRunner.class )
public class MyTest {
	
	@Test
	public void easyFirstTest() {
		fail(); // remove this line to make the test green
	}
	
	@Test
	public void notSoEasySecondTest() {
		assertTrue(false); // remove this line to make the test green
	}

}
```

2) One Suite class that defines the Test classes in the order to execute. This suite needs to use the `@RunWith( KoanSuiteRunner.class )` to produce the zen like output.

```java
@RunWith( KoanSuiteRunner.class )
@SuiteClasses( {
	MyTest.class
})
public class MySuite {}
```

## Enable ansi colors
To enable the ansi colors like in the screenshot above you need to add the `-Denable.ansi=true` property when launching your test suite. To get thes enice ansi colors within Eclipse you can install the nice [ansi-econsole](https://github.com/mihnita/ansi-econsole).

## License
The code is published under the terms of the MIT License.