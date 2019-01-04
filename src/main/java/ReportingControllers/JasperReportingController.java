package ReportingControllers;

import java.io.StringWriter;
import java.sql.SQLException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import haziraDBAPIs.TransactionAPI;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import ReportingUtility.DynamicReportingUtil;

@RestController
public class JasperReportingController {

	public JasperReportingController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping("/reportingService")
	public String getJasperReport(@RequestParam(value="LocationID", defaultValue="") String LocationID, @RequestParam(value="LaneTypeID", defaultValue="") String LaneTypeID, @RequestParam(value="LaneID", defaultValue="") String LaneID, @RequestParam(value="ExportOption", defaultValue="1") String ExportOption)
	{
		String ExportedReport = new String();
		JasperPrint jp;
		try 
		{
			jp = this.getJasperPrint(LocationID, LaneTypeID, LaneID);
			int eo = Integer.parseInt(ExportOption);
			
			switch (eo)
			{
			case 1: ExportedReport = this.ExportReportToXML(jp);
			break;
			case 2: ExportedReport = this.ExportReportToCSV(jp);
			break;
			case 3: ExportedReport = this.StringExportReportToText(jp);
			break;
			default: ExportedReport = "Invalid export option";
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
	
	private String ExportReportToXML(JasperPrint jp)
	{
		StringWriter XmlReportWriter = new StringWriter();
		
		try
		{
			SimpleXmlExporterOutput xmlOutput = new SimpleXmlExporterOutput(XmlReportWriter);
			xmlOutput.setEmbeddingImages(true);
			
			JRXmlExporter xmlExporter = new JRXmlExporter();
			xmlExporter.setExporterInput(new SimpleExporterInput(jp));
			xmlExporter.setExporterOutput(xmlOutput);
			xmlExporter.exportReport();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return XmlReportWriter.toString();
	}
	
	private String ExportReportToCSV(JasperPrint jp) throws JRException
	{
		StringWriter sw = new StringWriter();
		JRCsvExporter CSVExporter = new JRCsvExporter();
		SimpleExporterInput ExporterInput = new SimpleExporterInput(jp);
		SimpleWriterExporterOutput WriterOutput = new SimpleWriterExporterOutput(sw);
		CSVExporter.setExporterInput(ExporterInput);
		CSVExporter.setExporterOutput(WriterOutput);
		CSVExporter.exportReport();
		return sw.toString();
	}
	
	private String StringExportReportToText(JasperPrint jp) throws JRException
	{
		JRTextExporter TextExporter = new JRTextExporter();
		StringWriter sw = new StringWriter();
		SimpleExporterInput ExporterInput = new SimpleExporterInput(jp);
		SimpleWriterExporterOutput WriterOutput = new SimpleWriterExporterOutput(sw);
		
		SimpleTextExporterConfiguration TextExporterConfiguration = new SimpleTextExporterConfiguration ();
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
	
	public JasperPrint getJasperPrint(String LocationID, String LaneTypeID, String LaneID) throws JRException, SQLException
	{
		DynamicReportingUtil dru = new DynamicReportingUtil();
		DynamicReport report = dru.buildJasperReport(LocationID, LaneTypeID, LaneID);
		TransactionAPI RFIDTransAPI = new TransactionAPI();
		JRBeanCollectionDataSource TransactionDS = new JRBeanCollectionDataSource(RFIDTransAPI.getRFIDTransactions(LocationID, LaneTypeID, LaneID));
		return DynamicJasperHelper.generateJasperPrint(report, new ClassicLayoutManager(), TransactionDS);
		//return null;
	}
}
