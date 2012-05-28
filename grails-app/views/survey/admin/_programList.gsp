<g:searchBox controller="question" action="search" params="${[survey: survey?.id]}" entityName="Survey Question"/>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<th><g:message code="entity.code.label"/></th>
			<th><g:message code="entity.name.label"/></th>
			<th><g:message code="entity.datalocationtype.label"/></th>
			<th><g:message code="default.number.label" args="[message(code:'survey.section.label')]"/></th>
			<th><g:message code="entity.order.label"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="program">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'program', action:'edit', params:[id: program.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'program', action:'delete', params:[id: program.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" />
							</a>
						</li>
					</ul>
				</td>
				<td>${program.code}</td>
				<td><g:i18n field="${program.names}" /></td>
				<td>${program.typeCodeString}</td>
				<td>${program.sections.size()}</td>
				<td>${program.order}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="selected dropdown manage-btn" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="hidden manage-list js_dropdown-list dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'section', action:'list', params:['program.id': program.id])}">
										<g:message code="default.list.label" args="[message(code:'survey.section.label')]" />
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
