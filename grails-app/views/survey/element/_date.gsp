<%@ page import="org.chai.kevin.util.DataUtils" %>

<!-- Date type question -->
<div id="element-${element.id}-${suffix}" class="element element-date ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<input id="date-${element.id}-${suffix}" type="text" value="${DataUtils.formatDate(value?.dateValue)}" name="elements[${element.id}].value${suffix}" class="idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<g:if test="${lastValue!=null && !lastValue.null}">
		<g:set var="tooltipValue" value="${DataUtils.formatDate(lastValue?.dateValue)}" />
		
		<g:render template="/templates/help_tooltip" model="[names: tooltipValue]" />
	</g:if>
	
	<g:render template="/survey/element/hints"/>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
<g:if test="${!print}">
	<script type="text/javascript">
		$(document).ready(
			function() {
				$(escape('#date-${element.id}-${suffix}')).glDatePicker({
					onChange : function(target, newDate) {
						target.val(newDate.getDate() + "-" + (newDate.getMonth() + 1) + "-" + newDate.getFullYear());
						target.trigger('change')
					},
					zIndex : "10"
				});
			}
		);
	</script>
</g:if>
