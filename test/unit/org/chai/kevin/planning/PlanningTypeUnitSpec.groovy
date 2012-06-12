package org.chai.kevin.planning

import org.chai.kevin.LanguageService;
import org.chai.kevin.Translation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.util.JSONUtils;

import grails.plugin.spock.UnitSpec;

class PlanningTypeUnitSpec extends UnitSpec {

	def "test get sections"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER()	
		]))))
		def planningType = new PlanningType(formElement: formElement)
		
		then:
		planningType.sections == ["[_].key1"]
		
	}
	
	def "test get value prefixes ignores fixed header"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER(),
			"key2": Type.TYPE_STRING()
		]))))
		def planningType = new PlanningType(fixedHeader: "[_].key1", formElement: formElement)
		
		then:
		planningType.getValuePrefixes("").equals (['[_].key2'])
		
	}
	
	def "get type when type does not exist returns null"() {
		when:
		def formElement = new FormElement(dataElement: new RawDataElement(type: Type.TYPE_LIST(Type.TYPE_MAP([
			"key1": Type.TYPE_NUMBER(),
			"key2": Type.TYPE_ENUM("code"),
			"key3": Type.TYPE_STRING()
		]))))
		def planningType = new PlanningType(fixedHeader: "[_].key1", formElement: formElement)
		
		then:
		planningType.getType("[_].doesnotexist") == null
	}
	
	def "build groups works properly"() {
		setup:
		def planningType
		def languageService = Mock(LanguageService)
		languageService.getCurrentLanguage() >> "en"
		
		when:
		planningType = new PlanningType(costs: [
			new PlanningCost(id: 1, names: j(["en": "Test1"])),
			new PlanningCost(id: 2, names: j(["en": "Test2"]))
		])
		planningType.buildGroupHierarchy(languageService)
		
		then:
		planningType.getGroups([]) == null 
		planningType.getPlanningCostsInGroup([])*.names.en == ["Test1", "Test2"]
		planningType.getPlanningCosts([])*.names.en == ["Test1", "Test2"]
		
		when:
		planningType = new PlanningType(costs: [
			new PlanningCost(id: 1, names: j(["en": "Group - Test1"])),
			new PlanningCost(id: 2, names: j(["en": "Group - Test2"]))
		])
		planningType.buildGroupHierarchy(languageService)
		
		then:
		planningType.getGroups([]).equals(["Group"])
		planningType.getPlanningCostsInGroup([]) == null
		planningType.getPlanningCostsInGroup(["Group"])*.names.en == ["Group - Test1", "Group - Test2"]
		planningType.getPlanningCosts([])*.names.en == ["Group - Test1", "Group - Test2"]
		
		when:
		planningType = new PlanningType(costs: [
			new PlanningCost(id: 1, names: j(["en": "Test1"])),
			new PlanningCost(id: 2, names: j(["en": "Group - Test2"]))
		])
		planningType.buildGroupHierarchy(languageService)
		
		then:
		planningType.getGroups([]).equals(["Group"])
		planningType.getPlanningCostsInGroup([])*.names.en == ["Test1"]
		planningType.getPlanningCostsInGroup(["Group"])*.names.en == ["Group - Test2"]
		planningType.getPlanningCosts([])*.names.en == ["Test1", "Group - Test2"]
	}

	static j(def map) {
		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
	}
	
}
