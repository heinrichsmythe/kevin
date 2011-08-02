<div id="top-container">
	<div id="survey-iteration-box" class="grey-rounded-box-top">
		<h5>Year:
			<span class="survey-highlight-title"> 
				<g:dateFormat format="yyyy" date="${period.startDate}" />
			</span>
			</h5>
	</div>
	<div id="survey-objective-box" class="grey-rounded-box-top">
		<div id="survey-container-left-side">
			<h5>
				Facility Name: <span class="survey-highlight-title">${organisation.name}</span>
			</h5>
		</div>
		<div id="survey-container-right-side">
			<h5>
				<g:if test="${objective}">
					Strategic Objective: 
					<g:if test="${objective != null}">
						<span class="survey-highlight-title">
							<g:i18n field="${objective.names}" />
						</span>
					</g:if>
					<g:else>
						-
					</g:else>
				</g:if>
			</h5>
		</div>
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>