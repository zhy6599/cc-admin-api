package cc.admin.modules.bi.entity;

import lombok.Data;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2020-11-21 20:03
 */
@Data
public class SeriesData {
	String name;
	String axisIndex;
	List<Object> dataList;
}
