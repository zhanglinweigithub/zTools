package com.zhanglinwei.zTools.doc.apidoc.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.doc.config.DocConfig;
import com.zhanglinwei.zTools.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.common.constants.SpringPool.EMPTY;

public class JavaProperty {

    private String name;
    private String originName;
    private String typeName;
    private String comment;
    private Object example;
    private boolean required;

    private JavaProperty parent;
    private List<JavaProperty> children;
    private boolean cycle;

    private PsiType psiType;
    private Project project;
    private PsiAnnotation[] psiAnnotations;
    private Map<PsiTypeParameter, PsiType> genericTypeMap;

    private JavaProperty(){}

    private JavaProperty(PsiType psiType, Project project, JavaProperty parent) {
        this.psiType = psiType;
        this.project = project;
        this.parent = parent;
    }

    public boolean hasAnnotation(String annotationName) {
        return AnnotationUtil.findAnnotationByName(psiAnnotations, annotationName) != null;
    }

    public boolean hasChildren() {
        return AssertUtils.isNotEmpty(this.children);
    }

    private static JavaProperty create(PsiParameter psiParameter, Map<String, String> paramDescMap) {
        PsiType parameterType = psiParameter.getType();
        PsiAnnotation[] annotations = psiParameter.getAnnotations();

        JavaProperty javaProperty = new JavaProperty();
        javaProperty.setPsiType(parameterType);
        javaProperty.setProject(psiParameter.getProject());
        javaProperty.setPsiAnnotations(annotations);
        javaProperty.setOriginName(psiParameter.getName());
        javaProperty.setName(AnnotationUtil.extractParamName(psiParameter.getName(), annotations));
        javaProperty.setTypeName(parameterType.getPresentableText());
        javaProperty.setGenericTypeMap(resolveGenerics(parameterType));
        javaProperty.setComment(paramDescMap.get(psiParameter.getName()));
        javaProperty.setExample(ExampleUtils.createNormalExample(parameterType, annotations));
        javaProperty.setRequired(AnnotationUtil.isRequired(annotations));
        javaProperty.setParent(null);
        javaProperty.setCycle(false);
        javaProperty.setChildren(resolveChildren(javaProperty));

        return javaProperty;
    }

    public static List<JavaProperty> create(PsiMethod psiMethod) {
        PsiDocComment docComment = psiMethod.getDocComment();
        Map<String, String> paranDescMap = CommentsUtil.extractParamCommentsMap(docComment);
        return Arrays.stream(psiMethod.getParameterList().getParameters())
                .map(item -> create(item, paranDescMap))
                .collect(Collectors.toList());
    }

    public static JavaProperty create(PsiField psiField, JavaProperty parent) {
        PsiType fieldType = resolveGeneric(psiField.getType(), parent);

        JavaProperty property = new JavaProperty();
        property.setProject(psiField.getProject());
        property.setPsiType(fieldType);
        property.setTypeName(fieldType.getPresentableText());
        property.setPsiAnnotations(psiField.getAnnotations());
        property.setParent(parent);
        property.setName(AnnotationUtil.extractParamName(psiField.getName(), psiField.getAnnotations()));
        property.setOriginName(psiField.getName());
        property.setComment(CommentsUtil.extractComments(psiField, EMPTY));
        property.setGenericTypeMap(resolveGenerics(fieldType));
        property.setRequired(AnnotationUtil.isRequired(psiField.getAnnotations()));
        property.setExample(ExampleUtils.createNormalExample(fieldType, psiField.getAnnotations()));

        if (TypeUtils.isIterableType(fieldType)) {
            TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(fieldType);
            JavaProperty simple = JavaProperty.createSimple(nestedInfo.getRealType(), psiField.getProject(), parent);
            property.setCycle(simple.isCycle());
            property.setChildren(simple.getChildren());
        } else {
            property.setChildren(resolveChildren(property));
        }

        return property;
    }

    public static JavaProperty createSimple(PsiType psiType, Project project, JavaProperty parent) {
        JavaProperty property = new JavaProperty(psiType, project, parent);
        property.setGenericTypeMap(resolveGenerics(psiType));
        property.setChildren(resolveChildren(property));
        return property;
    }

    private static boolean needResolveChildren(JavaProperty javaProperty) {
        PsiType psiType = javaProperty.getPsiType();
        if (psiType == null) {
            return false;
        }

        // 1. 可迭代普通类型
        if (TypeUtils.isIterableType(psiType)) {
            PsiType realType = TypeUtils.deepExtractIterableType(psiType).getRealType();
            if (TypeUtils.isNormalType(realType)) {
                return false;
            }
        }

        // 2. 普通类型
        if(TypeUtils.isNormalType(psiType)) {
            return false;
        }

        // 3. 枚举类型
        if(TypeUtils.isEnum(psiType)) {
            return false;
        }

        // 4. Map 类型
        if(TypeUtils.isMapType(psiType)) {
            return false;
        }

        // 5. Multipart 类型
        if(TypeUtils.isMultipartType(psiType)) {
            return false;
        }

        // 6. IO 相关
        if(TypeUtils.isIOType(psiType)) {
            return false;
        }

        // 7. Servlet 相关
        if(TypeUtils.isServletType(psiType)) {
            return false;
        }

        // 8. 循环引用
        if (hasCycleReference(psiType, javaProperty.getParent())) {
            javaProperty.setCycle(true);
            return false;
        }

        return true;
    }

    private static boolean hasCycleReference(PsiType psiType, JavaProperty propertyParent) {
        if (propertyParent == null) {
            return false;
        }

        Set<PsiType> resolvedTypeSet = new HashSet<>();
        JavaProperty dummyParent = propertyParent;
        while (dummyParent != null) {
            resolvedTypeSet.add(dummyParent.getPsiType());
            dummyParent = dummyParent.getParent();
        }

        PsiType realType = TypeUtils.deepExtractIterableType(psiType).getRealType();
        return resolvedTypeSet.contains(realType);
    }


    private static List<JavaProperty> resolveChildren(JavaProperty parentProperty) {
        PsiType parameterType = parentProperty.getPsiType();

        // 1. 是否需要解析
        if (!needResolveChildren(parentProperty)) {
            return Collections.emptyList();
        }

        // 2. 如果是数组
        if(parameterType instanceof PsiArrayType) {
            return resolveArrayType((PsiArrayType) parameterType, parentProperty);
        }

        // 3. 如果是JavaClass
        if (parameterType instanceof PsiClassType) {
            return resolveClassType((PsiClassType) parameterType, parentProperty);
        }

        return Collections.emptyList();
    }

    private static List<JavaProperty> resolveArrayType(PsiArrayType arrayType, JavaProperty parent) {
        // 1. 得到数组类型
        PsiType componentType = arrayType.getComponentType();
        // 2. 创建真实类型对象
        JavaProperty child = new JavaProperty(componentType, parent.getProject(), parent);
        // 3. 解析子属性
        return resolveChildren(child);
    }

    private static List<JavaProperty> resolveCollectionType(PsiClassType collectionType, JavaProperty parent) {
        // 1. 提取集合元素类型（支持多层泛型）
        TypeUtils.NestedInfo nestedInfo = TypeUtils.deepExtractIterableType(collectionType);
        PsiType realType = nestedInfo.getRealType();
        if (realType == null) {
            return Collections.emptyList();
        }

        // 2. 解析泛型实际类型（如 List<T> 中的 T）
        PsiType resolvedType = resolveGeneric(realType, parent);
        if (resolvedType == null) {
            return Collections.emptyList();
        }

        // 3. 创建真实类型对象
        JavaProperty elementProperty = new JavaProperty(resolvedType, parent.getProject(), parent);

        // 4. 处理循环引用标记
        if (hasCycleReference(resolvedType, parent)) {
            elementProperty.setCycle(true);
            return Collections.singletonList(elementProperty);
        }

        // 5. 继续解析子属性
        return resolveChildren(elementProperty);
    }

    private static List<JavaProperty> resolveClassType(PsiClassType classType, JavaProperty parent) {
        DocConfig.ApiDocConfig config = DocConfig.getInstance(parent.getProject()).getApiDocConfig();

        // 1. 处理集合类型
        if (TypeUtils.isCollectionType(classType)) {
            return resolveCollectionType(classType, parent);
        }

        // 2. 处理普通类类型 解析泛型 获得Class
        PsiType resolvedType = resolveGeneric(classType, parent);
        PsiClass psiClass = PsiUtil.resolveClassInType(resolvedType);
        if (psiClass == null) {
            return Collections.emptyList();
        }

        // 3. 处理第三方 JAR 包中的类
        if (psiClass instanceof ClsClassImpl) {
            psiClass = resolveJarPackage(psiClass);
        }

        // 4. 收集
        return new ArrayList<>(
                Arrays.stream(psiClass.getAllFields())
                        .filter(field -> !config.getExcludeFieldList().contains(field.getName())) // 过滤配置忽略的字段
                        .filter(field -> !ModifierUtils.isStatic(field)) // 过滤静态字段
                        .filter(field -> !ModifierUtils.isIgnored(field)) // 过滤需要忽略的字段
                        .collect(
                                Collectors.toMap(
                                        PsiField::getName,
                                        field -> JavaProperty.create(field, parent),
                                        (oldVal, newVal) -> oldVal, // 继承的情况下会出现子类与父类拥有相同名称的字段 保留首次出现的字段
                                        LinkedHashMap::new
                                )
                        )
                        .values()
        );
    }

    private static PsiClass resolveJarPackage(PsiClass psiClass) {
        Project currentProject = psiClass.getProject();
        StubElement parentStub = ((ClsClassImpl) psiClass).getStub().getParentStub();
        if(parentStub instanceof PsiJavaFileStubImpl) {
            String sourcePath = ((PsiJavaFileStubImpl) parentStub)
                    .getPsi().getViewProvider().getVirtualFile().toString()
                    .replace(".jar!", "-sources.jar!");
            sourcePath = sourcePath.substring(0, sourcePath.length() - 5)+"java";
            VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(sourcePath);
            if(virtualFile != null) {
                FileViewProvider fileViewProvider = new SingleRootFileViewProvider(PsiManager.getInstance(currentProject), virtualFile);
                PsiFile psiFile = new PsiJavaFileImpl(fileViewProvider);
                return PsiTreeUtil.findChildOfAnyType(psiFile.getOriginalElement(), PsiClass.class);
            }
        }

        return psiClass;
    }

    private static Map<PsiTypeParameter, PsiType> resolveGenerics(PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return Collections.emptyMap();
        }

        PsiClassType classType = (PsiClassType) psiType;
        PsiClass psiClass = classType.resolve();
        if (psiClass == null) {
            return Collections.emptyMap();
        }

        PsiTypeParameter[] genericParameters = psiClass.getTypeParameters(); // 泛型类型
        PsiType[] actualParameters = classType.getParameters(); // 实际类型

        int len = Math.min(genericParameters.length, actualParameters.length);
        Map<PsiTypeParameter, PsiType> mapping = new HashMap<>();
        for (int i = 0; i < len; i++) {
            mapping.put(genericParameters[i], actualParameters[i]);
        }

        return mapping;
    }

    /**
     * 根据泛型获取对应的PsiType
     */
    private static PsiType resolveGeneric(PsiType psiType, JavaProperty javaProperty) {
        if(null == psiType){
            return null;
        }

        JavaProperty propertyParent = javaProperty.getParent();
        Map<PsiTypeParameter, PsiType> mapping = propertyParent != null ? propertyParent.getGenericTypeMap() : javaProperty.getGenericTypeMap();

        if(AssertUtils.isNotEmpty(mapping)){
            for (PsiTypeParameter psiTypeParameter : mapping.keySet()) {
                if(Objects.equals(psiTypeParameter.getName(), psiType.getPresentableText())){
                    return mapping.get(psiTypeParameter);
                }
            }
        }
        return psiType;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public JavaProperty getParent() {
        return parent;
    }

    public void setParent(JavaProperty parent) {
        this.parent = parent;
    }

    public List<JavaProperty> getChildren() {
        return children;
    }

    public void setChildren(List<JavaProperty> children) {
        this.children = children;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public void setPsiType(PsiType psiType) {
        this.psiType = psiType;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public PsiAnnotation[] getPsiAnnotations() {
        return psiAnnotations;
    }

    public void setPsiAnnotations(PsiAnnotation[] psiAnnotations) {
        this.psiAnnotations = psiAnnotations;
    }

    public Map<PsiTypeParameter, PsiType> getGenericTypeMap() {
        return genericTypeMap;
    }

    public void setGenericTypeMap(Map<PsiTypeParameter, PsiType> genericTypeMap) {
        this.genericTypeMap = genericTypeMap;
    }
}
