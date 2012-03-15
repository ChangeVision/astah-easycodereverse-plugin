package com.change_vision.astah.extension.plugin.easycodereverse.internal.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.AstahAPIHandler;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.util.AstahApiUtil;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ClassDiagramEditor;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IRealization;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class AstahModelBuilder {
	private static final Logger logger = LoggerFactory.getLogger(AstahModelBuilder.class);
	
	private final AstahAPIHandler handler = new AstahAPIHandler();
	private final BasicModelEditor modelEditor = handler.getBasicModelEditor();
	private final ClassDiagramEditor diagramEditor = handler.getClassDiagramEditor();
	
	private ClassInfo classInfo = null;
	private Point location = null;
	
	public AstahModelBuilder(ClassInfo classInfo, Point location) {
		this.classInfo = classInfo;
		this.location = location;
	}
	
	public IClass build() throws InvalidEditingException {
		return build(true);
	}
	
	public IClass build(boolean isInTransaction) throws InvalidEditingException {
		IClass clazz = null;
		
		ITransactionManager transactionManager = handler.getProjectAccessor().getTransactionManager();
		try {
			if (isInTransaction) transactionManager.beginTransaction();
			
			clazz = createClass();
			
			if (isInTransaction) transactionManager.endTransaction();
		} catch (InvalidEditingException e) {
			if (isInTransaction) transactionManager.abortTransaction();
			throw e;
		}
		return clazz;
	}
	
	public IClass createClass() throws InvalidEditingException {
		// Create model
		String type = AstahApiUtil.TYPE_CLASS;
		if (classInfo.isInterface()) {
			type = AstahApiUtil.TYPE_INTERFACE;
		} else if (classInfo.isEnum()) {
			type = AstahApiUtil.TYPE_ENUM;
		}
		
		IClass clazz = AstahApiUtil.findOrCreateClass(getCurrentProject(), classInfo.getQualifiedTypeName(), type);
		
		// clear fields and methods before add new properties
		AstahApiUtil.clearFieldsAndMethods(clazz);
		
		clazz.setLeaf(classInfo.isFinal());
		clazz.setAbstract(classInfo.isAbstract());
		clazz.setVisibility(findVisibility(classInfo));
    	
    	// Set diagram
		diagramEditor.setDiagram(handler.getClassDiagram());
		
		String className = classInfo.getName();
		INodePresentation[] classPresentations = findNodePresentation(className, AstahApiUtil.TYPE_CLASS);
		INodePresentation classPresentation = (classPresentations != null 
														&& classPresentations.length > 0) 
														? classPresentations[0] : null;
		if (classPresentation == null) {
			classPresentation = diagramEditor.createNodePresentation(clazz, location);
		}
		
		// Create impl interfaces
		for (ClassInfo implInterfaceInfo : classInfo.getImplementsInterfaces()) {
			createImplInterface(clazz, classPresentation, implInterfaceInfo);
		}
		
		// Create super classes
		for (ClassInfo superClassInfo : classInfo.getExtendsClasses()) {
			createSuperClass(clazz, classPresentation, superClassInfo);
		}
		
		// Create fields
		for (FieldInfo fieldInfo : classInfo.getFields()) {
			createField(clazz, fieldInfo);
		}
		
		// Create methods
		for (MethodInfo methodInfo : classInfo.getMethods()) {
			createMethod(clazz, methodInfo);
		}

    	return clazz;
	}

	private IClass createImplInterface(IClass clazz, INodePresentation classPresentation, ClassInfo interfaceInfo) throws InvalidEditingException {
		IClass implInterface = AstahApiUtil.findOrCreateInterface(getCurrentProject(), interfaceInfo.getQualifiedTypeName());

		String interfaceName = interfaceInfo.getName();
		INodePresentation[] interfacePresentations = findNodePresentation(interfaceName, AstahApiUtil.TYPE_CLASS);
		INodePresentation interfacePresentation = (interfacePresentations != null 
														&& interfacePresentations.length > 0) 
														? interfacePresentations[0] : null;
		if (interfacePresentation == null) {
			interfacePresentation = diagramEditor.createNodePresentation(implInterface, location);
		}
		
		try {
			IRealization realization = AstahApiUtil.findOrCreateRealization(clazz, implInterface);
			ILinkPresentation linkPs = findLinkPresentation(realization, classPresentation, interfacePresentation);
			if (linkPs == null) {
				diagramEditor.createLinkPresentation(realization, classPresentation, interfacePresentation);
			}
		} catch (Exception e) {e.printStackTrace();}
		
		return implInterface;
	}

	private IClass createSuperClass(IClass clazz,	INodePresentation classPresentation, ClassInfo superClassInfo) throws InvalidEditingException {
		String type = superClassInfo.isInterface() ? AstahApiUtil.TYPE_INTERFACE : AstahApiUtil.TYPE_CLASS;
		IClass superClass = AstahApiUtil.findOrCreateClass(getCurrentProject(), superClassInfo.getQualifiedTypeName(), type);
		superClass.setLeaf(superClassInfo.isFinal());
		superClass.setAbstract(superClassInfo.isAbstract());
		superClass.setVisibility(findVisibility(superClassInfo));

		String superClassName = superClassInfo.getName();
		INodePresentation[] superClassPresentations = findNodePresentation(superClassName, AstahApiUtil.TYPE_CLASS);
		INodePresentation superClassPresentation = (superClassPresentations != null 
														&& superClassPresentations.length > 0) 
														? superClassPresentations[0] : null;
		if (superClassPresentation == null) {
			superClassPresentation = diagramEditor.createNodePresentation(superClass, location);
		}
		
		try {
			IGeneralization generalization = AstahApiUtil.findOrCreateGeneralization(clazz, superClass);
			
			ILinkPresentation linkPs = findLinkPresentation(generalization, classPresentation, superClassPresentation);
			if (linkPs == null) {
				diagramEditor.createLinkPresentation(generalization, classPresentation, superClassPresentation);
			}
		} catch (Exception e) {e.printStackTrace();}
		
		return superClass;
	}

	private IAttribute createField(IClass clazz, FieldInfo fieldInfo) throws InvalidEditingException {
		String name = fieldInfo.getName();
		ClassInfo type = fieldInfo.getType();
		
		IAttribute field = null;
		if (!type.isPrimitive()) {
			IClass typeClass = AstahApiUtil.findOrCreateClass(getCurrentProject(), type.getQualifiedTypeName());
			field = modelEditor.createAttribute(clazz, name, typeClass);
		} else {
			field = modelEditor.createAttribute(clazz, name, type.getName());
		}
			
		String visibility = findVisibility(fieldInfo);
		if (StringUtils.isNotBlank(visibility)) {
			field.setVisibility(visibility);
		}
		
		field.setStatic(fieldInfo.isStatic());
		field.setChangeable(fieldInfo.isFinal());
		
		return field;
	}

	private IOperation createMethod(IClass clazz, MethodInfo methodInfo) throws InvalidEditingException {
		String name = methodInfo.getName();
		ClassInfo returnType = methodInfo.getReturnType();
		
		IOperation method = null;
		if (!returnType.isVoid() && !returnType.isPrimitive()) {
			IClass typeClass = AstahApiUtil.findOrCreateClass(getCurrentProject(), returnType.getQualifiedTypeName());	
			method = modelEditor.createOperation(clazz, name, typeClass);
		} else {
			method = modelEditor.createOperation(clazz, name, returnType.getName());
		}
					
		String visibility = findVisibility(methodInfo);
		if (StringUtils.isNotBlank(visibility)) {
			method.setVisibility(visibility);
		}
		
		method.setStatic(methodInfo.isStatic());
		method.setAbstract(methodInfo.isAbstract());
		method.setLeaf(methodInfo.isFinal());
		
		createParameters(method, methodInfo.getParameters());
		
		return method;
	}

	private List<IParameter> createParameters(IOperation method, List<ParameterInfo> params) throws InvalidEditingException {		
		List<IParameter> parameters = new ArrayList<IParameter>();
		if (params != null) {
			for (ParameterInfo param : params) {
				if (!param.getType().isPrimitive()) {
					IClass paramTypeClass = AstahApiUtil.findOrCreateClass(getCurrentProject(), param.getType().getQualifiedTypeName());
					parameters.add(modelEditor.createParameter(method, param.getName(), paramTypeClass));
				} else {
					parameters.add(modelEditor.createParameter(method, param.getName(), param.getType().getName()));
				}
			}
		}
		
		return parameters;
	}

	private INodePresentation[] findNodePresentation(String name, String type) {
		IClassDiagram classDiagram = handler.getClassDiagram();
		List<INodePresentation> nodePresentations = new ArrayList<INodePresentation>();
		try {
			IPresentation[] presentations = classDiagram.getPresentations();
			for (IPresentation presentation : presentations) {
				if (presentation instanceof INodePresentation
						&& type.equals(presentation.getType())
						&& name.equals(presentation.getLabel())) {
					nodePresentations.add((INodePresentation) presentation);
				}
			}
		} catch (InvalidUsingException e) {
			logger.error(e.getMessage(), e);
		}
		return nodePresentations.toArray(new INodePresentation[0]);
	}

	private ILinkPresentation findLinkPresentation(
			IElement model,
			INodePresentation sourcePresentation,
			INodePresentation targetPresentation) {
		IClassDiagram classDiagram = handler.getClassDiagram();
		try {
			IPresentation[] presentations = classDiagram.getPresentations();
			for (IPresentation presentation : presentations) {
				if (presentation instanceof ILinkPresentation
						&& presentation.getModel() == model) {
					ILinkPresentation linkPresentation = (ILinkPresentation)presentation;
					if ((linkPresentation.getSource() == sourcePresentation
							&& linkPresentation.getTarget() == targetPresentation)
							|| (linkPresentation.getSource() == targetPresentation
									&& linkPresentation.getTarget() == sourcePresentation)) {
						return linkPresentation;
					}
				}
			}
		} catch (InvalidUsingException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private IModel getCurrentProject() {
		IModel project = null;
		try {
			project = handler.getProjectAccessor().getProject();
		} catch (ProjectNotFoundException e) {
			throw new IllegalStateException("project is null.");
		}
		return project;
	}

	private String findVisibility(HasVisibility target) {
		if (target.isPublic()) {
			return INamedElement.PUBLIC_VISIBILITY;
		} else if (target.isProtected()) {
			return INamedElement.PROTECTED_VISIBILITY;
		} else if (target.isPackage()) {
			return INamedElement.PACKAGE_VISIBILITY;
		} else if (target.isPrivate()) {
			return INamedElement.PRIVATE_VISIBILITY;
		}
		return "";
	}
}
