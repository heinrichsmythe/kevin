package org.chai.kevin.data

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

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.DataService
import org.chai.kevin.data.Constant;
import org.chai.kevin.data.DataElement;

class ConstantController extends AbstractEntityController {
	
	DataService dataService

	def getEntity(def id) {
		return Constant.get(id)
	}

	def createEntity() {
		return new Constant()
	}

	def getTemplate() {
		return "createConstant";
	}

	def getModel(def entity) {
		return [constant: entity]
	}

	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save()
	}

	def deleteEntity(def entity) {
		entity.delete()
	}

	def bindParams(def entity) {
		entity.properties = params

		// FIXME GRAILS-6967 makes this necessary
		// http://jira.grails.org/browse/GRAILS-6967
		if (params.names!=null) entity.names = params.names
		if (params.descriptions!=null) entity.descriptions = entity.descriptions
	}

	def list = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[constants: Constant.list(params), constantCount: Constant.count()]
	}

	def getData = {
		def constants = dataService.searchConstants(params['searchText']);
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/templates/constants', model:[constants: constants])
		}
	}

	def getConstantDescription = {
		def constant = null;
		if (NumberUtils.isNumber(params['constant'])) {
			constant = Constant.get(params['constant'])
		}

		if (constant == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/templates/constantDescription', model: [constant: constant])
			}
		}
	}
}