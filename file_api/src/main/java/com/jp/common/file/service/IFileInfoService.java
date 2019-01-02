package com.jp.common.file.service;

import com.jp.common.file.entity.customized.FileInfoAO;
import com.jp.common.file.entity.gen.FileInfoCriteria;
import com.jp.framework.common.model.ServiceResult;
import com.jp.framework.service.IBaseAOService;


/**
 * @author yangfeng
 * @create 2018-06-04 12:57
 **/
public interface IFileInfoService extends IBaseAOService<FileInfoAO, FileInfoCriteria> {

    /**
     * 保存文件
     *
     * @param fileInfo
     * @return
     */
    ServiceResult<Boolean> save(FileInfoAO fileInfo);
}
