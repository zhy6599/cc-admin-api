package cc.admin.modules.jp.util;

import cc.admin.modules.jp.entity.JpPerson;
import com.google.common.collect.Maps;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratorSingleTxt {

	private static Map<String,List<String>> resultMap = Maps.newHashMap();

	private static final int MAP_LENGTH = 300;
	private PersonSingleDis personSingleDis = new PersonSingleDis();

	public GeneratorSingleTxt(){

		for(int i=1;i<MAP_LENGTH;i++){
			resultMap.put(i+"", new ArrayList<String>());
		}
	}

	/**
	 * 根据位置信息打印用户信息到结果列中
	 * @param persons
	 */
	public void printPersonsInfo(List<JpPerson> persons){
		if(persons==null)
			return ;
		for(JpPerson p : persons){
			personSingleDis.printPersonInfo(resultMap, p);
			printPersonsInfo(p.getChildren());
		}
	}


	public void adjustWife(List<JpPerson> persons){
		if(persons==null)
			return ;
		for(JpPerson p : persons){
			personSingleDis.printPersonInfo(resultMap, p);
			adjustWife(p.getChildren());
		}
	}

	/**
	 * 向文件和控制台打印结果信息
	 * @param fw
	 * @throws IOException
	 */
	public void printResult(FileWriter fw) throws IOException{

		for(int i=1;i<MAP_LENGTH;i++){
			List<String> l= resultMap.get(i+"");
			StringBuilder sb = new StringBuilder();
			for(String str : l){
				sb.append(str);
			}
			if("".equals(sb.toString()))
				break;
			sb.append("\r\n");
			fw.write(sb.toString());
		}

	}

}
