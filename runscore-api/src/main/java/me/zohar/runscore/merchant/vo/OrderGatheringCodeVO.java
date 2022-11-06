package me.zohar.runscore.merchant.vo;

import java.math.BigDecimal;
import java.util.Date;

import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.merchant.domain.MerchantOrder;

@Data
public class OrderGatheringCodeVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 收款渠道
	 */
	private String gatheringChannelCode;

	private String gatheringChannelName;

	/**
	 * 收款金额
	 */
	private Double gatheringAmount;

	/**
	 * 收款金额 界面展示
	 */
	private String gatheringAmountView;

	/**
	 * 订单状态
	 */
	private String orderState;

	private String orderStateName;

	/**
	 * 提交时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date submitTime;

	/**
	 * 有效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date usefulTime;

	private String gatheringCodeStorageId;

	private String gatheringCodeUrl;

	private String bankCode;//银行卡号

	private String bankAddress;//银行名称

	private String bankUsername;//卡户主存款人名字

	//附言码
	private String attch;

	/**
	 * 同步通知地址
	 */
	private String returnUrl;

	/**
	 *外部商户订单号
	 * @param outOrderNo
	 * @return
	 */
	private String outOrderNo;

	public static OrderGatheringCodeVO convertFor(MerchantOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		OrderGatheringCodeVO vo = new OrderGatheringCodeVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		vo.setReturnUrl(merchantOrder.getPayInfo().getReturnUrl());
		if (merchantOrder.getGatheringChannel() != null) {
			vo.setGatheringChannelCode(merchantOrder.getGatheringChannel().getChannelCode());
			vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
			if(merchantOrder.getGatheringCode()!=null && "bankcard".equals(merchantOrder.getGatheringChannel().getChannelCode()) || "UnionPayScan".equals(merchantOrder.getGatheringChannel().getChannelCode())){//卡转卡和银联扫码
				vo.setBankCode(merchantOrder.getGatheringCode().getBankCode());//卡号
				vo.setBankAddress(merchantOrder.getGatheringCode().getBankAddress());//银行名称
				vo.setBankUsername(merchantOrder.getGatheringCode().getBankUsername());//卡户主
				vo.setAttch(merchantOrder.getPayInfo().getAttch());//附言码
			}
			if((merchantOrder.getGatheringCode()!=null && "wechat".equals(merchantOrder.getGatheringChannel().getChannelCode())) || (merchantOrder.getGatheringCode()!=null && "alipay".equals(merchantOrder.getGatheringChannel().getChannelCode()))){//微信或者支付宝支付
				vo.setAttch(merchantOrder.getPayInfo().getAttch());//附言码
			}
		}
		//vo.setGatheringAmountView();//用于界面展示格式化金额
		String vv= DecimalFormatUtil.formatString(new BigDecimal(vo.getGatheringAmount()), null);
		vo.setGatheringAmountView(vv);//银行卡总余额使用界面展示吧金额格式化了 "#,###.00";
		return vo;
	}

}
