package com.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Test {
	public static void main(String[] args) throws Exception {

		String ZEROCELLROWZERO = "Batch Historical Data - Top 10 alerts addressed by the Nightly BBNMS-LS Batch (per order type)";
		String ZEROCELLROWFST = "Yellow Arrow - shaded cell is over historical average. Red Arrow is spike cell shaded in rose              - green is a decrease";

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sample sheet");
		List<Map<String, Object>> date_part = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> day_part = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ArrayList<Object> lists = new ArrayList<Object>();
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DriverManagerDataSource dataSource = new DriverManagerDataSource(
				"jdbc:oracle:thin:@localhost:1521:xe", "hibernate", "hibernate");
		DataSource dataSource1 = dataSource;
		jdbcTemplate.setDataSource(dataSource1);

		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select distinct trim(btch.SUB_CATEGORY) SUB_CATEGORY, category from btch where category='Top 10 alerts for Provide' order by SUB_CATEGORY");
		int date_indx = 0;
		for (Map<String, Object> map : queryForList) {

			System.out.println(">" + map.get("SUB_CATEGORY").toString());
			String sql = "select headcount rpt_count,DATE_CREATED  from btch where category='Top 10 alerts for Provide' and trim(SUB_CATEGORY)='"
					+ map.get("SUB_CATEGORY").toString()
					+ "' order by SUB_CATEGORY";

			if (date_indx == 0) {
				date_part = jdbcTemplate
						.queryForList("select to_char(to_date(date_created,'mm/dd/yyyy'),'mm/dd') dt from btch where category='Top 10 alerts for Provide' and SUB_CATEGORY=' ACTIVATION_UNIDENTIFIED_ERROR_CODE' order by SUB_CATEGORY");
				day_part = jdbcTemplate
						.queryForList("select day_created day from btch where category='Top 10 alerts for Provide' and SUB_CATEGORY=' ACTIVATION_UNIDENTIFIED_ERROR_CODE' order by SUB_CATEGORY");

				// ---------------------------

				Map<String, Object> weekly = new HashMap<String, Object>();
				Map<String, Object> daily = new HashMap<String, Object>();
				Map<String, Object> df = new HashMap<String, Object>();

				weekly.put("day", ("Daily Average Rolling Work Week"));
				daily.put("day", ("Daily Average"));
				df.put("day",("Difference between Daily Average and Daily Average Rolling Work Week"));
				day_part.add(daily);
				day_part.add(weekly);
				day_part.add(df);
				// ---------------------------

				createDateDayRow(sheet, 0, day_part, ZEROCELLROWFST, "day");
				createDateDayRow(sheet, 1, date_part, ZEROCELLROWZERO, "dt");
				int cellnum = 0;
				Row row = sheet.createRow(2);

				Cell createCell = row.createCell(cellnum);

				createCell.setCellValue("Top 10 alerts for Provide");

			}

			List<Map<String, Object>> queryForList2 = jdbcTemplate
					.queryForList(sql);
			//getAvergeGrpByDate(queryForList2,list,dataSource);
			System.out.println(queryForList2.size());
			List<Map<String, Object>> agerValues = getAgerValues(
					map.get("SUB_CATEGORY").toString(), jdbcTemplate);
			queryForList2.addAll(agerValues);
			try {
				writeCollection(null, "Sheet1", queryForList2,
						map.get("SUB_CATEGORY").toString(), date_part,
						(date_indx) + 3, workbook, sheet,lists);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			date_indx++;
		}
		
		System.out.println(lists);
		getAvergeGrpByDate(lists,list,dataSource,sheet,(date_indx+5));
		
		FileOutputStream out = new FileOutputStream("E:\\BTCH\\excel1.xlsx");
		workbook.write(out);

		out.close();
	}

	private static void writeCollection(File file, String sheetName,
			List<Map<String, Object>> collection, String subcate,
			List<Map<String, Object>> date_part, int rownum,
			XSSFWorkbook workbook, XSSFSheet sheet, List<Object> lists) throws Exception {
		try {

			Row row = sheet.createRow(rownum);

			int cellnum = 0;
			Cell createCell = row.createCell(cellnum);
			createCell.setCellValue(subcate);
			cellnum = 1;
			for (Map<String, Object> map : collection) {
				Cell cell = row.createCell(cellnum++);
				if(rownum==3 && null!=map.get("DATE_CREATED")){
				lists.add(map.get("DATE_CREATED").toString());
				}
				
				cell.setCellValue(Integer.parseInt(map.get("rpt_count").toString()));
			}

			

		} catch (Exception e) {
			throw e;
		}
	}

	public static int getColumnIndex(XSSFSheet sheet, String columnName) {
		int columnIndex = -1;
		try {
			int colNum = sheet.getRow(0).getLastCellNum();
			XSSFRow firstRow = sheet.getRow(0);
			for (int colIndex = 0; colIndex < colNum; colIndex++) {
				XSSFCell cell = firstRow.getCell(colIndex);
				if (cell.toString().equals(columnName)) {
					columnIndex = colIndex;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return columnIndex;
	}

	public static void createDateDayRow(XSSFSheet sheet, int rowNum,
			List<Map<String, Object>> collection, String cellValue,
			String mapValue) {
		Row row = sheet.createRow(rowNum);
		int cellnum = 0;
		Cell createCell = row.createCell(cellnum);
		createCell.setCellValue(cellValue);
		cellnum = 1;
		for (Map<String, Object> map : collection) {
			Cell cell = row.createCell(cellnum++);
			cell.setCellValue(map.get(mapValue).toString());
		}
	}

	public static List<Map<String, Object>> getAgerValues(String subcat,
			JdbcTemplate jdbcTemplate) {
		int diff = 0;

		List<Map<String, Object>> agrValues = new ArrayList<Map<String, Object>>();
		String sql = "select (select nvl(round(sum(headcount)/7),0)  from btch where id "
				+ "between ( select max(id)-6 from btch where trim(SUB_CATEGORY)='"
				+ subcat
				+ "') and "
				+ "(select max(id) from btch where trim(SUB_CATEGORY)='"
				+ subcat
				+ "') )weekly , "
				+ "(select  nvl(round(sum(headcount)/count(headcount)),0)  from btch where trim(SUB_CATEGORY)='Activation Failed') daily"
				+ "   from dual";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		Map<String, Object> weekly = new HashMap<String, Object>();
		Map<String, Object> daily = new HashMap<String, Object>();
		Map<String, Object> df = new HashMap<String, Object>();
		Map<String, Object> mapRes = queryForList.get(0);
		weekly.put("rpt_count", mapRes.get("weekly"));
		daily.put("rpt_count", mapRes.get("daily"));

		String weeklys = mapRes.get("weekly").toString();
		String dailys = mapRes.get("daily").toString();

		diff = (int) (Double.parseDouble(weeklys) - Double.parseDouble(dailys));
		df.put("rpt_count", diff);
		
		agrValues.add(daily);
		agrValues.add(weekly);
		agrValues.add(df);
		return agrValues;
	}
	
	public static void getAvergeGrpByDate(List<Object> query,List<Map<String, Object>> list,DataSource dataSource, XSSFSheet sheet, int i) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource);
		
		Map<String, List<Object>> paramMap = Collections.singletonMap("DATE_CREATED", query);
		
		
		NamedParameterJdbcTemplate template = 
			    new NamedParameterJdbcTemplate(dataSource);
		
		
		String sql = "select nvl(round(sum(a.headcount)),0) average, DATE_CREATED from btch a where a.DATE_CREATED in(:DATE_CREATED)  GROUP BY date_created order by DATE_CREATED desc ";
		//List query2 = jdbcTemplate.query(sql, new Object[] {"02/11/2016","02/12/2016"},new AvgResult());
		
		//List<String> queryForList = template.queryForList(sql, paramMap, String.class);
		List<Map<String, Object>> queryForList2 = template.queryForList(sql, paramMap);
		List<Integer> avg = new ArrayList<Integer>();
		
		List<Map<String, Object>> querys  = new ArrayList<Map<String, Object>>(); 
		for (Object st  :  query) {
			String val = (String) st;
			Map<String, Object> map  = new HashMap<String, Object>();
			for (Map<String, Object> mapp : queryForList2) {
				if(mapp.get("DATE_CREATED").toString().equalsIgnoreCase(val)) {
					map.put("rpt_count", Integer
							.parseInt(mapp.get("AVERAGE").toString()));
					querys.add(map);
					avg.add(Integer
							.parseInt(mapp.get("AVERAGE").toString()));
				}
			}
		}
		
		Row row = sheet.createRow(i);
		int cellnum = 0;
		Cell createCell = row.createCell(cellnum);
		createCell.setCellValue("Gross Errors by day");
		cellnum = 1;
		for (Map<String, Object> map : querys) {
			Cell cell = row.createCell(cellnum++);
			cell.setCellValue(Integer.parseInt(map.get("rpt_count").toString()));
		}
		
		
		 DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		 for (Integer indx : avg) {
			 dataset.addValue( indx , " " , indx );
		      
		}
		 LineChart_AWT.main(dataset);
	}

}
