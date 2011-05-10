package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.dashboard.DashboardObjectiveService;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import sun.management.counter.Units;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;
import grails.test.GrailsUnitTestCase;

class ObjectiveServiceSpec extends IntegrationTests {
    
	def setup() {
		
		def target = new DashboardTarget(name: "target")
		target.save()
		def objective = new DashboardObjective(name: "objective", objectiveEntries: [])
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: target, weight: 1, order: 10)
		objective.save()
		def root = new DashboardObjective(name: "root", objectiveEntries: [])
		root.addObjectiveEntry new DashboardObjectiveEntry(entry: objective, weight: 1, order: 10, parent: root)
		root.save()
	}
	
	def "get parent"() {
		
		when:
		def child = DashboardTarget.findByName(childName)
		def parent = child.parent.parent
		
		then:
		parent != null
		parent.name == parentName
		
		where:
		childName	| parentName
		"target"	| "objective"
//		"objective"	| "root"
	}
	
	def "get parent of root"() {
		
		when:
		def child = DashboardObjective.findByName("root")
		def parent = child.parent
		
		then:
		parent == null
	}
	
	def "exception when multiple parents"() {
		
		setup:
		def root = new DashboardObjective(name: "root2", objectiveEntries: [])
		root.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardObjective.findByName("objective"), weight: 1, order: 10)
	
		when:
		root.save()
			
		then:
		thrown(DataIntegrityViolationException)
	}
	
}
