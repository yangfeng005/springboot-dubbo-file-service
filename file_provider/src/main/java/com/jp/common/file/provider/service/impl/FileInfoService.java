package com.jp.common.file.provider.service.impl;

import com.jp.common.file.entity.customized.FileInfoAO;
import com.jp.common.file.entity.gen.FileInfoCriteria;
import com.jp.common.file.provider.dao.gen.FileInfoGeneratedMapper;
import com.jp.common.file.service.IFileInfoService;
import com.jp.framework.common.model.ServiceResult;
import com.jp.framework.common.util.Constant;
import com.jp.framework.dao.BaseGeneratedMapper;
import com.jp.framework.service.AbstractBaseAOService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author yangfeng
 * @create 2018-06-04 10:47
 **/
@Service
public class FileInfoService extends AbstractBaseAOService<FileInfoAO, FileInfoCriteria> implements IFileInfoService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private FileInfoGeneratedMapper fileInfoGeneratedMapper;

    @Override
    protected BaseGeneratedMapper<FileInfoAO, FileInfoCriteria> getGeneratedMapper() {
        return fileInfoGeneratedMapper;
    }

    /**
     * 保存文件
     *
     * @param fileInfo
     * @return
     */
    @Override
    public ServiceResult<Boolean> save(FileInfoAO fileInfo) {
        if (fileInfo != null && StringUtils.isNotBlank(fileInfo.getMongoFileId())) {
            FileInfoCriteria criteria = new FileInfoCriteria();
            criteria.createCriteria().andMongoFileIdEqualTo(fileInfo.getMongoFileId());
            ServiceResult<List<FileInfoAO>> fileInfoRet = selectByCriteria(criteria);
            if (fileInfoRet != null && fileInfoRet.isSucceed() && CollectionUtils.isEmpty(fileInfoRet.getData())) {
                fileInfo.setCreateDateTime(new Date());
                fileInfo.setEnabled(Constant.VALID_FLG);
                fileInfo.setContentType("image/jpeg");
                return saveOrUpdate(fileInfo);
            }
        }
        return null;
    }
}
