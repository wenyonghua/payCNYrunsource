package me.zohar.runscore.rechargewithdraw.domain;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.useraccount.domain.UserAccount;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "inside_withdraw_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class InsidewithdrawRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", length = 32)
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
	private Date submitTime;

	/**
	 * 状态
	 */
	private String state;

	/**
	 * 备注
	 */
	private String note;

	/**
	 * 审核时间
	 */
	private Date approvalTime;

	/**
	 * 确认到帐时间
	 */
	private Date confirmCreditedTime;
	/**
	 * 银行卡总金额
	 */
	private String bankTotal;
//	/**
//	 * 用户账号id
//	 */
//	@Column(name = "user_account_id", length = 32)
//	private String userAccountId;
//
//	/**
//	 * 用户账号
//	 */
//	@NotFound(action = NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	private UserAccount userAccount;


	@Column(name = "gathering_code_id", length = 32)
	private String gatheringCodeId;
	/**
	 * 收款码
	 */
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_code_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringCode gatheringCode;

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
	 * 设置银行卡信息
	 */
	public void setBankInfo(UserAccount userAccount) {
		this.setWithdrawWay(Constant.提现方式_银行卡);
		this.setOpenAccountBank(userAccount.getOpenAccountBank());
		this.setAccountHolder(userAccount.getAccountHolder());
		this.setBankCardAccount(userAccount.getBankCardAccount());
	}

	/**
	 * 设置电子钱包信息
	 */
	public void setVirtualWalletInfo(UserAccount userAccount) {
		this.setWithdrawWay(Constant.提现方式_电子钱包);
		this.setVirtualWalletAddr(userAccount.getVirtualWalletAddr());
	}

	public void approved(String note) {
		this.setState(Constant.提现记录状态_审核通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void notApproved(String note) {
		this.setState(Constant.提现记录状态_审核不通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void confirmCredited() {
		this.setState(Constant.提现记录状态_已到账);
		this.setConfirmCreditedTime(new Date());
	}

}
