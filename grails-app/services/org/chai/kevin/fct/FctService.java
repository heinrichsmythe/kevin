package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Period;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.util.Utils.ReportType;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.SumValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private ReportService reportService;
	private ValueService valueService;
	private Set<String> locationSkipLevels;
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(Location location, ReportProgram program, FctTarget target, Period period, Set<DataLocationType> types, ReportType reportType) {		
		if (log.isDebugEnabled()) 
			log.debug("getFctTable(period="+period+",location="+location+",program="+program+",target="+target+")");				
				
		Map<CalculationLocation, Map<FctTargetOption, SumValue>> valueMap = new HashMap<CalculationLocation, Map<FctTargetOption, SumValue>>();
		List<CalculationLocation> calculationLocations = new ArrayList<CalculationLocation>();
		List<FctTarget> targets = new ArrayList<FctTarget>();
		List<FctTargetOption> targetOptions = new ArrayList<FctTargetOption>();
		List<CalculationLocation> topLevelLocations = new ArrayList<CalculationLocation>();
		
		Set<LocationLevel> skips = reportService.getSkipReportLevels(locationSkipLevels);			
		switch(reportType){
			case MAP:
				calculationLocations = location.getChildrenWithData(skips, types, true);
				break;
			case TABLE:
			default:
				calculationLocations = location.collectLocationTreeWithData(skips, types, true);
				topLevelLocations.addAll(location.getChildrenWithData(skips, types, true));
		}
		
		targetOptions = target.getTargetOptions();
		if(calculationLocations.isEmpty() || targetOptions.isEmpty())
			return new FctTable(valueMap, calculationLocations, targets, targetOptions, topLevelLocations);
		Collections.sort(targetOptions);
		
		for (CalculationLocation treeLocation : calculationLocations) {
			Map<FctTargetOption, SumValue> targetMap = new HashMap<FctTargetOption, SumValue>();
			for(FctTargetOption targetOption : targetOptions){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+targetOption.getSum());
				targetMap.put(targetOption, getFctValue(targetOption, treeLocation, period, types));
			}
			valueMap.put(treeLocation, targetMap);
		}				
		
		targets = getFctTargetsWithOptions(program);		
		Collections.sort(targets);
		
		FctTable fctTable = new FctTable(valueMap, calculationLocations, targets, targetOptions, topLevelLocations);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}

	private SumValue getFctValue(FctTargetOption targetOption, CalculationLocation location, Period period, Set<DataLocationType> types) {
		SumValue sumValue = (SumValue) valueService.getCalculationValue(targetOption.getSum(), location, period, types);
		return sumValue;
	}

	public List<FctTarget> getFctTargetsWithOptions(ReportProgram program){
		List<FctTarget> result = new ArrayList<FctTarget>();
		List<FctTarget> targets = reportService.getReportTargets(FctTarget.class, program);
		for(FctTarget target : targets){
			if(target.getTargetOptions() != null && !target.getTargetOptions().isEmpty())
				result.add(target);
		}
		return result;
	}
	
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setLocationSkipLevels(Set<String> locationSkipLevels) {
		this.locationSkipLevels = locationSkipLevels;
	}
	
	public Set<LocationLevel> getSkipLocationLevels(){
		return reportService.getSkipReportLevels(locationSkipLevels);
	}
}