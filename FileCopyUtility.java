package com.org.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.jam.annotation.DefaultAnnotationProxy;

public class FileCopyUtility {

	public static void main(String[] args) {
		try {
			FileInputStream file = new FileInputStream(new File(
					"E:\\BTCH\\Book.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			covertXlsxToXls(rowIterator, "E:\\BTCH\\test1.xls");
			file.close();
			
			FileInputStream file1 = new FileInputStream(new File(
					"E:\\BTCH\\test1.xls"));
			
			HSSFWorkbook workbookF = new HSSFWorkbook(file1);
			HSSFSheet sheetAt = workbookF.getSheetAt(0);
			Iterator<Row> iterator = sheetAt.iterator();
			covertXlsToXlsx(iterator, "E:\\BTCH\\test2.xlsx");
			file1.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void covertXlsxToXls(Iterator<Row> rowIterator,
			String fileName) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("FirstSheet");
			HSSFCreationHelper createHelper = workbook.getCreationHelper();
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
					"mm/dd/yyyy"));
			int rownum = 0;
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				HSSFRow rowh = sheet.createRow(rownum);
				Iterator<Cell> cellIterator = row.cellIterator();
				int num = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						HSSFCell createCell = rowh.createCell(num);
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							createCell.setCellStyle(cellStyle);
							createCell.setCellValue(cell.getDateCellValue());
						} else {
							createCell.setCellValue(cell.getNumericCellValue());
						}

						break;
					case Cell.CELL_TYPE_STRING:
						rowh.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						rowh.createCell(num).setCellValue(
								cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						rowh.createCell(num)
								.setCellValue(cell.getCellFormula());
						break;
					case Cell.CELL_TYPE_BLANK:
						rowh.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					default:
						rowh.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					}
					num++;
				}
				rownum++;
			}
			FileOutputStream fileOut = new FileOutputStream(fileName);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void covertXlsToXlsx(Iterator<Row> rowIterator,
			String fileName) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("firstSheet");
			XSSFCreationHelper creationHelper = workbook.getCreationHelper();
			XSSFCellStyle createCellStyle = workbook.createCellStyle();
			createCellStyle.setDataFormat(creationHelper.createDataFormat()
					.getFormat("mm/dd/yyyy"));
			int rownum = 0;
			while (rowIterator.hasNext()) {
				System.out.println(":");
				HSSFRow row = (HSSFRow) rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				int num = 0;
				Row xlsxrow = sheet.createRow(rownum);
				while (cellIterator.hasNext()) {
					HSSFCell cell = (HSSFCell) cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						Cell createCell = xlsxrow.createCell(num);
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							createCell.setCellStyle(createCellStyle);
							System.out.println("Row No.: " + row.getRowNum()
									+ " " + cell.getDateCellValue());
							createCell.setCellValue(cell.getDateCellValue());
						} else {
							createCell.setCellValue(cell.getNumericCellValue());
						}

						break;
					case Cell.CELL_TYPE_STRING:
						xlsxrow.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						xlsxrow.createCell(num).setCellValue(
								cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						xlsxrow.createCell(num).setCellValue(
								cell.getCellFormula());
						break;
					case Cell.CELL_TYPE_BLANK:
						xlsxrow.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					default:
						xlsxrow.createCell(num).setCellValue(
								cell.getStringCellValue());
						break;
					}
					num++;
				}
				rownum++;
			}

			FileOutputStream fileOut = new FileOutputStream(fileName);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
