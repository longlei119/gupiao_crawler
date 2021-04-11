package com.example.service.impl;

import com.example.model.auto.StockDayInfo;
import com.example.mapper.auto.StockDayInfoMapper;
import com.example.service.IStockDayInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 股票每日信息 服务实现类
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-10
 */
@Service
public class StockDayInfoServiceImpl extends ServiceImpl<StockDayInfoMapper, StockDayInfo> implements IStockDayInfoService {

}
