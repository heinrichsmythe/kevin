<div class="filter">
	<span class="dropdown js_dropdown">
		<a class="time selected" href="#" data-period="${currentPeriod.id}" data-type="period">
			<g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/>
		</a>
		<div class="hidden dropdown-list js_dropdown-list">
			<ul>
				<g:each in="${periods}" var="period">
					<% def periodLinkParams = new HashMap(linkParams) %>
					<% periodLinkParams << [period:period.id] %>
					<% linkParams = periodLinkParams %>
					<li>
						<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:linkParams)}">
							<span><g:dateFormat format="yyyy" date="${period.startDate}" /></span> 
						</a>
					</li>
				</g:each>
			</ul>
		</div> 
	</span>
</div>