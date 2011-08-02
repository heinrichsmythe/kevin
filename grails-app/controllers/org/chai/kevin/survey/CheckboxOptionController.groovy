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
package org.chai.kevin.survey

import org.chai.kevin.AbstractEntityController;
import org.chai.kevin.DataService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.chai.kevin.util.Utils
import org.chai.kevin.data.DataElement
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author Jean Kahigiso M.
 *
 */
class CheckboxOptionController extends AbstractEntityController {

	def getEntity(def id) {
		return SurveyCheckboxOption.get(id)
	}
	def createEntity() {
		def entity = new SurveyCheckboxOption();
		//FIXME find a better to do this
		if (!params['question']) entity.question = SurveyCheckboxQuestion.get(params.questionId)
		return entity
	}

	def getTemplate() {
		return "/survey/admin/createCheckboxOption"
	}

	def getModel(def entity) {
		[
					option: entity,
					groups: OrganisationUnitGroup.list(),
					groupUuids: Utils.getGroupUuids(entity.groupUuidString)
				]
	}
	
	def html(def entity){
		return g.render (template:'/templates/checkboxOption', model:[option: entity])
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.surveyElement.surveyQuestion = entity.question
		entity.save()
		
	}
	def deleteEntity(def entity) {
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params
		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		entity.groupUuidString =  params['groupUuids']!=null?Utils.getGroupUuidString(params['groupUuids']):null
		if (params.names!=null) entity.names = params.names
	}
}