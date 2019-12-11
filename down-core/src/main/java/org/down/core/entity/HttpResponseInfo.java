package org.down.core.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * <p>{@link HttpResponseInfo}</p>
 *
 * @author 白菜
 * @since Created in 2019/12/3 15:57
 */
@Data
@Accessors(chain = true)
public class HttpResponseInfo implements Serializable {

    private static final long serialVersionUID = 5694440690798548043L;
    private String fileName;
    /** 文件总大小 */
    private long totalSize;
    /** 是否支持分块下载 */
    private boolean supportRange;

    public HttpResponseInfo() {
    }

    public HttpResponseInfo(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        if (StringUtils.isBlank(fileName)) {
            fileName = "Unknown";
        }
        return fileName;
    }
}
