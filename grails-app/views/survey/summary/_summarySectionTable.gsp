<r:require modules="progressbar,dropdown,explanation,survey" />

<%@ page import="org.chai.kevin.survey.summary.SurveySummaryPage" %>

<div>
	<table class="listing">
		<thead>
			<g:sortableColumn property="${SurveySummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
			<g:sortableColumn property="${SurveySummaryPage.PROGRESS_SORT}" title="${message(code: 'survey.summary.progress')}" params="${params}" defaultOrder="desc"/>
			<th></th>
		</thead>
		<tbody>
			<g:each in="${summaryPage.locations}" var="location">
				<g:set var="questionSummary" value="${summaryPage.getQuestionSummary(location)}" />
				<tr>
					<td><g:i18n field="${location.names}"/></td>
					<td><span class="js_progress-bar">${questionSummary.completedQuestions}/${questionSummary.questions}</span></td>
					<td>
						<a href="${createLink(controller: 'editSurvey', action: 'sectionPage', params: [section: currentSection.id, location: location.id])}">
							<g:message code="survey.summary.viewsurvey.label" />
						</a>
						<shiro:hasPermission permission="editSurvey:export"> 
							<a href="${createLink(controller: 'editSurvey', action: 'export', params: [section: currentSection.id, location: location.id])}">
								<g:message code="survey.summary.exportsection.label" />
							</a>
						</shiro:hasPermission>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
</div>
