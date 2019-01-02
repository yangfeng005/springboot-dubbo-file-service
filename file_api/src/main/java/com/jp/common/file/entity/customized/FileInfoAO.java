package com.jp.common.file.entity.customized;

import com.jp.common.file.entity.gen.FileInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.Serializable;

/**
 * 应用对象 - FileInfo.
 * <p>
 * 该类于 2018-06-25 15:50:42 首次生成，后由开发手工维护。
 * </p>
 * @author yangfeng
 * @version 1.0.0, Jun 25, 2018
 */
@JsonSerialize(include = Inclusion.ALWAYS)
public final class FileInfoAO extends FileInfo implements Serializable {

    /**
     * 默认的序列化 id.
     */
    private static final long serialVersionUID = 1L;

}
