<r:require module="foldable" />
<li class="${current?.id == program?.id ? 'current':''} foldable">	
	<% def programLinkParams = new HashMap(linkParams) %>
	<% programLinkParams.remove("dashboardEntity") %>		
	<% programLinkParams['program'] = program.id+"" %>
	<% linkParams = programLinkParams %>		
	<g:if test="${program.children != null && !program.children.empty && !programTree.disjoint(program.children)}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<a class="dropdown-link js_dropdown-link parameter" data-type="program"
			data-location="${program.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:linkParams)}">
			<g:i18n field="${program.names}"/>
	</a>
	<g:if test="${program.children != null || !program.children.empty}">					
		<g:each in="${program.children}" var="child">
			<g:if test="${programTree.contains(child)}">
				<ul class="location-fold" id="location-fold-${program.id}">
					<g:render template="/tags/filter/programTree"
						model="[
						controller: controller, 
						action: action,
						current: current, 
						program: child,
						programTree: programTree,
						linkParams:linkParams]" />
				</ul>
			</g:if>
		</g:each>
	</g:if>
</li>