<%@page import="org.chai.kevin.util.DataUtils"%>

<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="${i18nField(field: 'descriptions')}" params="[q:params.q]" title="${message(code: 'entity.description.label')}" />
  			<g:sortableColumn property="period.startDate" params="[q:params.q]" title="${message(code: 'survey.period.label')}" />
  			<g:sortableColumn property="active" params="[q:params.q]" title="${message(code: 'survey.active.label')}" />  		    
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="survey">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'survey', action:'edit', params:[id: survey.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'survey', action:'delete', params:[id: survey.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td>${survey.code}</td>
				<td><g:i18n field="${survey.names}" /></td>				
				<td><g:i18n field="${survey.descriptions}" /></td>
				<td>${DataUtils.formatDate(survey.period.startDate)}</td>
				<td>${survey?.active?'\u2713':''}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'program', action:'list', params:['survey.id':survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.program.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveySkipRule', action:'list', params:['survey.id': survey?.id])}"><g:message code="default.list.label" args="[message(code:'survey.skiprule.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'surveyValidationRule', action:'list', params:['survey.id': survey?.id])}"><g:message code="default.list.label" args="[message(code:'formelement.validationrule.label')]" /></a>
								</li>
								<li>
							    	<a href="${createLink(controller:'survey', action:'copy', params:[survey: survey.id])}"><g:message code="survey.clone.label" /> </a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>