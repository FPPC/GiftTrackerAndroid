package gov.ca.fppc.fppcgifttracker.controller;

import gov.ca.fppc.fppcgifttracker.model.ExcelOutput;
import gov.ca.fppc.fppcgifttracker.model.Gift;
import gov.ca.fppc.fppcgifttracker.model.Source;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;


import jxl.write.Label;

import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteExcel {
	private String fileName;
	private WritableCellFormat cellFormat;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void write(List<ExcelOutput> output) throws IOException, WriteException {
		File file = new File (fileName);
		WorkbookSettings wbSettings = new WorkbookSettings();
		
		wbSettings.setLocale(new Locale("en","EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Schedule D", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet);
		createContent(excelSheet,output);
		
		workbook.write();
		workbook.close();
	}
	
	private void createLabel(WritableSheet sheet)
		throws WriteException {
		//font
		WritableFont font  = new WritableFont(WritableFont.ARIAL, 8);
		//set font and format for every cells
		cellFormat = new WritableCellFormat(font);
		cellFormat.setWrap(true);
		
		CellView cv = new CellView();
		cv.setFormat(cellFormat);
		cv.setAutosize(true);
		
		addHeader(sheet, 0, 0, "NAME OF SOURCE");
		addHeader(sheet, 1, 0, "ADDRESS OF SOURCE (BUSINESS ADDRESS ACCEPTABLE)");
		addHeader(sheet, 2, 0, "ZIP CODE");
		addHeader(sheet, 3, 0, "BUSINESS ACTIVITY, IF ANY, OF SOURCE");
		addHeader(sheet, 4, 0, "DATE (MM/DD/YYYY)");
		addHeader(sheet, 5, 0, "VALUE");
		addHeader(sheet, 6, 0, "DESCRIPTION OF GIFT(S)");
	}
	
	private void addHeader(WritableSheet sheet, int col, int row, String s) throws RowsExceededException, WriteException {
		Label label;
		label = new Label(col,row,s,cellFormat);
		sheet.addCell(label);
	}
	
	private void createContent(WritableSheet sheet, List<ExcelOutput> output) throws WriteException, RowsExceededException {
		int line = 1;
		Source src;
		Gift gft;
		double a;
		for(int i = 0; i < output.size(); i++) {
			src = output.get(i).getSource();
			gft = output.get(i).getGift();
			a = output.get(i).getAmount();
			addHeader(sheet, 0, line+i, src.getName());
			addHeader(sheet, 1, line+i, src.getAddress());
			addHeader(sheet, 2, line+i, src.getZip());
			addHeader(sheet, 3, line+i, src.getActivity());
			addHeader(sheet, 4, line+i, String.format("%2d/%2d/%4d", gft.getMonth(), gft.getDay(), gft.getYear()));
			addHeader(sheet, 5, line+i, String.format("%.2f", a));
			addHeader(sheet, 6, line+i, gft.getDescription());
		}
	}
}
