package com.change_vision.astah.extension.plugin.easycodereverse.internal.model;

public class FieldInfo implements HasVisibility {
	private int line;
	private String name;
	private ClassInfo type;

	private boolean isStatic = false;
	private boolean isFinal = false;
	
	private boolean isTransient = false;
	private boolean isVolatile = false;
	
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

	public void setType(ClassInfo type) {
		this.type = type;
	}

	public ClassInfo getType() {
		return type;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public boolean isVolatile() {
		return isVolatile;
	}

	public void setVolatile(boolean isVolatile) {
		this.isVolatile = isVolatile;
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