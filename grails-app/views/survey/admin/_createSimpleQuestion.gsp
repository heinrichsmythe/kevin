<div id="add-question" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Simple Question</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container">
	<div class="data-field-column">
		<g:form url="[controller:'simpleQuestion', action:'save']" useToken="true">
			<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="250"  width="400" maxHeight="150" />
			
			<input type="hidden" name="descriptions.jsonText" value=" "/>
			
			<input type="hidden" name="surveyElement.dataElement.id"  value="${question.surveyElement?.dataElement?.id}" id="data-element-id" />
			<div class="row ${hasErrors(bean:question, field:'surveyElement', 'errors')}">
				<label for="survey">Data Element:</label> 
			    <input type="text" name="surveyElement.dataElement.name" value="${i18n(field: question.surveyElement?.dataElement?.names)}" id="data-element-name" class="idle-field" disabled />
			    <g:if test="${question.id != null}">
				   <span>
				       <a href="${createLink(controller:'surveyValidationRule', action:'list', params:[elementId: question.surveyElement.id])}">
				        View Validation Rules
				       </a> 
				   </span>
			    </g:if>
			    <div class="error-list"><g:renderErrors bean="${question}" field="surveyElement" /></div>
			 </div>
			 
			<g:input name="order" label="Order" bean="${question}" field="order"/>
			
			<div class="row ${hasErrors(bean:question, field:'section', 'errors')}">
				<label for="section.id">Objective:</label>
				<select name="section.id">
					<option value="null">-- Select an Section --</option>
					<g:each in="${sections}" var="section">
						<option value="${section.id}" ${section.id+''==fieldValue(bean: question, field: 'section.id')+''?'selected="selected"':''}>
							<g:i18n field="${section.names}"/>
						</option>
					</g:each>
				</select>
				<div class="error-list"><g:renderErrors bean="${question}" field="section" /></div>
			</div>
			
			<div class="row ${hasErrors(bean:question, field:'groupUuidString', 'errors')}">
				<label for="groups" class="display-in-block">Organisation Unit Group:</label>
					<select name="groupUuids" multiple="multiple" size="5" >
						<g:each in="${groups}" var="group">
							<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
					           ${group.name}
				            </option>
						</g:each>
					</select>
				<div class="error-list">
					<g:renderErrors bean="${question}" field="groupUuidString" />
				</div>
			</div>
			
			<g:if test="${question.id != null}">
				<input type="hidden" name="id" value="${question.id}"></input>
				<input type="hidden" name="surveyElement.id" value="${question.surveyElement.id}"></input>
			</g:if>
			<div class="row">
				<button type="submit" class="rich-textarea-form">Save Question</button>
				<button id="cancel-button">Cancel</button>
			</div>
		</g:form>
	</div>
	<div class="data-search-column">
		<g:form name="search-data-form" class="search-form" url="[controller:'dataElement', action:'getData']">
			<div class="row">
				<label for="searchText">Search: </label>
		    	<input name="searchText" class="idle-field"></input>
		    	<button type="submit">Search</button>
				<div class="clear"></div>
			</div>
		</g:form>
	    <ul class="filtered idle-field" id="data" ></ul>
	</div>
	<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getDataElement(function(event){
			$('#data-element-id').val($(this).data('code'));
			$('#data-element-name').val($.trim($(this).text()));
		});
		 getRichTextContent();
	});					
</script>