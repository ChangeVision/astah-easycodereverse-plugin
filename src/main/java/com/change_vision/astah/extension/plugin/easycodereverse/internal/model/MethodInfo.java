package com.change_vision.astah.extension.plugin.easycodereverse.internal.model;

import java.util.List;

public class MethodInfo implements HasVisibility {
	private int line;
	private String name;

	private ClassInfo returnType;
	private List<ParameterInfo> parameters;
	
	private boolean isConstructor = false;
	private boolean isAbstract = false;
	private boolean isFinal = false;
	private boolean isSynchronized = false;
	private boolean isNative = false;
	
	private boolean isStatic = false;
	
	private boolean isPublic = false;
	private boolean isProtected = false;
	private boolean isPackage = false;
	private boolean isPrivate = true;

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setReturnType(ClassInfo returnType) {
		this.returnType = returnType;
	}

	public ClassInfo getReturnType() {
		return returnType;
	}

	public void setParameters(List<ParameterInfo> parameters) {
		this.parameters = parameters;
	}

	public List<ParameterInfo> getParameters() {
		return parameters;
	}

    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		if (isAbstract) {
			setFinal(false);
		}
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
		if (isFinal) {
			setAbstract(false);
		}
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
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