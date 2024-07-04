package com.zhanglinwei.zTools.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.zhanglinwei.zTools.constant.DocConstants;
import com.zhanglinwei.zTools.constant.TypeEnum;
import com.zhanglinwei.zTools.normal.FieldFactory;
import com.zhanglinwei.zTools.normal.FieldType;
import com.zhanglinwei.zTools.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.zhanglinwei.zTools.util.CommonUtils.VERTICAL;
import static com.zhanglinwei.zTools.util.CommonUtils.YES;

public class FieldInfo {

    /** 排除的字段 */
//    public static final List<String> excludeFields = new ArrayList<>();
//
//    static {
//        excludeFields.add("serialVersionUID");
//    }

    /** 字段名称 */
    private String name;
    /** 字段类型 */
    private PsiType psiType;
    /** 必填 */
    private String required;
    /** 取值范围 */
    private String range;
    /** 注释 */
    private String desc = "";
    private String value = "";
    /** 示例 */
    private Object example = "";
    /** 字段类型 对象、集合、普通 */
    private TypeEnum paramType;
    private FieldType fieldType;
    /** 子 */
    private List<FieldInfo> children = new ArrayList<>();
    /** 父 */
    private FieldInfo parent;
    /** 注解 */
    private List<PsiAnnotation> annotations;
    private Project project;
    /** 兼容泛型 */
    private Map<PsiTypeParameter, PsiType> genericTypeMap;

    /** 循环引用 */
    private boolean cycle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldInfo fieldInfo = (FieldInfo) o;
        return name.equals(fieldInfo.name) &&
                Objects.equals(parent, fieldInfo.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parent);
    }

    public List<FieldInfo> cutFirstLayer() {
        if (TypeEnum.LITERAL.equals(this.paramType) || TypeEnum.ARRAY.equals(this.paramType)) {
            return Collections.singletonList(this);
        }
        if (TypeEnum.OBJECT.equals(this.paramType) && this.hasChildren()) {
            return this.children.stream().filter(field -> !field.hasChildren()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public String toMarkDownString(boolean withType) {
        return toMarkDownString(withType, "");
    }

    public String toMarkDownString(boolean withType, String prefix) {
        StringJoiner joiner = new StringJoiner(VERTICAL);
        if (withType) {
            return joiner.add(prefix + getFieldName(this, DocConstants.MD)).add(psiType.getPresentableText()).add(required).add(range).add(String.valueOf(example)).add(desc).toString() + "\n";
        }

        return joiner.add(prefix + getFieldName(this, DocConstants.MD)).add(required).add(range).add(String.valueOf(example)).add(desc).toString() + "\n";
    }

    public String toHtmlString(boolean withType) {
        return toHtmlString(withType, "");
    }

    public String toHtmlString(boolean withType, String prefix) {
        return toHtmlString(withType, prefix, DocConstants.ALIGN_LEFT);
    }

    public String toHtmlString(boolean withType, String prefix, String align) {
        if (withType) {
            return "<tr>\n" +
                    "<td align='" + align + "'>" + prefix + getFieldName(this, DocConstants.HTML) + "</td>\n" +
                    "<td align='" + align + "'>" + psiType.getPresentableText() + "</td>\n" +
                    "<td align='" + align + "'>" + required + "</td>\n" +
                    "<td align='" + align + "'>" + CommonUtils.getRange(range) + "</td>\n" +
                    "<td align='" + align + "'>" + example + "</td>\n" +
                    "<td align='" + align + "'>" + desc + "</td>\n" +
                    "</tr>\n";
        }

        return "<tr>\n" +
                "<td align='" + align + "'>" + prefix + getFieldName(this, DocConstants.HTML) + "</td>\n" +
                "<td align='" + align + "'>" + required + "</td>\n" +
                "<td align='" + align + "'>" + CommonUtils.getRange(range) + "</td>\n" +
                "<td align='" + align + "'>" + example + "</td>\n" +
                "<td align='" + align + "'>" + desc + "</td>\n" +
                "</tr>\n";
    }

    public List<String> toWordList(boolean withType) {
        return toWordList(withType, "");
    }

    public List<String> toWordList(boolean withType, String prefix) {
        List<String> stringList = new ArrayList<>();
        stringList.add(prefix + name);
        if (withType) {
            stringList.add(psiType.getPresentableText());
        }
        stringList.add(required);
        stringList.add(range);
        stringList.add(String.valueOf(example));
        stringList.add(desc);

        return stringList;
    }

    private String getFieldName(FieldInfo info, String fileType) {
        String fileName = info.getName();
        if (info.hasChildren()) {
            switch (fileType) {
                case DocConstants.HTML: return "<b>" + fileName + "</b>\n";
                case DocConstants.MD: return " **" + fileName + "** ";
                default: return fileName;
            }
        }
        return fileName;
    }

    /**
     * 根据泛型获取对应的PsiType
     */
    private PsiType resolveGeneric(PsiType psiType){
        if(null == psiType){
            return null;
        }
        Map<PsiTypeParameter, PsiType> map;
        if(this.parent != null){
            map = this.parent.genericTypeMap;
        }else {
            map = this.genericTypeMap;
        }
        if(AssertUtils.isNotEmpty(map)){
            for (PsiTypeParameter psiTypeParameter : map.keySet()) {
                if(Objects.equals(psiTypeParameter.getName(), psiType.getPresentableText())){
                    return map.get(psiTypeParameter);
                }
            }
        }
        return psiType;
    }

    public boolean hasChildren() {
        return AssertUtils.isNotEmpty(children);
    }

    /** 解析children */
    public void resolveChildren() {
        PsiType psiType = this.psiType;
        // 如果是数组
        if(psiType instanceof PsiArrayType) {
            // 数组类型
            PsiType componentType = ((PsiArrayType) psiType).getComponentType();
            FieldInfo fieldInfo = FieldFactory.buildByPsiType(componentType, this.parent, this.project);
            children =  fieldInfo.children;
            return;
        }
        if (psiType instanceof PsiClassType) {
            // 如果是集合类型
            if (FieldUtil.isCollectionType(psiType)) {
                PsiType iterableType = PsiUtil.extractIterableTypeParameter(psiType, false);
                if (iterableType == null) {
                    return;
                }
                // 兼容泛型
                PsiType realType = resolveGeneric(iterableType);
                FieldInfo fieldInfo = FieldFactory.buildByPsiType(realType, this.parent, this.project);
                children = fieldInfo.children;
                return ;
            }

            // 兼容泛型
            PsiType realType = resolveGeneric(psiType);
            PsiClass psiClass = PsiUtil.resolveClassInType(realType);
            if (psiClass == null) {
                return;
            }
            // 兼容第三方jar包
            if (psiClass instanceof ClsClassImpl){
                StubElement parentStub = ((ClsClassImpl) psiClass).getStub().getParentStub();
                if(parentStub instanceof  PsiJavaFileStubImpl) {
                    String sourcePath = ((PsiJavaFileStubImpl) parentStub)
                            .getPsi().getViewProvider().getVirtualFile().toString()
                            .replace(".jar!", "-sources.jar!");
                    sourcePath = sourcePath.substring(0, sourcePath.length() - 5)+"java";
                    VirtualFile virtualFile =
                            VirtualFileManager.getInstance().findFileByUrl(sourcePath);
                    if(virtualFile != null) {
                        FileViewProvider fileViewProvider = new SingleRootFileViewProvider(PsiManager.getInstance(project), virtualFile);
                        PsiFile psiFile1 = new PsiJavaFileImpl(fileViewProvider);
                        psiClass = PsiTreeUtil.findChildOfAnyType(psiFile1.getOriginalElement(), PsiClass.class);
                    }
                }
            }
            // 兼容继承
            Set<String> fieldNameList = new HashSet<>(128);
            for (PsiField psiField : psiClass.getAllFields()) {
                if (ConfigUtils.getExcludeFieldList().contains(psiField.getName())) {
                    continue;
                }
                if(FieldUtil.isStaticField(psiField)) {
                    continue;
                }
                if(FieldUtil.isIgnoredField(psiField)) {
                    continue;
                }
                // 继承情况下会出现子类与父类拥有相同名称的字段
                if(fieldNameList.contains(psiField.getName())) {
                    continue;
                }
                PsiType fieldType = psiField.getType();
                // 兼容泛型
                PsiType realFieldType = resolveGeneric(fieldType);
                FieldInfo fieldInfo = FieldFactory.buildFieldWithParent(project,this,psiField.getName(), realFieldType, DesUtil.getDescription(psiField.getDocComment(), psiField.getAnnotations()), psiField.getAnnotations());
                children.add(fieldInfo);
                fieldNameList.add(fieldInfo.getName());
            }
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = "".equals(example) ? " " : example;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    public boolean isRequired() {
        return required == YES;
    }

    public String getRequired() {
        return required;
    }

    public String getRange() {
        return range;
    }

    public String getDesc() {
        return desc == null ? " " : desc;
    }

    public TypeEnum getParamType() {
        return paramType;
    }

    public List<FieldInfo> getChildren() {
        return children;
    }

    public List<PsiAnnotation> getAnnotations() {
        return annotations;
    }

    public void setPsiType(PsiType psiType) {
        this.psiType = psiType;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public void setDesc(String desc) {
        this.desc = "".equals(desc) ? " " : desc;
    }

    public void setParamType(TypeEnum paramType) {
        this.paramType = paramType;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public void setChildren(List<FieldInfo> children) {
        this.children = children;
    }

    public FieldInfo getParent() {
        return parent;
    }

    public void setParent(FieldInfo parent) {
        this.parent = parent;
    }

    public void setAnnotations(List<PsiAnnotation> annotations) {
        this.annotations = annotations;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Map<PsiTypeParameter, PsiType> getGenericTypeMap() {
        return genericTypeMap;
    }

    public void setGenericTypeMap(Map<PsiTypeParameter, PsiType> genericTypeMap) {
        this.genericTypeMap = genericTypeMap;
    }

}
