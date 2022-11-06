package me.zohar.runscore.useraccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.useraccount.domain.UserAccount;

/**
 * 用户账号明细信息vo
 * 
 * @author zohar
 * @date 2019年2月22日
 *
 */
@Data
public class UserAccountDetailsInfoVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 真实姓名
	 */
	private String realName;
	
	private String mobile;

	/**
	 * 账号类型
	 */
	private String accountType;

	private String accountTypeName;

	/**
	 * 账号级别
	 */
	private Integer accountLevel;

	/**
	 * 返点
	 */
	private Double rebate;

	/**
	 * 保证金
	 */
	private Double cashDeposit;

	/**
	 * 状态
	 */
	private String state;

	private String stateName;

	/**
	 * 注册时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date registeredTime;

	/**
	 * 最近登录时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date latelyLoginTime;

	/**
	 * 开户银行
	 */
	private String openAccountBank;

	/**
	 * 开户人姓名
	 */
	private String accountHolder;

	/**
	 * 银行卡账号
	 */
	private String bankCardAccount;

	/**
	 * 银行资料最近修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date bankInfoLatelyModifyTime;

	/**
	 * 接单状态1真正接单,2是停止接单
	 */
	private String receiveOrderState;
	/**
	 * 接单状态名称
	 */
	private String receiveOrderStateName;

	private String inviterId;

	/**
	 * 邀请人
	 */
	private String inviterUserName;

	/**
	 * 登录IP白名单
	 * @param userAccounts
	 * @return
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
	 * 付款标记名称
	 */
	private String payerMarkName;
	/**
	 *付款范围
	 */
	private String paymentRange;

	public static List<UserAccountDetailsInfoVO> convertFor(List<UserAccount> userAccounts) {
		if (CollectionUtil.isEmpty(userAccounts)) {
			return new ArrayList<>();
		}
		List<UserAccountDetailsInfoVO> vos = new ArrayList<>();
		for (UserAccount userAccount : userAccounts) {
			vos.add(convertFor(userAccount));
		}
		return vos;
	}

	public static UserAccountDetailsInfoVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		UserAccountDetailsInfoVO vo = new UserAccountDetailsInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		vo.setAccountTypeName(DictHolder.getDictItemName("accountType", vo.getAccountType()));
		vo.setStateName(DictHolder.getDictItemName("accountState", vo.getState()));
		vo.setReceiveOrderState(vo.getReceiveOrderState());//接单状态
		vo.setReceiveOrderStateName(DictHolder.getDictItemName("receiveOrderState", vo.getReceiveOrderState()));//接单状态名称

		vo.setPayerMarkName(DictHolder.getDictItemName("PayerMark", vo.getPayerMark()));//付款人员标记名称
		vo.setPayerMark(vo.getPayerMark());//付款标记

		if (userAccount.getInviter() != null) {
			vo.setInviterUserName(userAccount.getInviter().getUserName());
		}
		vo.setSystemSource(vo.getSystemSource());//系统来源

		vo.setIpWhitelist(vo.getIpWhitelist());//登录IP白名单

		return vo;
	}

}
