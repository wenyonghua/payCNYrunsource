package me.zohar.runscore.merchant.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.dictconfig.ConfigHolder;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.merchant.domain.MerchantAutoOrder;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class MerchantAutoOrderVO {


	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 银行交易流水号
	 */
	@Excel(name = "银行交易流水号")
	private String bankFlow;

	/**
	 * 订单号
	 */
	@Excel(name = "订单号")
	private String orderNo;

	@Excel(name = "商户订单号")
	private String outOrderNo;//外部商户订单号

	/**
	 * 收款金额
	 */
	@Excel(name = "收款金额")
	private String gatheringAmount;

	/**
	 * 备注
	 */
	@Excel(name = "备注")
	private String note;

	/**
	 *银行卡号
	 */
	@Excel(name = "银行卡号")
	private String bankNumber;
	/**
	 * 银行卡总余额
	 */
	@Excel(name = "银行卡总余额")
	private String balance;

	@Excel(name = "自动机回调余额")
	private String autoAmount;



	/**
	 * 订单状态
	 */
	private String orderState;
	/**
	 * 订单状态
	 */
	@Excel(name = "订单状态")
	private String orderStateName;



	/**
	 * 系统来源
	 */
	@Excel(name = "系统来源")
	private String systemSource;



	/**
	 * 有效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date usefulTime;






	/**
	 * 备注
	 */
	@Excel(name = "备注")
	private String remark;

	/**
	 * 提交时间
	 */
	@Excel(name = "提交时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date submitTime;

	/**
	 * 确认时间
	 */
	@Excel(name = "完成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date confirmTime;

	@Excel(name = "自动机返回数据")
	private String returnMessage;//自动机返回的回调数据记录

	/**
	 * 银行完整附言信息
	 */
	@Excel(name = "银行完整附言信息")
	private String bankRemark;

	@Excel(name = "记录匹配失败错误数据")
	private String    saveFailError;

	/**
	 * 银行名称比如建设银行
	 */
	private String    bankName;
	/**
	 * 卡姓名比如张三
	 */
	private String    cardName;



	public static List<MerchantAutoOrderVO> convertFor(List<MerchantAutoOrder> platformOrders) {
		if (CollectionUtil.isEmpty(platformOrders)) {
			return new ArrayList<>();
		}
		List<MerchantAutoOrderVO> vos = new ArrayList<>();
		for (MerchantAutoOrder platformOrder : platformOrders) {
			vos.add(convertFor(platformOrder));
		}
		return vos;
	}

	public static MerchantAutoOrderVO convertFor(MerchantAutoOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		MerchantAutoOrderVO vo = new MerchantAutoOrderVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setOrderStateName(DictHolder.getDictItemName("merchantAutoOrderState", vo.getOrderState()));//自动机匹配状态

		if(merchantOrder.getGatheringAmount()!=null) {
			double dd = merchantOrder.getGatheringAmount();
			String vv = DecimalFormatUtil.formatString(new BigDecimal(dd), null);
			vo.setGatheringAmount(vv);//收款金额
		}

	      if(merchantOrder.getAutoAmount()!=null) {
			  vo.setAutoAmount(DecimalFormatUtil.formatString(new BigDecimal(merchantOrder.getAutoAmount()), null));//自动机回调金额
		  }
	      if(merchantOrder.getBalance()!=null) {
			  vo.setBalance(DecimalFormatUtil.formatString(new BigDecimal(merchantOrder.getBalance()), null));//银行卡总结余 用于界面展示
		  }
			  return vo;
	}




}
