package me.zohar.runscore.statisticalanalysis.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "v_payout_trade_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TradePayoutSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
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

}
