//package com.excel.eom.processor;
//
//import com.excel.eom.annotation.deprecated.AnnotationExcelColumn;
//import com.google.auto.service.AutoService;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.TypeElement;
//import java.util.Set;
//
//@SupportedAnnotationTypes("com.excel.eom.annotation.deprecated.AnnotationExcelColumn")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@AutoService(Processor.class)
//public class ExcelColumnProcessor extends AbstractProcessor {
//
//    /**
//     * 1. enum check
//     * 2. mapping object check
//     *  - must extends OldExcelObject
//     * */
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations,
//                           RoundEnvironment roundEnv) {
//        /*System.out.println(String.format("annotation processing start - %s", "Class-Name"));
//        Messager messager = super.processingEnv.getMessager();*/
//
//        for (TypeElement annotation : annotations) {
//            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
//
//            annotatedElements.stream().forEach(element -> {
//                AnnotationExcelColumn anno = element.getAnnotation(AnnotationExcelColumn.class);
//                /*Class<?> objectType = anno.mapping();*/
//
//                String msg =
//                        "<<@AnnotationExcelColumn(\"" + null + "\")>>\n"
//                                + "  Kind : " + element.getKind() + "\n"
//                                + "  SimpleName : " + element.getSimpleName() + "\n"
//                                + "  Modifiers : " + element.getModifiers() + "\n"
//                                + "  asType : " + element.asType() + "\n"
//                                + "  EnclosedElements : " + element.getEnclosedElements() + "\n"
//                                + "  EnclosingElement : " + element.getEnclosingElement() + "\n"
//                                + "  AnnotationMirrors : " + element.getAnnotationMirrors() + "\n";
//
//                /*<<@AnnotationExcelColumn("null")>>
//                Kind : ENUM
//                SimpleName : ExcelColumnAPI
//                Modifiers : [public, final]
//                asType : msd.got.sync.arenewal.model.excelcolumn.ExcelColumnAPI
//                EnclosedElements :
//                    values(),
//                    valueOf(java.lang.String),
//                    NAME,
//                    ENG,
//                    CH,
//                    JP,
//                    TC,
//                    TH,
//                    VI,
//                    IND,
//                    ORDER,
//                    order,
//                    field,
//                    type,
//                    ExcelColumnAPI(int,java.lang.String,java.lang.Class<?>),
//                    getPrimaryKey(),
//                    getOrder(),
//                    getField(),
//                    getType()
//                EnclosingElement :
//                    msd.got.sync.arenewal.model.excelcolumn
//                AnnotationMirrors :
//                    @com.excel.eom.annotation.deprecated.AnnotationExcelColumn(
//                        mapping=msd.got.sync.arenewal.model.excelobject.ExcelObjectAPI.class
//                        )
//                */
//
//                System.out.println(msg);
////
////                String elementName = ((Element) element).getSimpleName().toString();
////                System.out.println(String.format("Class Name : ", elementName));
////
////                // TODO enum check
//////                ExecutableType executableType = ((ExecutableType) element.asType());
//////                boolean isEnum = executableType.get;
//////                System.out.println(String.format("isEnum ? : ", isEnum));
////
////                // TODO field check
////                System.out.println(String.format("isEnum ? : ", ((Element) element).getClass().isEnum()));
////
////                // TODO constructor check
////
////                // TODO implements check -> method check
//
//            });
//
//            /*Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream()
//                    .collect(Collectors.partitioningBy(element ->
//                            ((ExecutableType) element.asType()).getParameterTypes().size() == 1 && element.getSimpleName().toString().startsWith("set")));*/
//
//            /*List<Element> setters = annotatedMethods.get(true);
//            List<Element> otherMethods = annotatedMethods.get(false);*/
//        }
//
//        return true;
//    }
//
//}
