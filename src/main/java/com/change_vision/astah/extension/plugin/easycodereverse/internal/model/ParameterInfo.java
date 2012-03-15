package com.change_vision.astah.extension.plugin.easycodereverse.internal.model;

import java.util.List;

public class ParameterInfo {
	private int line;
	
	private ClassInfo type;
	
	private String name;
	
	@Override
	public String toString() {
		List<ClassInfo> typeArgs = getType().getTypeArgs();
		StringBuilder builder = new StringBuilder();
		builder.append(getType().getQualifiedTypeName());
		builder.append(" ");
		builder.append(name);
		if (typeArgs != null) {
			builder.append("<");
			for (ClassInfo typeArg : typeArgs) {
				builder.append(typeArg.getQualifiedTypeName());
				builder.append(", ");
			}
			builder.delete(builder.lastIndexOf(", "), builder.length());
			builder.append(">");
		}
		return builder.toString();
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setType(ClassInfo type) {
		this.type = type;
	}

	public ClassInfo getType() {
		return type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
