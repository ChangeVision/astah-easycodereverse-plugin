package com.change_vision.astah.extension.plugin.easycodereverse.internal.util;

import org.apache.commons.lang.ArrayUtils;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IRealization;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;

public class AstahApiUtil {
	public static final String TYPE_CLASS = "Class";
	public static final String TYPE_INTERFACE = "Interface";
	public static final String TYPE_ENUM = "Enum";
	
	private static final String STEREOTYPE_INTERFACE = "interface";
	private static final String STEREOTYPE_ENUM = "enum";
	
	public static IPackage findOrCreatePackage(IPackage parent, String relativePath) throws InvalidEditingException {
		if (parent == null || relativePath == null) {
			throw new InvalidEditingException(InvalidEditingException.PARAMETER_ERROR_KEY, InvalidEditingException.PARAMETER_ERROR_MESSAGE);
		}

		String[] pathElements = relativePath.split("\\.");
		String packageName = (pathElements != null && pathElements.length > 0) ? pathElements[0] : relativePath;
		final String targetPath = (parent != null && parent.getOwner() != null) ? parent.getFullName(".") + "." + packageName : packageName;
		
		ProjectAccessor projectAccessor = null;
		INamedElement[] findElements = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			findElements = projectAccessor.findElements(new ModelFinder() {
				@Override
				public boolean isTarget(INamedElement element) {
					if (element instanceof IPackage) {
						String elementPath = ((IPackage) element).getFullName(".");
						return (targetPath.equals(elementPath));
					}
					return false;
				}
			});
		} catch (ProjectNotFoundException e) {
			throw new IllegalStateException("project is null.");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("ProjectAccessor class not found.");
		}
		
		IPackage targetPackage = null;
		if (findElements != null && findElements.length > 0) {
			targetPackage = (IPackage) findElements[0]; // パッケージが既にあれば作らない
		} else {
			BasicModelEditor modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
			targetPackage = modelEditor.createPackage(parent, packageName);
		}
		
		if (pathElements.length > 1) {
			String childPackageName = relativePath.substring(relativePath.indexOf(".") + 1, relativePath.length());
			return findOrCreatePackage(targetPackage, childPackageName);
		}
		
		return targetPackage;
	}
	
	public static IClass findOrCreateInterface(IModel project, String fullPath) throws InvalidEditingException {
		return findOrCreateClass(project, fullPath, TYPE_INTERFACE);
	}
	
	public static IClass findOrCreateClass(IModel project, String fullPath) throws InvalidEditingException {
		return findOrCreateClass(project, fullPath, TYPE_CLASS);
	}
	
	public static IClass findOrCreateEnum(IModel project, String fullPath) throws InvalidEditingException {
		return findOrCreateClass(project, fullPath, TYPE_ENUM);
	}
	
	public static IClass findOrCreateClass(IModel project, String fullPath, String type) throws InvalidEditingException {
		if (project == null || fullPath == null) {
			throw new InvalidEditingException(InvalidEditingException.PARAMETER_ERROR_KEY, InvalidEditingException.PARAMETER_ERROR_MESSAGE);
		}
		
		String className = fullPath;
		IPackage parent = project;
		String[] pathElements = fullPath.split("\\.");
		if (pathElements != null && pathElements.length > 1) {
			String packageName = fullPath.substring(0, fullPath.lastIndexOf("."));
			parent = findOrCreatePackage(project, packageName);
			className = fullPath.substring(fullPath.lastIndexOf(".") + 1, fullPath.length());
		}
		
		final String targetPath = (parent != null && parent.getOwner() != null) ? parent.getFullName(".") + "." + className : className;
		
		ProjectAccessor projectAccessor = null;
		INamedElement[] findElements = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			findElements = projectAccessor.findElements(new ModelFinder() {
				@Override
				public boolean isTarget(INamedElement element) {
					if (element instanceof IClass) {
						String elementPath = ((IClass) element).getFullName(".");
						return (targetPath.equals(elementPath));
					}
					return false;
				}
			});
		} catch (ProjectNotFoundException e) {
			throw new IllegalStateException("project is null.");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("ProjectAccessor class not found.");
		}
		
		IClass targetClass = null;
		if (findElements != null && findElements.length > 0) {
			targetClass = (IClass) findElements[0]; // クラスが既にあれば作らない
			if (!isInterface(targetClass)) {
				if (type.equals(TYPE_INTERFACE)) {
					targetClass.addStereotype(STEREOTYPE_INTERFACE);
				}
			}
			if (!isEnum(targetClass)) {
				if (type.equals(TYPE_ENUM)) {
					targetClass.addStereotype(STEREOTYPE_ENUM);
				}
			}
		} else {
			BasicModelEditor modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
			if (type.equals(TYPE_INTERFACE)) {
				targetClass = modelEditor.createInterface(parent, className);
			} else {
				targetClass = modelEditor.createClass(parent, className);
				if (type.equals(TYPE_ENUM)) {
					targetClass.addStereotype(STEREOTYPE_ENUM);
				}
			}
		}
		
		return targetClass;
	}
	
	private static boolean isInterface(IClass clazz) {
		String[] stereotypes = clazz.getStereotypes();
		if (stereotypes == null) {
			return false;
		}
		
		return (ArrayUtils.contains(stereotypes, STEREOTYPE_INTERFACE));
	}
	
	private static boolean isEnum(IClass clazz) {
		String[] stereotypes = clazz.getStereotypes();
		if (stereotypes == null) {
			return false;
		}
		
		return (ArrayUtils.contains(stereotypes, STEREOTYPE_ENUM));
	}
	
	public static IRealization findOrCreateRealization(final IClass clazz, final IClass implInterface) throws InvalidEditingException {
		//find realization
		IRealization[] realizations = clazz.getClientRealizations();
		for (int i = 0; i < realizations.length; i++) {
			IRealization realization = realizations[i];
			if (realization.getSupplier() == implInterface) {
				return realization; //found same realization
			}
		}
		
		//create new realization
		ProjectAccessor projectAccessor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("ProjectAccessor class not found.");
		}
		
		BasicModelEditor modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
		IRealization realization = modelEditor.createRealization(clazz, implInterface, "");
		
		return realization;
	}
	
	public static IGeneralization findOrCreateGeneralization(final IClass clazz, final IClass superClass) throws InvalidEditingException {
		//find generalization
		IGeneralization[] generalizations = clazz.getGeneralizations();
		for (int i = 0; i < generalizations.length; i++) {
			IGeneralization generalization = generalizations[i];
			if (generalization.getSuperType() == superClass) {
				return generalization; //found same generalization
			}
		}
		
		//create new generalization
		ProjectAccessor projectAccessor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("ProjectAccessor class not found.");
		}

		BasicModelEditor modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
		IGeneralization  generalization = modelEditor.createGeneralization(clazz, superClass, "");
		
		return generalization;
	}
	
	public static IAttribute findOrCreateField(final IClass clazz, final String name, IClass typeClass) throws InvalidEditingException {
		ProjectAccessor projectAccessor = null;
		INamedElement[] findElements = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			findElements = projectAccessor.findElements(new ModelFinder() {
				@Override
				public boolean isTarget(INamedElement element) {
					if (element instanceof IAttribute) {
						IElement owner = element.getOwner();
						return (owner == clazz && element.getName().equals(name));
					}
					return false;
				}
			});
		} catch (ProjectNotFoundException e) {
			throw new IllegalStateException("project is null.");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("ProjectAccessor class not found.");
		}
		
		IAttribute field = null;
		if (findElements != null && findElements.length > 0) {
			field = (IAttribute) findElements[0]; // 既にあれば作らない
		} else {
			BasicModelEditor modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
			field = modelEditor.createAttribute(clazz, name, typeClass);
		}
		
		return field;
	}
	
	public static void clearFieldsAndMethods(IClass clazz) {
		ProjectAccessor projectAccessor = null;
		BasicModelEditor modelEditor = null;
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			modelEditor = projectAccessor.getModelEditorFactory().getBasicModelEditor();
			IAttribute[] attributes = clazz.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				IAttribute attribute = attributes[i];
				modelEditor.delete(attribute);
			}
			IOperation[] methods = clazz.getOperations();
			for (int i = 0; i < methods.length; i++) {
				IOperation method = methods[i];
				modelEditor.delete(method);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (InvalidEditingException e) {
			e.printStackTrace();
			return;
		}
	}
}
