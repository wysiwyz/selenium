package org.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Arrays;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobItem {
    /**
     * 更新日期  b-tit__date
     */
    private String dateUpdated;
    /**
     * 職務名
     */
    private String jobName;
    /**
     * 公司名 data-cust-name
     */
    private String custName;
    /**
     * 行業別 indcat-desc
     */
    private String industryDesc;
    /**
     * 地區,經歷,學歷 (以|分隔)
     */
    private String jobListIntro;
    /**
     * 簡述
     */
    private String jobItemInfo;

    /**
     * 標籤, 以|分隔
     */
    private String jobItemTag;
    /**
     * URL
     */
    private String jobLink;

    public static String[] getHeaders() {
        Field[] fields = JobItem.class.getDeclaredFields();
        return Arrays.stream(fields)
                .map(Field::getName)
                .toArray(String[]::new);
    }
}
