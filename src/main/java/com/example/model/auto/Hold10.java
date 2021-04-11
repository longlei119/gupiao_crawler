package com.example.model.auto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 十大流通股东
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Hold10 extends Model {

    private static final long serialVersionUID = 1L;

    private String id;

    private String stockName;

    private String stockNum;

    private String holdName;

    private LocalDateTime createTime;

    private Date deliveryDate;

    private String holdNum;

    private String holdNumChange;

    private String holdRate;

    private String holdChangRate;

    private String type;

    /**
     * 是否来自公告列表的股票
     */
    private Integer isFromNotice;


}
