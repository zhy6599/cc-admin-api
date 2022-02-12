package cc.admin.modules.jp.util;

import cc.admin.modules.jp.entity.JpPerson;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class PersonSingleDis {

	private static final int HEIGHT = 4;
	private static final int SPACE = 1;
	private static final String SPLIT = "﹃";
	private static final int TOTALHEIGHT = HEIGHT + SPACE;
//	private static final List<String> TABLE = ImmutableList.of("┃", "┻", "━", "┏", "┓", "╋", "┳", "", "", "");
	private static final List<String> TABLE = ImmutableList.of("┃", "┻", "━", "┏", "┓", "╋", "┳", "", "", "");
//	private static final List<String> TABLE = ImmutableList.of("║", "╩", "═", "╔", "╗", "╬", "╦", "", "", "");
//	private static final List<String> TABLE = ImmutableList.of("│", "┴", "─", "┌", "┐", "┼", "┬", "", "", "");

	public PersonSingleDis() {
	}

	/**
	 * 打印用户信息到结果列中
	 *
	 * @param resultMap
	 * @param p
	 */
	public void printPersonInfo(Map<String, List<String>> resultMap, JpPerson p) {

		int oid = p.getOid();
		int pos = p.getPos();
		String wife = p.getWife();
		String name = p.getName();
		if (name == null || "".equals(name.trim()) || name.length() == 1) {
			name = "无";
		} else{
			name = name.trim();
			name = name.substring(1);
		}
		if (wife != null && wife.endsWith("氏")) {
			wife = wife.substring(0, wife.length() - 1);
		}
		int startHeight = 0, needStartPos = 0;
		startHeight = (oid - 1) * TOTALHEIGHT + 1;
		// 调整因为多少世而使得列宽不同
		needStartPos = startHeight;
		// 首先检查是否到了应该输出的位置，如果不到就补空格
		List<String> checkList = resultMap.get((needStartPos) + "");
		int loopCount = pos - checkList.size() - 1;
		for (int i = 0; i < loopCount; i++) {
			needStartPos = startHeight;
			for (int j = 0; j < TOTALHEIGHT-2; j++){
					resultMap.get((needStartPos++) + "").add("　");
			}
		}
		//由于媳妇姓氏平写，所以这里需要特殊处理
		List<String> lastList = resultMap.get((needStartPos) + "");
		int lastLoopCount = pos - lastList.size() - 1;
		for (int i = 0; i < lastLoopCount; i++)
			resultMap.get((needStartPos) + "").add("　");


		if(name.length()==1){
			resultMap.get((startHeight++) + "").add("　");
			resultMap.get((startHeight++) + "").add(name);
		}else{
			resultMap.get((startHeight++) + "").add(name.substring(0, 1));
			resultMap.get((startHeight++) + "").add(name.substring(1, 2));
		}

		resultMap.get((startHeight++) + "").add(SPLIT);
		if(null == wife)
			wife = "";
		else
			wife = wife.trim();
		if ("".equals(wife) || "-".equals(wife)) {
			resultMap.get((startHeight++) + "").add("　");
		} else if (wife.length() == 1) {
			resultMap.get((startHeight++) + "").add(wife);
		} else if (wife.length() == 2) {
			resultMap.get((startHeight) + "").add(wife.substring(0, 1));
			resultMap.get((startHeight++) + "").add(wife.substring(1, 2));
		} else {
			resultMap.get((startHeight) + "").add(wife.substring(0, 1));
			resultMap.get((startHeight) + "").add(wife.substring(1, 2));
			resultMap.get((startHeight++) + "").add(wife.substring(2, 3));
		}
		addTabs(resultMap.get((startHeight) + ""), p.getChildren(), p.getPos());

	}

	public void addTabs(List<String> resultList, List<JpPerson> persons, int parentPos) {
		int total = persons.size();
		if (total == 1) {
			JpPerson person = persons.get(0);
			int loopCount = person.getPos() - resultList.size() - 1;
			for (int j = 0; j < loopCount; j++) {
				resultList.add("　");
			}
			resultList.add(TABLE.get(0));
			return;
		}
		for (int i = 0; i < persons.size(); i++) {
			JpPerson person = persons.get(i);
			int loopCount = person.getPos() - resultList.size() - 1;
			for (int j = 0; j < loopCount; j++) {
				if (i > 0) {
					if (resultList.size() + 1 == parentPos)
						resultList.add(TABLE.get(1));
					else
						resultList.add(TABLE.get(2));
				} else
					resultList.add("　");
			}
			if (i == 0) {
				resultList.add(TABLE.get(3));
			} else if (i == persons.size() - 1) {
				resultList.add(TABLE.get(4));
			} else {
				if (total % 2 == 1 && (resultList.size() + 1 == parentPos))
					resultList.add(TABLE.get(5));
				else
					resultList.add(TABLE.get(6));
			}
		}
	}
}
