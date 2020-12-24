package cc.admin.modules.demo.service.impl;

import cc.admin.modules.demo.entity.DemoBook;
import cc.admin.modules.demo.mapper.DemoBookMapper;
import cc.admin.modules.demo.service.IDemoBookService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 图书
 * @Author: ZhangHouYing
 * @Date:   2020-11-07
 * @Version: V1.0.0
 */
@Service
public class DemoBookServiceImpl extends ServiceImpl<DemoBookMapper, DemoBook> implements IDemoBookService {

}
