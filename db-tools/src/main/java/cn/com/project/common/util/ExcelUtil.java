/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.project.common.util;
 
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 *
 * @author lenovo
 */
public class ExcelUtil   {   
 
    public HSSFCellStyle getExcelTitleCellValueStyle(HSSFWorkbook workbook) {
        HSSFCellStyle firstStyle = workbook.createCellStyle();
        // 创建字体样式
        HSSFFont firstFont = workbook.createFont();
        firstFont.setFontName("Verdana");
        firstFont.setBoldweight((short) 400);
        firstFont.setFontHeight((short) 500);
        firstFont.setColor(HSSFColor.GREEN.index);
        //设置粗体
        firstFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        firstStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        firstStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        firstStyle.setWrapText(true);
        firstStyle.setFont(firstFont);
        return firstStyle;
    }
    public HSSFCellStyle getHeaderCellValueStyle(HSSFWorkbook workbook) {
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        // 创建字体样式
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Verdana");
        headerFont.setBoldweight((short) 150);
        headerFont.setFontHeight((short) 250);
        headerFont.setColor(HSSFColor.GREEN.index);
        //设置粗体
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return headerStyle;
    }

    public HSSFCellStyle getDataCellValueStyle(HSSFWorkbook workbook) {
        HSSFCellStyle dateStyle = workbook.createCellStyle();
        //自动换行
        dateStyle.setWrapText(true);
        dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
        return dateStyle;
    }
}
