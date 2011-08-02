<g:each in="${dataElements}" var="dataElement">
	<li data-code="${dataElement.id}" id="data-element-${dataElement.id}">
		<a	class="no-link cluetip" onclick="return false;" title="${i18n(field:dataElement.names)}"
			href="${createLink(controller:'dataElement', action:'getDataElementDescription', params:[dataElement: dataElement.id])}"
			rel="${createLink(controller:'dataElement', action:'getDataElementDescription', params:[dataElement: dataElement.id])}">
		<g:i18n field="${dataElement.names}"/></a> <span>[${dataElement.id}]</span>
	</li>
</g:each>
