package me.zohar.runscore.statisticalanalysis.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import me.zohar.runscore.statisticalanalysis.domain.*;
import me.zohar.runscore.statisticalanalysis.repo.*;
import me.zohar.runscore.statisticalanalysis.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alicp.jetcache.anno.Cached;

@Validated
@Service
public class StatisticalAnalysisService {

	@Autowired
	private TotalAccountReceiveOrderSituationRepo totalAccountReceiveOrderSituationRepo;

	@Autowired
	private TodayAccountReceiveOrderSituationRepo todayAccountReceiveOrderSituationRepo;

	@Autowired
	private CashDepositBountyRepo cashDepositBountyRepo;

	@Autowired
	private TradeSituationRepo tradeSituationRepo;

	@Autowired
	private TradePayoutSituationRepo tradePayoutSituationRepo;



	@Transactional(readOnly = true)
	public AccountReceiveOrderSituationVO findMyTodayReceiveOrderSituation(@NotBlank String userAccountId) {
		return AccountReceiveOrderSituationVO
				.convertForToday(todayAccountReceiveOrderSituationRepo.findByReceivedAccountId(userAccountId));
	}

	@Transactional(readOnly = true)
	public AccountReceiveOrderSituationVO findMyTotalReceiveOrderSituation(@NotBlank String userAccountId) {
		return AccountReceiveOrderSituationVO
				.convertForTotal(totalAccountReceiveOrderSituationRepo.findByReceivedAccountId(userAccountId));
	}

	//5分钟内，只会调用一次
	//@Cached(name = "totalTop10BountyRank", expire = 300)
	@Transactional(readOnly = true)
	public List<BountyRankVO> findTotalTop10BountyRank() {
		List<TotalAccountReceiveOrderSituation> receiveOrderSituations = totalAccountReceiveOrderSituationRepo
				.findTop10ByOrderByBountyDesc();
		return BountyRankVO.convertFor(receiveOrderSituations);
	}
	//5分钟内，只会调用一次
	//@Cached(name = "todayTop10BountyRank", expire = 300)
	@Transactional(readOnly = true)
	public List<BountyRankVO> findTodayTop10BountyRank() {
		List<TodayAccountReceiveOrderSituation> todayReceiveOrderSituations = todayAccountReceiveOrderSituationRepo
				.findTop10ByOrderByBountyDesc();
		return BountyRankVO.convertForToday(todayReceiveOrderSituations);
	}

	@Transactional(readOnly = true)
	public CashDepositBountyVO findCashDepositBounty() {
		CashDepositBounty cashDepositBounty = cashDepositBountyRepo.findTopBy();
		return CashDepositBountyVO.convertFor(cashDepositBounty);
	}

	@Transactional(readOnly = true)
	public TradeSituationVO findTradeSituation() {
		TradeSituation tradeSituation = tradeSituationRepo.findTopBy();
		return TradeSituationVO.convertFor(tradeSituation);
	}

	/**
	 * 付款统计
	 * @return
	 */
	@Transactional(readOnly = true)
	public TradePayoutSituationVO findPayoutTradeSituation() {
		TradePayoutSituation trtradeSituation = tradePayoutSituationRepo.findTopBy();
		return TradePayoutSituationVO.convertFor(trtradeSituation);
	}

}
