package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.KevinPage;

import geb.Page;
import geb.error.RequiredPageContentNotPresent;

abstract class ReportPage extends KevinPage {
	
	private static final Log log = LogFactory.getLog(ReportPage)
	
	static content = {

	}
	
	def addTarget() {
		addTarget.click()
		waitFor {
			try { 
				ReportPage.log.debug("waiting for creation pane to be displayed");
				createTarget.present?createTarget.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			} 
		}
	}
	
	def addObjective() {
		addObjective.click()
		waitFor { 
			try {
				ReportPage.log.debug("waiting for creation pane to be displayed");
				createObjective.present?createObjective.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			}
		}
	}
	
}