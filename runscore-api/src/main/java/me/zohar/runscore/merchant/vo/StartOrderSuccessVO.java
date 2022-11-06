package me.zohar.runscore.merchant.vo;

import lombok.Data;
import me.zohar.runscore.dictconfig.ConfigHolder;

@Data
public class StartOrderSuccessVO {

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



//	public static StartOrderSuccessVO convertFor(String orderNo) {
//		StartOrderSuccessVO vo = new StartOrderSuccessVO();
//		vo.setPayUrl(ConfigHolder.getConfigValue("merchantOrderPayUrl") + orderNo);//支付地址
//		vo.setBillNo(orderNo);
//		return vo;
//	}

}
