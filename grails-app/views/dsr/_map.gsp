<li>
	<g:render template="/templates/reportTitle" model="[entity: currentProgram, title: i18n(field:currentProgram.names), file: 'star_small.png']"/>
	<g:reportExport linkParams="${params}" />
	<g:reportView linkParams="${params}" exclude="${['indicators']}"/>
	
	<div class="selector">
		<g:reportCategoryFilter selected="${currentCategory}" program="${currentProgram}" linkParams="${params}" exclude="${['indicators']}"/>
	</div>
	
	<g:render template="/dsr/reportProgramMap" 
	model="[linkParams:params, reportTable: dsrTable, reportLocations: dsrTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dsrTable.indicators]"/>
	<g:render template="/dsr/reportProgramMapTable" 
		model="[linkParams:params, reportTable: dsrTable, reportLocations: dsrTable.getLocations(currentLocation, locationSkipLevels, currentLocationTypes), reportIndicators: dsrTable.indicators]"/>
</li>