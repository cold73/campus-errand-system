package com.cold73.campuserrand.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cold73.campuserrand.entity.RunnerIncome;
import com.cold73.campuserrand.mapper.RunnerIncomeMapper;
import com.cold73.campuserrand.service.RunnerIncomeService;
import com.cold73.campuserrand.vo.IncomeRecordVO;
import com.cold73.campuserrand.vo.IncomeSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RunnerIncomeServiceImpl implements RunnerIncomeService {

    private final RunnerIncomeMapper runnerIncomeMapper;

    @Override
    public IncomeSummaryVO getSummary(Long runnerId) {
        List<RunnerIncome> records = runnerIncomeMapper.selectList(
                Wrappers.<RunnerIncome>lambdaQuery()
                        .eq(RunnerIncome::getRunnerId, runnerId)
        );

        BigDecimal total = records.stream()
                .map(RunnerIncome::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal settled = records.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 1)
                .map(RunnerIncome::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new IncomeSummaryVO(total, settled, records.size());
    }

    @Override
    public List<IncomeRecordVO> listRecords(Long runnerId) {
        List<RunnerIncome> records = runnerIncomeMapper.selectList(
                Wrappers.<RunnerIncome>lambdaQuery()
                        .eq(RunnerIncome::getRunnerId, runnerId)
                        .orderByDesc(RunnerIncome::getCreateTime)
        );

        return records.stream().map(r -> new IncomeRecordVO(
                r.getId(),
                r.getOrderId(),
                r.getAmount(),
                r.getStatus(),
                r.getRemark(),
                r.getCreateTime()
        )).collect(Collectors.toList());
    }
}
