package org.chai.kevin.fct

import org.chai.kevin.AbstractController
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.period.Period
import org.hisp.dhis.period.Period;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportObjective
import org.chai.kevin.reports.ReportService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class FctController extends AbstractController {

	FctService fctService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		if (log.isDebugEnabled()) log.debug("fct.view, params:"+params)

		Period period = getPeriod();
		LocationEntity entity = LocationEntity.get(params.int('organisation'));
		ReportObjective objective = ReportObjective.get(params.int('objective'));
		LocationLevel level = LocationLevel.get(params.int('level'));
		List<DataEntityType> facilityTypes = getOrganisationUnitGroups(true);
		
		FctTable fctTable = null;

		if (period != null && objective != null && entity != null && level != null) {
			fctTable = fctService.getFctTable(entity, objective, period, level, new HashSet(facilityTypes));
		}
		
		if (log.isDebugEnabled()) log.debug('fct: '+fctTable+" root objective: "+objective)				
		
		[
			fctTable: fctTable,
			currentPeriod: period,
			currentObjective: objective,
			currentOrganisation: entity,
			currentLevel: level,
			currentFacilityTypes: facilityTypes,
			periods: Period.list(),
			facilityTypes: locationService.listTypes(),
			objectives: ReportObjective.list(),
			organisationTree: locationService.getRootLocation(),
			levels: locationService.listLevels()
		]
	}
}
