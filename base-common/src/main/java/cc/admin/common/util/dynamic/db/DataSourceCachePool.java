package cc.admin.common.util.dynamic.db;

import cc.admin.common.util.SpringContextUtils;
import com.alibaba.druid.pool.DruidDataSource;
import cc.admin.common.constant.CacheConstant;
import cc.admin.common.sys.api.ISysBaseAPI;
import cc.admin.common.sys.vo.DynamicDataSourceModel;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.HashMap;
import java.util.Map;


/**
 * 数据源缓存池
 */
public class DataSourceCachePool {
    /** 数据源连接池缓存【本地 class缓存 - 不支持分布式】 */
    private static Map<String, DruidDataSource> dbSources = new HashMap<>();
    private static RedisTemplate<String, Object> redisTemplate;

    private static RedisTemplate<String, Object> getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate<String, Object>) SpringContextUtils.getBean("redisTemplate");
        }
        return redisTemplate;
    }

    /**
     * 获取多数据源缓存
     *
     * @param dbId
     * @return
     */
    public static DynamicDataSourceModel getCacheDynamicDataSourceModel(String dbId) {
        String redisCacheKey = CacheConstant.SYS_DYNAMICDB_CACHE + dbId;
        if (getRedisTemplate().hasKey(redisCacheKey)) {
            return (DynamicDataSourceModel) getRedisTemplate().opsForValue().get(redisCacheKey);
        }
        ISysBaseAPI sysBaseAPI = SpringContextUtils.getBean(ISysBaseAPI.class);
        DynamicDataSourceModel dbSource = sysBaseAPI.getDynamicDbSourceById(dbId);
        if (dbSource != null) {
            getRedisTemplate().opsForValue().set(redisCacheKey, dbSource);
        }
        return dbSource;
    }

    public static DruidDataSource getCacheBasicDataSource(String dbId) {
        return dbSources.get(dbId);
    }

    /**
     * put 数据源缓存
     *
     * @param dbId
     * @param db
     */
    public static void putCacheBasicDataSource(String dbId, DruidDataSource db) {
        dbSources.put(dbId, db);
    }

    /**
     * 清空数据源缓存
     */
    public static void cleanAllCache() {
        //关闭数据源连接
        for(Map.Entry<String, DruidDataSource> entry : dbSources.entrySet()){
            String dbkey = entry.getKey();
            DruidDataSource druidDataSource = entry.getValue();
            if(druidDataSource!=null && druidDataSource.isEnable()){
                druidDataSource.close();
            }
            //清空redis缓存
            getRedisTemplate().delete(CacheConstant.SYS_DYNAMICDB_CACHE + dbkey);
        }
        //清空缓存
        dbSources.clear();
    }

    public static void removeCache(String dbId) {
        //关闭数据源连接
        DruidDataSource druidDataSource = dbSources.get(dbId);
        if(druidDataSource!=null && druidDataSource.isEnable()){
            druidDataSource.close();
        }
        //清空redis缓存
        getRedisTemplate().delete(CacheConstant.SYS_DYNAMICDB_CACHE + dbId);
        //清空缓存
        dbSources.remove(dbId);
    }

}
