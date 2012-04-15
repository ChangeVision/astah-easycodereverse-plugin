package com.change_vision.astah.extension.plugin.easycodereverse.internal.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.ClassInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.MethodInfo;

public class JavaParserTest {
	@Test
	public void parse() throws Exception {
		JavaCodeParser parser = new JavaCodeParser();
		List<ClassInfo> classes = parser.parse(JavaParserTest.class.getResourceAsStream("Hello.java"));
		ClassInfo info = classes.get(0);
		assertThat(info.getName(), is("Hello"));
		MethodInfo main = info.getMethods().get(0);
		assertThat(main.getName(), is("main"));
		assertThat(main.getParameters().size(), is(1));
		assertThat(main.getReturnType().getName(), is("void"));
		assertTrue(main.isStatic());
		
		classes = parser.parse(JavaParserTest.class.getResourceAsStream("ExInter.java"));
		info = classes.get(0);
		assertThat(info.getName(), is("ExInter"));
		MethodInfo sayHello = info.getMethods().get(0);
		assertThat(sayHello.getName(), is("sayHello"));
		assertThat(sayHello.getParameters().size(), is(1));
		assertThat(sayHello.getReturnType().getName(), is("void"));
		assertFalse(sayHello.isStatic());
		
		classes = parser.parse(JavaParserTest.class.getResourceAsStream("ExInterImpl.java"));
		info = classes.get(0);
		assertThat(info.getName(), is("ExInterImpl"));
		sayHello = info.getMethods().get(0);
		assertThat(sayHello.getParameters().size(), is(1));
		assertThat(sayHello.getReturnType().getName(), is("void"));
		assertFalse(sayHello.isStatic());
	}
	
	@Test
	public void parseMulti() throws Exception {
		JavaCodeParser parser = new JavaCodeParser();
		List<ClassInfo> classes = parser.parse(JavaParserTest.class.getResourceAsStream("MultiClasses.java"));
		assertThat(classes.size(), is(7));
	}
}
