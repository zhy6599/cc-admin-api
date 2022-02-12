package cc.admin.modules.jp.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Data
public class JpTreeChartData {

	private String name;
	private String pic;
	private Mate mate;

	private List<JpTreeChartData> children = new ArrayList<>();

	public JpTreeChartData() {
	}

	/**
	 * 将JpPerson对象转换成JpPersonTree对象
	 *
	 * @param jpPerson
	 */
	public JpTreeChartData(JpPerson jpPerson) {
		this.name = jpPerson.getName();
		this.pic = jpPerson.getPic();
		this.mate = new Mate(jpPerson.getWife());
	}
}
