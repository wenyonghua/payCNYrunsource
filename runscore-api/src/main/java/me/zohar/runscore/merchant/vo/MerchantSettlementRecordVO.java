package me.zohar.runscore.merchant.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.merchant.domain.MerchantSettlementRecord;

@Data
public class MerchantSettlementRecordVO {

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
	 * 提现金额
	 */
	private String withdrawAmountView;

	/**
	 * 申请时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date applyTime;

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
	 * 接入商户名称
	 */
	private String merchantName;

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
	 * 付款银行卡号
	 */
	private String payCardNo;

	/**
	 * 提现手续费
	 * @param merchantSettlementRecords
	 * @return
	 */
	private String serverCharge;


	public static List<MerchantSettlementRecordVO> convertFor(
			List<MerchantSettlementRecord> merchantSettlementRecords) {
		if (CollectionUtil.isEmpty(merchantSettlementRecords)) {
			return new ArrayList<>();
		}
		List<MerchantSettlementRecordVO> vos = new ArrayList<>();
		for (MerchantSettlementRecord merchantSettlementRecord : merchantSettlementRecords) {
			vos.add(convertFor(merchantSettlementRecord));
		}
		return vos;
	}

	public static MerchantSettlementRecordVO convertFor(MerchantSettlementRecord merchantSettlementRecord) {
		if (merchantSettlementRecord == null) {
			return null;
		}
		MerchantSettlementRecordVO vo = new MerchantSettlementRecordVO();
		BeanUtils.copyProperties(merchantSettlementRecord, vo);
		vo.setStateName(DictHolder.getDictItemName("merchantSettlementState", vo.getState()));
		if (merchantSettlementRecord.getMerchant() != null) {
			vo.setMerchantName(merchantSettlementRecord.getMerchant().getMerchantName());
		}
		if (merchantSettlementRecord.getMerchantBankCard() != null) {
			vo.setOpenAccountBank(merchantSettlementRecord.getMerchantBankCard().getOpenAccountBank());//银行名称
			vo.setAccountHolder(merchantSettlementRecord.getMerchantBankCard().getAccountHolder());
			vo.setBankCardAccount(merchantSettlementRecord.getMerchantBankCard().getBankCardAccount());
		}
		vo.setPayCardNo(merchantSettlementRecord.getPayCardNo());//付款银行卡号
		vo.setServerCharge(merchantSettlementRecord.getServerCharge());//提现手续费

		String vv= DecimalFormatUtil.formatString(new BigDecimal(vo.getWithdrawAmount()), null);
		vo.setWithdrawAmountView(vv);//订单金额使用界面展示吧金额格式化了 "#,###.00";
		return vo;
	}

}
