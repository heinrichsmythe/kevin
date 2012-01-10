package org.chai.kevin.dashboard

/*
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

import grails.plugin.springcache.annotations.CacheFlush
import org.chai.kevin.reports.ReportObjective

class DashboardObjectiveController extends AbstractObjectiveController {

	def getEntity(def id) {
		return DashboardObjective.get(id)
	}
	
	def createEntity() {
		def entity = new DashboardObjective()
		return entity
	}
	
	def getLabel() {
		return "dashboard.objective.label"
	}
	
	def getTemplate() {
		return '/dashboard/createObjective';
	}
	
	def deleteEntity(def entity) {
		List<DashboardEntity> dashboardEntities = dashboardService.getDashboardEntities(entity.getReportObjective());
		if(dashboardEntities.size() == 0){
			if (log.isInfoEnabled()) log.info("deleting objective entity: "+entity)
			entity.delete()
		}
		else {
			flash.message = message(code: 'dashboard.objective.haschildren', args: [message(code: getLabel(), default: 'entity'), params.id], default: 'Dashboard obejctive {0} still has associated children.')
		}
	}
	
	def getModel(def entity) {
		def model = super.getModel(entity)
		
		def reportObjectives = ReportObjective.list()
		def dashboardObjectives = DashboardObjective.list()
		for(objective in dashboardObjectives)
			reportObjectives.remove(objective.getObjective())
		
		model << [entity: entity, objectives: reportObjectives]
		return model;
	}
	
	def bindParams(def entity) {		
		entity.properties = params
//		if(params != null) {
			if(params.objective != null){					
				 def reportObjective = ReportObjective.get(params.objective.id)
				 entity.objective = params.objective
				 
				 entity.names = reportObjective.names
				 entity.descriptions = reportObjective.descriptions
				 entity.code = reportObjective.code
			}
//		}
	}
	
	@CacheFlush("dashboardCache")
	def edit = {
		super.edit()
	}
	
	@CacheFlush("dashboardCache")
	def save = {
		super.save()
	}
	
	@CacheFlush("dashboardCache")
	def delete = {
		super.delete()
	}
	
}
