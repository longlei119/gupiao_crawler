package com.example.util;

import lombok.Data;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年04月10日 17:49
 */
@Data
public class StockEntity {
    /**
     * 编码
     */
    private String f12;
    /**
     * 名称
     */
    private String f14;
    /**
     * 涨跌幅
     */
    private String f3;
    /**
     * 成交额
     */
    private String f6;
    /**
     * 量比
     */
    private String f10;
    /**
     * 换手率
     */
    private String f8;
    /**
     *市盈率
     */
    private String f9;
    /**
     * 市净率
     */
    private String f23;
}
