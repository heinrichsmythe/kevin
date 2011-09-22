package org.chai.kevin.survey;
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

import org.apache.shiro.SecurityUtils
import org.chai.kevin.AbstractReportController
import org.chai.kevin.Organisation
import org.chai.kevin.OrganisationService
import org.chai.kevin.ValueService
import org.chai.kevin.security.User
import org.chai.kevin.util.Utils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class EditSurveyController extends AbstractReportController {
	
	SurveyPageService surveyPageService;
	ValidationService validationService;
	ValueService valueService;
	SurveyElementService surveyElementService;
	
	def index = {
		redirect (action: 'view', params: params)
	}
	
	def view = {
		// this action redirects to the current survey if a SurveyUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("survey.view, params:"+params)
		User user = User.findByUsername(SecurityUtils.subject.principal)
		
		if (user.hasProperty('organisationUnit') != null) {
			Survey survey = Survey.get(params.int('survey'))
			Organisation organisation = organisationService.getOrganisation(user.organisationUnit.id)
			
			redirect (action: 'surveyPage', params: [survey: survey.id, organisation: organisation.id])
		}
		else {
			redirect (action: 'summaryPage')
		}
	}
	
	def validateParameters(def organisation, def groups) {
		def valid = true;
		
		if (organisation == null) valid = false
		
		int level = organisationService.getLevel(organisation);
		if (level != organisationService.getFacilityLevel()) valid = false

		organisationService.loadGroup(organisation)
		if (groups != null && !groups.contains(organisation.organisationUnitGroup.uuid)) valid = false
		
		if (!valid) {
			response.sendError(404)
		}
		return valid
	}
	
	def summaryPage = {
		Organisation currentOrganisation = getOrganisation(false)
		Survey currentSurvey = Survey.get(params.int('survey'))
		
		SummaryPage summaryPage = surveyPageService.getSummaryPage(currentOrganisation, currentSurvey);
		
		Integer organisationLevel = ConfigurationHolder.config.facility.level;
		def organisationTree = organisationService.getOrganisationTreeUntilLevel(organisationLevel)

		render (view: '/survey/summaryPage', model: [
			summaryPage: summaryPage,
			surveys: Survey.list(),
			organisationTree: organisationTree
		])
	}

	def objectiveTable = {
		Organisation currentOrganisation = getOrganisation(false)
		Survey currentSurvey = Survey.get(params.int('survey'))
		
		SummaryPage summaryPage = surveyPageService.getObjectiveTable(currentOrganisation, currentSurvey)
		
		render (view: '/survey/objectiveTable', model: [
			summaryPage: summaryPage
		])
	}
	
	def sectionTable = {
		Organisation currentOrganisation = getOrganisation(false)
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'))
		
		SummaryPage summaryPage = surveyPageService.getSectionTable(currentOrganisation, currentObjective)
		
		render (view: '/survey/sectionTable', model: [
			summaryPage: summaryPage
		])
	}
	
	def sectionPage = {
		if (log.isDebugEnabled()) log.debug("survey.section, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveySection currentSection =  SurveySection.get(params.int('section'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentSection.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentSection)
				
			render (view: '/survey/sectionPage', model: [surveyPage: surveyPage])
		}
	}
	
	def objectivePage = {
		if (log.isDebugEnabled()) log.debug("survey.objective, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation,currentObjective)
			
			render (view: '/survey/objectivePage', model: [surveyPage: surveyPage])
		}
	}
	
	
	def surveyPage = {
		if (log.isDebugEnabled()) log.debug("survey.survey, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		
		if (validateParameters(currentOrganisation, null)) {
			Survey survey = Survey.get(params.int('survey'))
			
			def surveyPage = surveyPageService.getSurveyPage(currentOrganisation, survey)
			
			render (view: '/survey/surveyPage', model: [surveyPage: surveyPage])
		}
	}
	
	def refresh = {
		if (log.isDebugEnabled()) log.debug("survey.refresh, params:"+params)
		
		// TODO make sure this is a facility
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		
		Survey survey = Survey.get(params.int('survey'))
		surveyPageService.refresh(currentOrganisation, survey, params.boolean('closeIfComplete')==null?false:params.boolean('closeIfComplete'));

		redirect (action: "surveyPage", params: [organisation: currentOrganisation.id, survey: survey.id])
	}
	
	def reopen = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			surveyPageService.reopen(currentOrganisation, currentObjective);
			
			redirect (action: "objectivePage", params: [organisation: currentOrganisation.id, objective: currentObjective.id])
		}
	}
	
	def submit = {
		if (log.isDebugEnabled()) log.debug("survey.submit, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		SurveyObjective currentObjective = SurveyObjective.get(params.int('objective'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentObjective.groupUuidString))) {
			boolean success = surveyPageService.submit(currentOrganisation, currentObjective);
						
			if (success) {
				flash.message = "survey.objective.submitted";
				flash.default = "Thanks for submitting";
			}
			else {
				flash.message = "survey.objective.review";
				flash.default = "The survey could not be submitted, please review the sections."
			}
			
			redirect (action: "objectivePage", params: [organisation: currentOrganisation.id, objective: currentObjective.id])
		}
	}
	
	def saveValue = {
		if (log.isDebugEnabled()) log.debug("survey.saveValue, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))

		def currentSection = SurveySection.get(params.int('section'))
		def currentObjective = SurveyObjective.get(params.int('objective'))		
		
		def surveyQuestion = surveyElementService.getSurveyQuestion(Long.parseLong(params['question']))
		def surveyElements = [SurveyElement.get(params.int('element'))]
		
		def surveyPage = surveyPageService.modify(currentOrganisation, surveyElements, params);
			
		def invalidQuestionsHtml = ''
		def incompleteSectionsHtml = ''
		
		if (currentSection == null) {
			completeSurveyPage = surveyPageService.getSurveyPage(currentOrganisation, currentObjective)
			invalidQuestionsHtml = g.render(template: '/survey/invalidQuestions', model: [surveyPage: completeSurveyPage])
			incompleteSectionsHtml = g.render(template: '/survey/incompleteSections', model: [surveyPage: completeSurveyPage])
		}
		
		render(contentType:"text/json") {
			
			invalidQuestions = invalidQuestionsHtml
			
			incompleteSections = incompleteSectionsHtml

			objectives = array {  
				surveyPage.objectives.each { objective, enteredObjective -> 
					obj (
						id: objective.id,
						status: enteredObjective.displayedStatus
					)
				}
			}
			sections = array {
				surveyPage.sections.each { section, enteredSection ->
					sec (
						id: section.id,
						objectiveId: section.objective.id,
						invalid: enteredSection.invalid,
						complete: enteredSection.complete,
						status: enteredSection.displayedStatus
					)
				}	
			}
			questions = array { 
				surveyPage.questions.each { question, enteredQuestion ->
					ques (
						id: question.id,
						sectionId: question.section.id,
						complete: enteredQuestion.complete,
						invalid: enteredQuestion.invalid,
						skipped: enteredQuestion.skipped,
					)
				}	
			}
			elements = array { 
				surveyPage.elements.each { surveyElement, enteredValue ->
					elem (
						id: surveyElement.id,
						questionId: surveyElement.surveyQuestion.id,
						skipped: array {
							enteredValue.skippedPrefixes.each { prefix ->
								element prefix
							}
						},
						invalid: array {
							enteredValue.invalidPrefixes.each { invalidPrefix ->
								pre (
									prefix: invalidPrefix,
									valid: enteredValue.isValid(invalidPrefix),
									errors: g.renderUserErrors(element: enteredValue, suffix: invalidPrefix)
								)
							}
						}
					)
				}
			}
			
		}
	}
	
	def save = {
		if (log.isDebugEnabled()) log.debug("survey.save, params:"+params)
		
		Organisation currentOrganisation = organisationService.getOrganisation(params.int('organisation'))
		def currentSection = SurveySection.get(params.int('section'));
		
		if (validateParameters(currentOrganisation, Utils.split(currentSection.groupUuidString))) {
			def surveyElements = getSurveyElements()
			surveyPageService.modify(currentOrganisation, surveyElements, params);
			def sectionPage = surveyPageService.getSurveyPage(currentOrganisation, currentSection)
			
			def action
			def params = [organisation: sectionPage.organisation.id]
			if (!sectionPage.sections[currentSection].invalid) {
				if (sectionPage.isLastSection(currentSection)) {
					// we go to the next objective
					action = 'objectivePage'
					params << [objective: sectionPage.objective.id]
				}
				else {
					// we get the next section
					action = 'sectionPage'
					params << [section: sectionPage.getNextSection(currentSection).id]
				}
			}
			else {
				action = 'sectionPage'
				params << [section: sectionPage.section.id]
			}
			
			redirect (action: action, params: params)
		}
	}
	
	
	private def getSurveyElements() {
		def result = []
		// TODO test this
		if (params.surveyElements instanceof String) result.add(SurveyElement.get(params.int('surveyElements')))
		else {
			params.surveyElements.each { id ->
				result.add(SurveyElement.get(id))
			}
		}
		return result
	}
	

}