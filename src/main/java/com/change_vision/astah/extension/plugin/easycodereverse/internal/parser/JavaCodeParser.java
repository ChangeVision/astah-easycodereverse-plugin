package com.change_vision.astah.extension.plugin.easycodereverse.internal.parser;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.Messages;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.exception.ParseException;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.ClassInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.FieldInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.MethodInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.ParameterInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.util.JavaTypeUtil;

public class JavaCodeParser implements SourceCodeParser<ClassInfo> {
	private static final Logger logger = LoggerFactory.getLogger(JavaCodeParser.class.getName());
	
	private CompilationUnit unit;

	public ClassInfo parse(InputStream is) throws IOException, ParseException {
		final List<FieldInfo> fields = new ArrayList<FieldInfo>();
		final List<MethodInfo> methods = new ArrayList<MethodInfo>();
		final ClassInfo classInfo = new ClassInfo("", fields, methods);

		try {
			unit = japa.parser.JavaParser.parse(is);
		} catch (japa.parser.ParseException e) {
			throw new ParseException(Messages.getMessage("parse_worker.error.parse"), e);
		}

		new VoidVisitorAdapter<Object>() {
			Map<String, String> importMap = new HashMap<String, String>();
			
			@Override
			public void visit(ImportDeclaration node, Object arg) {
				importMap.put(node.getName().getName(), node.getName().toString());
				super.visit(node, arg);
			}
			
			@Override
			public void visit(EnumDeclaration node, Object arg) {
				classInfo.setLine(node.getBeginLine());

				String name = node.getName();
				PackageDeclaration packageDec = unit.getPackage();
				classInfo.setQualifiedTypeName((packageDec != null) ? packageDec.getName() + "." + name : name);
				classInfo.setName(name);
				
				classInfo.setEnum(true);
				classInfo.setAbstract(ModifierSet.isAbstract(node.getModifiers()));
				classInfo.setFinal(ModifierSet.isFinal(node.getModifiers()));
				classInfo.setStatic(ModifierSet.isStatic(node.getModifiers()));
				classInfo.setStrictfp(ModifierSet.isStrictfp(node.getModifiers()));
				classInfo.setPublic(ModifierSet.isPublic(node.getModifiers()));

				super.visit(node, arg);
			}
			
			@Override
			public void visit(ClassOrInterfaceDeclaration node, Object arg) {
				if (StringUtils.isNotBlank(classInfo.getName())) {
					return;
				}

				classInfo.setLine(node.getBeginLine());

				List<TypeParameter> typeParameters = node.getTypeParameters();
				if (typeParameters != null && typeParameters.size() > 0) {
					List<ClassInfo> parameters = new ArrayList<ClassInfo>();
					for (TypeParameter typeParameter : typeParameters) {
						ClassInfo parameterInfo = new ClassInfo();
						parameterInfo.setName(typeParameter.getName());
						
					}
					classInfo.setTypeArgs(parameters);
				}

				String name = node.getName();
				PackageDeclaration packageDec = unit.getPackage();
				classInfo.setQualifiedTypeName((packageDec != null) ? packageDec.getName() + "." + name : name);
				classInfo.setName(name);
				
				classInfo.setInterface(node.isInterface());
				classInfo.setAbstract(ModifierSet.isAbstract(node.getModifiers()));
				classInfo.setFinal(ModifierSet.isFinal(node.getModifiers()));
				classInfo.setStatic(ModifierSet.isStatic(node.getModifiers()));
				classInfo.setStrictfp(ModifierSet.isStrictfp(node.getModifiers()));
				classInfo.setPublic(ModifierSet.isPublic(node.getModifiers()));
				
				copyExtendsInfo(node, classInfo);
				copyImplementsInfo(node, classInfo);

				super.visit(node, arg);
			}

			private void copyExtendsInfo(ClassOrInterfaceDeclaration node, final ClassInfo classInfo) {
				List<ClassOrInterfaceType> extendsTypes = node.getExtends();
				if (extendsTypes != null) {
					List<ClassInfo> extendsClasses = new ArrayList<ClassInfo>();
					for (ClassOrInterfaceType coi : extendsTypes) {
						String superClassName = coi.getName();
						ClassInfo superClass = new ClassInfo(superClassName);
						
//						List<Type> typeParameters = coi.getTypeArgs();
//						if (typeParameters != null && typeParameters.size() > 0) {
//							List<String> typeArgs = Arrays.asList(typeParameters.toArray(new String[0]));
//							superClass.setTypeArgs(typeArgs);
//						}
						
						String qualifiedTypeName = findQualifiedTypeName(superClassName);
						superClass.setQualifiedTypeName(qualifiedTypeName);
						superClass.setInterface(classInfo.isInterface());
						
						extendsClasses.add(superClass);
					}
					classInfo.setExtendsClasses(extendsClasses);
				}
			}

			private void copyImplementsInfo(ClassOrInterfaceDeclaration node, final ClassInfo classInfo) {
				List<ClassOrInterfaceType> implementsTypes = node.getImplements();
				if (implementsTypes != null) {
					List<ClassInfo> implementsInterfaces = new ArrayList<ClassInfo>();
					for (ClassOrInterfaceType coi : implementsTypes) {
						String interfaceName = coi.getName();
						ClassInfo implementsInterface = new ClassInfo(interfaceName);
						
//						List<Type> typeParameters = coi.getTypeArgs();
//						if (typeParameters != null && typeParameters.size() > 0) {
//							List<String> typeArgs = Arrays.asList(typeParameters.toArray(new String[0]));
//							implementsInterface.setTypeArgs(typeArgs);
//						}
						
						String qualifiedTypeName = findQualifiedTypeName(interfaceName);
						
						implementsInterface.setQualifiedTypeName(qualifiedTypeName);
						implementsInterface.setInterface(true);
						implementsInterface.setPublic(true);
						implementsInterface.setAbstract(true);
						
						implementsInterfaces.add(implementsInterface);
					}
					classInfo.setImplementsInterfaces(implementsInterfaces);
				}
			}

			@Override
			public void visit(FieldDeclaration node, Object arg) {
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.setLine(node.getBeginLine());
				
				VariableDeclarator variableDeclarator = node.getVariables().get(0);
				fieldInfo.setName(variableDeclarator.getId().getName());
				
				String fieldTypeName = node.getType().toString().replaceAll("<.*>", "");
				String qualifiedTypeName = findQualifiedTypeName(fieldTypeName);
				ClassInfo type = new ClassInfo();
				type.setName(fieldTypeName);
				type.setQualifiedTypeName(qualifiedTypeName);
				type.setPrimitive(JavaTypeUtil.isPrimitive(fieldTypeName));
				fieldInfo.setType(type);
				
				fieldInfo.setStatic(ModifierSet.isStatic(node.getModifiers()));
				fieldInfo.setFinal(ModifierSet.isFinal(node.getModifiers()));
				fieldInfo.setTransient(ModifierSet.isTransient(node.getModifiers()));
				fieldInfo.setVolatile(ModifierSet.isVolatile(node.getModifiers()));
				fieldInfo.setPublic(ModifierSet.isPublic(node.getModifiers()));
				fieldInfo.setProtected(ModifierSet.isProtected(node.getModifiers()));
				fieldInfo.setPrivate(ModifierSet.isPrivate(node.getModifiers()));
				
				fields.add(fieldInfo);

				super.visit(node, arg);
			}

			@Override
			public void visit(MethodDeclaration node, Object arg) {
				MethodInfo methodInfo = new MethodInfo();
				methodInfo.setLine(node.getBeginLine());
				
				String returnTypeName = node.getType().toString().replaceAll("<.*>", "");
				String qualifiedReturnTypeName = findQualifiedTypeName(returnTypeName);
				
				ClassInfo returnType = new ClassInfo(returnTypeName);
				returnType.setQualifiedTypeName(qualifiedReturnTypeName);
				returnType.setVoid(JavaTypeUtil.isVoid(returnTypeName));
				returnType.setPrimitive(JavaTypeUtil.isPrimitive(returnTypeName));
				
				methodInfo.setReturnType(returnType);
				methodInfo.setName(node.getName().toString());
				
				methodInfo.setFinal(ModifierSet.isFinal(node.getModifiers()));
				methodInfo.setSynchronized(ModifierSet.isSynchronized(node.getModifiers()));
				methodInfo.setNative(ModifierSet.isNative(node.getModifiers()));
				
				methodInfo.setStatic(ModifierSet.isStatic(node.getModifiers()));
				
				methodInfo.setPublic(ModifierSet.isPublic(node.getModifiers()));
				methodInfo.setProtected(ModifierSet.isProtected(node.getModifiers()));
				methodInfo.setPrivate(ModifierSet.isPrivate(node.getModifiers()));

				List<ParameterInfo> parameters = new ArrayList<ParameterInfo>();
				List<Parameter> params = node.getParameters();
				if (params != null) {
					for (Parameter param : params) {
						String name = param.getId().getName();
						String typeName = param.getType().toString().replaceAll("<.*>", "");;
						ParameterInfo info = new ParameterInfo();
						info.setLine(param.getBeginLine());
						info.setName(name);
						ClassInfo type = new ClassInfo();
						type.setName(typeName);
						type.setQualifiedTypeName(findQualifiedTypeName(typeName));
						type.setPrimitive(JavaTypeUtil.isPrimitive(typeName));
						
//						type.setInterface(); // TODO インターフェースかどうかの設定
						type.setAbstract(ModifierSet.isAbstract(param.getModifiers()));
						type.setFinal(ModifierSet.isFinal(param.getModifiers()));
						type.setStatic(ModifierSet.isStatic(param.getModifiers()));
						type.setStrictfp(ModifierSet.isStrictfp(param.getModifiers()));
						type.setPublic(ModifierSet.isPublic(param.getModifiers()));
						
						info.setType(type);
						parameters.add(info);
					}
				}
				
				methodInfo.setParameters(parameters);
				methods.add(methodInfo);

				super.visit(node, arg);
			}

			private String findQualifiedTypeName(String simpleTypeName) {
				String qualifiedTypeName = simpleTypeName;
				if(importMap.containsKey(simpleTypeName)) {
					qualifiedTypeName = importMap.get(simpleTypeName);
				} else if (!JavaTypeUtil.isVoid(simpleTypeName)
						&& !JavaTypeUtil.isPrimitive(simpleTypeName)
						&& !JavaTypeUtil.isJavaLang(simpleTypeName)){
					PackageDeclaration packageDec = unit.getPackage();
					qualifiedTypeName = (packageDec != null) ? packageDec.getName() + "." + simpleTypeName : simpleTypeName;
				} else if (JavaTypeUtil.isJavaLang(simpleTypeName)) {
					qualifiedTypeName = JavaTypeUtil.JAVA_LANG_PACKAGE + "." + simpleTypeName;
				}
				return qualifiedTypeName;
			}
		}.visit(unit, null);

		logger.info("Parse finished: " + classInfo.getName());
		return classInfo;
	}
}