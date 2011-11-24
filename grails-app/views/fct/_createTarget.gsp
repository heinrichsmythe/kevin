<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Fct Target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'fctTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
	    <g:if test="${target != null}">
			<input type="hidden" name="id" value="${target.id}"/>
		</g:if>
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
		<g:input name="format" label="Format" bean="${target}" field="format"/>
		
   		<g:selectFromList name="groupUuids" label="${message(code:'facility.type.label')}" bean="${target}" field="groupUuidString" 
				from="${groups}" value="${target.groupUuids*.toString()}" optionValue="name" optionKey="uuid" multiple="true"/>
	
		<g:selectFromList name="objective.id" label="Objective" bean="${target}" field="objective" optionKey="id" multiple="false"
			from="${objectives}" value="${target.objective?.id}" values="${objectives.collect{i18n(field:it.names)}}" />
		
		<g:selectFromList name="sum.id" label="Sum" bean="${target}" field="sum" optionKey="id" multiple="false"
			from="${sums}" value="${target.sum?.id}" values="${sums.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		<div class="row">
			<button type="submit">Save Target</button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
