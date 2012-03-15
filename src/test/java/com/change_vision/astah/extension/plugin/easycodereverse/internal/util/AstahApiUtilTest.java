package com.change_vision.astah.extension.plugin.easycodereverse.internal.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class AstahApiUtilTest {
	ProjectAccessor projectAccessor = null;
	IModel project = null;
	BasicModelEditor modelEditor = null;

	@Before
	public void before() throws Throwable {
		projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		projectAccessor.open(AstahApiUtilTest.class.getResourceAsStream("util.asta"));
		project = projectAccessor.getProject();
		
		modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
	}
	
	@After
	public void after() {
		if (TransactionManager.isInTransaction()) {
			TransactionManager.abortTransaction();
		}
		
		projectAccessor.close();
		projectAccessor = null;
	}
	
	@Test
	public void createClassInDefaultPackage() throws Exception {
		String className = "CreateTestClass";
		
		TransactionManager.beginTransaction();
		IClass clazz = AstahApiUtil.findOrCreateClass(project, className);
		TransactionManager.endTransaction();
		
		assertThat(clazz.getFullName("."), is(className));
		assertNull(clazz.getOwner().getOwner());
	}
	
	@Test
	public void createClassInPackage() throws Exception {
		String className = "com.CreateTestClass";
		TransactionManager.beginTransaction();
		IClass clazz = AstahApiUtil.findOrCreateClass(project, className);
		TransactionManager.endTransaction();
		assertThat(clazz.getFullName("."), is(className));
		
		className = "com.change_vision.CreateTestClass";
		TransactionManager.beginTransaction();
		clazz = AstahApiUtil.findOrCreateClass(project, className);
		TransactionManager.endTransaction();
		assertThat(clazz.getFullName("."), is(className));
	}
	
	@Test
	public void createDefaultPackage() throws Exception {
		String packageName = "px0";
		TransactionManager.beginTransaction();
		IPackage testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
	}
	
	@Test
	public void createDeepPackage() throws Exception {
		String packageName = "px0.px1";
		TransactionManager.beginTransaction();
		IPackage testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
		
		packageName = "p0.px1";
		TransactionManager.beginTransaction();
		testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
		
		packageName = "px0.p1";
		TransactionManager.beginTransaction();
		testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
		
		packageName = "p0.p1";
		TransactionManager.beginTransaction();
		testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
		
		packageName = "p0.p1.p2.p3";
		TransactionManager.beginTransaction();
		testPackage = AstahApiUtil.findOrCreatePackage(project, packageName);
		TransactionManager.endTransaction();
		assertThat(testPackage.getFullName("."), is(packageName));
		
		String subPackageName = "p4.p5";
		TransactionManager.beginTransaction();
		IPackage subTestPackage = AstahApiUtil.findOrCreatePackage(testPackage, subPackageName);
		TransactionManager.endTransaction();
		assertThat(subTestPackage.getFullName("."), is(packageName + "." + subPackageName));
	}
}
