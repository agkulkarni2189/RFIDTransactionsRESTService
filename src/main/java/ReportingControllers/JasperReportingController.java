package ReportingControllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JasperReportingController {

	public JasperReportingController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping("/reportingService")
	public String getJasperReport(@RequestParam(value="LocationID", defaultValue="") String LocationID, @RequestParam(value="LaneTypeID", defaultValue="") String LaneTypeID, @RequestParam(value="LaneID", defaultValue="") String LaneID)
	{
		return null;
		
	}
}
