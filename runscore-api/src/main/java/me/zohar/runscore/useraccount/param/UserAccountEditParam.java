package me.zohar.runscore.useraccount.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserAccountEditParam {

	/**
	 * 主键id
	 */
	@NotBlank
	private String id;

	/**
	 * 用户名
	 */
	@NotBlank
	private String userName;

	/**
	 * 真实姓名
	 */
	@NotBlank
	private String realName;
	
	@NotBlank
	private String mobile;

	/**
	 * 账号类型
	 */
	@NotBlank
	private String accountType;

	/**
	 * 状态
	 */
	@NotBlank
	private String state;

	/**
	 * 登录IP白名单
	 */
	private String ipWhitelist;

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

	/**
	 *付款范围
	 */
	private String paymentRange;
}
