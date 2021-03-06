package org.chai.kevin.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.LanguageService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.EnumService;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type.ValueVisitor;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyCheckboxOption;
import org.chai.kevin.survey.SurveyCheckboxQuestion;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveySimpleQuestion;
import org.chai.kevin.survey.SurveyTableColumn;
import org.chai.kevin.survey.SurveyTableQuestion;
import org.chai.kevin.survey.SurveyTableRow;
import org.chai.kevin.survey.SurveyValueService;
import org.chai.kevin.util.Utils;
import org.chai.kevin.util.DataUtils;
import org.chai.kevin.value.Value;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.location.LocationService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class SurveyExportService {
	
	private static final Log log = LogFactory.getLog(SurveyExportService.class);
	
	private SessionFactory sessionFactory;
	private EnumService enumService;
	private LanguageService languageService;
	private LocationService locationService;
	private SurveyValueService surveyValueService;
	private Set<String> skipLevels;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setEnumService(EnumService enumService){
		this.enumService = enumService;
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
	public void setSkipLevels(Set<String> skipLevels) {
		this.skipLevels = skipLevels;
	}
	
	public List<LocationLevel> getLevels() {
		List<LocationLevel> result = locationService.listLevels();
		for (String level : skipLevels) {
			result.remove(locationService.findLocationLevelByCode(level));
		}
		return result;
	}
	
	private final static String CSV_FILE_EXTENSION = ".csv";

	// TODO refactor this to use messages.properties files
	private final static String DATA_LOCATION_HEADER = "Location";
	private final static String DATA_LOCATION_TYPE_HEADER = "Location Type";
	private final static String SURVEY_HEADER = "Survey";
	private final static String PROGRAM_HEADER = "Program";
	private final static String SECTION_HEADER = "Section";
	private final static String QUESTION_TYPE_HEADER = "Question Type";
	private final static String QUESTION_VALUE_TYPE_HEADER = "Value Type";
	private final static String QUESTION_HEADER = "Question";
	private final static String DATA_VALUE_HEADER = "Value";
	
	private String[] getExportDataHeaders() {
		List<String> headers = new ArrayList<String>();
		
		headers.add(SURVEY_HEADER);		
		for(LocationLevel level : getLevels()){
			headers.add(level.getCode());
		}
		headers.add(DATA_LOCATION_HEADER);
		headers.add(DATA_LOCATION_TYPE_HEADER);
		headers.add(PROGRAM_HEADER);
		headers.add(SECTION_HEADER);
		headers.add(QUESTION_TYPE_HEADER);
		headers.add(QUESTION_VALUE_TYPE_HEADER);
		headers.add(QUESTION_HEADER);
		headers.add(DATA_VALUE_HEADER);
		return headers.toArray(new String[0]);
	}	
	
	public String getExportFilename(CalculationLocation location, SurveySection section, SurveyProgram program, Survey survey){
		String code = null;
		if (survey != null) code = survey.getCode();
		if (program != null) code = program.getCode();
		if (section != null) code = section.getCode();
		String exportFilename = code + "_" + location.getCode() + "_";
		return exportFilename;
	}
	
	@Transactional(readOnly=true)
	public File getSurveyExportFile(String filename, CalculationLocation location, SurveySection section, SurveyProgram program, Survey survey) throws IOException { 
				
		List<DataLocation> dataLocations = location.collectDataLocations(null);
		File csvFile = File.createTempFile(filename, CSV_FILE_EXTENSION);
		
		FileWriter csvFileWriter = new FileWriter(csvFile);
		ICsvListWriter writer = new CsvListWriter(csvFileWriter, CsvPreference.EXCEL_PREFERENCE);
		try {
			String[] csvHeaders = null;
			
			// headers
			if(csvHeaders == null){
				csvHeaders = getExportDataHeaders();
				writer.writeHeader(csvHeaders);
			}
			
			for(DataLocation dataLocation : dataLocations){	
				if (log.isDebugEnabled()) log.debug("getSurveyExportFile(dataLocation="+dataLocation+")");
				
				if(program != null){
					survey = program.getSurvey();
				}
				if(section != null){
					program = section.getProgram();
					survey = section.getSurvey();
				}
				
				List<SurveyProgram> surveyPrograms = survey.getPrograms(dataLocation.getType());
				for (SurveyProgram surveyProgram : surveyPrograms) {
					if (program != null && program != surveyProgram) continue;						
					List<SurveySection> surveySections = surveyProgram.getSections(dataLocation.getType());
					for (SurveySection surveySection : surveySections) {
						if (section != null && section != surveySection) continue;
						
						List<FormEnteredValue> formEnteredValues = surveyValueService.getFormEnteredValues(dataLocation, surveySection, surveyProgram, survey);					
						Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>();
						for(FormEnteredValue formEnteredValue : formEnteredValues){
							surveyElementValueMap.put((SurveyElement)formEnteredValue.getFormElement(), formEnteredValue);
						}
						
						List<SurveyQuestion> surveyQuestions = surveySection.getQuestions(dataLocation.getType());				
						for (SurveyQuestion surveyQuestion : surveyQuestions) {
							if (log.isDebugEnabled()) log.debug("getSurveyExportData(question="+surveyQuestion.getNames() +" section="+surveySection.getNames() +" program="+surveyProgram.getNames() + " dataLocation="+dataLocation + ")");

							List<SurveyExportDataPoint> surveyExportDataPoints = 
									getSurveyExportDataPoints(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyElementValueMap);
							
							// data points
							for (SurveyExportDataPoint dataPoint : surveyExportDataPoints){
								writer.write(dataPoint);
							}
						}
					}
				}									
			}
		} catch (IOException ioe){
			// TODO is this good ?
			throw ioe;
		} finally {
			writer.close();
		}
		
		return csvFile;
	}

	public List<SurveyExportDataPoint> getSurveyExportDataPoints(DataLocation dataLocation, Survey survey, SurveyProgram surveyProgram, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, Map<SurveyElement, FormEnteredValue> surveyElementValueMap){				
		
		List<SurveyExportDataPoint> surveyExportDataPoints = new ArrayList<SurveyExportDataPoint>();						
		
		switch(surveyQuestion.getType()){					
		
			case TABLE:
				SurveyTableQuestion surveyTableQuestion = (SurveyTableQuestion) surveyQuestion;
				List<SurveyTableRow> surveyTableRows = surveyTableQuestion.getRows(dataLocation.getType());
				List<SurveyTableColumn> surveyTableColumns = surveyTableQuestion.getColumns(dataLocation.getType());
				
				for (SurveyTableRow surveyTableRow : surveyTableRows) {
					for(SurveyTableColumn surveyTableColumn : surveyTableColumns){						
						SurveyElement surveyElement = surveyTableRow.getSurveyElements().get(surveyTableColumn);						
						List<String> surveyQuestionItems = new ArrayList<String>();	
						String surveyQuestionRow = DataUtils.noNull(surveyTableRow.getNames());						
						String surveyQuestionColumn = DataUtils.noNull(surveyTableColumn.getNames());
						surveyQuestionItems.add(surveyQuestionRow);
						surveyQuestionItems.add(surveyQuestionColumn);
						FormEnteredValue formEnteredValue = null;
						if(surveyElement != null) formEnteredValue = surveyElementValueMap.get(surveyElement);
						addDataPoints(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, surveyQuestionItems, formEnteredValue);
					}
				}
				break;
			case CHECKBOX:
				SurveyCheckboxQuestion surveyCheckboxQuestion = (SurveyCheckboxQuestion) surveyQuestion;
				List<SurveyCheckboxOption> surveyCheckboxOptions = surveyCheckboxQuestion.getOptions(dataLocation.getType());
				for(SurveyCheckboxOption surveyCheckboxOption : surveyCheckboxOptions){
					SurveyElement surveyElement = surveyCheckboxOption.getSurveyElement();					
					List<String> surveyQuestionItems = new ArrayList<String>();						
					String surveyCheckboxName = DataUtils.noNull(surveyCheckboxOption.getNames());																	
					surveyQuestionItems.add(surveyCheckboxName);
					FormEnteredValue formEnteredValue = null;
					if(surveyElement != null) formEnteredValue = surveyElementValueMap.get(surveyElement);
					addDataPoints(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, surveyQuestionItems, formEnteredValue);						
				}
				break;
			case SIMPLE:
				SurveySimpleQuestion surveySimpleQuestion = (SurveySimpleQuestion) surveyQuestion;
				SurveyElement surveyElement = surveySimpleQuestion.getSurveyElement();
				List<String> surveyQuestionItems = new ArrayList<String>();
				FormEnteredValue formEnteredValue = null;
				if(surveyElement != null) formEnteredValue = surveyElementValueMap.get(surveyElement);
				addDataPoints(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyExportDataPoints, surveyElement, surveyQuestionItems, formEnteredValue);
				break;
			default:
				throw new NotImplementedException();	
		}
		return surveyExportDataPoints;
	}

	private void addDataPoints(DataLocation dataLocation, Survey survey, SurveyProgram surveyProgram, SurveySection surveySection, SurveyQuestion surveyQuestion,
			List<SurveyExportDataPoint> surveyExportDataPoints, SurveyElement surveyElement, List<String> surveyQuestionItems, FormEnteredValue formEnteredValue) {
		if(surveyElement == null) 
			surveyExportDataPoints.add(getBasicInfoDataPoint(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyElement));
		else{
			SurveyExportDataPoint dataPoint = getBasicInfoDataPoint(dataLocation, survey, surveyProgram, surveySection, surveyQuestion, surveyElement);
			List<SurveyExportDataPoint> dataPoints = new ArrayList<SurveyExportDataPoint>();
						
			Type type = surveyElement.getDataElement().getType();			
			
			if(formEnteredValue != null){
				Value value = formEnteredValue.getValue();
				
				DataPointVisitor dataPointVisitor = new DataPointVisitor(surveyElement, surveyQuestionItems, dataPoint);
				type.visit(value, dataPointVisitor);
				dataPoints = dataPointVisitor.getDataPoints();
			}
			else{
				dataPoint.add(formatExportDataItem(null));
				if(type.getType().equals(ValueType.ENUM)){
					String enumCode = type.getEnumCode();
					Enum enume = enumService.getEnumByCode(enumCode);
					List<EnumOption> enumOptions = enume.getAllEnumOptions();
					for(EnumOption enumOption : enumOptions)
						surveyQuestionItems.add(DataUtils.noNull(enumOption.getNames()));
				}
				for(String surveyQuestionItem : surveyQuestionItems)
					dataPoint.add(formatExportDataItem(surveyQuestionItem));
				dataPoints.add(dataPoint);
			}
			
			sessionFactory.getCurrentSession().evict(formEnteredValue);
			
			surveyExportDataPoints.addAll(dataPoints);
		}
	}
	
	private SurveyExportDataPoint getBasicInfoDataPoint(DataLocation dataLocation, Survey survey, SurveyProgram surveyProgram, 
			SurveySection surveySection, SurveyQuestion surveyQuestion, SurveyElement surveyElement){
		
		SurveyExportDataPoint dataPoint = new SurveyExportDataPoint();
		dataPoint.add(formatExportDataItem(DataUtils.noNull(survey.getNames())));
		
		for (LocationLevel level : getLevels()){			
			Location parent = dataLocation.getParentOfLevel(level);
			if (parent != null) dataPoint.add(DataUtils.noNull(parent.getNames()));
			else dataPoint.add("");
		}
		dataPoint.add(formatExportDataItem(DataUtils.noNull(dataLocation.getNames())));
		dataPoint.add(formatExportDataItem(DataUtils.noNull(dataLocation.getType().getNames())));			
		dataPoint.add(formatExportDataItem(DataUtils.noNull(surveyProgram.getNames())));
		dataPoint.add(formatExportDataItem(DataUtils.noNull(surveySection.getNames())));
		dataPoint.add(formatExportDataItem(DataUtils.noNull(surveyQuestion.getType().toString())));
		
		if(surveyElement != null){
			DataElement<?> dataElement = surveyElement.getDataElement();
			Type type = dataElement.getType();
			ValueType valueType = type.getType();
			
			String dataType = valueType.toString();	
			dataPoint.add(formatExportDataItem(dataType));		
			dataPoint.add(formatExportDataItem(surveyQuestion.getNames()));	
		}
		
		return dataPoint;
	}

	private void addDataPointValue(SurveyExportDataPoint surveyExportDataPoint, Type dataType, Value dataValue){						
		if(dataValue == null || dataValue.isNull()) {
			surveyExportDataPoint.add(formatExportDataItem(null));
		}
		if(dataValue != null && !dataValue.isNull()){
			surveyExportDataPoint.add(formatExportDataItem(DataUtils.getValueString(dataType,dataValue)));
		}
	}
	
	private String formatExportDataItem(String value){		
		if (value != null) value = Utils.stripHtml(value);
		if (value == null) value = "null";
		return value;
	}
		
	private class DataPointVisitor extends ValueVisitor{

		private int line;
		private SurveyElement surveyElement;
		private List<String> surveyQuestionItems;
		private SurveyExportDataPoint baseDataPoint;
		private List<SurveyExportDataPoint> dataPoints;	
		
		public List<SurveyExportDataPoint> getDataPoints(){
			return dataPoints;
		}
		
		public DataPointVisitor(SurveyElement surveyElement, List<String> surveyQuestionItems, SurveyExportDataPoint baseDataPoint) {
			line = 0;
			this.surveyElement = surveyElement;
			this.surveyQuestionItems = surveyQuestionItems;			
			this.baseDataPoint = baseDataPoint;
			dataPoints = new ArrayList<SurveyExportDataPoint>();
		}
		
		public DataPointVisitor(int line, SurveyElement surveyElement, List<String> surveyQuestionItems, SurveyExportDataPoint baseDataPoint) {
			this.line = line;
			this.surveyElement = surveyElement;
			this.surveyQuestionItems = surveyQuestionItems;			
			this.baseDataPoint = baseDataPoint;
			dataPoints = new ArrayList<SurveyExportDataPoint>();
		}
		
		public void handle(Type type, Value value, String prefix, String genericPrefix) {				
			SurveyExportDataPoint dataPoint = new SurveyExportDataPoint(baseDataPoint);						
			if(!type.isComplexType()){
				addDataPointValue(dataPoint, type, value);
				if(line > 0)
					dataPoint.add(formatExportDataItem("Line " + line));
				if(surveyQuestionItems != null){
					for(String surveyQuestionItem : surveyQuestionItems)
						dataPoint.add(formatExportDataItem(surveyQuestionItem));				
				}
				String surveyQuestionItem = null;								
				for(String genericTypeKey : this.getGenericTypes().keySet()){
					if(!this.getGenericTypes().get(genericTypeKey).getType().equals(ValueType.LIST)){
						surveyQuestionItem = DataUtils.noNull(surveyElement.getHeaders(languageService.getCurrentLanguage()).get(genericTypeKey));
						if(surveyQuestionItem != null && !surveyQuestionItem.isEmpty()) 
							dataPoint.add(formatExportDataItem(surveyQuestionItem));						
					}
				}				
				if(type.getType().equals(ValueType.ENUM)){
					String enumCode = type.getEnumCode();
					Enum enume = enumService.getEnumByCode(enumCode);
					if(enume != null){
						List<EnumOption> enumOptions = enume.getAllEnumOptions();
						for(EnumOption enumOption : enumOptions){
							surveyQuestionItem = DataUtils.noNull(enumOption.getNames());
							if(surveyQuestionItem != null && !surveyQuestionItem.isEmpty()) 
								dataPoint.add(formatExportDataItem(surveyQuestionItem));
						}	
					}															
				}
				dataPoints.add(dataPoint);
			}
		}						
	}
}
