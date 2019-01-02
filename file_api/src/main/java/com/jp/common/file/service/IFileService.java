package com.jp.common.file.service;

import com.jp.common.file.entity.customized.FileInfoAO;
import com.jp.framework.common.model.ServiceResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFileService {

    /**
     * base64字符串图片上传
     *
     * @param base64Str
     * @return
     */
    ServiceResult<FileInfoAO> base64StrUpload(String base64Str);

    /**
     * 文件上传
     *
     * @param request
     * @param userId
     * @return
     */
    ServiceResult<FileInfoAO> uploadFile(HttpServletRequest request, String userId);


    ServiceResult<FileInfoAO> compressUploadFile(HttpServletRequest request, String userId);


    /**
     * 下载
     *
     * @param fileId   文件id
     * @param response
     * @return
     */
    void downloadFile(String fileId, HttpServletRequest request, HttpServletResponse response);

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    ServiceResult<Object> deleteFile(String fileId);
}
