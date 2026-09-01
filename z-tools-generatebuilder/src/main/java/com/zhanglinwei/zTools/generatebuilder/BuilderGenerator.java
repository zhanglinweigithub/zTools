package com.zhanglinwei.zTools.generatebuilder;

import com.intellij.codeInsight.generation.OverrideImplementUtil;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.generatebuilder.enums.BuilderOption;
import com.zhanglinwei.zTools.util.AssertUtils;
import com.zhanglinwei.zTools.util.ClassUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BuilderGenerator implements Runnable {

    protected static final String BUILDER_CLASS_NAME = "Builder";
    protected static final String CLASS_GENERIC = "C";
    protected static final String BUILDER_GENERIC = "B";

    protected final Project project;
    protected final PsiFile file;
    protected final Editor editor;
    protected final List<PsiFieldMember> selectedFields;
    protected final PsiElementFactory psiElementFactory;
    protected Set<BuilderOption> builderOptions;

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

        builderOptions = currentSelectedBuilderOptions();

        // builderClass
        PsiClass builderClass = createBuilderClass(targetClass);
        if (builderClass == null) {
            return;
        }
        PsiType builderType = psiElementFactory.createTypeFromText(builderClass.getName(), null);

        // builderFields
        PsiElement lastField = null;
        if (ClassUtils.isRecordClass(targetClass)) {
            PsiField[] allFields = targetClass.getAllFields();
            for (PsiField psiField : allFields) {
                PsiField newField = generateBuilderField(psiField.getName(), psiField.getType(), null);
                lastField = addField(builderClass, newField, lastField);
            }
        } else {
            for (PsiFieldMember fieldMember : selectedFields) {
                String modifier = isLiteBuilder() ? PsiModifier.PRIVATE : PsiModifier.PROTECTED;
                PsiField psiField = fieldMember.getElement();
                PsiField newField = generateBuilderField(psiField.getName(), psiField.getType(), modifier);

                lastField = addField(builderClass, newField, lastField);
            }
        }

        // builderMethods
        PsiElement lastAddedElement = null;
        if (ClassUtils.isRecordClass(targetClass)) {
            PsiField[] allFields = targetClass.getAllFields();
            for (PsiField psiField : allFields) {
                PsiMethod setterMethod = generateBuilderSetter(builderType, psiField, "return this;");
                lastAddedElement = addMethod(builderClass, lastAddedElement, setterMethod, false);
            }
        } else {
            PsiType builderMethodReturnType = isLiteBuilder() ? builderType : psiElementFactory.createTypeFromText(BUILDER_GENERIC, null);
            String builderMethodReturnStatement = isLiteBuilder() ? "return this;" : "return (" + BUILDER_GENERIC + ") this;";

            for (PsiFieldMember member : selectedFields) {
                PsiMethod setterMethod = generateBuilderSetter(builderMethodReturnType, member.getElement(), builderMethodReturnStatement);
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
        PsiMethod newBuilderMethod = generateNewBuilderMethod(targetClass, builderClass);
        addMethod(targetClass, null, newBuilderMethod, true);

        // format
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(file);
        CodeStyleManager.getInstance(project).reformat(builderClass);
    }

    private PsiMethod generateNewBuilderMethod(PsiClass targetClass, PsiClass builderClass) {
        PsiMethod newBuilderMethod;
        if (isLiteBuilder() || ClassUtils.isRecordClass(targetClass)) {
            PsiType builderType = psiElementFactory.createTypeFromText(builderClass.getName(), null);
            newBuilderMethod = psiElementFactory.createMethod("builder", builderType);
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
        } else {
            // 非 lite：返回 Builder<?, ?>，方法体 return new Builder<>();
            PsiType builderWildcardType = psiElementFactory.createTypeFromText(
                    builderClass.getName() + "<?, ?>", targetClass);
            newBuilderMethod = psiElementFactory.createMethod("builder", builderWildcardType);
            PsiUtil.setModifierProperty(newBuilderMethod, PsiModifier.STATIC, true);
            PsiUtil.setModifierProperty(newBuilderMethod, PsiModifier.PUBLIC, true);

            PsiCodeBlock body = newBuilderMethod.getBody();
            if (body != null) {
                PsiStatement returnStmt = psiElementFactory.createStatementFromText(
                        "return new " + builderClass.getName() + "<>();", newBuilderMethod);
                body.add(returnStmt);
            }
        }
        return newBuilderMethod;
    }

    private PsiField generateBuilderField(String fieldName, PsiType fieldType, String modifier) {
        PsiField psiField = psiElementFactory.createField(fieldName, fieldType);
        if (AssertUtils.isNotBlank(modifier)) {
            PsiUtil.setModifierProperty(psiField, modifier, true);
        }
        return psiField;
    }

    private PsiMethod generateBuilderSetter(PsiType builderType, PsiField psiField, String returnStatement) {
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
            PsiStatement returnThisStatement = psiElementFactory.createStatementFromText(returnStatement, null);
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
        PsiType buildMethodReturnType;
        if (isLiteBuilder() || ClassUtils.isRecordClass(targetClass)) {
            buildMethodReturnType = psiElementFactory.createType(targetClass);
        } else {
            buildMethodReturnType = psiElementFactory.createTypeFromText(CLASS_GENERIC, null);
        }

        PsiMethod buildMethod = psiElementFactory.createMethod("build", buildMethodReturnType);
        buildMethod.getModifierList().setModifierProperty(PsiModifier.PUBLIC, true);

        PsiCodeBlock buildMethodBody = buildMethod.getBody();
        if (buildMethodBody != null) {
            String fieldNames = Arrays.stream(builderClass.getAllFields())
                    .map(PsiField::getName)
                    .collect(Collectors.joining(", "));

            PsiStatement returnStatement;
            if (isLiteBuilder() || ClassUtils.isRecordClass(targetClass)) {
                returnStatement = psiElementFactory.createStatementFromText(
                        String.format("return new %s(%s);", targetClass.getName(), fieldNames),
                        buildMethod
                );
            } else {
                returnStatement = psiElementFactory.createStatementFromText(
                        String.format("return (" + CLASS_GENERIC + ") new %s(%s);", targetClass.getName(), fieldNames),
                        buildMethod
                );
            }
            buildMethodBody.add(returnStatement);
        }

        return buildMethod;
    }

    private PsiClass createBuilderClass(PsiClass targetClass) {
        PsiClass builderClass = targetClass.findInnerClassByName(BUILDER_CLASS_NAME, false);
        if (builderClass == null) {
            builderClass = (PsiClass) targetClass.add(psiElementFactory.createClass(BUILDER_CLASS_NAME));
            PsiUtil.setModifierProperty(builderClass, PsiModifier.STATIC, true);

            if (isLiteBuilder() || ClassUtils.isRecordClass(targetClass)) {
                return builderClass;
            }

            enrichGenerics(builderClass, targetClass);
        }
        return builderClass;
    }

    private void enrichGenerics(PsiClass builderClass, PsiClass targetClass) {
        PsiTypeParameterList typeParameterList = builderClass.getTypeParameterList();
        if (typeParameterList == null) {
            typeParameterList = psiElementFactory.createTypeParameterList();
            builderClass.add(typeParameterList);
        }

        // Parse type parameters from method text so "B extends Outer.Builder<C, B>" is kept in AST (not createTypeParameter which loses it)
        String outerName = targetClass.getName();
        String methodText = "public <" + CLASS_GENERIC + " extends " + outerName + ", " + BUILDER_GENERIC + " extends " + outerName + "." + BUILDER_CLASS_NAME + "<" + CLASS_GENERIC + ", " + BUILDER_GENERIC + ">> void _() {}";
        PsiMethod dummyMethod = psiElementFactory.createMethodFromText(methodText, targetClass);
        for (PsiTypeParameter p : dummyMethod.getTypeParameters()) {
            typeParameterList.add(p.copy());
        }
    }

    private boolean isLiteBuilder() {
        return builderOptions.contains(BuilderOption.USE_LITE_BUILDER);
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

    private static Set<BuilderOption> currentSelectedBuilderOptions() {
        Set<BuilderOption> builderOptions = new HashSet<>();
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

        for (BuilderOption builderOption : BuilderOption.values()) {
            if (builderOption.checkBox) {
                boolean isSelected = propertiesComponent.getBoolean(builderOption.name(), false);
                if (isSelected) {
                    builderOptions.add(builderOption);
                }
            }
        }

        return builderOptions;
    }

}