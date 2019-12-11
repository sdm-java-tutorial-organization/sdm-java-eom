//package com.excel.eom.builder;
//
//import com.excel.eom.annotation.ExcelColumn;
//import com.excel.eom.annotation.ExcelObject;
//import com.excel.eom.annotation.UniqueKey;
//import com.excel.eom.exception.EOMBodyException;
//import com.excel.eom.exception.body.EOMCellException;
//import com.excel.eom.util.ExcelFileUtil;
//import com.excel.eom.util.ExcelSheetUtil;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.IndexedColors;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING) // 사전식순서 (a_, b_, c_, ...)
//public class ExcelObjectMapperDemoTest {
//
//    static String DEPLOY_DEMO =
//            "src/main/resources/deploy/demo/excel_code.xlsx";
//
//    @Test
//    public void b_buildSheet() throws Throwable {
//        Workbook book = ExcelFileUtil.getXSSFWorkbookByFile(new File(DEPLOY_DEMO));
//        Sheet sheet = ExcelSheetUtil.getSheet(book, 0);
//        List<ExcelCode> items = null;
//        try {
//            items = ExcelObjectMapper.init()
//                    .initModel(ExcelCode.class)
//                    .initBook(book)
//                    .initSheet(sheet)
//                    .buildSheet();
//            items.stream().forEach(item -> {
//                System.out.println(item.toString());
//            });
//        } catch (EOMBodyException e) {
//            long nullPointerCount = e.getDetail().stream()
//                    .filter(detail -> detail.getClass().getSimpleName().equals("EOMNotNullException"))
//                    .map(detail -> {
//                        System.out.println(detail.getMessage());
//                        System.out.println(detail.getArgs());
//                        return detail;
//                    })
//                    .count();
//        }
//    }
//
//    @Data
//    @ExcelObject(name = "CODE", fixedRowCount = 1)
//    @UniqueKey({"opt_parent_code", "opt_code_type", "opt_code"})
//    @NoArgsConstructor
//    public static class ExcelCode {
//
//        @ExcelColumn(name = "PARENT", index = 0, nullable = false)
//        public String opt_parent_code; // 부모
//
//        @ExcelColumn(name = "CHILD", index = 1, nullable = false)
//        public String opt_code_type; // 자식
//
//        @ExcelColumn(name = "CODE", index = 2, nullable = false)
//        public String opt_code; // 코드
//
//        @ExcelColumn(name = "DESC", index = 3)
//        public String opt_code_desc; // 메모
//
//        @ExcelColumn(name = "NAME(KR)", index = 4, nullable = false)
//        public String opt_code_name; // 이름
//
//        @ExcelColumn(name = "NAME(ENG)", index = 5)
//        public String opt_code_eng;
//
//        @ExcelColumn(name = "NAME(CH)", index = 6)
//        public String opt_code_ch;
//
//        @ExcelColumn(name = "NAME(JP)", index = 7)
//        public String opt_code_jp;
//
//        @ExcelColumn(name = "NAME(TC)", index = 8)
//        public String opt_code_tc;
//
//        @ExcelColumn(name = "NAME(TH)", index = 9)
//        public String opt_code_th;
//
//        @ExcelColumn(name = "NAME(VI)", index = 10)
//        public String opt_code_vi;
//
//        @ExcelColumn(name = "NAME(IND)", index = 11)
//        public String opt_code_ind;
//    }
//
//}
