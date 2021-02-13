package cc.admin.modules.bi.service.impl;

import cc.admin.modules.bi.entity.BiFavorites;
import cc.admin.modules.bi.mapper.BiFavoritesMapper;
import cc.admin.modules.bi.service.IBiFavoritesService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 收藏夹
 * @Author: cc-admin
 * @Date:   2021-02-10
 * @Version: V1.0.0
 */
@Service
public class BiFavoritesServiceImpl extends ServiceImpl<BiFavoritesMapper, BiFavorites> implements IBiFavoritesService {

}
