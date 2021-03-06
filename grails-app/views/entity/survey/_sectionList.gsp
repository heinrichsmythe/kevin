<%@page import="org.chai.kevin.util.Utils"%>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q, 'program.id': params['program.id']]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q, 'program.id': params['program.id']]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="typeCodeString" params="[q:params.q, 'program.id': params['program.id']]" title="${message(code: 'entity.datalocationtype.label')}" />
			<th><g:message code="survey.label" /></th>
			<th><g:message code="survey.program.label" /></th>
			<g:sortableColumn property="order" params="[q:params.q, 'program.id': params['program.id']]" title="${message(code: 'entity.order.label')}" />
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="section"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
		    			    <a class="edit-link" href="${createLinkWithTargetURI(controller:'section', action:'edit', params:[id: section.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
		      				<a class="delete-link" href="${createLinkWithTargetURI(controller:'section', action:'delete', params:[id: section.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>${section.code}</td>
				<td><g:i18n field="${section.names}" /></td>
				<td><g:prettyList entities="${section.typeCodeString}" /></td>
				<td>${section.program.survey.code}</td>
				<td>${section.program.code}</td>
				<td>${section.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'question', action:'list',params:['section.id': section.id])}">
										<g:message code="default.list.label" args="[message(code:'survey.question.label')]" />
									</a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
