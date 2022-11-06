package me.zohar.runscore.useraccount.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserAccountQueryCondParam extends PageParam {
	
	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 系统来源
	 */
	private String systemSource;
	/**
	 * 接单状态
	 */
	private String receiveOrderState;

	/**
	 * 付款标记
	 */
	private String payerMark;

}
