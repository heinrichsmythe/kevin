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

import org.chai.kevin.AbstractEntityController
import org.chai.location.DataLocationType

/**
 * @author Jean Kahigiso M.
 *
 */
class TableQuestionController extends AbstractEntityController {

	def languageService
	def surveyService
	
	def getEntity(def id) {
		return SurveyTableQuestion.get(id)
	}
	
	def createEntity() {
		return new SurveyTableQuestion();
	}
	
	def deleteEntity(def entity) {
		surveyService.deleteQuestion(entity)
	}

	def getLabel() {
		return 'survey.tablequestion.label';
	}
	
	def getTemplate() {
		return "/survey/admin/createTableQuestion"
	}

	def getModel(def entity) {
		def columns = entity.columns?.sort({it.order})
		def rows = entity.rows?.sort({it.order})	
		[
			columns: columns,
			rows: rows,
			question: entity,
			types: DataLocationType.list([cache: true]),
			sections: (entity.section)!=null?entity.survey.sections:null
		]
	}

	def getEntityClass(){
		return SurveyTableQuestion.class;
	}
	
	def bindParams(def entity) {
		entity.properties = params
		
		['row', 'column'].each { type ->
			params[type+'Names'].each { i ->
				Map<String, String> translation = new HashMap<String, String>()
				languageService.availableLanguages.each { language ->
					translation[language] = params[type+'Names['+i+'].names.'+language]
				}
				// TODO what if i is bigger than list size
				entity."${type}s".get(Integer.parseInt(i)).names = translation
			}
		}
	}

	def preview = {
		def question = SurveyTableQuestion.get(params.int('question'))

		def model = getModel(question)
		model << [template: '/survey/admin/tablePreview']
		render (view: '/entity/edit', model: model)
	}

}
