package org.chai.kevin.dashboard;

import java.util.Date;


import grails.plugin.spock.UnitSpec
import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor;

import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.GroupCollection;
import org.chai.kevin.Organisation;
import org.chai.kevin.UnitTests;
import org.chai.kevin.ValueType;
import org.chai.kevin.dashboard.DashboardPercentage.Status;
import org.gmock.WithGMock;
import org.hisp.dhis.aggregation.AggregationService
import org.chai.kevin.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType

@WithGMock
class PercentageCalculatorSpec extends UnitTests {

	PercentageCalculator percentageCalculator;

	def setup() {
		addBasicData()
		
		// indicators
//		def one = new IndicatorType(names:j(["en":"one"]), factor: 1)
//		mockDomain(IndicatorType, [one])
//		def const10 = new Indicator(names:j(["en":"Constant 10"]), shortName: "Constant 10", code: "CONST10", numerator: "10", denominator: "1", indicatorType: IndicatorType.findByName("one"))
//		def const20 = new Indicator(names:j(["en":"Constant 20"]), shortName: "Constant 20", code: "CONST20", numerator: "20", denominator: "1", indicatorType: IndicatorType.findByName("one"))
		def const10 = new Expression(names:j(["en":"Constant 10"]), code:"CONST10", expression: "10", type: ValueType.VALUE)
		def const20 = new Expression(names:j(["en":"Constant 20"]), code:"CONST20", expression: "20", type: ValueType.VALUE)
		mockDomain(Expression, [const10, const20])
		
		// objectives and targets for dashboard
		def nursea1 = new DashboardTarget(
			names:j(["en":"Nurse A1"]), code:"A1", descriptions:j(["en":"Nurse A1"]),
			calculations: ["District Hospital":
				new Calculation(groupUuid: "District Hospital", expression: Expression.findByCode("CONST10")),
			]
		)
		def nursea2 = new DashboardTarget(
			names:j(["en":"Nurse A2"]), code:"A2", descriptions:j(["en":"Nurse A2"]),
			calculations: ["District Hospital":
				new Calculation(groupUuid: "District Hospital", expression: Expression.findByCode("CONST20")),
			]
		)
		mockDomain(DashboardTarget, [nursea1, nursea2])
		def staffing = new DashboardObjective(root: false, names:j(["en":"Staffing"]), code:"STAFFING", descriptions:j(["en":"Staffing"]), objectiveEntries: [])
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByCode("A1"), weight: 1, order: 1)
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByCode("A2"), weight: 1, order: 2)
		mockDomain(DashboardObjective, [staffing])
		
		
		
		def monthly = new MonthlyPeriodType();
		mockDomain(MonthlyPeriodType, [monthly])
		def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
		mockDomain(Period, [period])
		
	}
	
	def "test strategic target"() {
		setup:
		def expressionService = mock(ExpressionService)
		expressionService.getValue(match {true}, match {true}, match {true}, match {true}).returns(10.0d);
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.expressionService = expressionService;
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		
		when:
		DashboardPercentage percentage
		play {
			percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('A1'), new Organisation(OrganisationUnit.findByName('Butaro DH')), Period.list()[0]);
		}
		
		then:
		percentage.status == Status.VALID
		percentage.value == 10

	}
	
	def "test strategic objective"() {
		
		setup:		
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(10.0d, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(20.0d, null, null, null));
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.percentageService = percentageService;
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		
		when:
		DashboardPercentage percentage = null;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Butaro DH')), Period.list()[0]);
		}
		
		then:
		percentage.valid == true
		percentage.status == Status.VALID
		percentage.value == 15
	}
	
	def "test absent value on target"() {
		setup:
		def dataElement = new DataElement(names:j(["en":"Element 1"]), code: "CODE", type: ValueType.VALUE)
		mockDomain(DataElement, [dataElement])
		def expression = new Expression(names:j(["en":"Expression Element 1"]), code:"ELEM1", expression: "["+dataElement.id+"]", denominator: "1", type: ValueType.VALUE)
		mockDomain(Expression, [expression])
		
		def target = new DashboardTarget(
			names:j(["en":"Target 1"]), code:"TARGET1", descriptions:j(["en":"Target 1"]),
			calculations: ["District Hospital":
				new Calculation(groupUuid: "District Hospital", expression: Expression.findByCode("CODE")),
			]
		)
		mockDomain(DashboardTarget, [target])
		
		def expressionService = mock(ExpressionService);
		expressionService.getValue(match {true}, match {true}, match {true}, match {it[dataElement] = null; true}).returns(null);
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.expressionService = expressionService
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		def organisation = new Organisation(OrganisationUnit.findByName('Butaro DH'))
		organisation.children = []

		when:
		DashboardPercentage percentage
		play {
			percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('TARGET1'), organisation, Period.list()[0]);
		}
		
		then:
		percentage.status == Status.MISSING_VALUE
		percentage.hasMissingValue == true
	}
	
	def "test absent orgunit group on target"() {
		setup:
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		def organisation = new Organisation(OrganisationUnit.findByName('Kivuye HC'));
		organisation.children = new ArrayList();
		
		when:
		DashboardPercentage percentage = percentageCalculator.getPercentage(DashboardTarget.findByCode('A1'), organisation, Period.list()[0]);
		
		then:
		percentage.status == Status.MISSING_EXPRESSION
	}
	
	def "test absent orgunit group on objective"() {
		setup:
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(Status.MISSING_EXPRESSION, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(Status.MISSING_EXPRESSION, null, null, null));
		percentageCalculator.percentageService = percentageService;
		
		when:
		DashboardPercentage percentage;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Kivuye HC')), Period.list()[0]);
		}
		
		then:
		percentage.valid == false
		percentage.isHasMissingExpression() == true
	}
	
	def "test absent with non absent orgunit group on objective"() {
		setup:
		def percentageService = mock(PercentageService);
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(10.0d, null, null, null));
		percentageService.getPercentage(match {true}, match {true}, match {true}).returns(new DashboardPercentage(20.0d, null, null, null));
		def percentageCalculator = new PercentageCalculator();
		percentageCalculator.percentageService = percentageService;
		percentageCalculator.groupCollection = new GroupCollection(OrganisationUnitGroup.list())
		
		when:
		DashboardPercentage percentage;
		play {
			percentage = percentageCalculator.getPercentage(DashboardObjective.findByCode('STAFFING'), new Organisation(OrganisationUnit.findByName('Burera')), Period.list()[0]);
		}
		
		then:
		percentage.valid == true
		percentage.value == 15
	}

	
	public static Date getDate( int year, int month, int day )
	{
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}
}
