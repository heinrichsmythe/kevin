package org.chai.kevin.cost

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.Expression;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import com.sun.tools.javac.code.Type.ForAll;

class CostTargetController extends AbstractEntityController {
	
	def organisationService
	
	def getEntity(def id) {
		return CostTarget.get(id)
	}
	
	def createEntity() {
		return new CostTarget()
	}
	
	def getTemplate() {
		return "createTarget"
	}
	
	def getModel(def entity) {
		def currentObjective = null;
		if (params['currentObjective']) {
			currentObjective = CostObjective.get(params['currentObjective']);
			if (log.isInfoEnabled()) log.info('fetched current objective: '+currentObjective);
		}
		def groups = new GroupCollection(organisationService.getGroupsForExpression())
		[
			target: entity, 
			groupUuids: CostService.getGroupUuids(entity.groupUuidString),
			currentObjective: currentObjective, 
			expressions: Expression.list(), 
			costRampUps: CostRampUp.list(), 
			groups: groups
		]
	}
	
	def validateEntity(def entity) {
		return entity.validate()
	}
	
	def saveEntity(def entity) {
		if (entity.id == null) {
			def currentObjective = CostObjective.get(params['currentObjective']);
			currentObjective.addTarget entity
			currentObjective.save();
		}
		else {
			entity.save();
		}
	}
	
	def deleteEntity(def entity) {
		entity.delete()
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		entity.groupUuidString = CostService.getGroupUuidString(params['groupUuids']);
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions

	}
	
	
}
