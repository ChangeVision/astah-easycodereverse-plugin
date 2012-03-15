package com.change_vision.astah.extension.plugin.easycodereverse.internal.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.ClassInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.MethodInfo;

public class JavaParserTest {
	@Test
	public void parse() throws Exception {
		JavaCodeParser parser = new JavaCodeParser();
		ClassInfo info = parser.parse(JavaParserTest.class.getResourceAsStream("Hello.java"));
		assertThat(info.getName(), is("Hello"));
		MethodInfo main = info.getMethods().get(0);
		assertThat(main.getName(), is("main"));
		assertThat(main.getParameters().size(), is(1));
		assertThat(main.getReturnType().getName(), is("void"));
		assertTrue(main.isStatic());
		
		info = parser.parse(JavaParserTest.class.getResourceAsStream("ExInter.java"));
		assertThat(info.getName(), is("ExInter"));
		MethodInfo sayHello = info.getMethods().get(0);
		assertThat(sayHello.getName(), is("sayHello"));
		assertThat(sayHello.getParameters().size(), is(1));
		assertThat(sayHello.getReturnType().getName(), is("void"));
		assertFalse(sayHello.isStatic());
		
		info = parser.parse(JavaParserTest.class.getResourceAsStream("ExInterImpl.java"));
		assertThat(info.getName(), is("ExInterImpl"));
		sayHello = info.getMethods().get(0);
		assertThat(sayHello.getParameters().size(), is(1));
		assertThat(sayHello.getReturnType().getName(), is("void"));
		assertFalse(sayHello.isStatic());
	}
}
