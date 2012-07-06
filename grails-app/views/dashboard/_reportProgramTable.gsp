<table class="horizontal-graph">
<thead>
  <tr>
	<th>
		<g:i18n field="${currentProgram.names}"/>
		&nbsp;
		<g:render template="/templates/help_tooltip" 
			model="[names: i18n(field: currentProgram.names), descriptions: i18n(field: currentProgram.descriptions)]" />
	</th>	
	<th><g:message code="dashboard.report.table.score"/></th>
	<th></th>
  </tr>
</thead>
<g:if test="${dashboard != null && dashboard.dashboardEntities != null && !dashboard.dashboardEntities.empty}">
	<tbody>
		<g:each in="${dashboard.dashboardEntities}" var="entity">			
			<tr>
				<g:set var="percentageValue" />
				<td>
					<g:if test="${!entity.isTarget()}">
						<a href="${createLink(controller:'dashboard', action:'view',
						params:[period: currentPeriod.id, program: entity.program.id, location: currentLocation.id, dashboardEntity: entity.id])}">
							<g:i18n field="${entity.program.names}" />
						</a>
						&nbsp;
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: entity.program.names), descriptions: i18n(field: entity.program.descriptions)]" />
					</g:if> 
					<g:else>
						<g:i18n field="${entity.names}" />
						&nbsp;
						<g:render template="/templates/help_tooltip" 
							model="[names: i18n(field: entity.names), descriptions: i18n(field: entity.descriptions)]" />
					</g:else>									 
					</td>
				<td>
					<g:set var="percentageValue" value="${dashboard.getPercentage(currentLocation, entity)}" />
					<g:if test="${!percentageValue.isNull()}">
						<g:reportValue value="${percentageValue}" type="${dashboard.type}" format="#%"/>
					</g:if>
					<g:else>
						<g:message code="report.value.na"/>
					</g:else>
					</td>					
				<td>
					<!-- percentage value -->
					<g:if test="${percentageValue.isNull()}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="null"
							style="width:0%"							 
							original-title="null"></div>
					</g:if>
					<g:elseif test="${percentageValue.numberValue <= 1}">
						<div class="js_bar_horizontal tooltip horizontal-bar" 
							data-percentage="${percentageValue.numberValue * 100}"
							style="width:${percentageValue.numberValue * 100}%"							 
							original-title="${percentageValue.numberValue * 100}%"></div>
					</g:elseif>
					<g:else>
						<div class="js_bar_horizontal tooltip horizontal-bar expand-bar" 
							data-percentage="${percentageValue.numberValue * 100}" 
							style="width:100%"							 
							original-title="${percentageValue.numberValue * 100}%"></div>
					</g:else>						
					<!-- comparison value -->
					<div id="compare-dashboard-entity-${entity.id}" 
					class="js_bar_horizontal tooltip horizontal-bar-avg hidden" 							
						data-percentage="45" 
						style="width:45%;" 
						original-title="45%"></div>
				</td>
			</tr>
		</g:each>
	</tbody>
</g:if>
</table>