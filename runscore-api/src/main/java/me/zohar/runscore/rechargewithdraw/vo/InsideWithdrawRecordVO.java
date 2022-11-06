package me.zohar.runscore.rechargewithdraw.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.domain.WithdrawRecord;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class InsideWithdrawRecordVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 提现金额
	 */
	private Double withdrawAmount;

	/**
	 * 提现方式
	 */
	private String withdrawWay;

	private String withdrawWayName;

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
	 * 电子钱包地址
	 */
	private String virtualWalletAddr;

	/**
	 * 提交时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date submitTime;

	/**
	 * 状态
	 */
	private String state;

	private String stateName;

	/**
	 * 备注
	 */
	private String note;

	/**
	 * 审核时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date approvalTime;

	/**
	 * 确认到帐时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date confirmCreditedTime;

	/**
	 * 用户账号id
	 */
	private String userAccountId;

	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 收款手续费
	 */
	private String serviceCharge;

	/**
	 * 收款银行卡号
	 * @param withdrawRecords
	 * @return
	 */
	private String shoukuanNumber;
	/**
	 * 银行卡总金额
	 */
	private String bankTotal;

	public static List<InsideWithdrawRecordVO> convertFor(List<InsidewithdrawRecord> withdrawRecords) {
		if (CollectionUtil.isEmpty(withdrawRecords)) {
			return new ArrayList<>();
		}
		List<InsideWithdrawRecordVO> vos = new ArrayList<>();
		for (InsidewithdrawRecord withdrawRecord : withdrawRecords) {
			vos.add(convertFor(withdrawRecord));
		}
		return vos;
	}

	public static InsideWithdrawRecordVO convertFor(InsidewithdrawRecord withdrawRecord) {
		if (withdrawRecord == null) {
			return null;
		}
		InsideWithdrawRecordVO vo = new InsideWithdrawRecordVO();
		BeanUtils.copyProperties(withdrawRecord, vo);
		vo.setWithdrawWayName(DictHolder.getDictItemName("withdrawWay", vo.getWithdrawWay()));//支付方式
		vo.setStateName(DictHolder.getDictItemName("withdrawRecordState", vo.getState()));//支付状态
		vo.setBankTotal(withdrawRecord.getBankTotal());//银行卡总金额
		//if (withdrawRecord.getUserAccount() != null) {
			vo.setUserName("系统内部转折");
		//}
		return vo;
	}
//1.备用金卡，2.存款卡，3.付款卡
}
