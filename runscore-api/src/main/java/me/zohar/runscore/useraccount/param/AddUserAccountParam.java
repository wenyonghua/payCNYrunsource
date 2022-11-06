package me.zohar.runscore.useraccount.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.useraccount.domain.UserAccount;

@Data
public class AddUserAccountParam {

	/**
	 * 邀请人
	 */
	private String inviterUserName;

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
	private String accountType;
	
	/**
	 * 状态
	 */
	private String state;

	/**
	 * 登录密码
	 */
	@NotBlank
	private String loginPwd;

	/**
	 * 登录IP白名单
	 */
	private String ipWhitelist;

	/**
	 * 系统来源
	 */
	private String systemSource;
	/**
	 * 付款标记
	 */
	private String payerMark;

	/**
	 *付款范围
	 */
	private String paymentRange;

	public UserAccount convertToPo() {
		UserAccount po = new UserAccount();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setState(Constant.账号状态_启用);
		po.setDeletedFlag(false);
		po.setAccountLevel(0);
		po.setAccountLevelPath(po.getId());
		po.setCashDeposit(0d);
		po.setRegisteredTime(new Date());
		po.setMoneyPwd(po.getLoginPwd());
		po.setReceiveOrderState(Constant.接单状态_停止接单);
		po.setSystemSource(po.getSystemSource());//系统来源
		return po;
	}

}
