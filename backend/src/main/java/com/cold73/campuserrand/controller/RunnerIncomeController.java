package com.cold73.campuserrand.controller;

import com.cold73.campuserrand.common.Result;
import com.cold73.campuserrand.service.RunnerIncomeService;
import com.cold73.campuserrand.vo.IncomeRecordVO;
import com.cold73.campuserrand.vo.IncomeSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 跑腿员收益接口
 */
@RestController
@RequestMapping("/api/runner/income")
@RequiredArgsConstructor
public class RunnerIncomeController {

    private final RunnerIncomeService runnerIncomeService;

    /**
     * 收益汇总（累计收入、已结算收入、订单数）
     *
     * @param runnerId 跑腿员ID
     */
    @GetMapping("/summary")
    public Result<IncomeSummaryVO> summary(@RequestParam Long runnerId) {
        return Result.success(runnerIncomeService.getSummary(runnerId));
    }

    /**
     * 收益明细列表（按入账时间倒序）
     *
     * @param runnerId 跑腿员ID
     */
    @GetMapping("/list")
    public Result<List<IncomeRecordVO>> list(@RequestParam Long runnerId) {
        return Result.success(runnerIncomeService.listRecords(runnerId));
    }
}
