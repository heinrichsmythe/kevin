package org.chai.kevin.fct

import org.chai.kevin.data.Type;

import grails.validation.ValidationException;

class FctTargetSpec extends FctIntegrationTests {

	def "can save target"() {
		setup:
		def program = newReportProgram(CODE(1))
		def sum = newSum("1", CODE(1))
		
		when:
		new FctTarget(program: program, code: CODE(2)).save(failOnError: true)
		
		then:
		FctTarget.count() == 1
	}
	
	def "cannot save target with null code"() {
		setup:
		def program = newReportProgram(CODE(1))
		
		when:
		new FctTarget(program: program).save(failOnError: true)
		
		then:
		thrown ValidationException
		
	}
	
}
