package me.zohar.runscore.merchant.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.domain.BankcardRecord;
import me.zohar.runscore.util.DecimalFormatUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BankCardListReportVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 内部订单号
	 */
	@Excel(name = "内部订单号")
	private String orderNo;
	/**
	 * 外部商户订单号
	 */
	@Excel(name = "外部商户订单号")
	private String merchantOrderNo;


	/**
	 * 支付通道id
	 */
	private String payChannelId;

	/**
	 * 支付通道名称
	 */
	private String payChannelName;


	/**
	 * 充值金额
	 */
	@Excel(name = "充值金额")
	private Double rechargeAmount;

	/**
	 * 实际到账金额
	 */
	@Excel(name = "实际到账金额")
	private Double actualPayAmount;

    /**
	 * 手续费费
	 */
	@Excel(name = "手续费费")
	private Double serviceChange;
	/**
	 * 银行卡结余
	 */
	private Double cardTotal;
	/**
	 * 银行卡结余界面展示
	 */
	@Excel(name = "银行卡结余")
	private String cardTotalView;

	/**
	 * 创建时间
	 */
	@Excel(name = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date createTime;

	/**
	 * 结算时间,即更新到账号余额的时间
	 */
	@Excel(name = "结算时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date settlementTime;



	//@Column(name = "gathering_code_id", length = 32)
	private String gatheringCodeId;
	@Excel(name = "银行卡号")
	private String bankNum;//银行卡号

	//备注信息
	private String note;

	/**
	 * 银行存款总数
	 */
	private String bankDepositTotal;

	/**
	 * 银行付款总数
	 */
	private String bankPayoutTotal;

//	/**
//	 * 收款码
//	 */
//	@NotFound(action = NotFoundAction.IGNORE)
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "gathering_code_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//	private GatheringCode gatheringCode;







	/**
	 * 订单状态
	 */
	private String orderState;

	private String orderStateName;



	private String payCategory;

	/**
	 * 支付地址
	 */
	private String payUrl;

	/**
	 * 支付时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date payTime;

	/**
	 * 处理时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date dealTime;





	/**
	 * 存款时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+7")
	private Date depositTime;

	/**
	 * 存款人姓名
	 */
	private String depositor;

	private String bankName;

	/**
	 * 开户人姓名
	 */
	private String accountHolder;

	/**
	 * 银行卡账号
	 */
	private String bankCardAccount;







	public static List<BankCardListReportVO> convertFor(List<BankcardRecord> rechargeOrders) {
		if (CollectionUtil.isEmpty(rechargeOrders)) {
			return new ArrayList<>();
		}
		List<BankCardListReportVO> vos = new ArrayList<>();
		for (BankcardRecord rechargeOrder : rechargeOrders) {
			vos.add(convertFor(rechargeOrder));
		}
		return vos;
	}

	/**
     * 转换对象
	 * @param rechargeOrder
     * @return
     */
	public static BankCardListReportVO convertFor(BankcardRecord rechargeOrder) {
		if (rechargeOrder == null) {
			return null;
		}
		BankCardListReportVO vo = new BankCardListReportVO();
		//BeanUtils.copyProperties(rechargeOrder, vo);


		vo.setId(rechargeOrder.getId());//id 号
		vo.setOrderNo(rechargeOrder.getMerchantOrder()==null?rechargeOrder.getMerchantOrderId():rechargeOrder.getMerchantOrder().getOrderNo());//订单号
		vo.setMerchantOrderNo(rechargeOrder.getMerchantOrder()==null?rechargeOrder.getMerchantOrderNo():rechargeOrder.getMerchantOrder().getPayInfo().getOrderNo());//商户订单号
		vo.setRechargeAmount(rechargeOrder.getMerchantOrder()==null?rechargeOrder.getActualIncome():rechargeOrder.getMerchantOrder().getGatheringAmount());//充值金额

		vo.setActualPayAmount(rechargeOrder.getActualIncome());//实际到账金额
		vo.setServiceChange(rechargeOrder.getServiceCharge());//手续费
		vo.setCardTotal(rechargeOrder.getCardTotal());//商户结余
		vo.setOrderStateName(rechargeOrder.getMerchantOrder()==null?"提现完成":DictHolder.getDictItemName("merchantOrderState", rechargeOrder.getMerchantOrder().getOrderState()));//订单状态rechargeOrder.getMerchantOrder().getOrderState()

		vo.setGatheringCodeId(rechargeOrder.getMerchantOrder()==null?"":rechargeOrder.getMerchantOrder().getGatheringCodeId());//收款码id

		vo.setCreateTime(rechargeOrder.getCreateTime());//创建时间
		vo.setSettlementTime(rechargeOrder.getSettlementTime());//结束时间

		vo.setBankNum(rechargeOrder.getGatheringCode()==null?"":rechargeOrder.getGatheringCode().getBankCode());//银行卡号

		String vv= DecimalFormatUtil.formatString(new BigDecimal(rechargeOrder.getCardTotal()), null);
		vo.setCardTotalView(vv);//卡金额使用界面展示吧金额格式化了 "#,###";
		vo.setNote(rechargeOrder.getNote());//备注

		return vo;
	}



	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}
}
