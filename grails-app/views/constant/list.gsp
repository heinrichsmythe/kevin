
<%@ page import="org.hisp.dhis.dataelement.Constant" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'constant.label', default: 'Constant')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="entity-list">
    		<div id="constants">
	            <h5><g:message code="default.list.label" args="[entityName]" /></h5>
	            <g:if test="${flash.message}">
	            <div class="message">${flash.message}</div>
	            </g:if>
				<div class="float-right">
					<a id="add-constant-link" href="${createLink(controller:'constant', action:'create')}">new constant</a>
				</div>
	            
	            <div class="list">
	                <table>
	                    <thead>
	                        <tr>
	                            <g:sortableColumn property="name" title="${message(code: 'constant.name.label', default: 'Name')}" />
	
	                            <g:sortableColumn property="value" title="${message(code: 'constant.value.label', default: 'Value')}" />
	                        
	                            <g:sortableColumn property="shortName" title="${message(code: 'constant.shortName.label', default: 'Short Name')}" />
	                        
	                            <g:sortableColumn property="description" title="${message(code: 'constant.description.label', default: 'Description')}" />
	                        
	                        	<td></td>
	                        </tr>
	                    </thead>
	                    <tbody>
	                    <g:each in="${constants}" status="i" var="constant">
	                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
	                            <td class="edit-constant-link"><g:link action="edit" id="${constant.id}">${fieldValue(bean: constant, field: "name")}</g:link></td>
	                        
	                            <td>${fieldValue(bean: constant, field: "value")}</td>
	                        
	                            <td>${fieldValue(bean: constant, field: "shortName")}</td>
	                        
	                            <td>${fieldValue(bean: constant, field: "description")}</td>
	                        
	                        	<td class="delete-constant-link"><a href="${createLink(controller:'constant', action:'delete', id:constant.id)}">delete</a></td>
	                        </tr>
	                    </g:each>
	                    </tbody>
	                </table>
	            </div>
	            <div class="paginateButtons">
	                <g:paginate total="${constantCount}" />
	            </div>
	            
	        </div>
			<div class="flow-container"></div>
        </div>
        
        <script type="text/javascript">
			$(document).ready(function() {
				$('#constants').flow({
					addLinks: '#add-constant-link',
					onSuccess: function(data) {
						if (data.result == 'success') {
							location.reload();
						}
					}
				});
			});
		</script>
    </body>
</html>
