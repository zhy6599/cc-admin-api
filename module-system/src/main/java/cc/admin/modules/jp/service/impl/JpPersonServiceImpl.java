package cc.admin.modules.jp.service.impl;

import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.jp.entity.JpPerson;
import cc.admin.modules.jp.entity.JpPersonTree;
import cc.admin.modules.jp.entity.JpTreeChartData;
import cc.admin.modules.jp.mapper.JpPersonMapper;
import cc.admin.modules.jp.service.IJpPersonService;
import cc.admin.modules.jp.util.LevelFreeMaker;
import cc.admin.modules.jp.util.PersonSingleDis;
import cc.admin.modules.jp.util.WordUtil;
import cc.admin.modules.sys.entity.SysDictItem;
import cc.admin.modules.sys.service.ISysDictItemService;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Service
public class JpPersonServiceImpl extends ServiceImpl<JpPersonMapper, JpPerson> implements IJpPersonService {


	@Autowired
	private ISysDictItemService sysDictItemService;

	@Autowired
	JpPersonMapper jpPersonMapper;

	private static String ancestorPath;

	private static String mergePath;

	private Map<String,List<String>> resultMap = Maps.newHashMap();

	private static final int MAP_LENGTH = 300;
	private PersonSingleDis personSingleDis = new PersonSingleDis();

	public void init(){
		resultMap = Maps.newHashMap();
		for(int i=1;i<MAP_LENGTH;i++){
			resultMap.put(i+"", new ArrayList<String>());
		}
	}


	@Value("${cc.admin.path.upload}")
	public void setUploadPath(String uploadPath) {
		this.ancestorPath = uploadPath + File.separator + "ancestor/";
		this.mergePath = uploadPath + File.separator + "ancestor/merge/";
	}

	public List<JpPersonTree> getJpPersonTree(List<JpPerson> jpPersonList, String pid) {
		List<JpPersonTree> jpPersonTreeList = Lists.newArrayList();
		if (StrUtil.isNotEmpty(pid)) {
			for (JpPerson jpPerson : jpPersonList) {
				if (pid.equals(jpPerson.getPid())) {
					JpPersonTree jpPersonTree = new JpPersonTree(jpPerson);
					jpPersonTree.setChildren(getJpPersonTree(jpPersonList, jpPerson.getId()));
					jpPersonTreeList.add(jpPersonTree);
				}
			}
		}
		return jpPersonTreeList;
	}

	@Override
	public List<JpPersonTree> queryJpPersonTree() {
		LambdaQueryWrapper<JpPerson> wrapper = new LambdaQueryWrapper<>();
		wrapper.orderByAsc(JpPerson::getSortBy);
		List<JpPerson> jpPersonList = this.list(wrapper);
		return getJpPersonTree(jpPersonList, "-1");
	}

	@Override
	public JpTreeChartData queryTreeChartData() {
		LambdaQueryWrapper<JpPerson> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(JpPerson::getIsSon, "1");
		wrapper.orderByAsc(JpPerson::getSortBy);
		List<JpPerson> jpPersonList = this.list(wrapper);
		List<SysDictItem> dict = sysDictItemService.selectItemsByDictCode("live");
		Map<String, String> liveMap = Maps.newHashMap();
		dict.forEach(sysDictItem -> {
			liveMap.put(sysDictItem.getItemValue(), sysDictItem.getItemText());
		});
		List<JpTreeChartData> dataList = getJpTreeChartData(jpPersonList, "-1", liveMap);
		if (dataList.size() == 1) {
			return dataList.get(0);
		}
		return null;
	}

	private List<JpTreeChartData> getJpTreeChartData(List<JpPerson> jpPersonList, String pid, Map<String, String> liveMap) {
		List<JpTreeChartData> jpPersonTreeList = Lists.newArrayList();
		if (StrUtil.isNotEmpty(pid)) {
			for (JpPerson jpPerson : jpPersonList) {
				if (pid.equals(jpPerson.getPid())) {
					JpTreeChartData jpTreeChartData = new JpTreeChartData(jpPerson);
					List<JpTreeChartData> children = getJpTreeChartData(jpPersonList, jpPerson.getId(), liveMap);
					if (children.size() > 0) {
						jpTreeChartData.setChildren(children);
					} else {
						jpTreeChartData.setChildren(null);
						//没有子节点把地名写进去
						//String live = jpPerson.getLive();
						//JpTreeChartData liveData = new JpTreeChartData();
						//liveData.setName(String.format("(%s)",liveMap.get(live)));
						//jpTreeChartData.setChildren(ImmutableList.of(liveData));
					}
					jpPersonTreeList.add(jpTreeChartData);
				}
			}
		}
		return jpPersonTreeList;
	}

	public static int doMerge(List<List<Integer>> needMergeList, int merge) {
		try {
			for (List<Integer> mergeList : needMergeList) {
				int maxLines = 0;
				BufferedWriter mergeFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format(ancestorPath + "/merge/%d.txt", merge++), true), "UTF-8"));
				List<List<String>> contents = Lists.newArrayList();
				//可以先把所有文件内容读取出来，然后在内存拼接，最后写入文件
				for (int fileId : mergeList) {
					List<String> content = FileUtil.readLines(String.format(ancestorPath + "/%d.txt", fileId), Charsets.UTF_8);
					contents.add(content);
				}
				for (List<String> content : contents) {
					if (content.size() > maxLines)
						maxLines = content.size();
				}
				for (int i = 0; i < maxLines; i++) {
					String str = "";
					for (List<String> content : contents) {
						if (content.size() > i) {
							str += content.get(i);
						} else {
							//文件长度不够只能补空格
							String firstLine = content.get(0);
							for (int j = 0; j < firstLine.length(); j++) {
								str += "　";
							}
						}
					}
					mergeFile.write(str + "\r\n");
				}
				mergeFile.flush();
				mergeFile.close();
			}
		} catch (Exception e) {
		}
		return merge;
	}


	private static void setMaxSonNums(List<JpPerson> persons,int maxSon){
		if(null != persons){
			for(JpPerson p : persons){
				p.setSonNum(maxSon);
				setMaxSonNums(p.getChildren(), maxSon);
			}
		}
	}

	/**
	 * 媳妇个数
	 * @param wife
	 * @return
	 */
	private static int getWifeNum(String wife){
		if(null == wife || "".equals(wife.trim())){
			return 1;
		}else{
			return wife.length();
		}
	}

	private static int getMaxSonNum(String wife,int sonNum){
		int maxSonNum = 1;
		if(null == wife || "".equals(wife.trim())){
			maxSonNum = 1;
		}else{
			maxSonNum = wife.length();
		}
		if(sonNum>maxSonNum){
			maxSonNum = sonNum;
		}
		return maxSonNum;
	}
	/**
	 * 设定开始位置和自身位置
	 * @param persons
	 * @param pStart
	 * @param pSonNum
	 */
	public static void setStartAndPos(List<JpPerson> persons,int pStart,int pSonNum){
		if(null == persons)
			return ;
		int start = pStart;
		int absPos = 0;
		for(int i=0;i<persons.size();i++){
			JpPerson p = persons.get(i);
			absPos = p.getSonNum()/2+1;
			if((i==persons.size()/2) && (persons.size()%2==0) && p.getOid()>1){
				start++;
			}
			p.setPos(start + absPos);
			p.setStartPos(start);

			setStartAndPos(p.getChildren(),start,p.getSonNum());

			start = start + p.getSonNum();
		}
	}

	/**
	 * 算出所有子节点个数
	 * @param persons
	 * @return
	 */
	public static int setSonNum(List<JpPerson> persons){
		int total = 0;
		if(null != persons){
			for(JpPerson p : persons){
				//如果没有子节点，那么本身调整下宽度即可
				if(p.getChildren() == null || p.getChildren().size()==0){
					int maxSonNum = getMaxSonNum(p.getWife(),1);
					p.setSonNum(maxSonNum);
					total +=maxSonNum;
				}else {
					int num = setSonNum(p.getChildren());
					int wifeNum = getWifeNum(p.getWife());
					//媳妇宽度大于子节点宽度，那么所有的子节点都要把媳妇的宽度预留出来
					if(wifeNum > num){
						setMaxSonNums(p.getChildren(),wifeNum);
						num = wifeNum;
					}
					if(num>1 && num%2==0)
						num = num+1;
					total +=num;

					p.setSonNum(num);
				}
			}
		}
		return total;
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
	/**
	 * 向文件和控制台打印结果信息
	 * @param fw
	 * @throws IOException
	 */
	public void printResult(BufferedWriter fw) throws IOException{
		int max = 0;
		for(int i=1;i<MAP_LENGTH;i++){
			List<String> l= resultMap.get(i+"");
			StringBuilder sb = new StringBuilder();
			for(String str : l){
				sb.append(str);
			}
			if("".equals(sb.toString()))
				continue;
			int length = sb.toString().length();
			if(length>max)
				max = length;
		}


		for(int i=1;i<MAP_LENGTH;i++){
			List<String> l= resultMap.get(i+"");
			StringBuilder sb = new StringBuilder();
			for(String str : l){
				sb.append(str);
			}
			if("".equals(sb.toString()))
				continue;
			int count = max - sb.toString().length();
			for(int j=0;j<count;j++){
				sb.append("　");
			}
			sb.append("\r\n");
			fw.write(sb.toString());
		}

	}

	public void doGenerate(String pid, int level, int pageNum) throws IOException {
		List<JpPerson> persons = getPersonsByPidAndLevel(pid, level);
		setSonNum(persons);
		setStartAndPos(persons, 0, 0);
		printPersonsInfo(persons);
		BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.format(ancestorPath + "/%d.txt", pageNum), true), "UTF-8"));
		printResult(fw);
		fw.flush();
		fw.close();
	}

	/**
	 * 将节点的所有子节点都找出来
	 *
	 * @param pid
	 * @param level
	 * @return
	 * @throws SQLException
	 */
	public List<JpPerson> getPersonsByPidAndLevel(String pid, int level) {
		QueryWrapper<JpPerson> queryWrapper = QueryGenerator.initQueryWrapper(new JpPerson(), Maps.newHashMap());
		queryWrapper.eq("pid", pid);
		queryWrapper.eq("is_son", "1");
		queryWrapper.le("oid", level);
		queryWrapper.orderByAsc("sort_by");
		List<JpPerson> personList = jpPersonMapper.selectList(queryWrapper);
		List<JpPerson> persons = Lists.newArrayList();
		for (int i = 0; i < personList.size(); i++) {
			JpPerson jpPerson = personList.get(i);
			jpPerson.setChildren(getPersonsByPidAndLevel(jpPerson.getId(), level));
			persons.add(jpPerson);
		}
		return persons;
	}

	public List<JpPerson> getPersonsByLevelOrderByPidAndId(int level){
		QueryWrapper<JpPerson> queryWrapper = QueryGenerator.initQueryWrapper(new JpPerson(), Maps.newHashMap());
		queryWrapper.eq("oid", level);
		queryWrapper.orderByAsc("sort_by");
		List<JpPerson> personList = jpPersonMapper.selectList(queryWrapper);
		return personList;
	}

	public String[] splitStringByByte(String str,int split){
		return new String[]{str.substring(0, split),str.substring(split)};
	}

	/**
	 * 这里所文件拆分和合并
	 * @param from 从第几个文件开始
	 * @param to  到第几个文件
	 * @param maxCount 一列最多显示多少个字符
	 * @param title 标题文件名字
	 * @param split 拆分文件从多少开始
	 * @return
	 * @throws IOException
	 */
	public List<List<Integer>> getNeedMergeList(int from,int to,int maxCount,int title,int split) throws IOException{
		List<List<Integer>> needMerge = Lists.newArrayList();
		int count =0;
		List<Integer> mergeList = Lists.newArrayList(title);
		for(int i=from;i<to;i++){
			List<String> lines = FileUtil.readLines(String.format(ancestorPath+"/%d.txt", i),Charsets.UTF_8);
			int length = lines.get(0).length();
			if(count+length>maxCount){
				needMerge.add(mergeList);
				count =0;
				mergeList = Lists.newArrayList(title);
			}
			count = count+length;
			mergeList.add(i);
			//自身都超过了，那么需要做拆分操作
			if(length>maxCount){
				needMerge.add(mergeList);
				count =0;
				mergeList = Lists.newArrayList(title);
				//这里做文件拆分
				BufferedWriter fwBefore = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (String.format(ancestorPath+"/%d.txt", i),true),Charsets.UTF_8));
				BufferedWriter fwLast = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (String.format(ancestorPath+"/%d.txt", split),true),Charsets.UTF_8));
				for(String line:lines){
					if("".equals(line))
						continue;
					String[] results = splitStringByByte(line, maxCount);
					if(count==0){
						count = results[1].length();
					}
					fwBefore.write(results[0]+"\r\n");
					fwLast.write(results[1]+"\r\n");
				}
				fwBefore.flush();
				fwBefore.close();
				fwLast.flush();
				fwLast.close();

				mergeList.add(split++);
			}
		}
		needMerge.add(mergeList);
		return needMerge;
	}

	public void writeLevel(){
		List<String> levelList = ImmutableList.of("9000","9001","9002","9003");
		for (String level : levelList) {
			String content = LevelFreeMaker.renderContent(level+".ftl", Maps.newHashMap());
			FileUtil.writeBytes(content.getBytes(), ancestorPath+level+".txt");
		}

	}

	public void generateWord() throws IOException {
		int startNum = 1;
		int pageNum = 1;
		int level = 6;
		int mergeIndex = 0;
		int maxCount = 50;
		FileUtil.del(ancestorPath);
		FileUtil.mkdir(ancestorPath);
		FileUtil.mkdir(mergePath);
		init();
		//把层级写进去
		writeLevel();
//		//第一代
		doGenerate("-1", level, pageNum++);
		List<List<Integer>> fiestNeedMergeList = Lists.newArrayList();
		Lists.newArrayList();
		fiestNeedMergeList.add(ImmutableList.of(9000,1));
		mergeIndex = doMerge(fiestNeedMergeList,mergeIndex);

		//第六代
		List<JpPerson> persons = getPersonsByLevelOrderByPidAndId(level);
		for(int i=2;i<4;i++){
			JpPerson p = persons.get(i);
			doGenerate(p.getId(),level*2-1,pageNum++);
		}
		List<List<Integer>> needMergeList = getNeedMergeList(2,pageNum,maxCount,9001,1000);
		mergeIndex = doMerge(needMergeList,mergeIndex);


		startNum = pageNum;
		//第十一代
		List<JpPerson> sonPersons = getPersonsByLevelOrderByPidAndId(level*2-1);
		for(JpPerson p:sonPersons){
			doGenerate(p.getId(),level*3-2,pageNum++);
		}
		needMergeList = getNeedMergeList(startNum,pageNum,maxCount,9002,2000);
		mergeIndex = doMerge(needMergeList,mergeIndex);



		startNum = pageNum;
		//第十六代
		List<JpPerson> grandsonPersons = getPersonsByLevelOrderByPidAndId(level*3-2);
		for(JpPerson p:grandsonPersons){
			List<JpPerson> sons = getPersonsByPidAndLevel(p.getId(),30);
			if(sons!=null && sons.size()>0)
				doGenerate(p.getId(),level*4-3,pageNum++);
		}
		needMergeList = getNeedMergeList(startNum,pageNum,maxCount,9003,3000);
		mergeIndex = doMerge(needMergeList,mergeIndex);

		WordUtil.writeWord(mergePath, ancestorPath+"/jp.doc", mergeIndex);
	}


	@Override
	public ResponseEntity<FileSystemResource> exportWord(HttpServletRequest request, JpPerson jpPerson, Class<JpPerson> jpPersonClass, String s) {
		try {
			generateWord();

			File file = new File(ancestorPath+"/jp.doc");
			if (file == null){
				return null;
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Content-Disposition", "attachment; filename=jp.doc" );
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			return ResponseEntity
					.ok()
					.headers(headers)
					.contentLength(file.length())
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(new FileSystemResource(file));

			//ModelAndView mv = new ModelAndView(new PoiEntityExcelView());
			//mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
			//mv.addObject(NormalExcelConstants.CLASS, clazz);
			//mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title));
			//mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
			//return Result.ok(ancestorPath+"/jp.doc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
