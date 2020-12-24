/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2019 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain getHardWareId copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package cc.admin.modules.bi.core.model;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Criterion {

	private String column;

	private String operator;

	private Object value;

	private String dataType;

	private Object secondValue;

	private boolean noValue;

	private boolean singleValue;

	private boolean betweenValue;

	private boolean listValue;

	public Criterion(String column, String operator, Object value, String dataType) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = value;
		this.dataType = dataType;
		if (value instanceof List<?>) {
			this.listValue = true;
		} else {
			this.singleValue = true;
		}
	}

	public Criterion(String column, String operator, Object value, Object secondValue, String dataType) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = value;
		this.dataType = dataType;
		this.secondValue = secondValue;
		this.betweenValue = true;
	}

	public boolean isNeedApostrophe(){
		return !Arrays.stream(SqlFilter.NumericDataType.values())
				.filter(value -> this.dataType.equalsIgnoreCase(value.getType())).findFirst()
				.isPresent();
	}

}
