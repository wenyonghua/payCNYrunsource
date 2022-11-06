package me.zohar.runscore.merchant.domain;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.constants.Constant;
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
@Table(name = "merchant_settlement_payout_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantSettlementPayOutRecord implements Serializable {

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
	 * 付款金额
	 * @param payCardNo
	 */
	@Column(name = "pay_amount", length = 32)
	private String payAmount;

	/**
	 * 付款银行卡号
	 * @param payCardNo
	 */
	@Column(name = "pay_cardno", length = 32)
	private String payCardNo;

	/**
	 * 付款手续费
	 * @param serverCharge
	 */
	@Column(name = "server_charge", length = 32)
	private String serverCharge;

	/**
	 * 备注
	 */
	private String note;

	@Column(name = "merchant_settl_id", length = 32)
	private String merchantSettlId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_settl_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantSettlementRecord merchantSettlementRecord;

}
