package com.example.model.auto;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 股票每日信息
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class StockDayInfo extends Model {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 股票编码
     */
    private String stockNum;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 涨跌幅
     */
    private String upDownRate;

    /**
     * 成交额
     */
    private Float dealMoney;

    /**
     * 量比
     */
    private String numRate;

    /**
     * 换手率
     */
    private String changeRate;

    /**
     * 市盈率
     */
    private String syl;

    /**
     * 市净率
     */
    private String sjl;

    private LocalDateTime createTime;


}
