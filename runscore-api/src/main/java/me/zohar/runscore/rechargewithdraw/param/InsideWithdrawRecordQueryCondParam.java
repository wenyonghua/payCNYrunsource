package me.zohar.runscore.rechargewithdraw.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class InsideWithdrawRecordQueryCondParam extends PageParam {
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 状态
	 */
	private String state;
	
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
