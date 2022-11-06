package me.zohar.runscore.statisticalanalysis.vo;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import me.zohar.runscore.statisticalanalysis.domain.merchant.MerchantPayoutTradeSituation;
import me.zohar.runscore.statisticalanalysis.domain.merchant.MerchantTradeSituation;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 付款代付统计数据
 */
@Data
public class MerchantPayoutTradeSituationVO {

	private String id;

	private String merchantName;

	private Double totalTradeAmount;

	private Double totalPoundage;//手续费
	
	private Double totalActualIncome;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double monthTradeAmount;

	private Double monthPoundage;
	
	private Double monthActualIncome;

	private Long monthPaidOrderNum;

	private Long monthOrderNum;

	private Double monthSuccessRate;

	private Double yesterdayTradeAmount;

	private Double yesterdayPoundage;
	
	private Double yesterdayActualIncome;

	private Long yesterdayPaidOrderNum;

	private Long yesterdayOrderNum;

	private Double yesterdaySuccessRate;

	private Double todayTradeAmount;

	private Double todayPoundage;
	
	private Double todayActualIncome;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

	public static List<MerchantPayoutTradeSituationVO> convertFor(List<MerchantPayoutTradeSituation> situations) {
		if (CollectionUtil.isEmpty(situations)) {
			return new ArrayList<>();
		}
		List<MerchantPayoutTradeSituationVO> vos = new ArrayList<>();
		for (MerchantPayoutTradeSituation situation : situations) {
			vos.add(convertFor(situation));
		}
		return vos;
	}

	public static MerchantPayoutTradeSituationVO convertFor(MerchantPayoutTradeSituation situation) {
		if (situation == null) {
			return null;
		}
		MerchantPayoutTradeSituationVO vo = new MerchantPayoutTradeSituationVO();
		BeanUtils.copyProperties(situation, vo);
		return vo;
	}

}
