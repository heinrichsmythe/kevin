<!-- Date type question -->
<g:set var="surveyEnteredValue" value="${surveyPage.enteredValues[surveyElement]}"/>
<g:set var="surveyElementValue" value="${surveyPage.surveyElements[surveyElement.id]}"/>

<div class="element element-date element-${surveyElement.id} ${surveyEnteredValue.skipped?'skipped':''} ${!surveyEnteredValue.valid?'errors':''}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<input type="text" value="${surveyEnteredValue?.value}" name="surveyElements[${surveyElement.id}].value" class="idle-field"  ${readonly?'disabled="disabled"':''}/>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>