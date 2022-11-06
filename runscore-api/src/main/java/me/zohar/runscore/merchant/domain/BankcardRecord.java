package me.zohar.runscore.merchant.domain;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
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
@Table(name = "bank_card_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class BankcardRecord implements Serializable {

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
	 * 实收金额
	 */
	private Double actualIncome;

	private Date createTime;

	/**
	 * 结算时间
	 */
	private Date settlementTime;
	
	private Boolean availableFlag;//1表示成功

	private Double cardTotal;//卡结余

	private String merchantOrderNo;//外部商户订单号

	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;

	/**
	 * 手续费
	 */
	private Double serviceCharge;

	/**
     * 订单ID号
	 */
	@Column(name = "merchant_order_id", length = 32)
	private String merchantOrderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantOrder merchantOrder;

	/**
     * 商户ID号
	 */
	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;
//	/**
//	 * 最新收款码表
//	 */
//	@Column(name = "gathering_code_usage_id", length = 32)
//	private String gatheringCodeUsageId;
//	@NotFound(action = NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "gathering_code_usage_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	private GatheringCodeUsage gatheringCodeUsage;

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
	 * 备注信息
	 */
	@Column(name = "note", length = 320)
	private String note;



	public static BankcardRecord build(Double actualIncome, String merchantOrderId, String merchantId, Double serviceCharge, String merchantOrderNo, MerchantOrder merchantOrder) {
		BankcardRecord po = new BankcardRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setAvailableFlag(true);//1表示成功
		po.setActualIncome(actualIncome);//实际收款金额
		po.setMerchantOrderId(merchantOrderId);//订单号
		po.setMerchantId(merchantId);//商户号id
		po.setServiceCharge(serviceCharge);//手续费
		po.setMerchantOrderNo(merchantOrderNo);//商户订单号
		po.setGatheringCodeId(merchantOrder!=null?merchantOrder.getGatheringCodeId():"");//新的收款码 提现没有收款码
		//po.settlementTime();

				//settlementTime(new Date());//结算时间
		return po;
	}



}
