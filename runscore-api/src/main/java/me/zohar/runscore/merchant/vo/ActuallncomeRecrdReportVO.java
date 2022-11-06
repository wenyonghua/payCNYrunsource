package me.zohar.runscore.merchant.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.merchant.domain.ActualIncomeRecord;
import me.zohar.runscore.merchant.repo.MerchantPayOutOrderRepo;
import me.zohar.runscore.rechargewithdraw.domain.RechargeOrder;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ActuallncomeRecrdReportVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 内部订单号
	 */
	@Excel(name = "订单号")
	private String orderNo;
	/**
	 * 外部商户订单号
	 */
	@Excel(name = "商户订单号")
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
	@Excel(name = "手续费")
	private Double serviceChange;
	/**
	 * 商户结余
	 */
	private Double merchantTotal;
	/**
	 * 商户结余使用界面展示
	 */
	@Excel(name = "商户结余")
	private String  merchantTotalView;
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
	private String gatheringCodeId;//收款码ID

	/**
	 * 商户号
	 */
	private String merchantNum;

	/**
	 * 接入商户名称
	 */
	private String merchantName;






	/**
	 * 订单状态
	 */
	private String orderState;

	private String orderStateName;

	/**
	 * 备注
	 */
	private String note;

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






	public static List<ActuallncomeRecrdReportVO> convertFor(List<ActualIncomeRecord> rechargeOrders) {
		if (CollectionUtil.isEmpty(rechargeOrders)) {
			return new ArrayList<>();
		}
		List<ActuallncomeRecrdReportVO> vos = new ArrayList<>();
		for (ActualIncomeRecord rechargeOrder : rechargeOrders) {
			vos.add(convertFor(rechargeOrder));
		}
		return vos;
	}

	/**
	 * 转换对象
	 * @param rechargeOrder
	 * @return
	 */
	public static ActuallncomeRecrdReportVO convertFor(ActualIncomeRecord rechargeOrder) {
		if (rechargeOrder == null) {
			return null;
		}
		ActuallncomeRecrdReportVO vo = new ActuallncomeRecrdReportVO();
		//BeanUtils.copyProperties(rechargeOrder, vo);

	    //System.out.println(rechargeOrder.getMerchantId());//商户ID号
		//System.out.println(rechargeOrder.getMerchantOrderId());//商户订单号

		vo.setId(rechargeOrder.getId());//id 号
		vo.setOrderNo(rechargeOrder.getMerchantOrderId()==null?"":rechargeOrder.getMerchantOrderId());//订单号
				//(rechargeOrder.getMerchantOrder()==null?rechargeOrder.getMerchantOrderId():rechargeOrder.getMerchantOrder().getOrderNo());//订单号
		vo.setMerchantOrderNo(rechargeOrder.getMerchantOrderNo()==null?"":rechargeOrder.getMerchantOrderNo());//商户订单号
		vo.setRechargeAmount(rechargeOrder.getActualIncome());////充值金额
				//(rechargeOrder.getMerchantOrder()==null?rechargeOrder.getActualIncome():rechargeOrder.getMerchantOrder().getGatheringAmount());//充值金额

		vo.setActualPayAmount(rechargeOrder.getActualIncome());//实际到账金额
		vo.setServiceChange(rechargeOrder.getServiceCharge());//手续费
		vo.setMerchantTotal(rechargeOrder.getMerchantTotal());//商户结余
		String vv= DecimalFormatUtil.formatString(new BigDecimal(rechargeOrder.getMerchantTotal()), null);
		vo.setMerchantTotalView(vv);//订单金额使用界面展示吧金额格式化了 "#,###.00";

        if(rechargeOrder.getPayoutFlag()!=null && "1".equals(rechargeOrder.getPayoutFlag())){//获取付款标记
			vo.setOrderStateName("代付成功");//代付成功
		}else {
			vo.setOrderStateName(rechargeOrder.getMerchantOrder() == null ? "商户提现完成" : DictHolder.getDictItemName("merchantOrderState", rechargeOrder.getMerchantOrder().getOrderState()));//订单状态rechargeOrder.getMerchantOrder().getOrderState()
		}
		vo.setGatheringCodeId(rechargeOrder.getGatheringCodeId()==null?"":rechargeOrder.getGatheringCodeId());//收款码id

		vo.setCreateTime(rechargeOrder.getCreateTime());//创建时间
		vo.setSettlementTime(rechargeOrder.getSettlementTime());//结束时间
       vo.setMerchantName(rechargeOrder.getMerchant()==null?"":rechargeOrder.getMerchant().getMerchantName());//商户名称
		vo.setMerchantNum(rechargeOrder.getMerchant()==null?"":rechargeOrder.getMerchant().getMerchantNum());//商户号


		return vo;
	}



	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}
}
