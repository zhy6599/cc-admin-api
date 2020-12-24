package cc.admin.modules.app.service.impl;

import cc.admin.modules.app.entity.TestPerson;
import cc.admin.modules.app.mapper.TestPersonMapper;
import cc.admin.modules.app.service.ITestPersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: test_person
 * @Author: ZhangHouYing
 * @Date:   2020-05-31
 * @Version: V1.0
 */
@Service
public class TestPersonServiceImpl extends ServiceImpl<TestPersonMapper, TestPerson> implements ITestPersonService {

}
