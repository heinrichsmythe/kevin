<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <g:sortableColumn property="id" title="${message(code: 'dataelement.id.label', default: 'Id')}" />
  			<th><g:message code="entity.name.label" default="Name"/></th>
  			<th><g:message code="type.label" default="Type"/></th>
  			<g:sortableColumn property="code" title="${message(code: 'dataelement.code.label', default: 'Code')}" />
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="dataElement"> 
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  					<ul class="horizontal">
  						<li>
  							<a class="edit-link" href="${createLinkWithTargetURI(controller:'dataElement', action:'edit', params:[id: dataElement.id])}">
  								<g:message code="default.link.edit.label" default="Edit" />
  							</a>
  						</li>
  						<li>
  							<a class="delete-link" href="${createLinkWithTargetURI(controller:'dataElement', action:'delete', params:[id: dataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
  								<g:message code="default.link.delete.label" default="Delete" />
  							</a>
  						</li>
  					</ul>
  				</td>
  				<td>${dataElement.id}</td> 
  				<td class="data-element-explainer" data-data="${dataElement.id}">
  					<a href="${createLink(controller:'dataElement', action:'getExplainer', params:[dataElement: dataElement.id])}"><g:i18n field="${dataElement.names}" /></a>
  				</td>
  				<td><g:toHtml value="${dataElement.type.getDisplayedValue(2, 2)}"/></td>
  				<td>${dataElement.code}</td>
  			</tr>
  			<tr>
  				<td colspan="5" class="explanation-row">
  					<div class="explanation-cell" id="explanation-${dataElement.id}"></div>
  				</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>

<script type="text/javascript">
	
	$(document).ready(function() {
		$('.data-element-explainer').bind('click', function() {
			var dataElement = $(this).data('data');
			explanationClick(this, dataElement, function(){});
			return false;
		});
	});
	
</script>