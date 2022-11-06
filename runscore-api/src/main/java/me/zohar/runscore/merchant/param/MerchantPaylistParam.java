package me.zohar.runscore.merchant.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantPaylistParam extends PageParam {

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 订单状态
	 */
	private String orderState;
	/**
	 *银行卡号
	 */
	private String bankNum;
	/**
	 *商户ID号主键ID
	 */
	private String merchantId;

	/**
	 *商户号
	 */
	private String merchantNo;
	/**
	 * 商户订单号外部订单号
	 */
	private String merchantOrderNo;
	


	/**
	 * 提交开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitStartTime;

	/**
	 * 提交结束时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitEndTime;



}
