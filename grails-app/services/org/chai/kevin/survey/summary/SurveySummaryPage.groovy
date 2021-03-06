package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.location.DataLocation;
import org.chai.kevin.survey.SurveyEnteredProgram;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveySection;

public class SurveySummaryPage {

	private static final String PROGRESS_SORT = "progress";
	private static final String LOCATION_SORT = "location";
	
	private QuestionSummary summary;
	private List<DataLocation> locations;
	
	// for survey summary page
	private Map<DataLocation, ProgramSummary> programSummaryMap;
	
	// for program summary page
	private Map<DataLocation, SurveyEnteredProgram> enteredProgramSummaryMap;
	
	// for survey + program + section summary page
	private Map<DataLocation, QuestionSummary> questionSummaryMap;
	
	// for smaller information tables
	private Map<SurveyProgram, SurveyEnteredProgram> enteredProgramTableMap;
	private Map<SurveyProgram, QuestionSummary> programQuestionTableMap;
	private Map<SurveySection, QuestionSummary> sectionQuestionTableMap;

	// for survey summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocation> locations, Map<DataLocation, QuestionSummary> questionSummaryMap, Map<DataLocation, ProgramSummary> programSummaryMap) {
		this.summary = summary;
		this.locations = locations;
		this.questionSummaryMap = questionSummaryMap;
		this.programSummaryMap = programSummaryMap;
	}
	
	// for program summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocation> locations, Map<DataLocation, QuestionSummary> questionSummaryMap, Map<DataLocation, SurveyEnteredProgram> enteredProgramMap, boolean test) {
		this.summary = summary;
		this.locations = locations;
		this.enteredProgramSummaryMap = enteredProgramMap;
		this.questionSummaryMap = questionSummaryMap;
	}

	// for section summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocation> locations, Map<DataLocation, QuestionSummary> questionSummaryMap) {
		this.summary = summary;
		this.locations = locations;
		this.questionSummaryMap = questionSummaryMap;
	}
	
	// for program table page
	public SurveySummaryPage(Map<SurveyProgram, SurveyEnteredProgram> enteredProgramTableMap, Map<SurveyProgram, QuestionSummary> programQuestionTableMap) {
		this.enteredProgramTableMap = enteredProgramTableMap;
		this.programQuestionTableMap = programQuestionTableMap;
	}
	
	// for section table page
	public SurveySummaryPage(Map<SurveySection, QuestionSummary> sectionQuestionTableMap) {
		this.sectionQuestionTableMap = sectionQuestionTableMap;
	}
	
	
	public void sort(String parameter, String order, String language) {
		if (locations == null || parameter == null || order == null) return;
		if (parameter.equals(LOCATION_SORT)) {
			locations.sort({it.names})
			if (order.equals("desc")) Collections.reverse(locations); 
		}
		else if (parameter.equals(PROGRESS_SORT)) {
			Collections.sort(locations, new Comparator<DataLocation>() {
				@Override
				public int compare(DataLocation arg0, DataLocation arg1) {
					QuestionSummary summary0 = questionSummaryMap.get(arg0);
					QuestionSummary summary1 = questionSummaryMap.get(arg1);
					return summary0.compareTo(summary1);
				}
			});
			if (order.equals("desc")) Collections.reverse(locations);
		}
	}
	
	public List<SurveyProgram> getPrograms() {
		List<SurveyProgram> sortedPrograms = new ArrayList<SurveyProgram>(programQuestionTableMap.keySet());
		Collections.sort(sortedPrograms, new Comparator<SurveyProgram>() {
			@Override
			public int compare(SurveyProgram arg0, SurveyProgram arg1) {
				QuestionSummary summary0 = programQuestionTableMap.get(arg0);
				QuestionSummary summary1 = programQuestionTableMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedPrograms);
		return sortedPrograms;
	}	

	public List<SurveySection> getSections() {
		List<SurveySection> sortedSections = new ArrayList<SurveySection>(sectionQuestionTableMap.keySet());
		Collections.sort(sortedSections, new Comparator<SurveySection>() {
			@Override
			public int compare(SurveySection arg0, SurveySection arg1) {
				QuestionSummary summary0 = sectionQuestionTableMap.get(arg0);
				QuestionSummary summary1 = sectionQuestionTableMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedSections);
		return sortedSections;
	}
	
	public QuestionSummary getSummary() {
		return summary;
	}
	
	public List<DataLocation> getLocations() {
		return locations;
	}

	public QuestionSummary getQuestionSummary(DataLocation location) {
		return questionSummaryMap.get(location);
	}

	public ProgramSummary getProgramSummary(DataLocation location) {
		return programSummaryMap.get(location);
	}

	public SurveyEnteredProgram getSurveyEnteredProgram(DataLocation location) {
		return enteredProgramSummaryMap.get(location);
	}
	
	public QuestionSummary getQuestionSummary(SurveyProgram program) {
		return programQuestionTableMap.get(program);
	}

	public QuestionSummary getQuestionSummary(SurveySection section) {
		return sectionQuestionTableMap.get(section);
	}

	public SurveyEnteredProgram getSurveyEnteredProgram(SurveyProgram program) {
		return enteredProgramTableMap.get(program);
	}
	
}
