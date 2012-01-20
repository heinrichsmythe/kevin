<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="dashboard.view.label" default="Dashboard" /></title>
        
        <!-- for admin forms -->
        <shiro:hasPermission permission="admin:dashboard">
        	<r:require modules="form"/>
        </shiro:hasPermission>
        
        <r:require module="dashboard"/>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    </head>
    <body>
		<div id="report">
			<div class="subnav">			
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[tab:'dashboard', linkParams:params]"/>
				<g:render template="/templates/dashboard/reportTabHelp"/>
				<ul class='clearfix' id='questions'>
  					<li class='question push-20'>
						<g:render template="/templates/dashboard/reportTableHeader" model="[table:'program']"/>						
						<g:if test="dashboard != null">
							<g:render template="/templates/dashboard/reportTableCompareFilter" model="[table:'program', params:params]"/>
							<div class='horizontal-graph-wrap'>
								<g:render template="/templates/dashboard/reportProgramTable" model="[params:params]"/>
			                </div>
		                </g:if>
	                </li>
	                <li class='question push-10'>
		                <g:render template="/templates/dashboard/reportTableHeader" model="[table:'location']"/>						
		                <g:if test="dashboard != null">
		                <g:render template="/templates/dashboard/reportTableCompareFilter" model="[table:'location',params:params]"/>
							<div class='horizontal-graph-wrap'>
								<g:render template="/templates/dashboard/reportLocationTable" model="[params:params]"/>			                  
							</div>
		                </g:if>
		            </li>
		        </ul>    
			</div>    	
	    </div>	      
    </body>
</html>