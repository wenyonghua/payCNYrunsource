package me.zohar.runscore.statisticalanalysis.vo;

import lombok.Data;
import me.zohar.runscore.statisticalanalysis.domain.TradePayoutSituation;
import me.zohar.runscore.statisticalanalysis.domain.TradeSituation;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

@Data
public class TradePayoutSituationVO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private Double totalTradeAmount;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double monthTradeAmount;

	private Long monthPaidOrderNum;

	private Long monthOrderNum;

	private Double monthSuccessRate;

	private Double yesterdayTradeAmount;

	private Long yesterdayPaidOrderNum;

	private Long yesterdayOrderNum;

	private Double yesterdaySuccessRate;

	private Double todayTradeAmount;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

	public static TradePayoutSituationVO convertFor(TradePayoutSituation tradeSituation) {
		if (tradeSituation == null) {
			return null;
		}
		TradePayoutSituationVO vo = new TradePayoutSituationVO();
		BeanUtils.copyProperties(tradeSituation, vo);
		return vo;
	}

}
