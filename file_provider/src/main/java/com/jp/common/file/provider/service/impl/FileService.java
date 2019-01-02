package com.jp.common.file.provider.service.impl;

import com.jp.common.file.entity.customized.FileInfoAO;
import com.jp.common.file.service.IFileService;
import com.jp.framework.common.model.ServiceResult;
import com.jp.framework.common.model.ServiceResultHelper;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService implements IFileService {

    // 获得SpringBoot提供的mongodb的GridFS对象
    @Autowired
    private GridFsTemplate gridFsTemplate;


    /**
     * base64字符串图片上传
     *
     * @param base64Str
     * @return
     */
    @Override
    public ServiceResult<FileInfoAO> base64StrUpload(String base64Str) {
        if (StringUtils.isEmpty(base64Str)) {
            return ServiceResultHelper.genResultWithFaild("base64字符串为空", -1);
        }
        base64Str = base64Str.replaceAll("data:image/jpeg;base64,", "");
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] imgByte = decoder.decodeBuffer(base64Str);
            for (int i = 0; i < imgByte.length; ++i) {
                if (imgByte[i] < 0) {// 调整异常数据
                    imgByte[i] += 256;
                }
            }
            InputStream is = new ByteArrayInputStream(imgByte);
            // 生成jpeg图片
            String fileName = new String((UUID.randomUUID().toString().replace("-", "") + ".jpg").getBytes("gb2312"), "ISO8859-1");
            // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
            GridFSFile gridFSFile = gridFsTemplate.store(is, fileName, "image/jpeg");
            FileInfoAO fileInfo = new FileInfoAO();
            fileInfo.setFileName(fileName);
            fileInfo.setContentType("image/jpeg");
            fileInfo.setMongoFileId(gridFSFile.getId().toString());
            return ServiceResultHelper.genResultWithSuccess(fileInfo);
        } catch (Exception e) {
            return ServiceResultHelper.genResultWithFaild("图片上传失败", -1);
        }
    }

    /**
     * 文件压缩上传
     *
     * @param request
     * @return
     */
    @Override
    public ServiceResult<FileInfoAO> compressUploadFile(HttpServletRequest request, String userId) {
        try {
            Part part = request.getPart("file");
            // 获得提交的文件名
            String fileName = part.getSubmittedFileName();
            // 获得文件类型
            String contentType = part.getContentType();
            // 获得文件输入流
            InputStream ins = part.getInputStream();
            BufferedImage image = Thumbnails.of(ins).scale(1f).outputQuality(0.25f).asBufferedImage();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
            ImageIO.write(image, "jpg", imOut);
            InputStream is = new ByteArrayInputStream(bs.toByteArray());
            // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
            GridFSFile gridFSFile = gridFsTemplate.store(is, fileName, contentType);
            FileInfoAO fileInfo = new FileInfoAO();
            fileInfo.setContentType(contentType);
            fileInfo.setFileName(fileName);
            fileInfo.setLastUpdateBy(userId);
            fileInfo.setMongoFileId(gridFSFile.getId().toString());
            return ServiceResultHelper.genResultWithSuccess(fileInfo);
        } catch (Exception e) {
            return ServiceResultHelper.genResultWithFaild("图片上传失败", -1);
        }
    }

    /**
     * 文件上传
     *
     * @param request
     * @param userId
     * @return
     */
    @Override
    public ServiceResult<FileInfoAO> uploadFile(HttpServletRequest request, String userId) {
        try {
            Part part = request.getPart("file");

            // 获得提交的文件名
            String fileName = part.getSubmittedFileName();
            // 获得文件输入流
            InputStream ins = part.getInputStream();

            // 获得文件类型
            String contentType = part.getContentType();
            // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
            GridFSFile gridFSFile = gridFsTemplate.store(ins, fileName, contentType);
            FileInfoAO fileInfo = new FileInfoAO();
            fileInfo.setContentType(contentType);
            fileInfo.setFileName(fileName);
            fileInfo.setLastUpdateBy(userId);
            fileInfo.setMongoFileId(gridFSFile.getId().toString());
            return ServiceResultHelper.genResultWithSuccess(fileInfo);
        } catch (Exception e) {
            return ServiceResultHelper.genResultWithFaild("图片上传失败", -1);
        }
    }

    /**
     * 下载文件
     *
     * @param fileId   文件id
     * @param request
     * @param response
     */
    @Override
    public void downloadFile(String fileId, HttpServletRequest request, HttpServletResponse response) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 查询单个文件
        GridFSDBFile gfsfile = gridFsTemplate.findOne(query);
        try {
            if (gfsfile == null) {
                return;
            }
            String fileName = gfsfile.getFilename().replace(",", "");
            //处理中文文件名乱码
            if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                    request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                    || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                //非IE浏览器的处理：
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            // 通知浏览器进行文件下载
            response.setContentType(gfsfile.getContentType());
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            gfsfile.writeTo(response.getOutputStream());
        } catch (IOException e) {

        }
    }

    @Override
    public ServiceResult<Object> deleteFile(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        // 查询单个文件
        GridFSDBFile gfsfile = gridFsTemplate.findOne(query);
        if (gfsfile == null) {
            return ServiceResultHelper.genResultWithFaild("文件不存在", -1);
        }
        try {
            gridFsTemplate.delete(query);
        } catch (final Exception e) {
            return ServiceResultHelper.genResultWithFaild("删除失败", -1);
        }
        return ServiceResultHelper.genResultWithSuccess();
    }
}
