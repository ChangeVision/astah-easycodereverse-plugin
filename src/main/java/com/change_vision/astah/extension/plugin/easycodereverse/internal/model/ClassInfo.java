package com.change_vision.astah.extension.plugin.easycodereverse.internal.model;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo implements HasVisibility {
	private int line;
	private String qualifiedTypeName;
	private String name;
	private List<ClassInfo> typeArgs;
	private List<FieldInfo> fields;
	private List<MethodInfo> methods;
	private List<ClassInfo> extendsClasses;
	private List<ClassInfo> implementsInterfaces;
	
	private boolean isVoid = false;
	private boolean isPrimitive = false;
	
	private boolean isInterface = false;
	private boolean isEnum = false;

	private boolean isAbstract = false;
	private boolean isFinal = false;
	
	private boolean isStatic = false; // inner class only.
	
	private boolean isStrictfp = false;
	
	private boolean isPublic = false;
	private boolean isProtected = false; // inner class only.
	private boolean isPackage = true;
	private boolean isPrivate = false; // inner class only.
	
	public ClassInfo() {
	}
	
	public ClassInfo(String name) {
		this(name, null, null);
	}
	
	public ClassInfo(String name, List<FieldInfo> fields, List<MethodInfo> methods) {
		this.name = name;
		this.fields = fields;
		this.methods = methods;
	}
	
	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setQualifiedTypeName(String qualifiedTypeName) {
		this.qualifiedTypeName = qualifiedTypeName;
	}
	
	public String getQualifiedTypeName() {
		return this.qualifiedTypeName;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setTypeArgs(List<ClassInfo> typeArgs) {
		this.typeArgs = typeArgs;
	}

	public List<ClassInfo> getTypeArgs() {
		return typeArgs;
	}

	public void setFields(List<FieldInfo> fields) {
		this.fields = fields;
	}

	public List<FieldInfo> getFields() {
		return (fields != null) ? fields : new ArrayList<FieldInfo>();
	}

	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}

	public List<MethodInfo> getMethods() {
		return (methods != null) ? methods : new ArrayList<MethodInfo>();
	}

	public void setExtendsClasses(List<ClassInfo> extendsClasses) {
		this.extendsClasses = extendsClasses;
	}

	public List<ClassInfo> getExtendsClasses() {
		return (extendsClasses != null) ? extendsClasses : new ArrayList<ClassInfo>();
	}

	public void setImplementsInterfaces(List<ClassInfo> implementsInterfaces) {
		this.implementsInterfaces = implementsInterfaces;
	}

	public List<ClassInfo> getImplementsInterfaces() {
		return (implementsInterfaces != null) ? implementsInterfaces : new ArrayList<ClassInfo>();
	}
	
	public boolean isVoid() {
		return isVoid;
	}

	public void setVoid(boolean isVoid) {
		this.isVoid = isVoid;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public void setPrimitive(boolean isPrimitive) {
		this.isPrimitive = isPrimitive;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	
	public boolean isInterface() {
		return isInterface;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isStrictfp() {
		return isStrictfp;
	}

	public void setStrictfp(boolean isStrictfp) {
		this.isStrictfp = isStrictfp;
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
		if (isPublic) {
			setProtected(false);
			setPackage(false);
			setPrivate(false);
		}
	}

	@Override
	public boolean isProtected() {
		return isProtected;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
		if (isProtected) {
			setPublic(false);
			setPackage(false);
			setPrivate(false);
		}
	}

	@Override
	public boolean isPackage() {
		return isPackage;
	}

	public void setPackage(boolean isPackage) {
		this.isPackage = isPackage;
		if (isPackage) {
			setPublic(false);
			setProtected(false);
			setPrivate(false);
		}
	}

	@Override
	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
		if (isPrivate) {
			setPublic(false);
			setProtected(false);
			setPackage(false);
		}
	}
}
