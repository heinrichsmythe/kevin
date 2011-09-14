<div class="filter">
	<div class="dropdown white-dropdown">
		<a class="selected" href="#" data-type="question">New Question</a>
		<div class="hidden dropdown-list">
			<ul>
				<li><a class="flow-add"
					href="${createLink(controller:'simpleQuestion', action:'create', params:[sectionId: section.id])}">New
						Simple Question</a>
				</li>
				<li><a class="flow-add"
					href="${createLink(controller:'checkboxQuestion', action:'create', params:[sectionId: section.id])}">New
						Checkbox Question</a>
				</li>
				<li><a class="flow-add"
					href="${createLink(controller:'tableQuestion', action:'create', params:[sectionId: section.id])}">New
						Table Question</a>
				</li>
			</ul>
		</div>
	</div>
</div>