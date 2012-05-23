package org.chai.kevin.dsr

import org.chai.kevin.data.Type
import org.chai.kevin.location.Location
import org.chai.kevin.reports.ReportProgram

class DsrControllerSpec extends DsrIntegrationTests {

	def dsrController
	
	def "get dsr"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)		
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program)
		
		when: "valid table"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.location = Location.findByCode(RWANDA).id
		dsrController.params.program = program.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentProgram.equals(program)
		model.dsrTable != null		
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
	}
		
	def "get dsr with no program and no location, default to root program and root location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program)
		
		when: "no program and no location"
		dsrController = new DsrController()
		dsrController.params.period = period.id
		def model = dsrController.view()
		
		then:
		model.currentPeriod.equals(period)
		model.currentLocation.equals(Location.findByCode(RWANDA))
		model.currentProgram.equals(ReportProgram.findByCode(ROOT))
		model.dsrTable != null		
		model.dsrTable.valueMap.isEmpty() == false
		model.dsrTable.hasData() == true
		
	}
	
	def "get dsr with no targets"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		
		when:
		dsrController = new DsrController()
		dsrController.params.period = period.id
		dsrController.params.location = Location.findByCode(BURERA).id
		dsrController.params.program = program.id
		def model = dsrController.view()
		
		then:
		model.dsrTable != null
		model.dsrTable.hasData() == false		
	}
	
	def "get dsr with invalid program parameter"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def program = newReportProgram(ROOT)
		def dataElement = newRawDataElement(CODE(3), Type.TYPE_NUMBER())
		def target = newDsrTarget(CODE(4), 1, dataElement, program)
		
		when:
		dsrController = new DsrController()
		dsrController.params.program = program.id+1
		def model = dsrController.view()
		
		then:
		model.currentProgram.id == program.id
		model.dsrTable != null
		model.dsrTable.hasData() == true
	}
}
