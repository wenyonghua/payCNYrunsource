package me.zohar.runscore.gatheringcode.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.merchant.domain.GatheringChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;

@Getter
@Setter
@Entity
@Table(name = "v_gathering_code_usage")
@DynamicInsert(true)
@DynamicUpdate(true)
public class GatheringCodeUsage implements Serializable {

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
	 * 状态,启用:1;禁用:0
	 */
	private String state;

	private Boolean fixedGatheringAmount;

	/**
	 * 收款金额
	 */
	private Double gatheringAmount;

	/**
	 * 收款人
	 */
	private String payee;

	/**
	 * 使用中
	 */
	private Boolean inUse;

	/**
	 * 发起审核时间
	 */
	private Date initiateAuditTime;

	/**
	 * 审核类型
	 */
	private String auditType;

	/**
	 * 创建时间
	 */
	private Date createTime;

	@Column(name = "storage_id", length = 32)
	private String storageId;

	@Column(name = "gathering_channel_id", length = 32)
	private String gatheringChannelId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_channel_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringChannel gatheringChannel;

	/**
	 * 用户账号id
	 */
	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	/**
	 * 银行卡总数据
	 */
	private Double bankTotalAmount;//银行卡总金额
	/**
	 * 收款卡额度限制
	 */
	private Double bankReflect;//收款卡额度限制

	/**
	 * 卡用途 1.存款卡，2.付款卡，3.备用金卡，
	 * @param userAccountId
	 * @return
	 */
	private String cardUse;




//	@Column(name = "gathering_code_id", length = 32)
//	private String gathering_code_id;
//
//	@NotFound(action = NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "gathering_code_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	private GatheringCode gatheringCode;


	private Double totalTradeAmount;//累计收款金额

	private Long totalPaidOrderNum;//收款次数

	private Long totalOrderNum;//收款次数

	private Double totalSuccessRate;

	private Double todayTradeAmount;//今日收款今日收款金额

	private Long todayPaidOrderNum;//今日

	private Long todayOrderNum;

	private Double todaySuccessRate;//今日成功率

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
	 * 银行卡单日限额
	 */
	private String dailyQuota;

	/**
	 * 卡使用期限 1是永久使用，0是永久停用
	 */
	private String qiXianUser;//


}
