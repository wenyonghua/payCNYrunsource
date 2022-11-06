package me.zohar.runscore.merchant.param;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import me.zohar.runscore.merchant.domain.MerchantPayOutOrder;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 付款对象
 */
@Data
public class StartPayOutOrderParam {

	/**
	 * 商户号
	 */
	@NotBlank
	private String merchantNum;

	/**
	 * 商户订单号 就是外部订单号客户提供过来的订单号
	 */
	@NotBlank
	private String orderNo;

	/**
	 * 支付类型
	 */
	@NotBlank
	private String payType;

	/**
	 * 支付金额
	 */
	@NotBlank
	private String amount;

	/**
	 * 回调地址
	 */
	private String notifyUrl;

	private String returnUrl;

	private String attch;

	@NotBlank
	private String sign;

	private String specifiedReceivedAccountId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	public Date publishTime;
	/**
	 * 系统来源
	 */
	private String systemSorce;

	/**
	 * 收款人名字
	 */
	private String shoukuBankPayee;

	/**
	 * 收款银行卡号
	 */
	private String shoukuBankNumber;
	/**
	 * 收款银行名称
	 */
	private String shoukuBankName;
	/**
	 * 收款银行支行信息
	 */

	private String shoukuBankBranch;

	public MerchantPayOutOrder convertToPo(String merchantId, String gatheringChannelId, Double rate,
										   Integer orderEffectiveDuration, String outOrderNo) {
		MerchantPayOutOrder po = new MerchantPayOutOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setMerchantId(merchantId);
		po.setGatheringChannelId(gatheringChannelId);
		po.setGatheringAmount(Double.parseDouble(this.getAmount()));
		po.setSubmitTime(new Date());
		po.setOrderState(Constant.商户订单状态_等待接单);
		po.setRate(rate);
		po.setOutOrderNo(outOrderNo);//外部商户订单号
		po.setUsefulTime(DateUtil.offset(po.getSubmitTime(), DateField.MINUTE, orderEffectiveDuration));//有效时间
		if (StrUtil.isBlank(po.getSpecifiedReceivedAccountId())) {
			po.setSpecifiedReceivedAccountId(null);
		}
		if (this.getPublishTime() != null) {
			po.setOrderState(Constant.商户订单状态_定时发单);
			po.setSubmitTime(this.getPublishTime());
			po.setUsefulTime(DateUtil.offset(po.getSubmitTime(), DateField.MINUTE, orderEffectiveDuration));
		}
		return po;
	}

//	public MerchantOrderPayInfo convertToPayInfoPo(String merchantOrderId) {
//		MerchantOrderPayInfo po = new MerchantOrderPayInfo();
//		BeanUtils.copyProperties(this, po);
//		po.setId(IdUtils.getId());
//		po.setMerchantOrderId(merchantOrderId);
//		po.setNoticeState(Constant.商户订单支付通知状态_未通知);
//		return po;
//	}

	/**
	 * 使用新的方法进行设置值
	 * @param merchantId
	 * @param gatheringChannelId
	 * @param rate
	 * @param orderEffectiveDuration
	 * @param outOrderNo
	 * @return
	 */
	public MerchantPayOutOrder convertNewToPo(String merchantId, String gatheringChannelId, Double rate,
									 Integer orderEffectiveDuration,String outOrderNo) {
		MerchantPayOutOrder po = new MerchantPayOutOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setMerchantId(merchantId);//商户ID
		po.setGatheringChannelId(gatheringChannelId);
		po.setGatheringAmount(Double.parseDouble(this.getAmount()));
		po.setSubmitTime(new Date());
		po.setOrderState(Constant.商户订单状态_已接单);
		po.setRate(rate);//费率
		po.setOutOrderNo(outOrderNo);//外部商户订单号
		po.setUsefulTime(DateUtil.offset(po.getSubmitTime(), DateField.MINUTE, orderEffectiveDuration));//有效时间
		if (StrUtil.isBlank(po.getSpecifiedReceivedAccountId())) {
			po.setSpecifiedReceivedAccountId(null);
		}
		if (this.getPublishTime() != null) {
			po.setOrderState(Constant.商户订单状态_定时发单);
			po.setSubmitTime(this.getPublishTime());
			po.setUsefulTime(DateUtil.offset(po.getSubmitTime(), DateField.MINUTE, orderEffectiveDuration));
		}
		return po;
	}

}
