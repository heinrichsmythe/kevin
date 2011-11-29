<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 		<meta name="layout" content="main" />
		<title>Facility Count Tables</title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:fct">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
        
        <r:require modules="fct"/>
        
	</head>
	<body>
		<div id="report">
			<div class="subnav">
				<g:render template="/templates/iterationFilter" model="[linkParams:params]"/>
				<g:render template="/templates/organisationFilter" model="[linkParams:params << [filter:'organisation']]"/>
				<g:render template="/templates/objectiveFilter" model="[linkParams:params]"/>
				<g:render template="/templates/levelFilter" model="[linkParams:params << [filter:'level']]"/>												
				
				<div class="right">
				<!-- ADMIN SECTION -->
				<shiro:hasPermission permission="admin:fct">
					<span> <a href="${createLinkWithTargetURI(controller:'fctObjective', action:'create')}">Add Objective</a> </span>|
					<span> <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}">Add Target</a> </span>
				</shiro:hasPermission>
				<!-- ADMIN SECTION END -->
			</div>
		</div>
		
		<g:if test="${fctTable != null}">
			<g:render template="/templates/facilityTypeFilter" model="[facilityTypes: facilityTypes, currentFacilityTypes: currentFacilityTypes, linkParams:params]"/>
		</g:if>
		<div id="center" class="main">
			<div id="values">
				<g:if test="${fctTable != null}">
					<g:if test="${!fctTable.targets.empty}">
						<table class="nice-table">
							<thead>
								<tr>
									<th class="object-name-box" rowspan="2">
										<div>
											<g:i18n field="${currentObjective.names}" />
										</div> <shiro:hasPermission permission="admin:fct">
											<span> 
												<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctObjective', action:'edit', id:currentObjective.id)}">
													<g:message code="default.link.edit.label" default="Edit" />
												</a> 
											</span>
											<span> 
												<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctObjective', action:'delete', id:currentObjective.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
													<g:message code="default.link.edit.label" default="Delete" />
												</a>
											</span>
										</shiro:hasPermission></th>
									<g:set var="i" value="${0}" />
									<g:each in="${fctTable.targets}" var="target">
										<th class="title-th" rowspan="2">
											<div class="bt">
												<g:i18n field="${target.names}" />
											</div>
											<shiro:hasPermission permission="admin:fct">
												<span> 
													<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'edit', id:target.id)}">
														<g:message code="default.link.edit.label" default="Edit" />
													</a> 
												</span>
												<span> 
													<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'delete', id:target.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
														<g:message code="default.link.edit.label" default="Delete" />
													</a>
												</span>
											</shiro:hasPermission>
										</th>
									</g:each>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th class="box-report-organisation">${currentOrganisation.name} - Total</th>
									<g:each in="${fctTable.targets}" var="target">
										<td class="box-report-value">
											<g:if test="${!fctTable.getTotal(target) != null}">
												${fctTable.getTotal(target).value}
											</g:if>
										</td>
									</g:each>
								</tr>
								<g:each in="${fctTable.parents}" var="parent">
									<tr>
										<th colspan="${fctTable.targets.size()+1}" class="parent-row">${parent.name}</th>
									</tr>
									<g:each in="${fctTable.parents.get(parent)}" var="children">
										<g:each in="${children}" var="child">										
											<tr class="row organisation" data-group="${child.organisationUnitGroup?.uuid ?: 'Total'}">
												<th class="box-report-organisation">${child.organisationUnitGroup? child.name : 'Total'}</th>
												<g:each in="${fctTable.targets}" var="target">
													<td class="box-report-value">
														<g:if test="${!fctTable.getFct(child, target) != null}">
															${fctTable.getFct(child, target).value}
														</g:if>
													</td>
												</g:each>
											</tr>
										</g:each>
									</g:each>
								</g:each>
							</tbody>
						</table>
					</g:if>
					<g:else>
						<div>
							Please <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}"> Add Target </a>
						</div>
					</g:else>
				</g:if>
				<g:else>
					<div class="help">Please select an Organisation / Objective</div>
				</g:else>
			</div>
		</div>
	</div>

</body>
</html>