package ReportingControllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ReportingUtility.DynamicReportingUtil;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import haziraDBAPIs.LaneAPI;
import haziraDBAPIs.LaneTypeAPI;
import haziraDBAPIs.LocationAPI;
import haziraDBAPIs.TransactionAPI;
import haziraDBEntities.Lane;
import haziraDBEntities.LaneType;
import haziraDBEntities.Location;
import haziraDBEntities.RFIDTransaction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.JsonMetadataExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleJsonExporterConfiguration;
import net.sf.jasperreports.export.SimpleJsonExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import net.sf.jasperreports.export.type.PdfPrintScalingEnum;

@RestController
public class JasperReportingController {

	public JasperReportingController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping("/reportingService")
	public String getJasperReport(@RequestParam(value = "LocationID", defaultValue = "") String LocationID,
			@RequestParam(value = "LaneTypeID", defaultValue = "") String LaneTypeID,
			@RequestParam(value = "LaneID", defaultValue = "") String LaneID,
			@RequestParam(value = "FromCreationDate", defaultValue = "") String FromCreationDate,
			@RequestParam(value = "ToCreationDate", defaultValue = "") String ToCreationDate,
			@RequestParam(value = "ExportOption", defaultValue = "2") String ExportOption) {
		String ExportedReport = new String();
		JasperPrint jp;
		try {
			jp = this.getJasperPrint(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate, "1");
			int eo = Integer.parseInt(ExportOption);

			switch (eo) {
			case 1:
				ExportedReport = this.ExportReportToXML(jp);
				break;
			case 2:
				ExportedReport = this.ExportReportToCSV(jp);
				break;
			case 3:
				ExportedReport = this.StringExportReportToText(jp);
				break;
			case 4:
				// jp = this.getJasperPrint(LocationID, LaneTypeID, LaneID, "4");
				ExportedReport = this.getReportINJson(jp);
				break;
			case 5:
				JasperPrintManager.printReport(jp, true);
				break;
			default:
				ExportedReport = "Invalid export option";
				break;
			}
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ExportedReport;
	}

	@RequestMapping(value = "/printReport", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
	public void printReport(
			@RequestParam(value = "LocationID", defaultValue = "") String LocationID,
			@RequestParam(value = "LaneTypeID", defaultValue = "") String LaneTypeID,
			@RequestParam(value = "LaneID", defaultValue = "") String LaneID,
			@RequestParam(value = "FromCreationDate", defaultValue = "") String FromCreationDate,
			@RequestParam(value = "ToCreationDate", defaultValue = "") String ToCreationDate) throws JRException, SQLException {
		JasperPrintManager.printReport(this.getJasperPrint(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate, "1"), true);
	}

	@RequestMapping(value = "/exportPdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> ExportReportInPdf(
			@RequestParam(value = "LocationID", defaultValue = "") String LocationID,
			@RequestParam(value = "LaneTypeID", defaultValue = "") String LaneTypeID,
			@RequestParam(value = "LaneID", defaultValue = "") String LaneID,
			@RequestParam(value = "FromCreationDate", defaultValue = "") String FromCreationDate,
			@RequestParam(value = "ToCreationDate", defaultValue = "") String ToCreationDate)
			throws JRException, SQLException {
		JasperPrint jp = getJasperPrint(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate, "2");
		ByteArrayInputStream bis = this.getReportInPdf(jp);
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename=TransactionsReport.pdf");

		return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

	@RequestMapping(value = "/exportXlsx", method = RequestMethod.GET, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<InputStreamResource> ExportReportInExcel(
			@RequestParam(value = "LocationID", defaultValue = "") String LocationID,
			@RequestParam(value = "LaneTypeID", defaultValue = "") String LaneTypeID,
			@RequestParam(value = "LaneID", defaultValue = "") String LaneID,
			@RequestParam(value = "FromCreationDate", defaultValue = "") String FromCreationDate,
			@RequestParam(value = "ToCreationDate", defaultValue = "") String ToCreationDate)
			throws JRException, SQLException {
		ByteArrayInputStream bis = this.getReportInExcel(
				this.getJasperPrint(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate, "3"));
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename=TransactionsReport.xlsx");

		return ResponseEntity.ok().headers(header).contentType(MediaType.MULTIPART_FORM_DATA)
				.body(new InputStreamResource(bis));
	}

	@RequestMapping(value = "/getLocations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "*")
	public List<Location> getAllLocations() {
		LocationAPI LocationEntityMethods = new LocationAPI();
		return LocationEntityMethods.getAllLocations();
	}

	@RequestMapping(value = "/getLanes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "*")
	public List<Lane> getAllLanes() {
		LaneAPI LaneEntityMethods = new LaneAPI();
		return LaneEntityMethods.getAllLanes();
	}

	@RequestMapping(value = "/getLaneTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "*")
	public List<LaneType> getAllLaneTypes() {
		LaneTypeAPI LaneTypeEntityMethods = new LaneTypeAPI();
		return LaneTypeEntityMethods.getAllLaneTypes();
	}

	private String ExportReportToXML(JasperPrint jp) {
		StringBuilder XmlReportWriter = new StringBuilder();

		try {
			SimpleXmlExporterOutput xmlOutput = new SimpleXmlExporterOutput(XmlReportWriter);
			xmlOutput.setEmbeddingImages(true);

			JRXmlExporter xmlExporter = new JRXmlExporter();
			xmlExporter.setExporterInput(new SimpleExporterInput(jp));
			xmlExporter.setExporterOutput(xmlOutput);
			xmlExporter.exportReport();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return XmlReportWriter.toString();
	}

	private String ExportReportToCSV(JasperPrint jp) throws JRException {
		StringWriter sw = new StringWriter();
		JRCsvExporter CSVExporter = new JRCsvExporter();
		SimpleCsvExporterConfiguration exportConfig = new SimpleCsvExporterConfiguration();
		exportConfig.setFieldDelimiter(",");
		exportConfig.setRecordDelimiter("\n");
		CSVExporter.setConfiguration(exportConfig);
		SimpleExporterInput ExporterInput = new SimpleExporterInput(jp);
		SimpleWriterExporterOutput WriterOutput = new SimpleWriterExporterOutput(sw);
		CSVExporter.setExporterInput(ExporterInput);
		CSVExporter.setExporterOutput(WriterOutput);
		CSVExporter.exportReport();
		return sw.toString();
	}

	private String StringExportReportToText(JasperPrint jp) throws JRException {
		JRTextExporter TextExporter = new JRTextExporter();
		StringWriter sw = new StringWriter();
		SimpleExporterInput ExporterInput = new SimpleExporterInput(jp);
		SimpleWriterExporterOutput WriterOutput = new SimpleWriterExporterOutput(sw);

		SimpleTextExporterConfiguration TextExporterConfiguration = new SimpleTextExporterConfiguration();
		TextExporterConfiguration.setLineSeparator(",");
		TextExporterConfiguration.setPageSeparator(".");
		TextExporterConfiguration.setOverrideHints(false);
		TextExporter.setConfiguration(TextExporterConfiguration);

		SimpleTextReportConfiguration TextReportConfiguration = new SimpleTextReportConfiguration();
		TextReportConfiguration.setOverrideHints(false);
		TextReportConfiguration.setCharWidth(9.0f);
		TextReportConfiguration.setCharHeight(9.0f);
		TextExporter.setConfiguration(TextReportConfiguration);
		TextExporter.setExporterInput(ExporterInput);
		TextExporter.setExporterOutput(WriterOutput);

		TextExporter.exportReport();
		return sw.toString();
	}

	@SuppressWarnings("unused")
	private ByteArrayInputStream getReportInPdf(JasperPrint jp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			JRPdfExporter PdfExporter = new JRPdfExporter();
			PdfExporter.setExporterInput(new SimpleExporterInput(jp));
			PdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
			SimplePdfExporterConfiguration PdfConfig = new SimplePdfExporterConfiguration();
			PdfConfig.setPrintScaling(PdfPrintScalingEnum.DEFAULT);
			PdfExporter.setConfiguration(PdfConfig);
			PdfExporter.exportReport();
		} catch (JRException je) {
			je.printStackTrace();
		}

		return new ByteArrayInputStream(baos.toByteArray());
	}

	private ByteArrayInputStream getReportInExcel(JasperPrint jp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			JRXlsxExporter ExcelExporter = new JRXlsxExporter();
			ExcelExporter.setExporterInput(new SimpleExporterInput(jp));
			ExcelExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
			SimpleXlsxReportConfiguration ExcelReportConfig = new SimpleXlsxReportConfiguration();
			ExcelReportConfig.setOnePagePerSheet(false);
			ExcelReportConfig.setRemoveEmptySpaceBetweenRows(true);
			ExcelReportConfig.setRemoveEmptySpaceBetweenColumns(true);
			ExcelReportConfig.setSheetHeaderCenter("Transactions Report");
			ExcelReportConfig.setAutoFitPageHeight(true);
			ExcelReportConfig.setCellLocked(Boolean.FALSE);
			ExcelReportConfig.setSheetDirection(RunDirectionEnum.LTR);
			ExcelExporter.setConfiguration(ExcelReportConfig);
			ExcelExporter.exportReport();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ByteArrayInputStream(baos.toByteArray());

	}

	@SuppressWarnings("unused")
	private String getReportINJson(JasperPrint jp) throws JRException {
		StringWriter sw = new StringWriter();
		JsonMetadataExporter JsonExporter = new JsonMetadataExporter();

		JsonExporter.setExporterInput(new SimpleExporterInput(jp));
		JsonExporter.setExporterOutput(new SimpleJsonExporterOutput(sw));
		SimpleJsonExporterConfiguration JsonExporterConfig = new SimpleJsonExporterConfiguration();
		JsonExporter.exportReport();

		return sw.toString();
	}

	@RequestMapping(value = "/exportJSON")
	@CrossOrigin(origins = "*")
	public List<RFIDTransaction> getReportInJSON(
			@RequestParam(value = "LocationID", defaultValue = "") String LocationID,
			@RequestParam(value = "LaneTypeID", defaultValue = "") String LaneTypeID,
			@RequestParam(value = "LaneID", defaultValue = "") String LaneID,
			@RequestParam(value = "FromCreationDate", defaultValue = "") String FromCreationDate,
			@RequestParam(value = "ToCreationDate", defaultValue = "") String ToCreationDate) {
		TransactionAPI RFIDTransAPI = new TransactionAPI();
		return RFIDTransAPI.getRFIDTransactions(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate);

	}

	private JasperPrint getJasperPrint(String LocationID, String LaneTypeID, String LaneID, String FromCreationDate,
			String ToCreationDate, String ExportOption) throws JRException, SQLException {
		DynamicReportingUtil dru = new DynamicReportingUtil();
		int ExpOpt = Integer.parseInt(ExportOption);
		DynamicReport report = null;

		switch (ExpOpt) {
		case 1:
			report = dru.buildJasperReport(LocationID, LaneTypeID, LaneID);
			break;
		case 2:
			report = dru.getPDFJasperPrint(LocationID, LaneTypeID, LaneID);
			break;
		case 3:
			report = dru.getExcelJasperPrint(LocationID, LaneTypeID, LaneID);
			break;
		case 4:
			report = dru.getJSONJasperPrint(LocationID, LaneTypeID, LaneID);
			break;
		}

		TransactionAPI RFIDTransAPI = new TransactionAPI();
		JRBeanCollectionDataSource TransactionDS = new JRBeanCollectionDataSource(
				RFIDTransAPI.getRFIDTransactions(LocationID, LaneTypeID, LaneID, FromCreationDate, ToCreationDate));
		return DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), TransactionDS);
	}
}
