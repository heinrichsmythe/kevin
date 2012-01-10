package org.chai.kevin.fct;

import grails.plugin.springcache.annotations.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LocationService;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportObjective;
import org.chai.kevin.reports.ReportService;
import org.chai.kevin.reports.ReportValue;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.ValueService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class FctService {
	private static final Log log = LogFactory.getLog(FctService.class);
	
	private ReportService reportService;
	private LocationService locationService;
	private ValueService valueService;
	
	
	@Cacheable("fctCache")
	@Transactional(readOnly = true)
	public FctTable getFctTable(LocationEntity entity, ReportObjective objective, Period period, LocationLevel level, Set<DataEntityType> groups) {		
		if (log.isDebugEnabled()) log.debug("getFctTable(period="+period+",entity="+entity+",objective="+objective+",level="+level+")");		
		
		List<LocationEntity> organisations = locationService.getChildrenOfLevel(entity, level);
		Map<LocationEntity, List<LocationEntity>> organisationMap = new HashMap<LocationEntity, List<LocationEntity>>();
		LocationLevel groupLevel = locationService.getLevelBefore(level);
		if (groupLevel != null) organisationMap.putAll(reportService.getParents(organisations, groupLevel));
		
		List<FctTarget> targets = reportService.getReportTargets(FctTarget.class, objective);
		Map<FctTarget, ReportValue> totalMap = new HashMap<FctTarget, ReportValue>();				
		for(FctTarget target : targets){			
			totalMap.put(target, getFctValue(target, entity, period, groups));
		}
		Map<LocationEntity, Map<FctTarget, ReportValue>> valueMap = new HashMap<LocationEntity, Map<FctTarget, ReportValue>>();
		for (LocationEntity child : organisations) {
			Map<FctTarget, ReportValue> targetMap = new HashMap<FctTarget, ReportValue>();
			for(FctTarget target : targets){
				if (log.isDebugEnabled()) log.debug("getting values for sum fct with calculation: "+target.getSum());
				targetMap.put(target, getFctValue(target, child, period, groups));
			}
			valueMap.put(child, targetMap);
		}
		
		FctTable fctTable = new FctTable(totalMap, valueMap, targets, organisationMap);
		if (log.isDebugEnabled()) log.debug("getFctTable(...)="+fctTable);
		return fctTable;
	}


	private ReportValue getFctValue(FctTarget target, CalculationEntity entity, Period period, Set<DataEntityType> groups) {
		String value = null;
		CalculationValue<?> calculationValue = valueService.getCalculationValue(target.getSum(), entity, period, groups);
		if (calculationValue != null) value = calculationValue.getValue().getNumberValue().toString();
		return new ReportValue(value);
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
}
