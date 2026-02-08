package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuilderGenerator implements Runnable {

    protected static final String BUILDER_CLASS_NAME = "Builder";

    protected final Project project;
    protected final PsiFile file;
    protected final Editor editor;
    protected final List<PsiFieldMember> selectedFields;
    protected final PsiElementFactory psiElementFactory;

    public static void generate(Project project, Editor editor, PsiFile file, List<PsiFieldMember> selectedFields) {
        PsiClass targetClass = OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
        if (targetClass == null) {
            return;
        }

        Runnable builderGenerator = new BuilderGenerator(project, file, editor, selectedFields);
        ApplicationManager.getApplication().runWriteAction(builderGenerator);
    }

    protected BuilderGenerator(Project project, PsiFile file, Editor editor, List<PsiFieldMember> selectedFields) {
        this.project = project;
        this.file = file;
        this.editor = editor;
        this.selectedFields = selectedFields;
        this.psiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
    }

    @Override
    public void run() {
        PsiClass targetClass = OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
        if (targetClass == null) {
            return;
        }

        // builderClass
        PsiClass builderClass = createBuilderClass(targetClass);
        PsiType builderType = psiElementFactory.createTypeFromText(builderClass.getName(), null);

        // builderFields
        PsiElement lastField = null;
        if (ClassUtils.isRecordClass(targetClass)) {
            PsiField[] allFields = targetClass.getAllFields();
            for (PsiField psiField : allFields) {
                PsiField newField = generateBuilderField(psiField.getName(), psiField.getType());
                lastField = addField(builderClass, newField, lastField);
            }
        } else {
            for (PsiFieldMember fieldMember : selectedFields) {
                PsiField psiField = fieldMember.getElement();
                PsiField newField = generateBuilderField(psiField.getName(), psiField.getType());
                lastField = addField(builderClass, newField, lastField);
            }
        }

        // builderMethods
        PsiElement lastAddedElement = null;
        if (ClassUtils.isRecordClass(targetClass)) {
            PsiField[] allFields = targetClass.getAllFields();
            for (PsiField psiField : allFields) {
                PsiMethod setterMethod = generateBuilderSetter(builderType, psiField);
                lastAddedElement = addMethod(builderClass, lastAddedElement, setterMethod, false);
            }
        } else {
            for (PsiFieldMember member : selectedFields) {
                PsiMethod setterMethod = generateBuilderSetter(builderType, member.getElement());
                lastAddedElement = addMethod(builderClass, lastAddedElement, setterMethod, false);
            }
        }

        // builder.build() method
        PsiMethod buildMethod = generateBuildMethod(targetClass, builderClass);
        addMethod(builderClass, lastAddedElement, buildMethod, true);

        // constructor
        PsiMethod constructor = createConstructor(targetClass, builderClass);
        addMethod(targetClass, null, constructor, false);

        // new builder
        PsiMethod newBuilderMethod = generateNewBuilderMethod(builderType);
        addMethod(targetClass, null, newBuilderMethod, true);

        // format
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(file);
        CodeStyleManager.getInstance(project).reformat(builderClass);
    }

    private PsiMethod generateNewBuilderMethod(PsiType builderType) {
        PsiMethod newBuilderMethod = psiElementFactory.createMethod("builder", builderType);
        PsiUtil.setModifierProperty(newBuilderMethod, PsiModifier.STATIC, true);
        PsiUtil.setModifierProperty(newBuilderMethod, PsiModifier.PUBLIC, true);

        PsiCodeBlock newBuilderMethodBody = newBuilderMethod.getBody();
        if (newBuilderMethodBody != null) {
            PsiStatement newStatement = psiElementFactory.createStatementFromText(
                    String.format("return new %s();", builderType.getPresentableText()),
                    newBuilderMethod
            );
            newBuilderMethodBody.add(newStatement);
        }
        return newBuilderMethod;
    }

    private PsiField generateBuilderField(String fieldName, PsiType fieldType) {
        return psiElementFactory.createField(fieldName, fieldType);
    }

    private PsiMethod generateBuilderSetter(PsiType builderType, PsiField psiField) {
        PsiType fieldType = psiField.getType();
        String fieldName = psiField.getName();

        PsiParameter setterParameter = psiElementFactory.createParameter(fieldName, fieldType);
        PsiMethod setterMethod = psiElementFactory.createMethod(fieldName, builderType);
        setterMethod.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);
        setterMethod.getParameterList().add(setterParameter);

        PsiCodeBlock setterMethodBody = setterMethod.getBody();
        if (setterMethodBody != null) {
            PsiStatement assignStatement = psiElementFactory.createStatementFromText(
                    String.format("this.%s = %s;", fieldName, fieldName),
                    setterMethod
            );
            PsiStatement returnThisStatement = psiElementFactory.createStatementFromText("return this;", null);
            setterMethodBody.add(assignStatement);
            setterMethodBody.add(returnThisStatement);
        }
        return setterMethod;
    }


    private PsiMethod createConstructor(PsiClass targetClass, PsiClass builderClass) {
        PsiMethod allArgsConstructor = psiElementFactory.createConstructor(targetClass.getName());
        allArgsConstructor.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiField[] allFields = builderClass.getAllFields();

        for (PsiField psiField : allFields) {
            PsiParameter parameter = psiElementFactory.createParameter(psiField.getName(), psiField.getType());
            allArgsConstructor.getParameterList().add(parameter);
        }

        PsiCodeBlock constructorBody = allArgsConstructor.getBody();
        if (constructorBody != null) {
            for (PsiField psiField : allFields) {
                String fieldName = psiField.getName();
                String assignText = String.format("this.%s = %s;", fieldName, fieldName);
                PsiStatement assignStatement = psiElementFactory.createStatementFromText(assignText, null);

                constructorBody.add(assignStatement);
            }
        }

        return allArgsConstructor;
    }

    private PsiMethod generateBuildMethod(PsiClass targetClass, PsiClass builderClass) {
        PsiType targetClassType = psiElementFactory.createType(targetClass);
        PsiMethod buildMethod = psiElementFactory.createMethod("build", targetClassType);
        buildMethod.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiCodeBlock buildMethodBody = buildMethod.getBody();
        if (buildMethodBody != null) {
            String fieldNames = Arrays.stream(builderClass.getAllFields())
                    .map(PsiField::getName)
                    .collect(Collectors.joining(", "));
            PsiStatement returnStatement = psiElementFactory.createStatementFromText(
                    String.format("return new %s(%s);", targetClass.getName(), fieldNames),
                    buildMethod
            );
            buildMethodBody.add(returnStatement);
        }

        return buildMethod;
    }


    private PsiClass createBuilderClass(PsiClass targetClass) {
        PsiClass builderClass = targetClass.findInnerClassByName(BUILDER_CLASS_NAME, false);
        if (builderClass == null) {
            builderClass = (PsiClass) targetClass.add(psiElementFactory.createClass(BUILDER_CLASS_NAME));
            PsiUtil.setModifierProperty(builderClass, PsiModifier.STATIC, true);
        }
        return builderClass;
    }

    private PsiElement addField(PsiClass targetClass, PsiField field, PsiElement after) {
        String fieldName = field.getName();

        PsiField existField = targetClass.findFieldByName(fieldName, false);
        if (existField != null) {
            return existField;
        }

        return targetClass.addAfter(field, after);
    }

    private PsiElement addMethod(PsiClass targetClass, PsiElement after, PsiMethod newMethod, boolean replace) {
        PsiMethod existingMethod = null;

        if (newMethod.isConstructor()) {
            for (PsiMethod constructor : targetClass.getConstructors()) {
                if (BuilderHelper.areParameterListsEqual(constructor.getParameterList(), newMethod.getParameterList())) {
                    existingMethod = constructor;
                    break;
                }
            }
        } else  {
            PsiMethod[] existMethods = targetClass.findMethodsByName(newMethod.getName(), false);
            for (PsiMethod method : existMethods) {
                if (BuilderHelper.areParameterListsEqual(method.getParameterList(), newMethod.getParameterList())) {
                    existingMethod = method;
                    break;
                }
            }
        }

        if (existingMethod == null) {
            if (after != null) {
                return targetClass.addAfter(newMethod, after);
            } else {
                return targetClass.add(newMethod);
            }
        } else if (replace) {
            try {
                existingMethod.replace(newMethod);
            } catch (Exception ex) {
                // ignore
            }
        }

        return existingMethod;
    }

}