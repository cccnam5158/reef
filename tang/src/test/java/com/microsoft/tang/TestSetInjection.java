package com.microsoft.tang;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Test;

import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.tang.annotations.Parameter;
import com.microsoft.tang.exceptions.BindException;
import com.microsoft.tang.exceptions.InjectionException;
import com.microsoft.tang.formats.ConfigurationFile;

public class TestSetInjection {
  @Test
  public void testStringInjectDefault() throws InjectionException {
    Set<String> actual = Tang.Factory.getTang().newInjector().getInstance(Box.class).numbers;

    Set<String> expected = new HashSet<>();
    expected.add("one");
    expected.add("two");
    expected.add("three");

    Assert.assertEquals(expected, actual);
  }
  @Test
  public void testObjectInjectDefault() throws InjectionException, BindException {
    Injector i = Tang.Factory.getTang().newInjector();
    i.bindVolatileInstance(Integer.class, 42);
    i.bindVolatileInstance(Float.class, 42.0001f);
    Set<Number> actual = i.getInstance(Pool.class).numbers;
    Set<Number> expected = new HashSet<>();
    expected.add(42);
    expected.add(42.0001f);
    Assert.assertEquals(expected, actual);
  }
  @Test
  public void testStringInjectBound() throws InjectionException, BindException {
    JavaConfigurationBuilder cb = Tang.Factory.getTang().newConfigurationBuilder();
    cb.bindSetEntry(SetOfNumbers.class, "four");
    cb.bindSetEntry(SetOfNumbers.class, "five");
    cb.bindSetEntry(SetOfNumbers.class, "six");
    Set<String> actual = Tang.Factory.getTang().newInjector(cb.build()).getInstance(Box.class).numbers;

    Set<String> expected = new HashSet<>();
    expected.add("four");
    expected.add("five");
    expected.add("six");

    Assert.assertEquals(expected, actual);
  }
  @Test
  public void testObjectInjectBound() throws InjectionException, BindException {
    JavaConfigurationBuilder cb = Tang.Factory.getTang().newConfigurationBuilder();
    cb.bindSetEntry(SetOfClasses.class, Short.class);
    cb.bindSetEntry(SetOfClasses.class, Float.class);
    
    Injector i = Tang.Factory.getTang().newInjector(cb.build());
    i.bindVolatileInstance(Short.class, (short)4);
    i.bindVolatileInstance(Float.class, 42.0001f);
    Set<Number> actual = i.getInstance(Pool.class).numbers;
    Set<Number> expected = new HashSet<>();
    expected.add((short)4);
    expected.add(42.0001f);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testStringInjectRoundTrip() throws InjectionException, BindException {
    JavaConfigurationBuilder cb = Tang.Factory.getTang().newConfigurationBuilder();
    cb.bindSetEntry(SetOfNumbers.class, "four");
    cb.bindSetEntry(SetOfNumbers.class, "five");
    cb.bindSetEntry(SetOfNumbers.class, "six");

    String s = ConfigurationFile.toConfigurationString(cb.build());
    JavaConfigurationBuilder cb2 = Tang.Factory.getTang().newConfigurationBuilder();
    ConfigurationFile.addConfiguration(cb2, s);

    Set<String> actual = Tang.Factory.getTang().newInjector(cb2.build()).getInstance(Box.class).numbers;

    Set<String> expected = new HashSet<>();
    expected.add("four");
    expected.add("five");
    expected.add("six");

    Assert.assertEquals(expected, actual);
  }
  @Test
  public void testObjectInjectRoundTrip() throws InjectionException, BindException {
    JavaConfigurationBuilder cb = Tang.Factory.getTang().newConfigurationBuilder();
    cb.bindSetEntry(SetOfClasses.class, Short.class);
    cb.bindSetEntry(SetOfClasses.class, Float.class);
    
    String s = ConfigurationFile.toConfigurationString(cb.build());
    JavaConfigurationBuilder cb2 = Tang.Factory.getTang().newConfigurationBuilder();
    ConfigurationFile.addConfiguration(cb2, s);
    
    Injector i = Tang.Factory.getTang().newInjector(cb2.build());
    i.bindVolatileInstance(Short.class, (short)4);
    i.bindVolatileInstance(Float.class, 42.0001f);
    Set<Number> actual = i.getInstance(Pool.class).numbers;
    Set<Number> expected = new HashSet<>();
    expected.add((short)4);
    expected.add(42.0001f);
    Assert.assertEquals(expected, actual);
  }

}

@NamedParameter(default_value="one,two,three")
class SetOfNumbers implements Name<Set<String>> { }

class Box {
  public final Set<String> numbers;
  @Inject
  Box(@Parameter(SetOfNumbers.class) Set<String> numbers) {
    this.numbers = numbers;
  }
}

@NamedParameter(default_value="java.lang.Integer,java.lang.Float")
class SetOfClasses implements Name<Set<Number>> { }

class Pool {
  public final Set<Number> numbers;
  @Inject
  Pool(@Parameter(SetOfClasses.class) Set<Number> numbers) {
    this.numbers = numbers;
  }
}