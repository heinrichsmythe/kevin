package org.chai.kevin.dashboard

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;

import grails.plugin.spock.UnitSpec;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard()
	}
	
	def "test calculations"() {
		expect:
		DashboardCalculation.count() == 4
		def nurse = DashboardTarget.findByCode("A1");
		nurse.save();
		DashboardCalculation.count() == 4
	}
	
	def "call twice in a row"() {
		
		expect:
		DashboardObjective.findByCode(objectiveCode).objectiveEntries.size() == expectedSize
		
		where:
		objectiveCode	| expectedSize
		"STAFFING"		| 2
		"STAFFING"		| 2
		"HRH"			| 1
		
	}
	
	def "objective order can be null"() {
		when:
		def objective = DashboardObjective.findByCode("HRH");
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test"]), code:"TEST"), weight: 1);
		objective.save(flush: true)
		
		then:
		def newObjective = DashboardObjective.findByCode("HRH");
		newObjective.objectiveEntries.size() == 2
	}
	
	def "objective save preserves order"() {
		when:
		def objective = DashboardObjective.findByCode("HRH");
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test 4"]), code:"TEST4"), weight: 1, order: 5);
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test 5"]), code:"TEST5"), weight: 1, order: 4);
		objective.save(flush: true)
		
		then:
		def newObjective = DashboardObjective.findByCode("HRH");
		newObjective.objectiveEntries.size() == 3
		newObjective.objectiveEntries[2].order == 5
		newObjective.objectiveEntries[1].order == 4
	}
	
	// integration test
	def "weighted objective cascade"() {
		
		when:
		def objective = DashboardObjective.findByCode("STAFFING");
		
		then:
		objective.objectiveEntries.size() == 2
	}
	
	// integration test
	def "objective delete cascade deletes parent"() {
		when:
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.parent.parent.objectiveEntries.remove(objective.parent)
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == 1
		DashboardTarget.count() == dashboardTargetCount;
	}

	def "delete objective entry cascade deletes targets"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		new ArrayList(objective.objectiveEntries).each { 
			objective.objectiveEntries.remove(it);
			it.delete(flush: true); 
		}
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 2
		DashboardTarget.count() == dashboardTargetCount;
	}
	
	def "objective save removes objective entries"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.objectiveEntries.clear()
		objective.save(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 2
		DashboardTarget.count() == dashboardTargetCount;
	}
	
	def "remove target deletes parent objective entry"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardTarget.findByCode("A1");
		objective.parent.parent.objectiveEntries.remove(objective.parent)
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 1
		DashboardTarget.count() == dashboardTargetCount - 1;
	}
	
	// integration test
	def "objective entries has parents"() {
		
		expect:
		DashboardObjectiveEntry.count() == 4
	}
	
	// integration test
	def "weighted objectives for target"() {
		when:
		def objective = DashboardObjective.findByCode("STAFFING");
		def objectiveEntry = DashboardObjectiveEntry.findByEntry(objective);
		
		then:
		objectiveEntry != null
		objectiveEntry.getParent() != null
		
	}
	
	def "save objective entry saves target"() {
		when:
		def dashboardTargetCount = DashboardTarget.count()
		def target = new DashboardTarget(name: "Test");
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: target, weight: 1, order: 1)
		objective.save();
		
		then:
		DashboardObjectiveEntry.count() == 5
		DashboardTarget.count() == dashboardTargetCount + 1
	}
}
