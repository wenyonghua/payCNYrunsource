package me.zohar.runscore.gatheringcode.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;

@Data
@EqualsAndHashCode(callSuper=false)
public class GatheringCodeQueryCondParam extends PageParam {
	
	private String state;
	
	/**
	 * 收款通道id
	 */
	private String gatheringChannelId;
	
	private String payee;//收款人
	
	private String userName;//所属账号
	
	/**
	 * 用户账号id
	 */
	private String userAccountId;
	private String bankCode;//银行卡号单个

	/**
	 *卡用途
	 */
	private String cardUse;

	/**
	 * 卡状态1使用,0停用
	 */
	private String inUseName;
	private  String inUse;

	/**
	 * 卡使用期限 1:永久使用，0：永久停用
	 */
	private String qiXianUser;

	/**
	 * 自动机状态 自动机 1:启用，0:停用
	 */
	private String autoRun;

   private String checkOrderModeState;//自动机查看方式 0=人工，1=自动
	/**
	 * 银行卡号 多个查询
	 */
	private String bankCardAccount;

	

}
