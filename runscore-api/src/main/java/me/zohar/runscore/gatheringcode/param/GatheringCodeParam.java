package me.zohar.runscore.gatheringcode.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;

@Data
public class GatheringCodeParam {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 所属账号
	 */
	private String userName;

	/**
	 * 收款通道id
	 */
	@NotBlank
	private String gatheringChannelId;

	/**
	 * 收款金额
	 */
	private Double gatheringAmount;

	private Boolean fixedGatheringAmount;

	/**
	 * 收款人
	 */
	@NotBlank
	private String payee;


	/**
	 * 图片ID
	 */
	private String storageId;

	/**
	 * 银行卡号
	 */
	private String bankCode;

	/**
	 * 开户行
	 */
	private String bankAddress;

	/**
	 * 卡户主
	 */

	private String bankUsername;

	/**
	 *
	 * @param userAccountId
	 * @return
	 */
    private String 	bankReflect;//卡收款额度限制

	/**
	 * 卡用途 1.存款卡，2.付款卡，3.备用金卡，
	 * @param userAccountId
	 * @return
	 */
	private String cardUse;
	/**
	 * 银行卡总额度
	 */
	private String bankTotalAmount;//银行卡总额度

	/**
	 * 卡状态 1是启用，0是停用
	 * @param userAccountId
	 * @return
	 */
	private String inUser;

	/**
	 *
	 * @param userAccountId
	 * @return
	 */
	private String inUseName;

	/**
	 * 银行卡单日限额
	 */
	private String dailyQuota;

	/**
	 * 银行卡期限使用 1：永久使用，0：永久停用
	 */
	private String qiXianUser;


	/**
	 * 银行的登录账号
	 * @param auditType
	 */
	private String bankAccount;

	/**
	 * 银行的登录账号密码
	 * @param auditType
	 */
	private String bankPassord;
	/**
	 * 银行的登录账号Ip
	 * @param auditType
	 */
	private String bankIp;

	/**
	 * 自动机 1:启用，0:停用
	 * @param auditType
	 */
	private String autoRun;

	/**
	 * 查单状态
	 * 0:人工
	 * 1：自动
	 */
	private String checkOrderModeState;

	private String checkOrderMode;



	public GatheringCode convertToPo(String userAccountId) {
		GatheringCode po = new GatheringCode();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setUserAccountId(userAccountId);
		po.setInUse(true);
		po.setState(Constant.收款码状态_正常);
		return po;
	}

}
