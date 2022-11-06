package me.zohar.runscore.merchant.vo;

import lombok.Data;

/**
 * 付款对象
 */
@Data
public class StartPayoutOrderSuccessVO {

	/**
	 * 支付地址
	 */
	private String payUrl;

	/**
	 *
	 * @param orderNo
	 * @return
	 */
	private String billNo;

	/**
	 *收款人
	 */
	private String payee;

	/**
	 * 卡号
	 */
	private String bankCode;
	/**
	 * 开户行  银行名称
	 */
	private String bankAddress;
	/**
	 * 姓名
	 */
	private String bankUsername;
	/**
	 * 1是处理中，2是失败，3是成功已支付
	 */
	private String status="0";//状态默认是处理中



//	public static StartOrderSuccessVO convertFor(String orderNo) {
//		StartOrderSuccessVO vo = new StartOrderSuccessVO();
//		vo.setPayUrl(ConfigHolder.getConfigValue("merchantOrderPayUrl") + orderNo);//支付地址
//		vo.setBillNo(orderNo);
//		return vo;
//	}

}
