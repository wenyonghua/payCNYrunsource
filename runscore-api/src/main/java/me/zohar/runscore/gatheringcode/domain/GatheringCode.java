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
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.merchant.domain.GatheringChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;

@Getter
@Setter
@Entity
@Table(name = "gathering_code")
@DynamicInsert(true)
@DynamicUpdate(true)
public class GatheringCode implements Serializable {

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
	 * 使用中 1是使用中
	 */
	private Boolean inUse;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 发起审核时间
	 */
	private Date initiateAuditTime;

	/**
	 * 审核类型 自动审核1是自动
	 */
	private String auditType;

	/**
	 * 银行卡总数据
	 */
	private Double bankTotalAmount;
	/**
	 * 收款卡额度限制
	 */
	private Double bankReflect;//收款卡额度限制

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
	 * 银行卡号
	 */
	private String bankCode;

	/**
	 * 开户行  银行名称
	 */
	private String bankAddress;

	/**
	 * 卡户主 姓名
	 */

	private String bankUsername;

	/**
	 *卡用途
	 * @param auditType
	 */
	private String cardUse;//卡用途 1.存款卡，2.付款卡，3.备用金卡

	/**
	 * 单日限额
	 * @param daily_quota
	 */
	@Column(name = "daily_quota", length = 555)
	private String dailyQuota;

	/**
	 * 银行卡期限 1：永久使用，0：永久停用
	 */
	@Column(name = "qi_xian_user", length = 255)
	private String qiXianUser;

	/**
	 * 银行的登录账号
	 * @param auditType
	 */
	@Column(name = "bank_account", length = 255)
	private String bankAccount;

	/**
	 * 银行的登录账号密码
	 * @param auditType
	 */
	@Column(name = "bank_password", length = 255)
	private String bankPassord;
	/**
	 * 银行的登录账号Ip
	 * @param auditType
	 */
	@Column(name = "bank_ip", length = 255)
	private String bankIp;

	/**
	 * 自动机 1:启用，0:停用
	 * @param auditType
	 */
	@Column(name = "auto_run", length = 255)
	private String autoRun;

	/**
	 * 同步自动机返回数据
	 * @param auditType
	 */
	@Column(name = "auto_run_message", length = 255)
	private String autoRunMessage;

	@Column(name = "chek_order_mode_state", length = 255)
	private String checkOrderModeState;//查单方式 0=人工，1是自动



	public void initiateAudit(String auditType) {
		this.setState(Constant.收款码状态_待审核);
		this.setInitiateAuditTime(new Date());
		this.setAuditType(auditType);
	}

}
