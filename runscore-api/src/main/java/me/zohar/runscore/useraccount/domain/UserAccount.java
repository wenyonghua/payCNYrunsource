package me.zohar.runscore.useraccount.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.agent.domain.InviteCode;

@Getter
@Setter
@Entity
@Table(name = "user_account")
@DynamicInsert(true)
@DynamicUpdate(true)
public class UserAccount implements Serializable {

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
	 * 用户名
	 */
	private String userName;

	/**
	 * 真实姓名
	 */
	private String realName;
	
	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 账号类型
	 */
	private String accountType;

	/**
	 * 账号级别
	 */
	private Integer accountLevel;

	/**
	 * 账号级别路径
	 */
	private String accountLevelPath;

	/**
	 * 登录密码
	 */
	private String loginPwd;

	/**
	 * 资金密码
	 */
	private String moneyPwd;

	/**
	 * 保证金
	 */
	private Double cashDeposit;

	/**
	 * 状态
	 */
	private String state;

	/**
	 * 注册时间
	 */
	private Date registeredTime;

	/**
	 * 最近登录时间
	 */
	private Date latelyLoginTime;

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
	 * 银行资料最近修改时间
	 */
	private Date bankInfoLatelyModifyTime;

	/**
	 * 电子钱包地址
	 */
	private String virtualWalletAddr;

	private Date virtualWalletLatelyModifyTime;

	/**
	 * 接单状态
	 */
	private String receiveOrderState;

	/**
	 * 逻辑删除
	 */
	private Boolean deletedFlag;

	/**
	 * 邀请人id
	 */
	@Column(name = "inviter_id", length = 32)
	private String inviterId;

	/**
	 * 系统来源
	 */
	private String systemSource;
	/**
	 * 付款标记 0是付款人员，1不是付款人员
	 */
	private String payerMark;
	/**
	 *付款范围使用_来分割
	 */
	private String paymentRange;
	/**
	 * 邀请人
	 */
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount inviter;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	@OrderBy("createTime ASC")
	private Set<AccountReceiveOrderChannel> receiveOrderChannels;

	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;
	/**
	 * ip白名单
	 */
	@Column(name = "ip_whitelist", length = 550)
	private String ipWhitelist;

	public void updateInviteInfo(InviteCode inviteCode) {
		this.setInviterId(inviteCode.getInviterId());
		this.setAccountLevel(inviteCode.getInviter().getAccountLevel() + 1);
		this.setAccountLevelPath(inviteCode.getInviter().getAccountLevelPath() + "." + this.getId());
		this.setAccountType(inviteCode.getAccountType());
	}

}
