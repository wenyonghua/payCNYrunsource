package me.zohar.runscore.merchant.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import me.zohar.runscore.common.utils.HttpUtils;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.dictconfig.ConfigHolder;
import me.zohar.runscore.dictconfig.service.ConfigService;
import me.zohar.runscore.dictconfig.vo.ConfigItemVO;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.mastercontrol.domain.SystemSetting;
import me.zohar.runscore.mastercontrol.repo.SystemSettingRepo;
import me.zohar.runscore.mastercontrol.service.MasterControlService;
import me.zohar.runscore.merchant.domain.*;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.*;
import me.zohar.runscore.merchant.vo.*;
import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.domain.RechargeOrder;
import me.zohar.runscore.rechargewithdraw.param.RechargeOrderQueryCondParam;
import me.zohar.runscore.rechargewithdraw.repo.RechargeOrderRepo;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import me.zohar.runscore.rechargewithdraw.vo.RechargeOrderVO;
import me.zohar.runscore.useraccount.vo.MerchantAccountDetails;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;
import me.zohar.runscore.util.CheckIntValue;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.utils.ThreadPoolUtils;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.mastercontrol.domain.ReceiveOrderSetting;
import me.zohar.runscore.mastercontrol.repo.ReceiveOrderSettingRepo;
import me.zohar.runscore.useraccount.domain.AccountChangeLog;
import me.zohar.runscore.useraccount.domain.AccountReceiveOrderChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.AccountChangeLogRepo;
import me.zohar.runscore.useraccount.repo.AccountReceiveOrderChannelRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;

@Validated
@Slf4j
@Service
public class MerchantOrderService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;

	@Autowired
	private MerchantOrderPayInfoRepo merchantOrderPayInfoRepo;

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private ReceiveOrderSettingRepo receiveOrderSettingRepo;

	@Autowired
	private OrderRebateRepo orderRebateRepo;

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;

	@Autowired
	private GatheringChannelRateRepo gatheringChannelRateRepo;

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Autowired
	private ActualIncomeRecordRepo actualIncomeRecordRepo;
	@Autowired
	private GatheringCodeUsageRepo gatheringCodeUsageRepo;

	@Autowired
	private BankcardRepo bankcardRepo;//银行卡交易记录数据

	@Autowired
	private InsideWithdrawService insideWithdrawService;

	@Autowired
	private ConfigService configService;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Autowired
	private MerchantPayOutOrderRepo merchantPayOutOrderRepo;



	@Transactional
	public void timingPublishOrder() {
		List<MerchantOrder> orders = merchantOrderRepo.findByOrderStateAndSubmitTimeLessThan(Constant.商户订单状态_定时发单,
				new Date());
		for (MerchantOrder order : orders) {
			order.setOrderState(Constant.商户订单状态_等待接单);
			merchantOrderRepo.save(order);
		}

	}

	@Transactional(readOnly = true)
	public MerchantOrderDetailsVO findMerchantOrderDetailsById(@NotBlank String orderId) {
		MerchantOrderDetailsVO vo = MerchantOrderDetailsVO.convertFor(merchantOrderRepo.getOne(orderId));
		return vo;
	}

	/**
	 * 取消订单退款
	 * 
	 * @param orderId
	 */
	@Transactional
	public void cancelOrderRefund(@NotBlank String orderId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);
		if (!(Constant.商户订单状态_申诉中.equals(merchantOrder.getOrderState())
				|| Constant.商户订单状态_已接单.equals(merchantOrder.getOrderState()))) {
			throw new BizException(BizError.只有申诉中或已接单的商户订单才能进行取消订单退款操作);
		}
		UserAccount userAccount = merchantOrder.getReceivedAccount();
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() + merchantOrder.getGatheringAmount(), 4)
				.doubleValue();
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		merchantOrder.customerCancelOrderRefund();
		merchantOrderRepo.save(merchantOrder);
		accountChangeLogRepo.save(AccountChangeLog.buildWithCustomerCancelOrderRefund(userAccount, merchantOrder));
	}

	@Transactional(readOnly = true)
	public OrderGatheringCodeVO getOrderGatheringCode(@NotBlank String orderNo) {
		MerchantOrder order = merchantOrderRepo.findByOrderNo(orderNo);
		if (order == null) {
			log.error("商户订单不存在;orderNo:{}", orderNo);
			throw new BizException(BizError.商户订单不存在);
		}
		OrderGatheringCodeVO vo = OrderGatheringCodeVO.convertFor(order);
		vo.setOutOrderNo(order.getOutOrderNo());//外部商户订单号
		return vo;
	}

	@Transactional(readOnly = true)
	public String getGatheringCodeStorageId(String receivedAccountId, String gatheringChannelCode,
			Double gatheringAmount) {
		ReceiveOrderSetting merchantOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (merchantOrderSetting.getUnfixedGatheringCodeReceiveOrder()) {
			GatheringCode gatheringCode = gatheringCodeRepo
					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndFixedGatheringAmountFalseAndInUseTrue(
							receivedAccountId, gatheringChannelCode, Constant.收款码状态_正常);
			if (gatheringCode != null) {
				return gatheringCode.getStorageId();
			}
		} else {
			GatheringCode gatheringCode = gatheringCodeRepo
					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndGatheringAmountAndStateAndInUseTrue(
							receivedAccountId, gatheringChannelCode, gatheringAmount, Constant.收款码状态_正常);
			if (gatheringCode != null) {
				return gatheringCode.getStorageId();
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public GatheringCode getGatheringCodeStorage(String receivedAccountId, String gatheringChannelCode,
											Double gatheringAmount) {
		ReceiveOrderSetting merchantOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (merchantOrderSetting.getUnfixedGatheringCodeReceiveOrder()) {//接单设置
			GatheringCode gatheringCode = gatheringCodeRepo
					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndFixedGatheringAmountFalseAndInUseTrue(
							receivedAccountId, gatheringChannelCode, Constant.收款码状态_正常);
			if (gatheringCode != null) {
				return gatheringCode;
			}
		} else {
			GatheringCode gatheringCode = gatheringCodeRepo
					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndGatheringAmountAndStateAndInUseTrue(
							receivedAccountId, gatheringChannelCode, gatheringAmount, Constant.收款码状态_正常);
			if (gatheringCode != null) {
				return gatheringCode;
			}
		}
		return null;
	}

	/**
	 * 新写的方法用与查询GatheringCodeUsage
	 * @param receivedAccountId
	 * @param gatheringChannelCode
	 * @param gatheringAmount
	 * @return
	 */
	@Transactional(readOnly = true)
	public GatheringCodeUsage getGatheringCodeUsage(String receivedAccountId, String gatheringChannelCode,
													Double gatheringAmount) {
		ReceiveOrderSetting merchantOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (merchantOrderSetting.getUnfixedGatheringCodeReceiveOrder()) {//接单设置1是设置

			GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo
//					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndFixedGatheringAmountFalseAndInUseTrue(
//							receivedAccountId, gatheringChannelCode, Constant.收款码状态_正常);
					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndInUseTrue(receivedAccountId, gatheringChannelCode, Constant.收款码状态_正常);
			if (gatheringCodeUsage != null) {
				return gatheringCodeUsage;
			}
		} else {
			GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo
//					.findTopByUserAccountIdAndGatheringChannelChannelCodeAndGatheringAmountAndStateAndInUseTrue(
//							receivedAccountId, gatheringChannelCode, gatheringAmount, Constant.收款码状态_正常);
			.findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndInUseTrue(receivedAccountId, gatheringChannelCode, Constant.收款码状态_正常);
			if (gatheringCodeUsage != null) {
				return gatheringCodeUsage;
			}
		}
		return null;
	}


	/**
	 * 确认支付
	 * @param userAccountId
	 * @param orderId
	 * @param  textValuedesk 备注信息
	 */
	@Transactional
	public boolean userConfirmToPaid(@NotBlank String userAccountId, @NotBlank String orderId,@NotBlank String textValuedesk) {
		MerchantOrder merchantOrder=null;
		try {
			 merchantOrder = merchantOrderRepo.findByIdAndReceivedAccountId(orderId, userAccountId);
			if (merchantOrder == null) {
				throw new BizException(BizError.商户订单不存在);
			}
			if (!Constant.商户订单状态_已接单.equals(merchantOrder.getOrderState())) {
				throw new BizException(BizError.订单状态为已接单才能转为确认已支付);
			}

			List<ActualIncomeRecord> actualIncomeRecordList=actualIncomeRecordRepo.findByMerchantOrderNoEquals(merchantOrder.getOutOrderNo());//外部商户订单号查询订单
			if(actualIncomeRecordList!=null && actualIncomeRecordList.size()>0){//如果商户交易记录有这个订单就不能添加商户交易记录
				log.info("确认支付按钮,id为={},商户订单号={},商户存款记录有这条数据", orderId,merchantOrder.getOutOrderNo());
				return false;
			}

			merchantOrder.confirmToPaid(textValuedesk);//设置备注信息 和设置订单状态 orderState=4表示支付成功
			merchantOrderRepo.save(merchantOrder);//修改状态
			receiveOrderSettlement(merchantOrder);//记录商户交易记录
			generateCardBankIncomeRecord(merchantOrder);//添加银行卡交易记录数据 记录收款银行卡数据
           return true;
		}catch (Exception ex){
		    log.info("确认支付按钮,id为={},商户订单号={},异常错误={}", orderId,merchantOrder.getOutOrderNo(),ex);
			return false;
		}
	}

	/**
	 * 取消按钮以后的确定支付
	 * @param orderId
	 */
	@Transactional
	public boolean confirmToPaidWithCancelOrderRefund(@NotBlank String orderId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);
		try {
			if (merchantOrder == null) {
				throw new BizException(BizError.商户订单不存在);
			}
			if (!Constant.商户订单状态_取消订单退款.equals(merchantOrder.getOrderState())) {//判断是否是取消状态
				throw new BizException(BizError.只有状态为取消订单退款的订单才能更改状态为已支付);
			}

			UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			String textValuedesk = "操作人[" + user.getUsername() + "]";
			merchantOrder.confirmToPaid(textValuedesk);//设置备注信息 和设置订单状态 orderState=4表示支付成功
			merchantOrderRepo.save(merchantOrder);//修改状态
			receiveOrderSettlement(merchantOrder);//记录商户交易记录
			generateCardBankIncomeRecord(merchantOrder);//添加银行卡交易记录数据 记录收款银行卡数据
			return true;
		}catch (Exception ex){
			log.info("确认支付按钮,id为={},商户订单号={},异常错误={}", orderId,merchantOrder.getOutOrderNo(),ex);
			return false;
		}

//		UserAccount userAccount = userAccountRepo.getOne(merchantOrder.getReceivedAccountId());
//		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 4)
//				.doubleValue();
//		if (cashDeposit < 0) {
//			throw new BizException(BizError.保证金不足无法接单);
//		}
//
//		// 接单扣款
//		userAccount.setCashDeposit(cashDeposit);
//		userAccountRepo.save(userAccount);
//		merchantOrder.setOrderState(Constant.商户订单状态_已接单);
//		merchantOrderRepo.save(merchantOrder);
//		accountChangeLogRepo.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder));
//
//
//		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
//				.getPrincipal();
//		String textValuedesk="操作人["+user.getUsername()+"]";
//		// 确认已支付
//		userConfirmToPaid(userAccount.getId(), orderId,textValuedesk);



	}

	/**
	 * 客服确认已支付
	 * 
	 * @param orderId
	 */
	@Transactional
	public void customerServiceConfirmToPaid(@NotBlank String orderId, String note) {
		MerchantOrder platformOrder = merchantOrderRepo.findById(orderId).orElse(null);
		if (platformOrder == null) {
			throw new BizException(BizError.商户订单不存在);
		}
		if (!Constant.商户订单状态_申诉中.equals(platformOrder.getOrderState())) {
			throw new BizException(BizError.订单状态为申述中才能转为确认已支付);
		}
		platformOrder.confirmToPaid(note);
		merchantOrderRepo.save(platformOrder);
		receiveOrderSettlement(platformOrder);
	}

	/**
	 * 接单结算
	 */
	@Transactional
	public void receiveOrderSettlement(MerchantOrder merchantOrder) {
		UserAccount userAccount = merchantOrder.getReceivedAccount();//用户
		double bounty = NumberUtil.round(merchantOrder.getGatheringAmount() * merchantOrder.getRebate() * 0.01, 4)
				.doubleValue();
		merchantOrder.updateBounty(bounty);//奖励金
		merchantOrderRepo.save(merchantOrder);//奖励金

		userAccount.setCashDeposit(NumberUtil.round(userAccount.getCashDeposit() + bounty, 4).doubleValue());
		userAccountRepo.save(userAccount);

		accountChangeLogRepo
				.save(AccountChangeLog.buildWithReceiveOrderBounty(userAccount, bounty, merchantOrder.getRebate()));

		//generateOrderRebate(merchantOrder);//订单返点 2022-08-05 注释代码

		generateActualIncomeRecord(merchantOrder);//生成实收金额记录 生成商户交易记录数据


//		ThreadPoolUtils.getPaidMerchantOrderPool().schedule(() -> {
//			redisTemplate.opsForList().leftPush(Constant.商户订单ID, merchantOrder.getId());//添加数据放入缓存里面redis 用于回调使用
//		}, 1, TimeUnit.SECONDS);


	}

	/**
	 * 生成实收金额记录
	 * 
	 * @param merchantOrder
	 */
	@Transactional
	public void generateActualIncomeRecord(MerchantOrder merchantOrder) {
		double actualIncome = merchantOrder.getGatheringAmount() * (100 - merchantOrder.getRate()) / 100;//比如20(订单金额）*(100-1.8汇率）/100
		actualIncome = NumberUtil.round(actualIncome, 4).doubleValue();//扣除手续费后的金额

		double serveiceChange=NumberUtil.round(merchantOrder.getGatheringAmount()* (merchantOrder.getRate()/100),4).doubleValue();//手续费

		Merchant merchant = merchantRepo.getOne(merchantOrder.getMerchantId());//通过商户ID查询商户号
		if (merchant == null) {
			throw new BizException(BizError.商户号不存在);
		}
		double withdrawableAmount = merchant.getWithdrawableAmount() + actualIncome;//商户总余额=商户以前余额+实收金额
		merchant.setWithdrawableAmount(withdrawableAmount);//设置商户总金额
		//更新商户余额
		merchantRepo.save(merchant);//更新商户余额

		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(actualIncome, merchantOrder.getId(),
				merchantOrder.getMerchantId(),serveiceChange,merchantOrder.getPayInfo().getOrderNo(),merchantOrder,"0");
		Date now = new Date();
		long time = 2*1000;//2秒
		Date afterDate1 = new Date(now.getTime() + time);//2秒后的时间 延迟2秒
		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间结束时间 延迟2秒
		actualIncomeRecord.setMerchantTotal(withdrawableAmount);//结余总数据 更新商户总余额
		actualIncomeRecordRepo.save(actualIncomeRecord);// 添加商户交易记录数据


//
//		Specification<ActualIncomeRecord> spec = new Specification<ActualIncomeRecord>() {
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<ActualIncomeRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(merchantOrder.getMerchantId())){//商户ID号主键
//					predicates.add(builder.equal(root.get("merchantId"), merchantOrder.getMerchantId()));//商户ID号
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//
//		Page<ActualIncomeRecord> result = actualIncomeRecordRepo.findAll(spec,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime")))); //结束时间 通过商户ID号和完成时间查询数据
//
//
//	List<ActualIncomeRecord> actualIncomeRecordList =result.getContent();
//
//		Double merchantTotal=0.00;//结余
//       if(actualIncomeRecordList!=null && actualIncomeRecordList.size()>0){//查询商户交易列表有数据
//		   ActualIncomeRecord  actualIncomeRecord=actualIncomeRecordList.get(0);
//		    merchantTotal= NumberUtil.round(actualIncomeRecord.getMerchantTotal()+actualIncome, 4).doubleValue();//商户交易列表总数据=历史数据第一条+收到的金额
//		 }else{
//		   merchantTotal=actualIncome;//金额
//	   }
//


	}

	/**
	 * 生成订单返点
	 * 
	 * @param
	 */
	public void generateOrderRebate(MerchantOrder merchantOrder) {
		UserAccount userAccount = merchantOrder.getReceivedAccount();
		UserAccount superior = merchantOrder.getReceivedAccount().getInviter();
		while (superior != null) {
			// 管理员账号没有返点
			if (Constant.账号类型_管理员.equals(superior.getAccountType())) {
				break;
			}
			AccountReceiveOrderChannel userAccountRebate = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelId(userAccount.getId(), merchantOrder.getGatheringChannelId());
			AccountReceiveOrderChannel superiorRebate = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelId(superior.getId(), merchantOrder.getGatheringChannelId());
			if (superiorRebate == null) {
				log.error("上级账号没有开通该接单通道,无法获得返点;下级账号id:{},上级账号id:{},接单通道:{}", userAccount.getId(), superior.getId(),
						merchantOrder.getGatheringChannel().getChannelCode());
				break;
			}
			double rebate = NumberUtil.round(superiorRebate.getRebate() - userAccountRebate.getRebate(), 4)
					.doubleValue();
			if (rebate < 0) {
				log.error("订单返点异常,下级账号的返点不能大于上级账号;下级账号id:{},上级账号id:{}", userAccount.getId(), superior.getId());
				break;
			}
			double rebateAmount = NumberUtil.round(merchantOrder.getGatheringAmount() * rebate * 0.01, 4).doubleValue();
			OrderRebate orderRebate = OrderRebate.build(rebate, rebateAmount, merchantOrder.getId(), superior.getId());
			orderRebateRepo.save(orderRebate);
			userAccount = superior;
			superior = superior.getInviter();
		}
	}

	@Transactional(readOnly = true)
	public void orderRebateAutoSettlement() {
		List<OrderRebate> orderRebates = orderRebateRepo.findBySettlementTimeIsNullAndAvailableFlagTrue();
		for (OrderRebate orderRebate : orderRebates) {
			redisTemplate.opsForList().leftPush(Constant.订单返点ID, orderRebate.getId());
		}
	}

	/**
	 * 通知指定的订单进行返点结算
	 * 
	 * @param orderId
	 */
	@Transactional(readOnly = true)
	public void noticeOrderRebateSettlement(@NotBlank String orderId) {
		List<OrderRebate> orderRebates = orderRebateRepo.findByMerchantOrderIdAndAvailableFlagTrue(orderId);
		for (OrderRebate orderRebate : orderRebates) {
			redisTemplate.opsForList().leftPush(Constant.订单返点ID, orderRebate.getId());
		}
	}

//	/**
//	 * 查询商户交易记录数据
//	 */
//	@Transactional(readOnly = true)
//	public void actualIncomeRecordAutoSettlement() {
//		List<ActualIncomeRecord> actualIncomeRecords = actualIncomeRecordRepo
//				.findBySettlementTimeIsNullAndAvailableFlagTrue();//结束时间是空就放到redis里面去
//		for (ActualIncomeRecord actualIncomeRecord : actualIncomeRecords) {
//			redisTemplate.opsForList().leftPush(Constant.实收金额记录ID, actualIncomeRecord.getId());
//		}
//	}

	@Transactional(readOnly = true)
	public void noticeActualIncomeRecordSettlement(@NotBlank String orderId) {
		ActualIncomeRecord actualIncomeRecord = actualIncomeRecordRepo
				.findTopByMerchantOrderIdAndAvailableFlagTrue(orderId);
		if (actualIncomeRecord != null) {
			redisTemplate.opsForList().leftPush(Constant.实收金额记录ID, actualIncomeRecord.getId());
		}
	}


	@Transactional
	public void actualIncomeRecordSettlement111(@NotBlank String actualIncomeRecordId) {
		ActualIncomeRecord actualIncomeRecord = actualIncomeRecordRepo.getOne(actualIncomeRecordId);//商户交易记录
		if (actualIncomeRecord.getSettlementTime() != null) {
			log.warn("当前的实收金额记录已结算,无法重复结算;id:{}", actualIncomeRecordId);
			return;
		}

		Merchant merchant = merchantRepo.getOne(actualIncomeRecord.getMerchantId());
		double withdrawableAmount = merchant.getWithdrawableAmount() + actualIncomeRecord.getActualIncome();//商户余额+实收金额

		log.info("当前的实收金额记录已结算,无法重复结算;id:{},更新商户余额={}", actualIncomeRecordId,withdrawableAmount);
		merchant.setWithdrawableAmount(NumberUtil.round(withdrawableAmount, 4).doubleValue());//设置商户总余额

		actualIncomeRecord.setSettlementTime(new Date());//结束时间
		actualIncomeRecordRepo.save(actualIncomeRecord);//修改数据
		merchantRepo.save(merchant);//更新商户余额
	}

	/**
	 * 订单返点结算
	 */
	@Transactional
	public void orderRebateSettlement(@NotBlank String orderRebateId) {
		OrderRebate orderRebate = orderRebateRepo.getOne(orderRebateId);
		if (orderRebate.getSettlementTime() != null) {
			log.warn("当前的订单返点记录已结算,无法重复结算;id:{}", orderRebateId);
			return;
		}
		orderRebate.settlement();
		orderRebateRepo.save(orderRebate);
		UserAccount userAccount = orderRebate.getRebateAccount();
		double cashDeposit = userAccount.getCashDeposit() + orderRebate.getRebateAmount();
		userAccount.setCashDeposit(NumberUtil.round(cashDeposit, 4).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithOrderRebate(userAccount, orderRebate));
	}

	@Transactional(readOnly = true)
	public List<MyWaitConfirmOrderVO> findMyWaitConfirmOrder(@NotBlank String userAccountId) {
		return MyWaitConfirmOrderVO
				.convertFor(merchantOrderRepo.findByOrderStateInAndReceivedAccountIdOrderBySubmitTimeDesc(
						Arrays.asList(Constant.商户订单状态_已接单), userAccountId));
	}

	@Transactional(readOnly = true)
	public List<MyWaitReceivingOrderVO> findMyWaitReceivingOrder(@NotBlank String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		List<AccountReceiveOrderChannel> accountReceiveOrderChannels = accountReceiveOrderChannelRepo
				.findByUserAccountIdAndChannelDeletedFlagFalse(userAccountId);
		if (CollectionUtil.isEmpty(accountReceiveOrderChannels)) {
			throw new BizException(BizError.未设置接单通道无法接单);
		}
		ReceiveOrderSetting merchantOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (merchantOrderSetting.getShowAllOrder()) {
			List<MerchantOrder> specifiedOrders = merchantOrderRepo.findTop10ByOrderStateAndSpecifiedReceivedAccountId(
					Constant.商户订单状态_等待接单, userAccount.getUserName());
			List<MerchantOrder> waitReceivingOrders = merchantOrderRepo
					.findTop10ByOrderStateAndSpecifiedReceivedAccountIdIsNull(Constant.商户订单状态_等待接单);
			// Collections.shuffle(waitReceivingOrders);
			specifiedOrders.addAll(waitReceivingOrders);
			return MyWaitReceivingOrderVO
					.convertFor(specifiedOrders.subList(0, specifiedOrders.size() >= 10 ? 10 : specifiedOrders.size()));
		}
		if (merchantOrderSetting.getUnfixedGatheringCodeReceiveOrder()) {
			List<GatheringCode> gatheringCodes = gatheringCodeRepo.findByFixedGatheringAmount(false);
			if (CollectionUtil.isEmpty(gatheringCodes)) {
				throw new BizException(BizError.未设置收款码无法接单);
			}

			Map<String, String> gatheringChannelCodeMap = new HashMap<>();
			for (GatheringCode gatheringCode : gatheringCodes) {
				if (!Constant.收款码状态_正常.equals(gatheringCode.getState())) {
					continue;
				}
				if (!gatheringCode.getInUse()) {
					continue;
				}
				boolean flag = false;
				for (AccountReceiveOrderChannel accountReceiveOrderChannel : accountReceiveOrderChannels) {
					if (gatheringCode.getGatheringChannelId().equals(accountReceiveOrderChannel.getChannelId())
							&& Constant.账号接单通道状态_开启中.equals(accountReceiveOrderChannel.getState())) {
						flag = true;
					}
				}
				if (!flag) {
					continue;
				}
				gatheringChannelCodeMap.put(gatheringCode.getGatheringChannel().getChannelCode(),
						gatheringCode.getGatheringChannel().getChannelCode());
			}
			List<MerchantOrder> specifiedOrders = merchantOrderRepo.findTop10ByOrderStateAndSpecifiedReceivedAccountId(
					Constant.商户订单状态_等待接单, userAccount.getUserName());
			List<MerchantOrder> waitReceivingOrders = merchantOrderRepo
					.findTop10ByOrderStateAndGatheringAmountIsLessThanEqualAndGatheringChannelChannelCodeInAndAndSpecifiedReceivedAccountIdIsNullOrderBySubmitTimeDesc(
							Constant.商户订单状态_等待接单, userAccount.getCashDeposit(),
							new ArrayList<>(gatheringChannelCodeMap.keySet()));
			// Collections.shuffle(waitReceivingOrders);
			specifiedOrders.addAll(waitReceivingOrders);
			return MyWaitReceivingOrderVO
					.convertFor(specifiedOrders.subList(0, specifiedOrders.size() >= 10 ? 10 : specifiedOrders.size()));
		}
		List<GatheringCode> gatheringCodes = gatheringCodeRepo.findByFixedGatheringAmount(true);
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			throw new BizException(BizError.未设置收款码无法接单);
		}
		Map<String, List<Double>> gatheringChannelCodeMap = new HashMap<>();
		for (GatheringCode gatheringCode : gatheringCodes) {
			if (!Constant.收款码状态_正常.equals(gatheringCode.getState())) {
				continue;
			}
			if (!gatheringCode.getInUse()) {
				continue;
			}
			boolean flag = false;
			for (AccountReceiveOrderChannel accountReceiveOrderChannel : accountReceiveOrderChannels) {
				if (gatheringCode.getGatheringChannelId().equals(accountReceiveOrderChannel.getChannelId())
						&& Constant.账号接单通道状态_开启中.equals(accountReceiveOrderChannel.getState())) {
					flag = true;
				}
			}
			if (!flag) {
				continue;
			}
			if (userAccount.getCashDeposit() < gatheringCode.getGatheringAmount()) {
				continue;
			}
			if (gatheringChannelCodeMap.get(gatheringCode.getGatheringChannel().getChannelCode()) == null) {
				gatheringChannelCodeMap.put(gatheringCode.getGatheringChannel().getChannelCode(), new ArrayList<>());
			}
			gatheringChannelCodeMap.get(gatheringCode.getGatheringChannel().getChannelCode())
					.add(gatheringCode.getGatheringAmount());
		}
		List<MerchantOrder> waitReceivingOrders = new ArrayList<>();
		for (Entry<String, List<Double>> entry : gatheringChannelCodeMap.entrySet()) {
			if (CollectionUtil.isEmpty(entry.getValue())) {
				continue;
			}
			List<MerchantOrder> tmpOrders = merchantOrderRepo
					.findTop10ByOrderStateAndGatheringAmountInAndGatheringChannelChannelCodeAndSpecifiedReceivedAccountIdIsNullOrderBySubmitTimeDesc(
							Constant.商户订单状态_等待接单, entry.getValue(), entry.getKey());
			waitReceivingOrders.addAll(tmpOrders);
		}
		List<MerchantOrder> specifiedOrders = merchantOrderRepo
				.findTop10ByOrderStateAndSpecifiedReceivedAccountId(Constant.商户订单状态_等待接单, userAccount.getUserName());
		// Collections.shuffle(waitReceivingOrders);
		specifiedOrders.addAll(waitReceivingOrders);
		return MyWaitReceivingOrderVO
				.convertFor(specifiedOrders.subList(0, specifiedOrders.size() >= 10 ? 10 : specifiedOrders.size()));
	}

	/**
	 * 立即接单
	 * 
	 * @param userAccountId
	 * @param orderId
	 * @return
	 */
	//@Lock(keys = "'receiveOrder_' + #orderId")
	@Transactional
	public void receiveOrder(@NotBlank String userAccountId, @NotBlank String orderId) {


		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能接单);
		}
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);//查询订单
		if (merchantOrder == null) {
			throw new BizException(BizError.商户订单不存在);
		}
		if (!Constant.商户订单状态_等待接单.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.订单已被接或已取消);
		}
		String key="receiveOrder_order_"+orderId;//定义key
		String stringValue=redisTemplate.opsForValue().get(key);
		if(stringValue==null){
			redisTemplate.opsForValue().set(key, orderId, 180, TimeUnit.SECONDS);//放到redis里面去180秒3分钟自动在redis取消没有数据
		}else{
			throw new BizException(BizError.系统接过次单请等3分钟在接这个订单);
		}

		AccountReceiveOrderChannel receiveOrderChannel = accountReceiveOrderChannelRepo
				.findByUserAccountIdAndChannelId(userAccountId, merchantOrder.getGatheringChannelId());//通过用户ID和渠道ID号找到数据
		if (receiveOrderChannel == null) {
			throw new BizException(BizError.接单通道未开通);
		}
		if (Constant.账号接单通道状态_已禁用.equals(receiveOrderChannel.getState())) {
			throw new BizException(BizError.接单通道已禁用);
		}
		if (Constant.账号接单通道状态_关闭中.equals(receiveOrderChannel.getState())) {
			throw new BizException(BizError.接单通道已关闭);
		}
		// 校验若达到接单上限,则不能接单
		List<MyWaitConfirmOrderVO> waitConfirmOrders = findMyWaitConfirmOrder(userAccountId);
		if (waitConfirmOrders.size() >= receiveOrderSetting.getReceiveOrderUpperLimit()) {
			throw new BizException(BizError.已达到接单数量上限);
		}
//		// 禁止接重复单
//		if (receiveOrderSetting.getBanReceiveRepeatOrder()) {
//			for (MyWaitConfirmOrderVO waitConfirmOrder : waitConfirmOrders) {
//				if (waitConfirmOrder.getGatheringChannelId().equals(merchantOrder.getGatheringChannelId())
//						&& waitConfirmOrder.getGatheringAmount().compareTo(merchantOrder.getGatheringAmount()) == 0) {
//					throw new BizException(BizError.禁止接重复单);
//				}
//			}
//		}
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);//获取用户信息
		if (receiveOrderSetting.getCashDepositMinimumRequire() != null) {
			if (userAccount.getCashDeposit() < receiveOrderSetting.getCashDepositMinimumRequire()) {
				throw new BizException(BizError.未达到接单保证金最低要求);
			}
		}
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 4)
				.doubleValue();//保证金-收款金额
		if (cashDeposit - receiveOrderSetting.getCashPledge() < 0) {
			throw new BizException(BizError.保证金不足无法接单);
		}


		List<GatheringCode> atheringCodeList= gatheringCodeRepo.findByUserAccountIdAndCardUseAndInUseTrue(userAccountId,"1");//查询用户下面的收款卡必须使用的而且是存款卡状态用途是存款卡
       if(atheringCodeList.size()==0){//如果没有收款卡接单就提示错误
	    throw new BizException(BizError.无法接单找不到对应金额的收款码);
         }

       for(int i=0;i<atheringCodeList.size();i++){
		   GatheringCode gatheringCode= atheringCodeList.get(i);
       	   String id=gatheringCode.getId();//主键ID号
		   GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);//查看收款码收款数据
		   Double todayTradeAmount=gatheringCodeUsage.getTodayTradeAmount();//获取今日收款金额

		   Double dailQuotaAmount=0.00;//获取今日限额配置金额
		  String dailyQuota=gatheringCode.getDailyQuota();//获取数据今日配置限额
		  if(StrUtil.isBlank(dailyQuota)){//获取数据今日限额是空
			  dailQuotaAmount=0.00;
		   }else{
			  dailQuotaAmount=Double.valueOf(dailyQuota);
		  }
		  if(todayTradeAmount>=dailQuotaAmount){//今日收款金额大于今日配置限额就停止这个银行卡不用于收款
              //停用这个停用卡
			  gatheringCode.setInUse(false);//停用这个收款码
			  gatheringCodeRepo.save(gatheringCode);//停用这个银行卡
		  }
	   }

		GatheringCode gatheringCode = atheringCodeList.get((int) (Math.random() * atheringCodeList.size()));//随机产生一个银行卡用于收款使用


		log.info("获取银行卡信息,id为{},接单银行卡号={}", orderId,gatheringCode.getBankCode());
		userAccount.setCashDeposit(cashDeposit);//保证金
		userAccountRepo.save(userAccount);//扣除保证金

		Integer orderEffectiveDuration = receiveOrderSetting.getOrderPayEffectiveDuration();//支付有效时长
		merchantOrder.updateReceived(userAccount.getId(), gatheringCode, receiveOrderChannel.getRebate());//更新接单人
		//merchantOrder.updateGatheringCodeUsageReceived(userAccount.getId(), gatheringCodeUsage, receiveOrderChannel.getRebate());//更新新写的接单数据方法
		merchantOrder.updateUsefulTime(
				DateUtil.offset(merchantOrder.getReceivedTime(), DateField.MINUTE, orderEffectiveDuration));
		merchantOrderRepo.save(merchantOrder);//更新订单号

		accountChangeLogRepo.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder));//构建接单扣款日志
	}


	/**
	 * 查询订单列表数据
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<MerchantOrderVO> findMerchantOrderByPage(MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {//订单号
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantName())) {//商户名称
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("merchantName"),
							param.getMerchantName()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {//商户订单号外部订单号
					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("orderNo"),
							param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getGatheringChannelCode())) {
					predicates.add(builder.equal(root.join("gatheringChannel", JoinType.INNER).get("channelCode"),
							param.getGatheringChannelCode()));
				}

				if (StrUtil.isNotBlank(param.getBankCardAccount())) {//查询银行卡数据
					String cardAccount=	param.getBankCardAccount();
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.join("gatheringCode", JoinType.INNER).get("bankCode");//定义查询的字
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//存入值
					}
					predicates.add(builder.and(builder.and(in)));//存入条件集合里
				  }//查询银行卡数据


				if (StrUtil.isNotBlank(param.getOrderState())) {//订单状态
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getReceiverUserName())) {
					predicates.add(builder.equal(root.join("receivedAccount", JoinType.INNER).get("userName"),
							param.getReceiverUserName()));//接单人
				}
				if (param.getSubmitStartTime() != null) {
					//System.out.println("开始时间="+param.getSubmitStartTime());
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));
				}
				if (param.getSubmitEndTime() != null) {
					//System.out.println("结束时间="+param.getSubmitEndTime());
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));
				}
				if (param.getReceiveOrderStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.beginOfDay(param.getReceiveOrderStartTime())));
				}
				if (param.getReceiveOrderEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.endOfDay(param.getReceiveOrderEndTime())));
				}
				if (StrUtil.isNotBlank(param.getNote())) {//备注信息
					predicates.add(builder.equal(root.get("note"), param.getNote()));
				}
				if (StrUtil.isNotBlank(param.getSystemSource())) {//系统来源
					predicates.add(builder.equal(root.get("systemSource"), param.getSystemSource()));//系统来源
				}
				if (StrUtil.isNotBlank(param.getPayFukuanName())) {//付款人员
					predicates.add(builder.equal(root.get("payFukuanName"), param.getPayFukuanName()));//付款人员
				}

				if (StrUtil.isNotBlank(param.getAttch())) {//附加信息附言
					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("attch"),
							param.getAttch()));//附加信息附言
				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<MerchantOrderVO> pageResult = new PageResult<>(MerchantOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<ReceiveOrderRecordVO> findMyReceiveOrderRecordByPage(MyReceiveOrderRecordQueryCondParam param) {
		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getGatheringChannelCode())) {
					predicates.add(builder.equal(root.join("gatheringChannel", JoinType.INNER).get("channelCode"),
							param.getGatheringChannelCode()));
				}
				if (StrUtil.isNotBlank(param.getReceiverUserName())) {
					predicates.add(builder.equal(root.join("receivedAccount", JoinType.INNER).get("userName"),
							param.getReceiverUserName()));
				}
				if (param.getReceiveOrderTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.beginOfDay(param.getReceiveOrderTime())));
					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.endOfDay(param.getReceiveOrderTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<ReceiveOrderRecordVO> pageResult = new PageResult<>(
				ReceiveOrderRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	/**
	 * 取消订单
	 * 
	 * @param orderId
	 * @param note
	 */
	@Transactional
	public void cancelOrder(String orderId,String note) {
		MerchantOrder platformOrder = merchantOrderRepo.getOne(orderId);
		if(platformOrder==null){
			throw new BizException("9012","订单为空!");
		}
		platformOrder.setOrderState(Constant.商户订单状态_取消订单退款);
		platformOrder.setDealTime(new Date());
		platformOrder.setNote(note);//备注消息
		platformOrder.setConfirmTime(new Date());//确认时间
		platformOrder.setDealTime(new Date());//处理时间

//		merchantOrder.customerCancelOrderRefund();
//		merchantOrderRepo.save(merchantOrder);
		merchantOrderRepo.save(platformOrder);
	}

	@Transactional
	public void forceCancelOrder(@NotBlank String id) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		if (!Constant.商户订单状态_已支付.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.只有已支付状态的商户订单才能强制取消);
		}
		merchantOrder.customerCancelOrderRefund();
		merchantOrderRepo.save(merchantOrder);
		UserAccount userAccount = merchantOrder.getReceivedAccount();
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() + merchantOrder.getGatheringAmount(), 4)
				.doubleValue();
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithCustomerCancelOrderRefund(userAccount, merchantOrder));
		cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getBounty(), 4).doubleValue();
		if (cashDeposit < 0) {
			throw new BizException(BizError.保证金不足);
		}
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		accountChangeLogRepo
				.save(AccountChangeLog.buildWithCustomerCancelOrderDeductBounty(userAccount, merchantOrder));
		List<OrderRebate> orderRebates = orderRebateRepo.findByMerchantOrderIdAndAvailableFlagTrue(id);
		for (OrderRebate orderRebate : orderRebates) {
			orderRebate.setAvailableFlag(false);
			orderRebateRepo.save(orderRebate);
			UserAccount superiorAccount = orderRebate.getRebateAccount();
			Double superiorAccountCashDeposit = NumberUtil
					.round(superiorAccount.getCashDeposit() - orderRebate.getRebateAmount(), 4).doubleValue();
			if (superiorAccountCashDeposit < 0) {
				throw new BizException(BizError.保证金不足);
			}
			superiorAccount.setCashDeposit(superiorAccountCashDeposit);
			userAccountRepo.save(superiorAccount);
			accountChangeLogRepo
					.save(AccountChangeLog.buildWithCustomerCancelOrderDeductRebate(superiorAccount, orderRebate));
		}

		ActualIncomeRecord actualIncomeRecord = actualIncomeRecordRepo.findTopByMerchantOrderIdAndAvailableFlagTrue(id);
		if (actualIncomeRecord != null) {
			actualIncomeRecord.setAvailableFlag(false);
			actualIncomeRecordRepo.save(actualIncomeRecord);
			Merchant merchant = actualIncomeRecord.getMerchant();
			Double withdrawableAmount = NumberUtil
					.round(merchant.getWithdrawableAmount() - actualIncomeRecord.getActualIncome(), 4).doubleValue();
			if (withdrawableAmount < 0) {
				throw new BizException(BizError.可提现金额不足);
			}
			merchant.setWithdrawableAmount(withdrawableAmount);
			merchantRepo.save(merchant);
		}
	}

	@Transactional
	public void orderTimeoutDeal() {
		Date now = new Date();
		List<MerchantOrder> orders = merchantOrderRepo.findByOrderStateAndUsefulTimeLessThan(Constant.商户订单状态_等待接单, now);
		for (MerchantOrder order : orders) {
			order.setDealTime(now);
			order.setOrderState(Constant.商户订单状态_超时取消);
			merchantOrderRepo.save(order);
		}
	}

	/**
	 * 管理员添加订单数据 测试使用
	 * @param params
	 */
	@Transactional
	public void manualStartOrder(@NotEmpty List<ManualStartOrderParam> params) {
		for (ManualStartOrderParam param : params) {
			Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());
			if (merchant == null) {
				throw new BizException(BizError.商户未接入);
			}
			String amount = new DecimalFormat("###################.###########").format(param.getGatheringAmount());
			StartOrderParam startOrderParam = new StartOrderParam();
			startOrderParam.setMerchantNum(param.getMerchantNum());//商户号
			startOrderParam.setOrderNo(param.getOrderNo());//商户订单号
			startOrderParam.setPayType(param.getGatheringChannelCode());//渠道名称
			startOrderParam.setAmount(amount);//金额
			startOrderParam.setNotifyUrl(param.getNotifyUrl());//支付地址
			startOrderParam.setReturnUrl(param.getReturnUrl());//回调地址
			startOrderParam.setAttch(param.getAttch());//随机数
			startOrderParam.setSpecifiedReceivedAccountId(param.getSpecifiedReceivedAccountId());
			startOrderParam.setPublishTime(param.getPublishTime());
			String systemSorce=param.getSystemSource();//系统来源
			if(systemSorce==null){
				systemSorce="Pay247";//如果没有系统来源就默认Pay247
			}
			startOrderParam.setSystemSorce(systemSorce);//设置系统系统来源

			String sign = startOrderParam.getMerchantNum() + startOrderParam.getOrderNo() + amount
					+ param.getNotifyUrl() + merchant.getSecretKey();//商户号+订单号+金额+回调地址+秘钥
			sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
			startOrderParam.setSign(sign);//签名
			startOrder(startOrderParam);
		}

	}

	@ParamValid
	@Transactional
	public StartOrderSuccessVO startOrder(StartOrderParam param) {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能发起订单);
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException(BizError.金额格式不正确);
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException(BizError.金额不能小于或等于0);
		}


		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException(BizError.签名不正确);
		}
		//系统设置的通道
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());
		if (gatheringChannel == null) {
			throw new BizException(BizError.发起订单失败通道不存在);
		}
		if (!gatheringChannel.getEnabled()) {
			throw new BizException(BizError.发起订单失败通道维护中);
		}
		//查询商户所设置的通道费率，以及通道是否开放
		GatheringChannelRate gatheringChannelRate = gatheringChannelRateRepo
				.findByMerchantIdAndChannelChannelCode(merchant.getId(), param.getPayType());
		if (gatheringChannelRate == null) {
			throw new BizException(BizError.发起订单失败该通道未开通);
		}
		if (!gatheringChannelRate.getEnabled()) {
			throw new BizException(BizError.发起订单失败该通道已关闭);
		}

		Integer orderEffectiveDuration = receiveOrderSetting.getReceiveOrderEffectiveDuration();
		MerchantOrder merchantOrder = param.convertToPo(merchant.getId(), gatheringChannel.getId(),
				gatheringChannelRate.getRate(), orderEffectiveDuration,param.getOrderNo());
		MerchantOrderPayInfo payInfo = param.convertToPayInfoPo(merchantOrder.getId());
		merchantOrder.setPayInfoId(payInfo.getId());//设置子订单
		//SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		merchantOrder.setSystemSource(param.getSystemSorce());//获取系统来源
		merchantOrderRepo.save(merchantOrder);//保存主订单
		merchantOrderPayInfoRepo.save(payInfo);//保存订单

		ConfigItemVO configItemVO=null;
		StartOrderSuccessVO vo = new StartOrderSuccessVO();
		if("FastPay".equals(param.getSystemSorce())){//FastPay 渠道
			configItemVO = configService.findConfigItemByConfigCode("FastPaymerchantOrderPayUrl");//FastPay 渠道支付地址
		}else{
			configItemVO = configService.findConfigItemByConfigCode("merchantOrderPayUrl");//Pay247 渠道支付地址
		}
		vo.setPayUrl( configItemVO.getConfigValue()+ merchantOrder.getOrderNo());//支付地址
		vo.setBillNo(merchantOrder.getOrderNo());//订单号
		return vo;
				//StartOrderSuccessVO.convertFor(merchantOrder.getOrderNo());
	}

	/**
	 * 支付成功异步通知
	 * 
	 * @param merchantOrderId
	 */
	@Transactional
	public String paySuccessAsynNotice(@NotBlank String merchantOrderId) {
		MerchantOrderPayInfo payInfo = merchantOrderPayInfoRepo.findByMerchantOrderId(merchantOrderId);
		if (payInfo == null) {
			return "商户订单号不存在";
		}
		if (Constant.商户订单支付通知状态_通知成功.equals(payInfo.getNoticeState())) {//通知状态 1未通知 2通知成功 3通知失败
			log.warn("商户订单支付已通知成功,无需重复通知;商户订单id为{}", merchantOrderId);
			return Constant.商户订单通知成功返回值;
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(payInfo.getMerchantNum());//找到商户信息
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}

		String sign = Constant.商户订单支付成功 + payInfo.getMerchantNum() + payInfo.getOrderNo() + payInfo.getAmount()
				+ merchant.getSecretKey();//签名串
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("merchantNum", payInfo.getMerchantNum());//商户号
		paramMap.put("orderNo", payInfo.getOrderNo());//渠道订单订单号
		paramMap.put("amount", payInfo.getAmount());
		paramMap.put("state", Constant.商户订单支付成功);//1是支付成功
		//paramMap.put("payTime",DateUtil.format(payInfo.getMerchantOrder().getConfirmTime(), DatePattern.NORM_DATETIME_PATTERN));
		paramMap.put("sign", sign);
		String result = "fail";
		// 通知3次
		for (int i = 0; i < 3; i++) {
			try {
				result = HttpUtils.postwayform(payInfo.getNotifyUrl(),paramMap, "UTF-8");
						//HttpUtil.get(payInfo.getNotifyUrl(), paramMap, 102500);//调用商户的回调地址
				log.info("回调订单号:{},渠道订单号:{},回调请求地址:{},回调请求参数:{},产品返回数据结果={}",merchantOrderId,payInfo.getOrderNo(),payInfo.getNotifyUrl(),paramMap,result);
				if (Constant.商户订单通知成功返回值.equals(result)) {
					break;
				}
			} catch (Exception e) {
				result = e.getMessage();
				log.error(MessageFormat.format("商户订单支付成功异步通知地址请求异常,id为{0}", merchantOrderId), e);
				log.info("回调订单号:{},回调请求地址:{},回调请求参数:{},通知对方返回参数:{},通知异常:{}",merchantOrderId,payInfo.getNotifyUrl(),paramMap,result,e);
			}
		}
		payInfo.setNoticeState(
				Constant.商户订单通知成功返回值.equals(result) ? Constant.商户订单支付通知状态_通知成功 : Constant.商户订单支付通知状态_通知失败);
		merchantOrderPayInfoRepo.save(payInfo);//记录数据
		return result;
	}

	@Transactional(readOnly = true)
	public PageResult<ReceiveOrderRecordVO> findLowerLevelAccountReceiveOrderRecordByPage(
			LowerLevelAccountReceiveOrderQueryCondParam param) {
		UserAccount currentAccount = userAccountRepo.getOne(param.getCurrentAccountId());
		UserAccount lowerLevelAccount = currentAccount;
		if (StrUtil.isNotBlank(param.getUserName())) {
			lowerLevelAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
			if (lowerLevelAccount == null) {
				throw new BizException(BizError.用户名不存在);
			}
			// 说明该用户名对应的账号不是当前账号的下级账号
			if (!lowerLevelAccount.getAccountLevelPath().startsWith(currentAccount.getAccountLevelPath())) {
				throw new BizException(BizError.不是上级账号无权查看该账号及下级的接单记录);
			}
		}
		String lowerLevelAccountId = lowerLevelAccount.getId();
		String lowerLevelAccountLevelPath = lowerLevelAccount.getAccountLevelPath();

		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(builder.equal(root.get("receivedAccountId"), lowerLevelAccountId));
				} else {
					predicates.add(builder.like(root.join("receivedAccount", JoinType.INNER).get("accountLevelPath"),
							lowerLevelAccountLevelPath + "%"));
				}
				if (StrUtil.isNotBlank(param.getOrderState())) {
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getGatheringChannelCode())) {
					predicates.add(builder.equal(root.join("gatheringChannel", JoinType.INNER).get("channelCode"),
							param.getGatheringChannelCode()));
				}
				if (param.getStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.beginOfDay(param.getStartTime())));
				}
				if (param.getEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.endOfDay(param.getEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<ReceiveOrderRecordVO> pageResult = new PageResult<>(
				ReceiveOrderRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void updateNote(@NotBlank String id, String note, String merchantId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		if (!merchantOrder.getMerchantId().equals(merchantId)) {
			throw new BizException(BizError.无权修改商户订单备注);
		}
		updateNoteInner(id, note);
	}

	@Transactional
	public void updateNoteInner(@NotBlank String id, String note) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		merchantOrder.setNote(note);
		merchantOrderRepo.save(merchantOrder);
	}

	/**
	 * 付款添加备注数据
	 * @param id
	 * @param note
	 */
	@Transactional
	public void updatePayOutNoteInner(@NotBlank String id, String note) {
		MerchantPayOutOrder merchantOrder = merchantPayOutOrderRepo.getOne(id);
		merchantOrder.setNote(note);
		merchantPayOutOrderRepo.save(merchantOrder);
	}

	/**
	 * 修改payFukuanName
	 * @param id
	 * @param chunkuan
	 */
	@Transactional
	public void updatePayFukuanName(@NotBlank String id, String chunkuan) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		merchantOrder.setPayFukuanName(chunkuan);
		merchantOrderRepo.save(merchantOrder);
	}





	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	/**
	 * 商户取消订单
	 *
	 * @param id
	 */
	@Transactional
	public void merchatCancelOrder(@NotBlank String merchantId, @NotBlank String id) {
		MerchantOrder platformOrder = merchantOrderRepo.getOne(id);
		if (!merchantId.equals(platformOrder.getMerchantId())) {
			throw new BizException(BizError.无权取消订单);
		}
		if (!Constant.商户订单状态_等待接单.equals(platformOrder.getOrderState())) {
			throw new BizException(BizError.只有等待接单状态的商户订单才能取消);
		}
		platformOrder.setOrderState(Constant.商户订单状态_商户取消订单);
		platformOrder.setDealTime(new Date());
		merchantOrderRepo.save(platformOrder);
	}


	/**
	 * 商户号交易列表
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<ActuallncomeRecrdReportVO> merchantPaylist(MerchantPaylistParam param) {

		Specification<ActualIncomeRecord> spec = new Specification<ActualIncomeRecord>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<ActualIncomeRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getMerchantId())) {//商户ID号主键
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));//商户ID号
				}

				if (StrUtil.isNotBlank(param.getOrderNo())) {//订单号
//					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("orderNo"),
//							param.getMerchantOrderNo()));
					predicates.add(builder.equal(root.join("merchantOrder", JoinType.INNER).get("orderNo"),
							param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {//外部商户订单号
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));//外部商户号
				}
//				if (StrUtil.isNotBlank(param.getOrderState())) {//订单状态
//					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
//				}
				if (param.getSubmitStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));//开始时间
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));//结束时间
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<ActualIncomeRecord> result = actualIncomeRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));//查询商户交易列表数据


		PageResult<ActuallncomeRecrdReportVO> pageResult = new PageResult<>(ActuallncomeRecrdReportVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}


	/**
	 * 银行卡交易列表
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult1<BankCardListReportVO> merchantBankListPaylist(MerchantPaylistParam param) {

		Specification<BankcardRecord> spec = new Specification<BankcardRecord>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<BankcardRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
//				if (StrUtil.isNotBlank(param.getMerchantId())) {//商户ID号主键
//					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));//商户ID号
//				}

				if (StrUtil.isNotBlank(param.getOrderNo())) {//订单号
//					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("orderNo"),
//							param.getMerchantOrderNo()));
					predicates.add(builder.equal(root.join("merchantOrder", JoinType.INNER).get("orderNo"),
							param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {//外部商户订单号
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));//外部商户号
				}
				if(StrUtil.isNotBlank(param.getBankNum())){//银行卡号
					predicates.add(builder.equal(root.join("gatheringCode", JoinType.INNER).get("bankCode"),
							param.getBankNum()));//通过银行卡查询
				}

				if (StrUtil.isNotBlank(param.getOrderState())) {//订单状态
					predicates.add(builder.equal(root.join("merchantOrder", JoinType.INNER).get("orderState"),
							param.getOrderState()));
				}
				if (param.getSubmitStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));//开始时间
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));//结束时间
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};

		Page<BankcardRecord> result = bankcardRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));

		Double depositTotal=0.00;//存款

		Double payoutTotal=0.00;//付款


		if(StrUtil.isBlank(param.getBankNum()) &&  param.getSubmitStartTime()==null && param.getSubmitEndTime()==null) {//银行卡号和开始时间和结束时间是空就查询全部
			depositTotal = bankcardRepo.findDepositByAll();//查询存款数据总金额
			payoutTotal = bankcardRepo.findPayoutByAll();//查询付款总金额

		}
		if(StrUtil.isNotBlank(param.getBankNum()) &&  param.getSubmitStartTime()==null && param.getSubmitEndTime()==null) {//通过银行卡号查询
			depositTotal = bankcardRepo.findDepositByBanKCode(param.getBankNum());//查询存款数据总金额
			payoutTotal = bankcardRepo.findPayoutByBanKCode(param.getBankNum());//查询付款总金额
		}

		if(StrUtil.isNotBlank(param.getBankNum()) && param.getSubmitStartTime() != null && param.getSubmitEndTime() != null) {//通过银行卡号查询和开始时间和结束时间查询
		     String startTime=DateUtil.beginOfDay(param.getSubmitStartTime()).toString();//开始时间
			String endTime=DateUtil.endOfDay(param.getSubmitEndTime()).toString();//结束时间
			depositTotal = bankcardRepo.findDepositByBanKCodeAndStartTimeAndEneTime(param.getBankNum(),startTime,endTime);//查询存款数据总金额
			payoutTotal = bankcardRepo.findPayoutByBanKCodeAndStartTimeAndEneTime(param.getBankNum(),startTime,endTime);//查询付款总金额
		}

		if(StrUtil.isBlank(param.getBankNum()) && param.getSubmitStartTime() != null && param.getSubmitEndTime() != null) {//开始时间和结束时间查询数据
			String startTime=DateUtil.beginOfDay(param.getSubmitStartTime()).toString();//开始时间
			String endTime=DateUtil.endOfDay(param.getSubmitEndTime()).toString();//结束时间
			depositTotal = bankcardRepo.findDepositByStartTimeAndEneTime(startTime,endTime);//查询存款数据总金额
			payoutTotal = bankcardRepo.findPayoutByStartTimeAndEneTime(startTime,endTime);//查询付款总金额
		}
		if(depositTotal==null){//存款金额
			depositTotal=0.00;
		}
		if(payoutTotal==null){//付款金额
			payoutTotal=0.00;
		}
		PageResult1<BankCardListReportVO> pageResult = new PageResult1<>(BankCardListReportVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements(),DecimalFormatUtil.formatString(new BigDecimal(depositTotal), null),DecimalFormatUtil.formatString(new BigDecimal(payoutTotal), null));

		return pageResult;
	}


	/**
	 * 生成银行卡交易记录
	 *
	 * @param merchantOrder
	 */
	@Transactional(readOnly = true)
	public void generateCardBankIncomeRecord(MerchantOrder merchantOrder) {

		double actualIncome = merchantOrder.getGatheringAmount() ;//收款金额
		actualIncome = NumberUtil.round(actualIncome, 4).doubleValue();
		Double banktotalAmount=0.00;
		String gatheringcodeid=merchantOrder.getGatheringCodeId();
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(gatheringcodeid);//获取收款码
		if(gatheringCode==null){
			throw new BizException(BizError.收款码不存在);
		}
			 banktotalAmount=gatheringCode.getBankTotalAmount()+actualIncome;//银行卡的总额度
			//banktotalAmount=banktotalAmount+actualIncome;//银行卡的总额度+收款卡的金额
			gatheringCode.setBankTotalAmount(banktotalAmount);//设置银行卡的总额度
			if(banktotalAmount>=gatheringCode.getBankReflect()){//如果卡的额度大于等于卡的提现额度设置就需要添加一个银行卡的内部转折记录数据
				List<InsidewithdrawRecord> insidewithdrawRecordList=insideWithdrawService.findByBankCardAccountAndState(gatheringCode.getBankCode(),Constant.提现记录状态_发起提现);
				log.info("查询内部转账记录数据;商户订单id={},银行卡号={},状态值={},查询结果数据={}", merchantOrder.getOrderNo(),gatheringCode.getBankCode(),Constant.提现记录状态_发起提现,insidewithdrawRecordList.size());
				if(insidewithdrawRecordList.size()==0) {//查询如果没有内部转账记录就需要添加内部转账记录数据
				   InsidewithdrawRecord insidewithdrawRecord = new InsidewithdrawRecord();
				   insidewithdrawRecord.setId(IdUtils.getId());//订单号
				   insidewithdrawRecord.setWithdrawAmount(0.00);//提现金额
				   insidewithdrawRecord.setSubmitTime(new Date());//提交时间
				   insidewithdrawRecord.setOrderNo(insidewithdrawRecord.getId());//订单号
				   insidewithdrawRecord.setWithdrawWay(Constant.提现方式_银行卡);//提现方式
				   insidewithdrawRecord.setState(Constant.提现记录状态_发起提现);
				   insidewithdrawRecord.setAccountHolder(gatheringCode.getPayee());//姓名
				   insidewithdrawRecord.setBankCardAccount(gatheringCode.getBankCode());//账号1234567
				   insidewithdrawRecord.setOpenAccountBank(gatheringCode.getBankAddress());//开户银行 银行名称
				   insidewithdrawRecord.setGatheringCodeId(gatheringCode.getId());//设置卡
				   insideWithdrawService.startInsideWithdrawWithVirtualWallet(insidewithdrawRecord);//添加内部转折记录
			   }
			}
			gatheringCodeRepo.save(gatheringCode);//更新银行卡数据

		double serveiceChange=0.00;//手续费
		BankcardRecord bankcardRecord = BankcardRecord.build(actualIncome, merchantOrder.getId(),
				merchantOrder.getMerchantId(),serveiceChange,merchantOrder.getPayInfo().getOrderNo(),merchantOrder);

		bankcardRecord.setCardTotal(banktotalAmount);//银行卡总数据
		Date now = new Date();
		long time = 2*1000;//2秒
		Date afterDate1 = new Date(now.getTime() + time);//2秒后的时间 延迟2秒
		bankcardRecord.setSettlementTime(afterDate1);//结束时间结束时间 延迟2秒
		bankcardRepo.save(bankcardRecord);//银行卡交易列表


//		Specification<BankcardRecord> spec = new Specification<BankcardRecord>() {
//			/**
//			 *
//			 */
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<BankcardRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(merchantOrder.getGatheringCode().getBankCode())){//银行卡号
//					predicates.add(builder.equal(root.join("gatheringCode", JoinType.INNER).get("bankCode"),
//							merchantOrder.getGatheringCode().getBankCode()));//通过银行卡查询
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//		Page<BankcardRecord> bankcardComeRecrdReportVOList = bankcardRepo.findAll(spec,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime"))));//查询银行卡的结束时间
//		Double cardTotal=0.00;//结余
//		List<BankcardRecord> BankcardRecordList=bankcardComeRecrdReportVOList.getContent();
//		if(BankcardRecordList!=null && BankcardRecordList.size()>0){//查询有数据
//				BankcardRecord  BankcardRecord=BankcardRecordList.get(0);//获取第一条数据
//			cardTotal= BankcardRecord.getCardTotal()+actualIncome;//银行卡的总余额+收款金额
//		}else{
//			cardTotal=actualIncome;
//		}

	}



	/**
	 * 外部接口添加订单
	 * @param param
	 */
	@Transactional
	public StartOrderSuccessVO outstartAddOrder(ManualStartOrderParam param) {

			Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
			String amount = new DecimalFormat("###################.###########").format(param.getGatheringAmount());
			StartOrderParam startOrderParam = new StartOrderParam();
			startOrderParam.setMerchantNum(param.getMerchantNum());//商户号
			startOrderParam.setOrderNo(param.getOrderNo());//商户订单号
			startOrderParam.setPayType(param.getGatheringChannelCode());//渠道名称 bankcard
			startOrderParam.setAmount(amount);//金额
			startOrderParam.setNotifyUrl(param.getNotifyUrl());//支付地址
			startOrderParam.setReturnUrl(param.getReturnUrl());//回调地址
			startOrderParam.setAttch(param.getAttch());//随机数
			startOrderParam.setSpecifiedReceivedAccountId(param.getSpecifiedReceivedAccountId());
			startOrderParam.setPublishTime(param.getPublishTime());

			String sign = startOrderParam.getMerchantNum() + startOrderParam.getOrderNo() + amount
					+ param.getNotifyUrl() + merchant.getSecretKey();//商户号+订单号+金额+回调地址+秘钥
			sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
			startOrderParam.setSign(sign);//签名
		StartOrderSuccessVO vo=	startOrder(startOrderParam);//保持订单
		return vo;

	}



	/**
	 * 使用外部系统调用
	 * @param param
	 * @param request
	 * @return
	 */
	@ParamValid
	@Transactional
	public StartOrderSuccessVO payStartOrder(StartOrderParam param,HttpServletRequest request) {

		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能发起订单);
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException(BizError.金额格式不正确);
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException(BizError.金额不能小于或等于0);
		}
		String ip=getRemortIP(request);
		System.out.println(">>>>>>请求的IP="+ip);//银行卡号
		log.info("产品给我的订单号,ip={},商户订单号={}", ip,param.getOrderNo());
		if(!merchant.getIpWhitelist().contains(ip)){//ip不在白名单里面
			throw new BizException(BizError.IP白名单不在商户里面);
		}

		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getSecretKey();//商户号+订单号+金额+回调地址+秘钥
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException(BizError.签名不正确);
		}

		//系统设置的通道
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());//查询渠道列表

		if (gatheringChannel == null) {
			throw new BizException(BizError.发起订单失败通道不存在);
		}
		if (!gatheringChannel.getEnabled()) {
			throw new BizException(BizError.发起订单失败通道维护中);
		}
		//查询商户所设置的通道费率，以及通道是否开放
		GatheringChannelRate gatheringChannelRate = gatheringChannelRateRepo
				.findByMerchantIdAndChannelChannelCode(merchant.getId(), param.getPayType());
		if (gatheringChannelRate == null) {
			throw new BizException(BizError.发起订单失败该通道未开通);
		}
		if (!gatheringChannelRate.getEnabled()) {
			throw new BizException(BizError.发起订单失败该通道已关闭);
		}

		Integer orderEffectiveDuration = receiveOrderSetting.getReceiveOrderEffectiveDuration();
		MerchantOrder merchantOrder = param.convertToPo(merchant.getId(), gatheringChannel.getId(),
				gatheringChannelRate.getRate(), orderEffectiveDuration,param.getOrderNo());

		MerchantOrderPayInfo payInfo = param.convertToPayInfoPo(merchantOrder.getId());
		merchantOrder.setPayInfoId(payInfo.getId());


		merchantOrder.setSystemSource(param.getSystemSorce());//获取系统来源
		merchantOrderRepo.save(merchantOrder);//保存订单信息

		ConfigItemVO configItemVO=null;
		if("FastPay".equals(param.getSystemSorce()) ){//FastPay 渠道
		configItemVO = configService.findConfigItemByConfigCode("FastPayadminOrderPayUrl");//查询配置回调管理员系统界面去
		}else{
			configItemVO = configService.findConfigItemByConfigCode("adminOrderPayUrl");//查询配置回调管理员系统界面去
		}
		payInfo.setReturnUrl(configItemVO.getConfigValue()+ merchantOrder.getOrderNo());//回调到到管理员系统 同步通知地址
		merchantOrderPayInfoRepo.save(payInfo);//保存外部订单信息 merchant_order_pay_info

		StartOrderSuccessVO vo = new StartOrderSuccessVO();
		if("FastPay".equals(param.getSystemSorce())){//FastPay 渠道
		configItemVO = configService.findConfigItemByConfigCode("FastPaymerchantOrderPayUrl");//FastPay 渠道支付地址
	   }else{
			configItemVO = configService.findConfigItemByConfigCode("merchantOrderPayUrl");//Pay247 渠道支付地址
	    }
		vo.setPayUrl( configItemVO.getConfigValue()+ merchantOrder.getOrderNo());//支付地址
		vo.setBillNo(merchantOrder.getOrderNo());//订单号
		return vo;
	}


	//https://pay.payali999999999.net/oln/callback/Pay247/1,
	// 回调请求参数:{amount=100000.00, orderNo=20220210162632149AHUB, merchantNum=AG005, sign=bdf9b188e1e7b3243acaab45aca59047, state=1}

	public static void main(String args[]) throws NoSuchAlgorithmException, KeyManagementException, IOException {
		Map<String,String> paramMap=new HashMap<>();
		String merchantOrderId="202202051528105872CRC";
		paramMap.put("amount","1313");
		paramMap.put("orderNo",merchantOrderId);
		paramMap.put("sign","242424");
		//String payInfo="https://uatpay.pay978.com/oln/callback/pay247/18";//UAT环境地址

		String payInfo="https://pay.payali999999999.net/oln/callback/Pay247/1";//运营环境地址
	String 	result = HttpUtils.postwayform(payInfo,paramMap, "UTF-8");
			//HttpUtil.get(url, paramMap, 2500);
		//HttpUtils.postwayform(payInfo.getNotifyUrl(),paramMap, "UTF-8");

		log.info("回调订单号:{},回调请求地址:{},回调请求参数:{},通知对方返回参数:{}",merchantOrderId,payInfo,paramMap,result);
	System.out.println(result);

	}

	/**
	 * 使用外部系统调用 新方法使用
	 * @param param
	 * @param request
	 * @return
	 */
	@ParamValid
	@Transactional
	public StartOrderSuccessVO payNewStartOrder(StartOrderParam param,HttpServletRequest request) {

		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能发起订单);
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException(BizError.金额格式不正确);
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException(BizError.金额不能小于或等于0);
		}
		String ip=getRemortIP(request);
		System.out.println(">>>>>>请求的IP="+ip);//银行卡号
		log.info("产品给我的订单号,ip={},商户订单号={}", ip,param.getOrderNo());
		if(!merchant.getIpWhitelist().contains(ip)){//ip不在白名单里面
			//throw new BizException(BizError.IP白名单不在商户里面);
			throw new BizException("1015","Ip不在白名单里面,请管理人员配置IP="+ip);
		}

		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getSecretKey();//商户号+订单号+金额+回调地址+秘钥
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException(BizError.签名不正确);
		}

		//系统设置的通道
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());//查询渠道列表

		if (gatheringChannel == null) {
			throw new BizException(BizError.发起订单失败通道不存在);
		}
		if (!gatheringChannel.getEnabled()) {
			throw new BizException(BizError.发起订单失败通道维护中);
		}
		//查询商户所设置的通道费率，以及通道是否开放
		GatheringChannelRate gatheringChannelRate = gatheringChannelRateRepo
				.findByMerchantIdAndChannelChannelCode(merchant.getId(), param.getPayType());//商户号和支付类型
		if (gatheringChannelRate == null) {
			throw new BizException(BizError.发起订单失败该通道未开通);
		}
		if (!gatheringChannelRate.getEnabled()) {
			throw new BizException(BizError.发起订单失败该通道已关闭);
		}

		Integer orderEffectiveDuration = receiveOrderSetting.getReceiveOrderEffectiveDuration();
		MerchantOrder merchantOrder = param.convertNewToPo(merchant.getId(), gatheringChannel.getId(),
				gatheringChannelRate.getRate(), orderEffectiveDuration,param.getOrderNo());

		MerchantOrderPayInfo payInfo = param.convertToPayInfoPo(merchantOrder.getId());
		merchantOrder.setPayInfoId(payInfo.getId());
		merchantOrder.setSystemSource(param.getSystemSorce());//获取系统来源


//*****************************************************************下面就是设置自动接单***************************************************

		//通过渠道去找用户  通过银行卡类型查询接单用户和
		List<AccountReceiveOrderChannel> accountReceiveOrderChannelList=accountReceiveOrderChannelRepo.findByChannelIdAndStateAndChannelDeletedFlagFalse(gatheringChannel.getId(),"1");//卡转卡渠道 account_receive_order_channel 用户和渠道关系表
		//log.info("产品给我的订单号,id为{}", orderNo);
		if(accountReceiveOrderChannelList.size()==0){
			//return Result.success().setData("没有接单用户开始接单!");
			//return Result.fail("商户订单号不能为空!");
			throw new BizException(BizError.没有接单用户开始接单);
		}

		List<UserAccount> userAccounts=new ArrayList<>();
		String webSiteTitle=param.getSystemSorce();//系统来源
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>产品给我的订单号,id为订单号={},获取产品调用接口转入的系统来源={}", param.getMerchantNum(),webSiteTitle);
		for(AccountReceiveOrderChannel accountReceiveOrderChannel:accountReceiveOrderChannelList){
			UserAccount userAccount=accountReceiveOrderChannel.getUserAccount();//获取用户信息
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>产品给我的订单号,id为={},接单用户={},获取网站标题={},用户的系统来源={}", param.getMerchantNum(),userAccount.getUserName(),webSiteTitle,userAccount.getSystemSource());
			if(userAccount.getSystemSource().equals(webSiteTitle)) {//获取网站标题进行对比 必须是同一个系统下面的用户才能使用
				if (userAccount.getReceiveOrderState().equals(Constant.账号接单通道状态_开启中)) {//判断用户接单状态 必须是接单状态才行
					userAccounts.add(userAccount);//添加用户信息
				}
			}
		}
		if(userAccounts.size()==0){
			throw new BizException(BizError.没有接单用户开始接单);
		}
		UserAccount userAccount = userAccounts.get((int) (Math.random() * userAccounts.size()));//随机使用一个用户
		log.info("产品给我的订单号,id为={},接单用户ID={},接单用户={}", param.getMerchantNum(),userAccount.getId(),userAccount.getUserName());

		//log.info("产品给我的订单号设置自动接单开始,id为={},接单用户={},订单号={}", param.getMerchantNum(),userAccount.getUserName(),vo.getBillNo());


//		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
//		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
//			throw new BizException(BizError.系统维护中不能接单);
//		}
//		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);//查询订单
//		if (merchantOrder == null) {
//			throw new BizException(BizError.商户订单不存在);
//		}
//		if (!Constant.商户订单状态_等待接单.equals(merchantOrder.getOrderState())) {
//			throw new BizException(BizError.订单已被接或已取消);
//		}
		String key="receiveOrder_order_"+merchantOrder.getOrderNo();//定义key
		String stringValue=redisTemplate.opsForValue().get(key);
		if(stringValue==null){
			redisTemplate.opsForValue().set(key, merchantOrder.getOrderNo(), 180, TimeUnit.SECONDS);//放到redis里面去180秒3分钟自动在redis取消没有数据
		}else{
			throw new BizException(BizError.系统接过次单请等3分钟在接这个订单);
		}
		AccountReceiveOrderChannel receiveOrderChannel = accountReceiveOrderChannelRepo
				.findByUserAccountIdAndChannelId(userAccount.getId(), merchantOrder.getGatheringChannelId());//通过用户ID和渠道ID号找到数据
		if (receiveOrderChannel == null) {
			throw new BizException(BizError.接单通道未开通);
		}
		if (Constant.账号接单通道状态_已禁用.equals(receiveOrderChannel.getState())) {
			throw new BizException(BizError.接单通道已禁用);
		}
		if (Constant.账号接单通道状态_关闭中.equals(receiveOrderChannel.getState())) {
			throw new BizException(BizError.接单通道已关闭);
		}
//		// 校验若达到接单上限,则不能接单
//		List<MyWaitConfirmOrderVO> waitConfirmOrders = findMyWaitConfirmOrder(userAccountId);
//		if (waitConfirmOrders.size() >= receiveOrderSetting.getReceiveOrderUpperLimit()) {
//			throw new BizException(BizError.已达到接单数量上限);
//		}

		//UserAccount userAccount = userAccountRepo.getOne(userAccountId);//获取用户信息
//		if (receiveOrderSetting.getCashDepositMinimumRequire() != null) {
//			if (userAccount.getCashDeposit() < receiveOrderSetting.getCashDepositMinimumRequire()) {
//				throw new BizException(BizError.未达到接单保证金最低要求);
//			}
//		}

//		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 4)
//				.doubleValue();//保证金-收款金额
//		if (cashDeposit - receiveOrderSetting.getCashPledge() < 0) {
//			throw new BizException(BizError.保证金不足无法接单);
//		}


		List<GatheringCode> atheringCodeList= gatheringCodeRepo.findByUserAccountIdAndCardUseAndInUseTrue(userAccount.getId(),"1");//查询用户下面的收款卡必须使用的而且是存款卡状态用途是存款卡
		if(atheringCodeList.size()==0){//如果没有收款卡接单就提示错误
			throw new BizException(BizError.无法接单找不到对应金额的收款码);
		}

		for(int i=0;i<atheringCodeList.size();i++){
			GatheringCode gatheringCode= atheringCodeList.get(i);
			String id=gatheringCode.getId();//主键ID号
			GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);//查看收款码收款数据
			Double todayTradeAmount=gatheringCodeUsage.getTodayTradeAmount();//获取今日收款金额

			Double dailQuotaAmount=0.00;//获取今日限额配置金额
			String dailyQuota=gatheringCode.getDailyQuota();//获取数据今日配置限额
			if(StrUtil.isBlank(dailyQuota)){//获取数据今日限额是空
				dailQuotaAmount=0.00;
			}else{
				dailQuotaAmount=Double.valueOf(dailyQuota);
			}
			if(todayTradeAmount>=dailQuotaAmount){//今日收款金额大于今日配置限额就停止这个银行卡不用于收款
				//停用这个停用卡
				gatheringCode.setInUse(false);//停用这个收款码
				gatheringCodeRepo.save(gatheringCode);//停用这个银行卡
			}
		}

		GatheringCode gatheringCode = atheringCodeList.get((int) (Math.random() * atheringCodeList.size()));//随机产生一个银行卡用于收款使用

		log.info("获取银行卡信息,id为{},接单银行卡号={},接单ID号={}", merchantOrder.getOrderNo(),gatheringCode.getBankCode(),gatheringCode.getId());
		//userAccount.setCashDeposit(cashDeposit);//保证金
		//userAccountRepo.save(userAccount);//扣除保证金

		//Integer orderEffectiveDuration = receiveOrderSetting.getOrderPayEffectiveDuration();//支付有效时长

		merchantOrder.updateReceived(userAccount.getId(), gatheringCode, receiveOrderChannel.getRebate());//更新接单人数据
		//merchantOrder.updateUsefulTime(DateUtil.offset(merchantOrder.getReceivedTime(), DateField.MINUTE, orderEffectiveDuration));//有效时间

		merchantOrderRepo.save(merchantOrder);//保存订单号


		ConfigItemVO configItemVO=null;
		if("FastPay".equals(param.getSystemSorce()) ){//FastPay 渠道
			configItemVO = configService.findConfigItemByConfigCode("FastPayadminOrderPayUrl");//查询配置回调管理员系统界面去 http://107.182.185.162:8089/api/payOrder?orderNo=
		}else if(param.getPayType().equals("wechat") || param.getPayType().equals("alipay")){//如果渠道支付类型是支付宝扫码或者微信扫码同步通知地址
			configItemVO=configService.findConfigItemByConfigCode("wechatAndAlipayPayUrlSuccess");//如果渠道支付类型是支付宝扫码或者微信扫码同步通知地址
		}else if("BankQr".equals(param.getSystemSorce())){//BankQr 渠道扫码
			configItemVO = configService.findConfigItemByConfigCode("BankQrReturnPayUrl");//回调返回地址

		} else{
			configItemVO =
					configService.findConfigItemByConfigCode("Pay247ReturnPayUrl");//查询配置回调管理员系统界面去
					//configService.findConfigItemByConfigCode("adminOrderPayUrl");//查询配置回调管理员系统界面去
		}
		payInfo.setReturnUrl(configItemVO.getConfigValue()+ merchantOrder.getOrderNo());//回调到到管理员系统 同步通知地址
		merchantOrderPayInfoRepo.save(payInfo);//保存外部订单信息 merchant_order_pay_info


		StartOrderSuccessVO vo = new StartOrderSuccessVO();
		if("FastPay".equals(param.getSystemSorce())){//FastPay 渠道
			configItemVO = configService.findConfigItemByConfigCode("FastPaymerchantOrderPayUrl");//FastPay 渠道支付地址
		}else if(param.getPayType().equals("wechat") || param.getPayType().equals("alipay")){//如果渠道支付类型是支付宝扫码或者微信扫码都跳转固定支付地址
			configItemVO=configService.findConfigItemByConfigCode("wechatAndAlipayPayUrl");//如果渠道支付类型是支付宝扫码或者微信扫码都跳转固定支付地址
		}else if("BankQr".equals(param.getSystemSorce())){
			configItemVO = configService.findConfigItemByConfigCode("BankQrOrderPayUrl");//BankQr 渠道支付地址 付款地址
		}
		else{
			configItemVO =
					configService.findConfigItemByConfigCode("Pay247merchantOrderPayUrl");//Pay247 渠道支付地址
					//configService.findConfigItemByConfigCode("merchantOrderPayUrl");//Pay247 渠道支付地址
		}
		vo.setPayUrl( configItemVO.getConfigValue()+ merchantOrder.getOrderNo());//支付地址
		vo.setBillNo(merchantOrder.getOrderNo());//订单号
		vo.setBankAddress(gatheringCode.getBankAddress());//开户行  银行名称
		vo.setBankCode(gatheringCode.getBankCode());//银行卡号
		vo.setBankUsername(gatheringCode.getBankUsername());//卡户主 姓名

		//merchantOrderRepo.save(merchantOrder);//保存订单信息
		//accountChangeLogRepo.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder));//构建接单扣款日志
		return vo;
	}



	/**
	 * 查询付款订单列表数据
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<MerchantPayOutOrderVO> findMerchantPayoutOrderByPage(MerchantPayOutOrderQueryCondParam param) {
		Specification<MerchantPayOutOrder> spec = new Specification<MerchantPayOutOrder>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantPayOutOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {//订单号
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantName())) {//商户名称
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("merchantName"),
							param.getMerchantName()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {//商户订单号外部订单号
					predicates.add(builder.equal(root.get("outOrderNo"),
							param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getGatheringChannelCode())) {
					predicates.add(builder.equal(root.join("gatheringChannel", JoinType.INNER).get("channelCode"),
							param.getGatheringChannelCode()));
				}
				if (StrUtil.isNotBlank(param.getShoukuBankNumber())) {//收款银行卡号
					predicates.add(builder.equal(root.get("shoukuBankNumber"),
							param.getShoukuBankNumber()));////收款银行卡号
				}
                if(StrUtil.isNotBlank(param.getFukuBankNumber())){//付款银行卡号
					predicates.add(builder.equal(root.get("fuku_bank_number"),
							param.getFukuBankNumber()));//付款银行卡号
				}

				if (StrUtil.isNotBlank(param.getBankCardAccount())) {//查询银行卡数据
					String cardAccount=	param.getBankCardAccount();
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.join("gatheringCode", JoinType.INNER).get("bankCode");//定义查询的字
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//存入值
					}
					predicates.add(builder.and(builder.and(in)));//存入条件集合里
				}//查询银行卡数据


				if (StrUtil.isNotBlank(param.getOrderState())) {//订单状态
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getReceiverUserName())) {//接单人员
					predicates.add(builder.equal(root.join("receivedAccount", JoinType.INNER).get("userName"),
							param.getReceiverUserName()));//接单人
				}
				if (param.getSubmitStartTime() != null) {
					//System.out.println("开始时间="+param.getSubmitStartTime());
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));
				}
				if (param.getSubmitEndTime() != null) {
					//System.out.println("结束时间="+param.getSubmitEndTime());
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));
				}
				if (param.getReceiveOrderStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.beginOfDay(param.getReceiveOrderStartTime())));
				}
				if (param.getReceiveOrderEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.endOfDay(param.getReceiveOrderEndTime())));
				}
				if (StrUtil.isNotBlank(param.getNote())) {//备注信息
					predicates.add(builder.equal(root.get("note"), param.getNote()));
				}
				if (StrUtil.isNotBlank(param.getSystemSource())) {//系统来源
					predicates.add(builder.equal(root.get("systemSource"), param.getSystemSource()));//系统来源
				}

//				if (StrUtil.isNotBlank(param.getAttch())) {//附加信息附言
//					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("attch"),
//							param.getAttch()));//附加信息附言
//				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantPayOutOrder> result = merchantPayOutOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));

		PageResult<MerchantPayOutOrderVO> pageResult = new PageResult<>(MerchantPayOutOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}


	/**
	 * 使用外部系统调用 新方法使用用于付款使用
	 * @param param
	 * @param request
	 * @return
	 */
	@ParamValid
	@Transactional
	public MerchantPayOutOrderVO payNewPayoutStartOrder(StartPayOutOrderParam param,HttpServletRequest request) {
		String username="admin";//默认一个管理员用户
		//查询付款用户列表数据 必须是付款人员标记而且接单状态必须是启用的
		List<UserAccount> userAccounts=  userAccountRepo.findByPayerMarkEqualsAndReceiveOrderState("0","1");//查询付款标记 是付款人员

		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能发起订单);
		}
		Merchant merchant = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());//查询商户号
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException(BizError.金额格式不正确);
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException(BizError.金额不能小于或等于0);
		}
		String ip=getRemortIP(request);
		System.out.println(">>>>>>请求的IP="+ip);//银行卡号
		log.info("产品给我的订单号,ip={},商户订单号={}", ip,param.getOrderNo());
		if(!merchant.getIpWhitelist().contains(ip)){//ip不在白名单里面
			throw new BizException("1015","Ip不在白名单里面,请管理人员配置IP="+ip);
		}

		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + merchant.getSecretKey();//商户号+订单号+金额+秘钥
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException(BizError.签名不正确);
		}

		//系统设置的通道
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());//查询渠道列表

		if (gatheringChannel == null) {
			throw new BizException(BizError.发起订单失败通道不存在);
		}
		if (!gatheringChannel.getEnabled()) {
			throw new BizException(BizError.发起订单失败通道维护中);
		}
		//查询商户所设置的通道费率，以及通道是否开放
		GatheringChannelRate gatheringChannelRate = gatheringChannelRateRepo
				.findByMerchantIdAndChannelChannelCode(merchant.getId(), param.getPayType());//商户号和支付类型
		if (gatheringChannelRate == null) {
			throw new BizException(BizError.发起订单失败该通道未开通);
		}
		if (!gatheringChannelRate.getEnabled()) {
			throw new BizException(BizError.发起订单失败该通道已关闭);
		}

		Integer orderEffectiveDuration = receiveOrderSetting.getReceiveOrderEffectiveDuration();

		Double shouxufei=Double.valueOf(param.getAmount())*Double.valueOf(merchant.getPayoutRate());//0.003 就是安0.3% 计算出手续费



		MerchantPayOutOrder merchantPayOutOrder  = param.convertNewToPo(merchant.getId(), gatheringChannel.getId(),
				Double.valueOf(merchant.getPayoutRate())*100, orderEffectiveDuration,param.getOrderNo());//费率界面配置的是0.003我需要剩余100等于0.3来计算统计界面来使用

		merchantPayOutOrder.setSystemSource(param.getSystemSorce());//获取系统来源
		merchantPayOutOrder.setBounty(shouxufei);//手续费

		if(userAccounts.size()==1){//如果付款接单人员就一个就设置这个值不需要通过金额范围来设置付款人员和接单状态是启用的
				merchantPayOutOrder.setReceivedAccountId(userAccounts.get(0).getId());//设置接单人员用户ID号
		}else if(userAccounts.size()>=2){//2个以上接单人员 就需要通个金额来分配订单
			for (UserAccount userAccount:userAccounts){
				if(userAccount.getReceiveOrderState().equals("1") && userAccount.getPaymentRange()!=null) {//必须是启用的状态 而且是付款范围必须有值
					String paymentRange = userAccount.getPaymentRange();//付款范围值
					String vv[] = paymentRange.split("_");//通过_来进行截取
					if (!(vv.length==2)) {//分配给管理人员使用 如果分割符合不对就默认给管理人员使用
						UserAccount userAccountValue = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(username);
						if (userAccountValue == null) {
							throw new BizException(BizError.代付没有接单人员进行接单);
						}
						merchantPayOutOrder.setReceivedAccountId(userAccountValue.getId());//设置接单人员用户ID号
						break;
					}
					String first = vv[0];//第一个
					String seconed = vv[1];//第2个
					String value = param.getAmount();//获取金额值
					if (new BigDecimal(first).compareTo(new BigDecimal(value)) == -1 && (new BigDecimal(value).compareTo(new BigDecimal(seconed)) == -1 || new BigDecimal(value).compareTo(new BigDecimal(seconed)) == 0)) {//a小于=b   0<value<=100
						merchantPayOutOrder.setReceivedAccountId(userAccount.getId());//设置接单人员用户ID号 就在这个金额范围已内
						break;
					}
				}
			}
		}
		if(merchantPayOutOrder.getReceivedAccountId()==null){//如果没有接单人员就默认设置一个管理人员来接单
			UserAccount userAccountValue = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(username);
			if(userAccountValue==null){
				throw new BizException(BizError.代付没有接单人员进行接单);
			}
			merchantPayOutOrder.setReceivedAccountId(userAccountValue.getId());//设置接单人员用户ID号
		}


		merchantPayOutOrder.setReceivedTime(new Date());//接单时间

		merchantPayOutOrderRepo.save(merchantPayOutOrder);//保存订单号

		return MerchantPayOutOrderVO.convertNewFor(merchantPayOutOrder);//转换数据
	}


	/**
	 * 查询付款订单详情
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public MerchantPayOutOrderVO findByPayoutOrder(@NotBlank String id) {
		return MerchantPayOutOrderVO.convertFor(merchantPayOutOrderRepo.getOne(id));
	}

	/**
	 * 通过订单号查询付款详情
	 * @param orderNo
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<MerchantPayOutOrder> findByPayoutOrderNo(@NotBlank String orderNo) {
		return merchantPayOutOrderRepo.findByOutOrderNo(orderNo);
	}


	/**
	 * 确认付款流程
	 * @param param
	 */
	@ParamValid
	@Transactional
	public void submitPayout(MerchantBankSettlementParam param,String note) {
		String id=param.getId();//提现的ID号


		String key="payout_order_"+id;//定义key
		String stringValue=redisTemplate.opsForValue().get(key);
		if(stringValue==null){
			redisTemplate.opsForValue().set(key, id, 180, TimeUnit.SECONDS);//放到redis里面去180秒3分钟自动在redis取消没有数据
		}else{
			throw new BizException(BizError.不能重复点击);
		}

		MerchantPayOutOrder merchantPayOutOrder=merchantPayOutOrderRepo.getOne(id);
		if(merchantPayOutOrder==null){
			throw new BizException("1014","付款订单不存在!");
		}
		Merchant merchant = merchantRepo.getOne(merchantPayOutOrder.getMerchantId());//商户ID号 获取商户信息
		Double gatheringAmount=merchantPayOutOrder.getGatheringAmount();//付款金额
		List<MerchantBankListParam> merchantBankListParamList=param.getBankList();//获取付款银行列表
		StringBuffer payCardNoList=new StringBuffer();
	//	Double serverChangeTotal=0.0;
		Double payAmountTotal=0.0;//默认付款金额总数
		if(merchantBankListParamList.size()>0 && merchantBankListParamList!=null){
			for(int i=0;i<merchantBankListParamList.size();i++){
				MerchantBankListParam merchantBankListParam=merchantBankListParamList.get(i);
				String payAmount=merchantBankListParam.getPayAmount();//付款金额
				if(!CheckIntValue.isNumeric1(payAmount)){//付款金额必须是数字
					throw new BizException(BizError.输入的付款金额不对必须是数字);
				}
				payAmountTotal=payAmountTotal+Double.valueOf(payAmount);//付款金额先加
				String serverChange=merchantBankListParam.getServerCharge().equals("")?"0.0":merchantBankListParam.getServerCharge();//获取手续费
				if(!CheckIntValue.isNumeric1(serverChange)){//判断输入必须是数字
					throw new BizException(BizError.输入的手续费不对必须是数字);
				}
				String payCardNo=merchantBankListParam.getPayCardNo();//付款银行卡号
				GatheringCode gatheringCode=gatheringCodeRepo.findByBankCode(payCardNo);//查询付款银行卡号
				if(gatheringCode==null){//输入的收款银行卡不对请重新输入
					throw new BizException(BizError.付款的银行卡不对请重新输入);
				}
				if(Double.valueOf(payAmount)>gatheringCode.getBankTotalAmount()){//付款银行卡号金额大于卡号总余额就提示错误
					throw new BizException("1014","付款的银行卡"+payCardNo+"余额不足请重新更换银行卡");
				}
				payCardNoList.append(payCardNo+";");//付款银行卡号多个使用";"号隔离开
			}
			if(!(Double.doubleToLongBits(payAmountTotal)==Double.doubleToLongBits(gatheringAmount))){//付款银行卡的总金额和提现银行卡总金额进行对比
				throw new BizException(BizError.付款总金额和提现金额不一致);//付款总金额和提现金额不一致
			}
			//记录付款银行卡交易记录数据****************************************************************
				for (MerchantBankListParam merchantSettlementPayOutRecord : merchantBankListParamList) {
					Double payAmount=Double.valueOf(merchantSettlementPayOutRecord.getPayAmount());//付款额度
					GatheringCode gatheringCode=gatheringCodeRepo.findByBankCode(merchantSettlementPayOutRecord.getPayCardNo());//查询付款银行卡号
					if(gatheringCode==null){//输入的收款银行卡不对请重新输入
						throw new BizException(BizError.付款的银行卡不对请重新输入);
					}
					Double serveiceChange = 0.00;//无手续
					if (StrUtil.isNotBlank(merchantSettlementPayOutRecord.getServerCharge()) && merchantSettlementPayOutRecord.getServerCharge() != null) {
						serveiceChange = Double.valueOf(merchantSettlementPayOutRecord.getServerCharge());//获取提现手续费  用于扣除付款银行卡手续使用
					}

					Double cardTotal = 	gatheringCode.getBankTotalAmount() - payAmount - serveiceChange;//银行卡的总结余-提现金额-手续费

					BankcardRecord po = new BankcardRecord();//提现银行卡交易列表数据
					po.setId(IdUtils.getId());//主键
					po.setCreateTime(new Date());//开始时间
					po.setAvailableFlag(true);//1表示成功
					po.setActualIncome(-payAmount);//实际收款金额
					po.setMerchantOrderId(id);//订单号
					po.setMerchantId(null);//商户号id
					po.setServiceCharge(serveiceChange);//手续费
					po.setMerchantOrderNo(id);//商户订单号
					po.setGatheringCodeId(gatheringCode.getId());//新的收款码 提现没有收款码
					po.setCardTotal(cardTotal);//银行卡总数据卡号结余

					Date now = new Date();
					long time = 2*1000;//2秒
					Date afterDate = new Date(now.getTime() + time);//3秒后的时间 延迟3秒
					po.setSettlementTime(afterDate);//结束时间 延迟3秒
					bankcardRepo.save(po);//记录银行卡交易列表数据
					//Double bankTotal = gatheringCode.getBankTotalAmount() - payAmount - serveiceChange;//银行卡的总余额=银行卡的总余额-提现额度-手续费
					gatheringCode.setBankTotalAmount(cardTotal);//设置新的额度
					gatheringCodeRepo.save(gatheringCode);//更新银行卡额度
				}
			}else{
				throw new BizException(BizError.没有付款银行卡);
			}

		//merchantPayOutOrder.getBounty();
         //更新商户余额*************************************************
		double withdrawableAmount = merchant.getWithdrawableAmount() -gatheringAmount-merchantPayOutOrder.getBounty();//商户余额-提现金额-手续费
		if (withdrawableAmount < 0) {
			throw new BizException(BizError.商户可提现金额不足);
		}
		merchant.setWithdrawableAmount(NumberUtil.round(withdrawableAmount, 4).doubleValue());//更新商户余额 可提现金额
		merchantRepo.save(merchant);//更新商户余额
		//记录商户交易记录数据*************************************
		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(-gatheringAmount, id,
				merchant.getId(),merchantPayOutOrder.getBounty(),merchantPayOutOrder.getOutOrderNo(),null,"1");//设置商户交易记录数据

		actualIncomeRecord.setMerchantTotal(NumberUtil.round(withdrawableAmount, 4).doubleValue());//结余总数据
		Date now1 = new Date();
		long time1 = 4*1000;//4秒
		Date afterDate1 = new Date(now1.getTime() + time1);//4秒后的时间 延迟4秒
		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间延迟4秒
		actualIncomeRecordRepo.save(actualIncomeRecord);//记录商户交易列表数据


		//*****************更新订单数据*********************
			merchantPayOutOrder.setFukuBankNumber(payCardNoList.toString());//付款的银行卡号 列表
			merchantPayOutOrder.setOrderState(Constant.商户订单状态_代付成功 );// 4 已支付 10 代付成功
			merchantPayOutOrder.setConfirmTime(new Date());//确认时间
		    merchantPayOutOrder.setNote(note);//备注信息
			merchantPayOutOrderRepo.save(merchantPayOutOrder);//更新付款订单数据
	}

	/**
	 * 取消付款订单
	 *
	 * @param id
	 */
	@Transactional
	public void cancelPayoutOrder(@NotBlank String id,String text) {
		MerchantPayOutOrder platformOrder = merchantPayOutOrderRepo.getOne(id);
		if (!Constant.商户订单状态_已接单.equals(platformOrder.getOrderState())) {
			throw new BizException("1019","接单状态的才能被取消!");
		}
		platformOrder.setOrderState(Constant.商户订单状态_人工取消);
		platformOrder.setDealTime(new Date());//处理时间
		platformOrder.setNote(text);//备注
		platformOrder.setConfirmTime(new Date());//确认时间
		merchantPayOutOrderRepo.save(platformOrder);
	}

	/**
	 * 使用外部系统调用 新方法使用
	 * @param request
	 * @return
	 */
	@ParamValid
	@Transactional
	public List<MerchantOrder> automaticCall(JSONObject json, HttpServletRequest request) {
		//进行IP拦截
		String ip=getRemortIP(request);
		System.out.println(">>>>>>请求的IP="+ip);//银行卡号


		String card=json.getString("card");//银行卡号
		String amount=json.getString("amount");//收款金额
		String balance=json.getString("balance");//银行卡号余额
		String remark=json.getString("remark");//备注信息
		String time=json.getString("time");//获取时间


		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();



				if (StrUtil.isNotBlank(card)) {//查询银行卡数据
					String cardAccount=	card;
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.join("gatheringCode", JoinType.INNER).get("bankCode");//定义查询的字
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//存入值
					}
					predicates.add(builder.and(builder.and(in)));//存入条件集合里
				}//查询银行卡数据

				SimpleDateFormat sdf =new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
				Date submitStartTime=null;//开始时间
				Date submitEndTime= null;//结束时间
				try {
					submitStartTime=sdf.parse(time);//获取自动机回调时间
					submitEndTime = sdf.parse(time);//获取自动机回调时间
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (submitStartTime != null) {
					System.out.println("开始时间="+	DateUtil.offsetHour(submitStartTime,-6));
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.offsetHour(submitStartTime,-6)));//开始时间=自动机回调的时间向前推6个小时
				}

				if (submitEndTime != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.offsetMinute(submitEndTime,5)));//结束时间多添加5分钟
				}
				System.out.println("开始时间="+	DateUtil.offsetHour(submitStartTime,-6));
				System.out.println("结束时间="+	DateUtil.offsetMinute(submitEndTime,5));//加5分钟

				predicates.add(builder.equal(root.get("orderState"), "2"));//查询已接单状态
				if (StrUtil.isNotBlank(amount)) {//订单金额
					predicates.add(builder.equal(root.get("gatheringAmount"), amount));//订单金额
				}

				//if (StrUtil.isNotBlank(remark)) {//附加信息附言
					predicates.add(builder.equal(root.join("payInfo", JoinType.INNER).get("attch"),
							remark));//附加信息附言
				//}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("submitTime"))));

		List<MerchantOrder> merchantOrders=result.getContent();
		if(merchantOrders!=null&&merchantOrders.size()>=1){
			return merchantOrders;
		}
		return null;
	}


	/**
	 * 使用外部系统调用 通过银行卡和金额和时间查询数据
	 * @param request
	 * @return
	 */
	@ParamValid
	@Transactional
	public List<MerchantOrder> automaticBankCadAndAmountCall(JSONObject json, HttpServletRequest request) {


		String card=json.getString("card");//银行卡号
		String amount=json.getString("amount");//收款金额
		String time=json.getString("time");//获取时间


		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();



				if (StrUtil.isNotBlank(card)) {//查询银行卡数据
					String cardAccount=	card;
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.join("gatheringCode", JoinType.INNER).get("bankCode");//定义查询的字
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//存入值
					}
					predicates.add(builder.and(builder.and(in)));//存入条件集合里
				}//查询银行卡数据

				SimpleDateFormat sdf =new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
				Date submitStartTime=null;//开始时间
				Date submitEndTime= null;//结束时间
				try {
					submitStartTime=sdf.parse(time);//获取自动机回调时间
					submitEndTime = sdf.parse(time);//获取自动机回调时间
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (submitStartTime != null) {
					System.out.println("开始时间="+	DateUtil.offsetHour(submitStartTime,-6));
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.offsetHour(submitStartTime,-6)));//开始时间=自动机回调的时间向前推6个小时
				}

				if (submitEndTime != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.offsetMinute(submitEndTime,5)));//结束时间多添加5分钟
				}
				System.out.println("开始时间="+	DateUtil.offsetHour(submitStartTime,-6));
				System.out.println("结束时间="+	DateUtil.offsetMinute(submitEndTime,5));//加5分钟

				predicates.add(builder.equal(root.get("orderState"), "2"));//查询已接单状态
				if (StrUtil.isNotBlank(amount)) {//订单金额
					predicates.add(builder.equal(root.get("gatheringAmount"), amount));//订单金额
				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("submitTime"))));

		List<MerchantOrder> merchantOrders=result.getContent();
		if(merchantOrders!=null&&merchantOrders.size()>=1){
			return merchantOrders;
		}
		return null;
	}

	/**
	 * 自动机确定支付
	 * @param orderId
	 * @param bankFlow
	 */
	@Transactional
	public boolean autoconfirmToPaidWithCancelOrderRefund(@NotBlank String orderId,String bankFlow) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);
		try {
			if (merchantOrder == null) {
				throw new BizException(BizError.商户订单不存在);
			}
			if (!Constant.商户订单状态_已接单.equals(merchantOrder.getOrderState())) {//订单状态为已接单才能转为确认已支付
				throw new BizException(BizError.订单状态为已接单才能转为确认已支付);
			}


			String name="自动机";
			String textValuedesk = "操作人[" + name + "]"+" 银行流水号="+bankFlow;
			merchantOrder.confirmToPaid(textValuedesk);//设置备注信息 和设置订单状态 orderState=4表示支付成功
			merchantOrderRepo.save(merchantOrder);//修改状态
			receiveOrderSettlement(merchantOrder);//记录商户交易记录
			generateCardBankIncomeRecord(merchantOrder);//添加银行卡交易记录数据 记录收款银行卡数据
			return true;
		}catch (Exception ex){
			log.info("确认支付按钮,id为={},商户订单号={},异常错误={}", orderId,merchantOrder.getOutOrderNo(),ex);
			return false;
		}
	}


}
