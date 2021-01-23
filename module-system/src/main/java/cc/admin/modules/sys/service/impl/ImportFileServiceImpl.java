package cc.admin.modules.sys.service.impl;

import lombok.extern.slf4j.Slf4j;
import cc.admin.common.util.CommonUtils;
import cc.admin.poi.excel.imports.base.ImportFileServiceI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * excel导入 实现类
 */
@Slf4j
@Service
public class ImportFileServiceImpl implements ImportFileServiceI {

    @Value("${cc.admin.path.upload}")
    private String upLoadPath;

    @Value(value="${cc.admin.uploadType}")
    private String uploadType;

    @Override
    public String doUpload(byte[] data) {
        return CommonUtils.uploadOnlineImage(data, upLoadPath, "import", uploadType);
    }
}
