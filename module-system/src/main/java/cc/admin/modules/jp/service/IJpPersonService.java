package cc.admin.modules.jp.service;

import cc.admin.modules.jp.entity.JpPerson;
import cc.admin.modules.jp.entity.JpPersonTree;
import cc.admin.modules.jp.entity.JpTreeChartData;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
public interface IJpPersonService extends IService<JpPerson> {

	List<JpPersonTree> queryJpPersonTree();

	JpTreeChartData queryTreeChartData();

	/**
	 * 导出word
	 * @param request
	 * @param jpPerson
	 * @param jpPersonClass
	 * @param s
	 * @return
	 */
	ResponseEntity<FileSystemResource> exportWord(HttpServletRequest request, JpPerson jpPerson, Class<JpPerson> jpPersonClass, String s);
}
