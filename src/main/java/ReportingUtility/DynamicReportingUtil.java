package ReportingUtility;

import java.awt.Color;
import java.sql.Date;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

public class DynamicReportingUtil {

	public DynamicReportingUtil() {
		// TODO Auto-generated constructor stub
		
	}
	
	public DynamicReport buildJasperReport(String LocationID, String LaneTypeID, String LaneID)
	{
		StringBuilder DBQuery = new StringBuilder();
		StringBuilder WhereCondition = new StringBuilder();
		
		DynamicReportBuilder drb = new DynamicReportBuilder();
		
		Style HeaderStyle = new Style();
		HeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		HeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		HeaderStyle.setBackgroundColor(Color.GRAY);
		HeaderStyle.setTransparency(Transparency.OPAQUE);
		HeaderStyle.setTransparent(false);
		Font HeaderFont = new Font();
		HeaderFont.setBold(true);
		HeaderFont.setFontName(Font.ARIAL_BIG.getFontName());
		HeaderFont.setFontSize(16.0f);
		HeaderStyle.setFont(HeaderFont);

		Style ContentStyle = new Style();
		ContentStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		ContentStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		ContentStyle.setBackgroundColor(Color.LIGHT_GRAY);
		ContentStyle.setTransparency(Transparency.OPAQUE);
		ContentStyle.setTransparent(false);
		Font ContentFont = new Font();
		ContentFont.setFontName(Font.ARIAL_SMALL.getFontName());
		ContentFont.setFontSize(13.0f);
		ContentStyle.setFont(ContentFont);
		
		AbstractColumn TagIDCol = ColumnBuilder.getNew().setColumnProperty("tag_id", Integer.class.getName())
				.setTitle("RFID Tag ID").setWidth(100).build();

		AbstractColumn ReqSrcID = ColumnBuilder.getNew()
				.setColumnProperty("boom_up_request_source", Integer.class.getName()).setTitle("Boom Up Request Source")
				.setWidth(100).build();

		AbstractColumn ReqSentTime = ColumnBuilder.getNew()
				.setColumnProperty("ack_sent_time", Date.class.getName())
				.setTitle("Tag Request Sent To Server Time").setWidth(150).build();

		AbstractColumn HHTBoomUpReqReceivedTime = ColumnBuilder.getNew()
				.setColumnProperty("hht_boomup_request_recd_time", Date.class.getName())
				.setTitle("HHT Boom Up Req Receive Time").setWidth(150).build();

		AbstractColumn RFIDBoomUpReqReceivedTime = ColumnBuilder.getNew()
				.setColumnProperty("rfid_boomup_request_recd_time", Date.class.getName())
				.setTitle("RFID Boom Up Req Receive Time").setWidth(150).build();

		AbstractColumn Lane = ColumnBuilder.getNew().setColumnProperty("lane_name", String.class.getName())
				.setTitle("Lane").setWidth(150).build();

		AbstractColumn Location = ColumnBuilder.getNew().setColumnProperty("location_name", String.class.getName())
				.setTitle("Location").setWidth(150).build();

		AbstractColumn Lane_Type_Name = ColumnBuilder.getNew()
				.setColumnProperty("lane_type_name", String.class.getName()).setTitle("Lane Type").setWidth(150)
				.build();
		
		FastReportBuilder frb = (FastReportBuilder) new FastReportBuilder()
				.addColumn(TagIDCol)
				.addColumn(ReqSrcID)
				.addColumn(ReqSentTime)
				.addColumn(HHTBoomUpReqReceivedTime)
				.addColumn(RFIDBoomUpReqReceivedTime)
				.addColumn(Lane)
				.addColumn(Lane_Type_Name)
				.addColumn(Location)
				.setDefaultStyles(null, null, HeaderStyle, ContentStyle)
				.setUseFullPageWidth(true);
		
		return frb.build();
	}
}
