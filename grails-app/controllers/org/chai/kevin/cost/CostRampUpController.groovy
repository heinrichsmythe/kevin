package org.chai.kevin.cost

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Expression;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class CostRampUpController extends AbstractEntityController {

	def costService
	
	def getEntity(def id) {
		return CostRampUp.get(id)
	}
	
	def createEntity() {
		return new CostRampUp()
	}
	
	def getTemplate() {
		return "/cost/costRampUp/createRampUp"
	}
	
	def getModel(def entity) {
		return [rampUp: entity, years: costService.years]
	}
	
	def validateEntity(def entity) {
		boolean valid = true;
		entity.years.each { key, value ->
			if (!value.validate()) valid = false
		}
		return entity.validate() & valid
	}
	
	def saveEntity(def entity) {
		entity.save()
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions
	}
	
	def deleteEntity(def entity) {
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
	}
	
	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[rampUps: CostRampUp.list(params), rampUpCount: CostRampUp.count(), years: costService.getYears()]
	}
	
}
