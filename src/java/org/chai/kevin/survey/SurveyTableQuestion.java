package org.chai.kevin.survey;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity(name = "SurveyTableQuestion")
@Table(name = "dhsst_survey_table_question")
public class SurveyTableQuestion extends SurveyQuestion {

	private List<SurveyTableColumn> columns;
	private List<SurveyTableRow> rows;

	public void setColumns(List<SurveyTableColumn> columns) {
		this.columns = columns;
	}

	@OneToMany(targetEntity = SurveyTableColumn.class, mappedBy = "question")
	public List<SurveyTableColumn> getColumns() {
		return columns;
	}

	public void setRows(List<SurveyTableRow> rows) {
		this.rows = rows;
	}

	@OneToMany(targetEntity = SurveyTableRow.class, mappedBy = "question")
	public List<SurveyTableRow> getRows() {
		return rows;
	}

	@Transient
	@Override
	public String getTemplate() {
		String gspName = "tableQuestion";
		return gspName;
	}

	@Transient
	public void addColumn(SurveyTableColumn column) {
		column.setQuestion(this);
		columns.add(column);
	}

	@Transient
	public void addRow(SurveyTableRow row) {
		row.setQuestion(this);
		rows.add(row);
	}

}