<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle default="Grails" /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	<!-- link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" / -->

	<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/cluetip',file:'jquery.cluetip.css')}"/ >
	
	<link href="${resource(dir:'css',file:'screen.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
	<link href="${resource(dir:'css',file:'print.css')}" media="print" rel="stylesheet" type="text/css" />
	<!--[if lt IE 8]>
		<link href="${resource(dir:'css',file:'ie.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
	<![endif]-->
	
	<g:layoutHead />

	<g:javascript library="jquery" plugin="jquery" />
	<g:javascript src="jquery/fieldselection/jquery.fieldselection.js" />
	<g:javascript src="jquery/cluetip/jquery.cluetip.js" />
	<g:javascript src="jquery/cluetip/lib/jquery.hoverIntent.js" />
	<g:javascript src="jquery/form/jquery.form.js" />
	<g:javascript src="jquery/fliptext/jquery.mb.flipText.js" />
	<g:javascript src="richeditor/nicEdit.js" />
	<g:javascript src="jquery/url/jquery.url.js" />
	<g:javascript src="jquery/progressbar/jquery.progressbar.js" />
	<g:javascript library="application" />
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<ga:trackPageviewAsynch />
</head>
<body class="bp two-columns">

	<div id="spinner" class="spinner" style="display: none;">
		<img src="${resource(dir:'images',file:'ajax-loader.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
	</div>

	<div id="container" class="">
		<div id="header">
		<div id="header-banner" style="background-image:url('${resource(dir:'images',file:'dhsst-banner.png')}');">
			<% def localeService = application.getAttribute("org.codehaus.groovy.grails.APPLICATION_CONTEXT").getBean("localeService") %>
			<div class="locales" id="switcher">
				<g:each in="${localeService.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<a class="${localeService.currentLanguage==language?'no-link':''}" href="${createLink(controller: controller, action: action, params:params)}">${language}</a>
				</g:each>
			</div>
			<shiro:user>
				<div id="logout">
					<a href="${createLink(controller: 'auth', action: 'signOut')}">logout</a>					
				</div>
			</shiro:user>
			<div class="clear"></div>
		</div>			
			<!--<h1>Welcome to Kevin</h1>-->
			<div id="navigation">
				<ul id="main-menu" class="menu">
				    <shiro:hasPermission permission="menu:survey">
						<li><a href="${createLink(controller: 'editSurvey', action:'view')}"><g:message code="header.navigation.survey" default="Survey"/></a></li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:reports">
						<li><a href="#"><g:message code="header.navigation.reports" default="Reports"/></a>
							<ul class="submenu">
								<li><a href="${createLink(controller: 'dashboard', action:'view')}"><g:message code="header.navigation.dashboard" default="Dashboard"/></a></li>
								<li><a href="${createLink(controller: 'cost', action:'view')}"><g:message code="header.navigation.costing" default="Costing"/></a></li>
								<li><a href="${createLink(controller: 'dsr', action:'view')}"><g:message code="header.navigation.dsr" default="District Summary Reports"/></a></li>
								<li><a href="${createLink(controller: 'maps', action:'view')}"><g:message code="header.navigation.maps" default="Maps"/></a></li>
							</ul>
						</li>
					</shiro:hasPermission>
					<shiro:hasPermission permission="menu:admin">
						<li><a href="#"><g:message code="header.navigation.administration" default="Administration"/></a>
							<ul class="submenu">
								<li><a href="${createLink(controller: 'expression', action:'list')}"><g:message code="header.navigation.expressions" default="Expressions"/></a></li>
								<li><a href="${createLink(controller: 'constant', action:'list')}"><g:message code="header.navigation.constants" default="Constants"/></a></li>
								<li><a href="${createLink(controller: 'dataElement', action:'list')}"><g:message code="header.navigation.dataelement" default="Data Element"/></a></li>
								<li><a href="${createLink(controller: 'enum', action:'list')}"><g:message code="header.navigation.enum" default="Enum"/></a></li>
								<li><a href="${createLink(controller: 'iteration', action:'list')}"><g:message code="header.navigation.iteration" default="Iterations"/></a></li>
								<li><a href="${createLink(controller: 'survey', action:'list')}"><g:message code="header.navigation.survey" default="Survey"/></a></li>
							</ul>
						</li>
					</shiro:hasPermission>
				</ul>
				<shiro:hasPermission permission="admin">
					<div class="float-right" style="background-color: red;"><a target="_blank" href="http://districthealth.moh.gov.rw/redmine">Found a bug? Go to REDMINE</a></div>
				</shiro:hasPermission>
				<div class="clear"></div>
			</div>
			<div class="clear"></div>
		</div>

		<div id="content">
			<g:layoutBody />
			<div class=clear></div>
		</div>

		<div id="footer">&copy; - Clinton Health Access Initiative - <a href="#"><g:message code="footer.labels.about" default="About"/></a> | 
		<a href="#"><g:message code="footer.labels.contact" default="Contact"/></a> 
		| <a href="#"><g:message code="footer.labels.helpdesk" default="Helpdesk"/></a></div>
	</div>

	<!-- utilities -->
	<script type="text/javascript">
	
		/**
		 * edition pane functionality
		 */
		(function($) {
			var defaults = {
				container: '.flow-container',
				addLinks: '.flow-edit,.flow-add,.flow-preview',
				deleteLinks: '.flow-delete',
				onSuccess: function(){}
			}
			
			$.fn.flow = function(options) {
			    this.config = $.extend({}, defaults, options || {}); 

				var values = this;
				var container = this.next(this.config.container).first();
				
				var self = this;
				$(document).find(this.config.addLinks).each(function(index, element){
					$(element).bind('click', function() {
						$.ajax({
							type : 'GET',
							dataType: 'json',
							url : $(this).attr('href'),
							success : function(data) {
								if (data.result == 'success') {
									$(values).slideUp(function() {
										$(container).html(data.html);
										$(container).slideDown('slow', function(){
											bindEvents(container, values, self.config.onSuccess);
										});
									});
								}
							}
						});
						return false;
					});
				});
				
				$(document).find(this.config.deleteLinks).each(function(index, element) {
					$(element).bind('click', function() {
						var del = confirm('Are you sure you want to delete this?')
						if (del) {
							$.ajax({
								type : 'POST',
								dataType: 'json',
								url : $(this).attr('href'),
								success : function() {
									location.reload();
								}
							});
						}
						return false;
					});
				});
			}
			
			function bindEvents(container, values, onSuccess) {
				var cancel = $(container).find("#cancel-button").first();
				var form = $(container).find("form").first();
				
				$(cancel).bind('click', function() {
					// $('#add-expression-cancel').click();
					clearForm(form);
					$(container).slideUp('slow', function() {
						$(values).slideDown();
					});
					return false;	
				});

				$(form).find("button[type=submit]").bind('click', function() {$(this).submit(); return false;})				
				$(form).bind('submit', function() {
					$.ajax({
						type : 'POST',
						dataType: 'json',
						data : $(this).serialize(),
						url : $(this).attr('action'),
						success : function(data, textStatus) {
							onSuccess(data);
							if (data.result == 'error') {
								$(container).html(data.html);
								bindEvents(container, values, onSuccess);
							} 
							else {
								$(container).slideUp('slow', function() {
									$(values).slideDown();
								});
								clearForm(form);
							}
						}
					});
					return false;
				});
			}

			function clearForm(form) {
				$(form).clearForm();
				$(form).find('.errors').removeClass('errors');
				$(form).find('.error-list ul').remove();
			}
			
		})(jQuery);
		// end of edition pane functionality
		/**
		 *
		 */
		
		/**
		 * data Eleement Search
		 */
		 function getDataElement(callback){
		$('.search-form button').bind('click', function(){$(this).submit(); return false;});
		$('.search-form').bind('submit', function() {
			var element = this;
			$.ajax({
				type: 'GET', data: $(element).serialize(), url: $(element).attr('action'), 
				success: function(data, textStatus){
					if (data.result == 'success') {
						var filtered = $(element).parent('div').find('.filtered');
						filtered.html(data.html);
						filtered.find('a.cluetip').cluetip(cluetipOptions);
						filtered.find('li').bind('mousedown', callback);
						filtered.find('li')
					}
				}
			});
			return false;
		});
		 }
		//Get Rich Text Area content
		function getRichTextContent(){
			$('.rich-textarea-form').bind('click',function(){
				$('textarea').each(function(){
					$(this).val($(this).prev('div').children().html())
				})
			});
		}
				
		/**
		 * options for cluetip plugin
		 */		
		var cluetipOptions = {
			ajaxProcess : function(data) {
				return data.html;
			},
			ajaxSettings : {
				dataType : 'json'
			},
			cluetipClass : 'jtip',
			hoverIntent : false,
			dropShadow : false,
			width : '400px',
			clickThrough : true,
			hoverIntent: {
				sensitivity:  1,
				interval:     500,
				timeout:      0
			}
		};

		/**
		 * nice table functionality
		 */
 		var selected = false;
		function addClass(column, row, className) {
			if (column != null) {
				addClassByType('col', column, className);
			}
			if (row != null) {
				addClassByType('row', row, className);
			}
		}
		
		function addClassByType(type, id, className) {
				$('.'+type+'-'+id).addClass(className);
		}    
		// END of nice table functionality
		
		/**
		 * explanation functionality
		 */
		function explanationClick(element, prefix, onSuccess){
			if ($(element).find('a').size() == 0) return;
			
			if ($(element).hasClass('me-open')) {
				toClose = true
			}
			else {
				toClose = false
			}
			$('.me-open').removeClass('me-open');
			
			if (toClose) {
				slideUp(element, function(){});
			}
			else {
				if ($('#explanation-'+prefix).hasClass('loaded')) {
					slideUpExplanation(element, function(){slideDown(prefix);});
				}
				else {
					$.ajax(
					{
						type:'GET', url: $(element).find('a').attr('href'),
						success: function(data) {
							slideUpExplanation(element, function() {
								addData(prefix, data);
								onSuccess(prefix);
							});
						}
					});
				}
				$(element).addClass('me-open');
			}
   		}
   		
   		function slideUpExplanation(element, callback) {
   			if ($(element).parents('table').first().find('.explanation-cell.visible').length == 0) {
				callback();
			}
			else {
				slideUp(element, callback);
			}
   		}
   		
   		function slideUp(element, callback) {
   			$(element).parents('table').first().find('.explanation-cell.visible').slideUp("slow", function() {callback();});
   			$(element).parents('table').first().find('.explanation-cell.visible').removeClass('visible');
      	}
   		
   		function slideDown(prefix) {
			$('#explanation-'+prefix).addClass('visible');
   			$('#explanation-'+prefix).slideDown('slow');
   		}
   		
   		function addData(prefix, data) {
   			$('#explanation-'+prefix).html(data);
			$('#explanation-'+prefix).addClass('loaded');
			
			slideDown(prefix);
   		}
		// END of explanation functionality
		
		$(document).ready(function() {
			$(document).delegate('.togglable a.toggle-link', 'click', function(){
				var togglable = $(this).parents('.togglable');
				var toggle = $(this).data('toggle')
				$(togglable).find('.toggle-entry').each(function(key, value){
					if (toggle != $(this).data('toggle')) $(this).hide();
					else $(this).show();
				});
				$(togglable).find('.toggle-link').each(function(key, value){
					if (toggle != $(this).data('toggle')) $(this).removeClass('no-link');
					else $(this).addClass('no-link');
				});
				return false;
			});
			 		
			//Fliping text 
			$(".tb").mbFlipText(true); //top to bottom
			$(".bt").mbFlipText(false); //bottom to top
			
			/**
			 * foldables
			 */
			$('.foldable a.foldable-toggle').click(function(event) {
				$(this).parent('.foldable').children('ul').toggle();
				$(this).toggleClass('toggled');
				return false;
			});
			// we hide everything
			$('.foldable ul').hide();
			// we show the current
			var current = $('.foldable .current');
			while (current.parents('li').hasClass('foldable') && current.size() > 0) {
				current.addClass('opened');
				current.children('ul').show();
				current.children('a').addClass('toggled');
				current = current.parents('li');
			}
			// we open the first one
			$('.foldable').each(function(index, element){
				if (!$(element).parents('li').hasClass('foldable')) {
					$(element).children('ul').show();
					$(element).children('a').addClass('toggled');
				}
			});
			
			/**
			 * drop-down menus
			 * TODO transform in jQuery Plugin style
			 **/
			$(document).delegate('.dropdown .selected', 'click', function() {
				$(this).parent(".dropdown").find("div.dropdown-list").toggle();
				return false;
			});
			$(".dropdown-list a.dropdown-link").bind('click', function() {
				$(this).parents('.dropdown-list').hide();
			});
			$(document).bind('click', function(e) {
				var clicked = e.target;
				$(".dropdown a.selected").each(function(){
					if (clicked != this) {
						$(this).parent(".dropdown").find("div.dropdown-list").hide();	
					}
				});
			});
			
			/**
			 * nice tables
			 */
			$('.nice-table .cell').bind({
				mouseenter: function(){
					$('.cell').removeClass('highlighted');
					addClass($(this).data('col'), $(this).data('row'), 'highlighted');
				},
				mouseleave: function(){
					if (selected) {
						$('.cell').removeClass('highlighted');
					}
					else {
						$('.cell.value').addClass('highlighted');
						$('.cell.label').removeClass('highlighted');
					}
				},
				click: function(){
					var row = $(this).data('row');
					var col = $(this).data('col');
				
					if ($(this).hasClass('me-selected')) {
						deselect = true
						selected = false;
					}
					else {
						deselect = false;
						selected = true;
					}
					$('.cell').removeClass('me-selected');
					$('.cell').removeClass('selected');
				
					if (!deselect) {
						$(this).addClass('me-selected');
						addClass(col, row, 'selected');
					}
				}
			});
			
			/** Nice Input form element  */
			$(document).delegate('input[type="text"],textarea','focus',function() {
				$(this).removeClass("idle-field completed-field").addClass("focus-field");
		        if (this.value == this.defaultValue && this.defaultValue==''){
		        	this.value = '';
		    	}
		        if(this.value != this.defaultValue){
			    	this.select();
		        }
		    });
		    $(document).delegate('input[type="text"],textarea','blur',function() {
		    	$(this).removeClass("focus-field").addClass("idle-field");
		    	if (this.value != this.defaultValue){
		    		this.value =$.trim(this.value);
		    		$(this).removeClass("focus-field").addClass("completed-field");
		    	}
		    });
			    
			//Styling the main menu
			$('#main-menu > li').hover(
				function () {
					//show its submenu
					if (!$('ul', this).hasClass('open')) {
						$('ul', this).addClass('open');
						$('ul', this).show();
					}
		 
				}, 
				function () {
					var self = this;
					//hide its submenu
					$('ul', this).slideUp(10, function(){
						$('ul', self).removeClass('open');
					});	
				}
			);
			
			/**
			 * element explanations
			 */
			$(document).delegate('.element', 'mouseenter mouseleave', function() {
				var data = $(this).data('id');
				
				$(this).parents('.info').find('.data-'+data).toggleClass('highlighted');
				$(this).toggleClass('highlighted');
			});
			
			$(document).delegate('.element', 'click', function(){
				if (!$(this).hasClass('selected')) {
					$(this).parents('.info').find('.element').removeClass('selected');
					$(this).parents('.info').find('.data').removeClass('selected');
				}
				var data = $(this).data('id');
				$(this).parents('.info').find('.data-'+data).toggleClass('selected');
				$(this).toggleClass('selected');
			});
						
		});
		function toggleFacilityType() {
			if ($('#facility-type-filter input').size() != 0) {
    			var checked = [];
    			$('#facility-type-filter input').each(function(){
					if (this.checked) checked.push($(this).val())
				});
				$('.row.organisation').each(function(){
					if ($.inArray($(this).data('group'), checked) >= 0) $(this).show();
					else $(this).hide()
				});
			}
		}
	</script>
</body>
</html>
