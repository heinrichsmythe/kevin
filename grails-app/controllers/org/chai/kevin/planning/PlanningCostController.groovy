/**
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chai.kevin.planning

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.PeriodSorter
import org.chai.kevin.Translation;
import org.chai.kevin.data.Enum;
import org.hisp.dhis.period.Period
/**
 * @author Jean Kahigiso M.
 *
 */
class PlanningCostController extends AbstractEntityController {
	
	def languageService
	
	def getEntity(def id) {
		return PlanningCost.get(id)
	}

	def createEntity() {
		return new PlanningCost()
	}

	def getLabel() {
		return 'planningCost.label'
	}
	
	def getTemplate() {
		return "/planning/admin/createPlanningCost"
	}

	def getModel(def entity) {
		def dataElements = []
		if (entity.dataElement != null) dataElements << entity.dataElement
		
		def sections = entity.planningType.sections
		def enume = Enum.findByCode(entity.planningType.discriminatorType.enumCode)
		[
			planningCost: entity,
			dataElements: dataElements,
			sections: sections,
			enume: enume
		]
	}

	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
	}
	
	def list = {
		adaptParamsForList()
		
		PlanningType planningType = PlanningType.get(params.int('planningType.id'))
		if (planningType == null) response.sendError(404)
		else {
			List<PlanningCost> planningCosts = planningType.costs
	
			render (view: '/planning/admin/list', model:[
				template:"planningCostList",
				entities: planningCosts,
				entityCount: planningCosts.size(),
				code: getLabel()
			])
		}
	}
	
}