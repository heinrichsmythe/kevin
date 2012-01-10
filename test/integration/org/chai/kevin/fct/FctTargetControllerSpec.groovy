package org.chai.kevin.fct

class FctTargetControllerSpec extends FctIntegrationTests {

	def fctTargetController
	def locationService
	
	def "save target"() {
		setup:
		def objective = newReportObjective(CODE(1))
		def sum = newSum("1", CODE(3))
		fctTargetController = new FctTargetController()
		fctTargetController.locationService = locationService
		
		when:
		fctTargetController.params.code = CODE(2)
		fctTargetController.params['objective.id'] = objective.id+""
		fctTargetController.params['sum.id'] = sum.id+""
		fctTargetController.params.groupUuids = [DISTRICT_HOSPITAL_GROUP]
		fctTargetController.saveWithoutTokenCheck()
		
		then:
		FctTarget.count() == 1
		FctTarget.list()[0].sum.equals(sum)
	}
	
}
