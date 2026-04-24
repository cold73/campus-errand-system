package com.cold73.campuserrand.service;

import com.cold73.campuserrand.vo.IncomeRecordVO;
import com.cold73.campuserrand.vo.IncomeSummaryVO;

import java.util.List;

/**
 * 跑腿员收益 Service 接口
 */
public interface RunnerIncomeService {

    /**
     * 查询收益汇总（累计收入、已结算收入、订单数）
     *
     * @param runnerId 跑腿员ID
     * @return 收益汇总
     */
    IncomeSummaryVO getSummary(Long runnerId);

    /**
     * 查询收益明细列表（按入账时间倒序）
     *
     * @param runnerId 跑腿员ID
     * @return 收益记录列表
     */
    List<IncomeRecordVO> listRecords(Long runnerId);
}
