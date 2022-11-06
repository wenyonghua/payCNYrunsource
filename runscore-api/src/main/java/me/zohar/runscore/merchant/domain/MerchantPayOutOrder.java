package me.zohar.runscore.merchant.domain;

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

/**
 * 付款对象数据
 */
@Getter
@Setter
@Entity
@Table(name = "merchant_payout_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantPayOutOrder implements Serializable {

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
	 * 收款金额
	 */
	private Double gatheringAmount;

	/**
	 * 提交时间
	 */
	private Date submitTime;

	/**
	 * 有效时间
	 */
	private Date usefulTime;

	/**
	 * 订单状态
	 */
	private String orderState;

	/**
	 * 备注信息
	 */
	private String note;

	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	/**
	 * 接单人账号id
	 */
	@Column(name = "received_account_id", length = 32)
	private String receivedAccountId;

	/**
	 * 接单时间
	 */
	private Date receivedTime;

	private String gatheringCodeStorageId;

	/**
	 * 处理时间
	 */
	private Date dealTime;

	/**
	 * 确认时间
	 */
	private Date confirmTime;

	/**
	 * 费率
	 */
	private Double rate;

	/**
	 * 返点
	 */
	private Double rebate;

	/**
	 * 奖励金 当付款的手续费使用
	 */
	private Double bounty;
	
	/**
	 * 指定接单人
	 */
	private String specifiedReceivedAccountId;

	/**
	 * 银行卡收款人
	 */
	private String payee;

	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;

	/**
	 * 通道编码号
	 */
	@Column(name = "gathering_channel_id", length = 32)
	private String gatheringChannelId;

	/**
	 * 外部商户订单号
	 */
	private String outOrderNo;//外部商户订单号

	/**
	 * 收款人
	 */
	@Column(name = "shouku_bank_payee", length = 255)
	private String shoukuBankPayee;

	/**
	 * 收款银行卡号
	 */
	@Column(name = "shouku_bank_number", length = 255)
	private String shoukuBankNumber;
	/**
	 * 收款银行名称
	 */
	@Column(name = "shouku_bank_name", length = 255)
	private String shoukuBankName;
	/**
	 * 收款银行支行信息
	 */
	@Column(name = "shouku_bank_branch", length = 255)
	private String shoukuBankBranch;

	/**
	 * 付款银行列表
	 */
	@Column(name = "fuku_bank_number", length = 255)
	private String fukuBankNumber;



	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_channel_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringChannel gatheringChannel;

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
	 * 系统来源
	 */
	@Column(name = "system_source", length = 32)
	private String systemSource;



	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount receivedAccount;

	@Column(name = "pay_info_id", length = 32)
	private String payInfoId;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pay_info_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantOrderPayInfo payInfo;

	public void updateBounty(Double bounty) {
		this.setBounty(bounty);
	}

	public void confirmToPaid(String note) {
		this.setOrderState(Constant.商户订单状态_已支付);//4是已经支付
		this.setConfirmTime(new Date());
		this.setDealTime(this.getConfirmTime());
		this.setNote(note);
	}

	public void updateReceived(String receivedAccountId, String gatheringCodeStorageId, Double rebate) {
		this.setReceivedAccountId(receivedAccountId);
		this.setGatheringCodeStorageId(gatheringCodeStorageId);
		this.setOrderState(Constant.商户订单状态_已接单);
		this.setReceivedTime(new Date());
		this.setRebate(rebate);
	}

	/**
	 * 更新接单数据
	 * @param receivedAccountId
	 * @param gatheringCode
	 * @param rebate
	 */
	public void updateReceived(String receivedAccountId, GatheringCode gatheringCode, Double rebate) {
		this.setReceivedAccountId(receivedAccountId);//接单人
		this.setGatheringCodeStorageId(gatheringCode.getStorageId());//图片路径地址
		this.setGatheringCodeId(gatheringCode.getId());//银行卡ID号
		this.setPayee(gatheringCode.getPayee());//银行卡收款人
		this.setOrderState(Constant.商户订单状态_已接单);
		this.setReceivedTime(new Date());//接单时间
		this.setRebate(rebate);//返点
	}



	public void customerCancelOrderRefund() {
		this.setOrderState(Constant.商户订单状态_取消订单退款);
		this.setConfirmTime(new Date());
		this.setDealTime(this.getConfirmTime());
	}

	public void updateUsefulTime(Date usefulTime) {
		this.setUsefulTime(usefulTime);
	}

}
