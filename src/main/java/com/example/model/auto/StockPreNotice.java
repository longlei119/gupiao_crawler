package com.example.model.auto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 股票业绩预告
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StockPreNotice extends Model {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 股票编码
     */
    private String stockNum;

    /**
     * 公告类型
     */
    private String noticeType;

    /**
     * 公告摘要
     */
    private String noticeSummary;

    /**
     * 净利润变动幅度
     */
    private String changeRate;

    /**
     * 公告日期
     */
    private LocalDate noticeDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
