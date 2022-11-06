package me.zohar.runscore.merchant.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.zohar.runscore.dictconfig.ConfigHolder;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantPayOutOrder;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 付款对象
 */
@Data
public class MerchantPayOutOrderVO {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 订单号
	 */
	@Excel(name = "订单号")
	private String orderNo;

	private String gatheringChannelName;

	/**
	 * 收款金额
	 */
	private Double gatheringAmount;




	/**
	 * 有效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date usefulTime;

	/**
	 * 订单状态
	 */
	private String orderState;

	/**
	 * 商户订单号
	 */
	@Excel(name = "商户订单号")
	private String merchantOrderNo;

	/**
	 * 收款金额 界面展示
	 */
	@Excel(name = "收款金额")
	private String  gatheringAmountView;

	@Excel(name = "订单状态")
	private String orderStateName;

	private String platformId;

	/**
	 * 商户名称
	 */
	private String platformName;
	/**
	 * 商户号
	 */
	private String merchantNum;

	/**
	 * 接单人账号id
	 */
	private String receivedAccountId;

	/**
	 * 接单人用户名
	 */
	private String receiverUserName;

	/**
	 * 接单时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date receivedTime;
	
	private String gatheringCodeStorageId;

	/**
	 * 处理时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date dealTime;


	/**
	 * 费率
	 */
	private Double rate;

	/**
	 * 返点
	 */
	private Double rebate;

	/**
	 * 奖励金
	 */
	private Double bounty;

	private String payUrl;

	/**
	 * 银行卡号
	 */
	@Excel(name = "银行卡号")
	public String bankCardAccount;
	/**
	 * 银行卡姓名
	 */
	@Excel(name = "银行卡姓名")
	public String bankCardUserName;
	/**
	 * 银行名称
	 */
	@Excel(name = "银行名称")
	public String bankCardBankName;
	/**
	 * 备注
	 */
	@Excel(name = "备注")
	private String note;
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

	/**
	 * 系统来源
	 */
	private String systemSource;
	/**
	 * 银行卡总余额总结余用于界面展示
	 */
	private String  bankTotalView ;


	private MerchantOrderPayInfoVO payInfo;


	/**
	 * 收款人
	 */
	private String shoukuBankPayee;

	/**
	 * 收款银行卡号
	 */
	private String shoukuBankNumber;
	/**
	 * 收款银行名称
	 */
	private String shoukuBankName;
	/**
	 * 收款银行支行信息
	 */
	private String shoukuBankBranch;

	/**
	 * 付款银行卡号
	 * @param platformOrders
	 * @return
	 */
	private String fukuBankNumber;



	public static List<MerchantPayOutOrderVO> convertFor(List<MerchantPayOutOrder> platformOrders) {
		if (CollectionUtil.isEmpty(platformOrders)) {
			return new ArrayList<>();
		}
		List<MerchantPayOutOrderVO> vos = new ArrayList<>();
		for (MerchantPayOutOrder platformOrder : platformOrders) {
			vos.add(convertFor(platformOrder));
		}
		return vos;
	}

	public static MerchantPayOutOrderVO convertFor(MerchantPayOutOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		MerchantPayOutOrderVO vo = new MerchantPayOutOrderVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		if (merchantOrder.getMerchant() != null) {
			vo.setPlatformName(merchantOrder.getMerchant().getMerchantName());
			vo.setMerchantNum(merchantOrder.getMerchant().getMerchantNum());//商户号
		}
		if (StrUtil.isNotBlank(merchantOrder.getReceivedAccountId()) && merchantOrder.getReceivedAccount() != null) {
			vo.setReceiverUserName(merchantOrder.getReceivedAccount().getUserName());
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行卡号
			vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCode());//设置银行卡号
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行卡姓名
			vo.setBankCardUserName(merchantOrder.getGatheringCode().getBankUsername());//设置银行卡姓名
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行名称
			vo.setBankCardBankName(merchantOrder.getGatheringCode().getBankAddress());//设置银行卡姓名
		}
        vo.setMerchantOrderNo(merchantOrder.getOutOrderNo());//设置商户订单号
		String vv= DecimalFormatUtil.formatString(new BigDecimal(vo.gatheringAmount), null);
		vo.setGatheringAmountView(vv);//订单金额使用界面展示吧金额格式化了 "#,###.00"; 收款金额
		vo.setFukuBankNumber(merchantOrder.getFukuBankNumber());//付款银行卡号



//		String vv= DecimalFormatUtil.formatString(new BigDecimal(vo.gatheringAmount), null);
//		vo.setGatheringAmountView(vv);//订单金额使用界面展示吧金额格式化了 "#,###.00";
//		vo.setSystemSource(merchantOrder.getSystemSource());//系统来源
//		GatheringCode gatheringCode=merchantOrder.getGatheringCode();
//		if(gatheringCode!=null) {
//		Double vvTotal=	gatheringCode.getBankTotalAmount();
//		vo.setBankTotalView(DecimalFormatUtil.formatString(new BigDecimal(vvTotal), null));//银行卡总结余 用于界面展示
//		}
		return vo;
	}


	/**
	 * 代付转换数据
	 * @param merchantOrder
	 * @return
	 */
	public static MerchantPayOutOrderVO convertNewFor(MerchantPayOutOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		MerchantPayOutOrderVO vo = new MerchantPayOutOrderVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		//vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		if (merchantOrder.getMerchant() != null) {
			vo.setPlatformName(merchantOrder.getMerchant().getMerchantName());
			vo.setMerchantNum(merchantOrder.getMerchant().getMerchantNum());//商户号
		}
		if (StrUtil.isNotBlank(merchantOrder.getReceivedAccountId()) && merchantOrder.getReceivedAccount() != null) {
			vo.setReceiverUserName(merchantOrder.getReceivedAccount().getUserName());
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行卡号
			vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCode());//设置银行卡号
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行卡姓名
			vo.setBankCardUserName(merchantOrder.getGatheringCode().getBankUsername());//设置银行卡姓名
		}
		if(merchantOrder.getGatheringCode()!= null){//设置银行名称
			vo.setBankCardBankName(merchantOrder.getGatheringCode().getBankAddress());//设置银行卡姓名
		}
		vo.setMerchantOrderNo(merchantOrder.getOutOrderNo());//设置商户订单号
		String vv= DecimalFormatUtil.formatString(new BigDecimal(vo.gatheringAmount), null);
		vo.setGatheringAmountView(vv);//订单金额使用界面展示吧金额格式化了 "#,###.00"; 收款金额
		vo.setFukuBankNumber(merchantOrder.getFukuBankNumber());//付款银行卡号
		return vo;
	}


}
