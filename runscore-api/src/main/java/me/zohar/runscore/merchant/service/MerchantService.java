package me.zohar.runscore.merchant.service;

import java.io.DataOutput;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.*;
import javax.validation.constraints.NotBlank;

import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.merchant.domain.*;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.*;
import me.zohar.runscore.merchant.vo.*;
import me.zohar.runscore.util.CheckIntValue;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;

@Validated
@Service
public class MerchantService {

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantBankCardRepo merchantBankCardRepo;

	@Autowired
	private MerchantSettlementRecordRepo merchantSettlementRecordRepo;

	@Autowired
	private ActualIncomeRecordRepo actualIncomeRecordRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;//收款码表
	@Autowired
	private BankcardRepo bankcardRepo;//银行卡交易记录数据

	@Autowired
	private MerchantSettlementPayoutRecordRepo settlementPayoutRecordRepo;//付款银行卡列表数据

	/**
	 * 商户提现确定到账
	 * @param id
	 */
	@ParamValid
	@Transactional
	public void settlementConfirmCredited(@NotBlank String id) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		//Double actualIncome=record.getWithdrawAmount();//提现金额
		List<MerchantSettlementPayOutRecord>  merchantSettlementPayOutRecordList=settlementPayoutRecordRepo.findByMerchantSettlId(id);//获取提现付款银行列表

		if (!(Constant.商户结算状态_审核通过.equals(record.getState()))) {
			throw new BizException(BizError.只有状态为审核通过的记录才能进行确认到帐操作);
		}


		if(merchantSettlementPayOutRecordList.size()>0 && merchantSettlementPayOutRecordList!=null) {//循环查询付款银行卡号
			for (MerchantSettlementPayOutRecord merchantSettlementPayOutRecord : merchantSettlementPayOutRecordList) {

				Double payAmount=Double.valueOf(merchantSettlementPayOutRecord.getPayAmount());//付款额度

				GatheringCode gatheringCode=gatheringCodeRepo.findByBankCode(merchantSettlementPayOutRecord.getPayCardNo());//查询付款银行卡号
				if(gatheringCode==null){//输入的收款银行卡不对请重新输入
					throw new BizException(BizError.付款的银行卡不对请重新输入);
				}

				Double serveiceChange = 0.00;//无手续
				if (StrUtil.isNotBlank(merchantSettlementPayOutRecord.getServerCharge()) && merchantSettlementPayOutRecord.getServerCharge() != null) {
					serveiceChange = Double.valueOf(merchantSettlementPayOutRecord.getServerCharge());//获取提现手续费  用于扣除付款银行卡手续使用
				}
				Double cardTotal = gatheringCode.getBankTotalAmount() - payAmount - serveiceChange;//银行卡的总结余-提现金额-手续费

//				//先处理提现的银行卡付款银行卡要扣钱
//				Specification<BankcardRecord> spec = new Specification<BankcardRecord>() {
//					/**
//					 *
//					 */
//					private static final long serialVersionUID = 1L;
//
//					public Predicate toPredicate(Root<BankcardRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//						List<Predicate> predicates = new ArrayList<Predicate>();
//						if (StrUtil.isNotBlank(gatheringCode.getBankCode())) {//提现银行卡号
//							predicates.add(builder.equal(root.join("gatheringCode", JoinType.INNER).get("bankCode"),
//									gatheringCode.getBankCode()));//提现银行卡查询
//						}
//						return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//					}
//				};
//				Page<BankcardRecord> bankcardComeRecrdReportVOList = bankcardRepo.findAll(spec,
//						PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime"))));//通过提现的银行卡查询银行卡交易数据
//
//				Double cardTotal = 0.00;//结余
//				List<BankcardRecord> BankcardRecordList = bankcardComeRecrdReportVOList.getContent();//获取数据列表
//
//				if (BankcardRecordList != null && BankcardRecordList.size() > 0) {//查询有数据
//					BankcardRecord BankcardRecord = BankcardRecordList.get(0);//获取第一条数据
//					cardTotal = BankcardRecord.getCardTotal() - payAmount - serveiceChange;//银行卡的总结余-付款金额-手续费
//				} else {//查询没有数据
//					cardTotal = gatheringCode.getBankTotalAmount() - payAmount - serveiceChange;//银行卡的总结余-提现金额-手续费
//				}


				BankcardRecord po = new BankcardRecord();//提现银行卡交易列表数据
				po.setId(IdUtils.getId());//主键
				po.setCreateTime(new Date());//开始时间
				po.setAvailableFlag(true);//1表示成功
				po.setActualIncome(-payAmount);//实际收款金额
				po.setMerchantOrderId(record.getId());//订单号
				po.setMerchantId(null);//商户号id
				po.setServiceCharge(serveiceChange);//手续费
				po.setMerchantOrderNo(record.getId());//商户订单号
				po.setGatheringCodeId(gatheringCode.getId());//新的收款码 提现没有收款码
				po.setCardTotal(cardTotal);//银行卡总数据卡号结余

				Date now = new Date();
				long time = 3*1000;//3秒
				Date afterDate = new Date(now.getTime() + time);//3秒后的时间 延迟3秒
				po.setSettlementTime(afterDate);//结束时间 延迟3秒
				bankcardRepo.save(po);//记录银行卡交易列表数据

				//Double bankTotal = gatheringCode.getBankTotalAmount() - payAmount - serveiceChange;//银行卡的总余额=银行卡的总余额-提现额度-手续费
				gatheringCode.setBankTotalAmount(cardTotal);//设置新的额度
				gatheringCodeRepo.save(gatheringCode);//更新银行卡额度

				record.confirmCredited();//商户结算状态_已到账 更新时间
				merchantSettlementRecordRepo.save(record);//修改状态
			}
		}else{
			throw new BizException(BizError.没有付款银行卡);
		}
	}

	/**
	 * 商户提现现确定审核通过
	 * 
	 * @param id
	 * payCardNo 付款银行卡号
	 */
	@ParamValid
	@Transactional
	public void settlementApproved(@NotBlank String id, String note,String payCardNo,String serverChange) {
	if(!CheckIntValue.isNumeric1(serverChange)){//判断输入必须是数字
		throw new BizException(BizError.输入的手续费不对必须是数字);
	}
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);//商户交易记录数据
		GatheringCode gatheringCode=gatheringCodeRepo.findByBankCode(payCardNo);//查询付款银行卡号
		if(gatheringCode==null){//输入的收款银行卡不对请重新输入
			throw new BizException(BizError.付款的银行卡不对请重新输入);
		}
		if(record.getWithdrawAmount()>gatheringCode.getBankTotalAmount()){//提现金额大于卡号总余额就提示错误
			throw new BizException(BizError.付款的银行卡余额不足请重新更换银行卡);
		}
		if (!Constant.商户结算状态_审核中.equals(record.getState())) {
			throw new BizException(BizError.只有状态为审核中的记录才能审核通过操作);
		}
		record.approved(note);//设置配置信息
		record.setPayCardNo(payCardNo);//设置银行卡
		record.setServerCharge(serverChange);//设置付款手续费
		merchantSettlementRecordRepo.save(record);
	}


	/**
	 * 添加付款银行卡号
	 * @param param
	 */
	@ParamValid
	@Transactional
	public void settlementPayoutApproved(MerchantBankSettlementParam param) {
	    String id=param.getId();//提现的ID号
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);//商户交易记录数据
		List<MerchantBankListParam> merchantBankListParamList=param.getBankList();
		StringBuffer payCardNoList=new StringBuffer();
		Double serverChangeTotal=0.0;
		Double payAmountTotal=0.0;//默认付款金额总数
		if(merchantBankListParamList.size()>0 && merchantBankListParamList!=null){
			for(int i=0;i<merchantBankListParamList.size();i++){
				MerchantBankListParam merchantBankListParam=merchantBankListParamList.get(i);
				String payAmount=merchantBankListParam.getPayAmount();//付款金额
				if(!CheckIntValue.isNumeric1(payAmount)){//付款金额必须是数字
					throw new BizException(BizError.输入的付款金额不对必须是数字);
				}

				payAmountTotal=payAmountTotal+Double.valueOf(payAmount);//付款金额先加
				String serverChange=merchantBankListParam.getServerCharge();//获取手续费
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
				if (!Constant.商户结算状态_审核中.equals(record.getState())) {
					throw new BizException(BizError.只有状态为审核中的记录才能审核通过操作);
				}
			}
			if(!(Double.doubleToLongBits(payAmountTotal)==Double.doubleToLongBits(record.getWithdrawAmount()))){//比较值
				throw new BizException(BizError.付款总金额和提现金额不一致);//付款总金额和提现金额不一致
			}

			for(int i=0;i<merchantBankListParamList.size();i++){
				MerchantBankListParam merchantBankListParam=merchantBankListParamList.get(i);
			    String serverChange=merchantBankListParam.getServerCharge();//获取手续费
				String payAmount=merchantBankListParam.getPayAmount();//付款金额
				String payCardNo=merchantBankListParam.getPayCardNo();//付款银行卡号

			    String note=merchantBankListParam.getNote();//获取备注数据
				MerchantSettlementPayOutRecord merchantSettlementPayOutRecord=new MerchantSettlementPayOutRecord();
				merchantSettlementPayOutRecord.setId(IdUtils.getId());//主键
				merchantSettlementPayOutRecord.setPayCardNo(payCardNo);//设置付款银行卡号
				merchantSettlementPayOutRecord.setServerCharge(serverChange);//设置付款手续费
				merchantSettlementPayOutRecord.setPayAmount(payAmount);//设置付款金额
				merchantSettlementPayOutRecord.setNote(note);//备注
				merchantSettlementPayOutRecord.setMerchantSettlId(id);//设置提现记录
				payCardNoList.append(payCardNo+";");//付款银行卡号
				serverChangeTotal=serverChangeTotal+Double.valueOf(serverChange);//手续费总金额
				settlementPayoutRecordRepo.save(merchantSettlementPayOutRecord);//添加提现银行卡列表数据
			}
			record.setState(Constant.商户结算状态_审核通过);//设置审核通过
			record.setApprovalTime(new Date());
			record.setPayCardNo(payCardNoList.toString());//设置银行卡
			record.setServerCharge(serverChangeTotal.toString());//设置付款手续费
			merchantSettlementRecordRepo.save(record);//更新审核通过数据
		}else{
			throw new BizException(BizError.输入的数据不对);
		}
	}


	/**
	 * 确认审核不通过 拒绝
	 * @param id
	 * @param note
	 */
	@Transactional
	public void settlementNotApproved(@NotBlank String id, String note) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_审核中.equals(record.getState()) || Constant.商户结算状态_审核通过.equals(record.getState()))) {
			throw new BizException(BizError.只有状态为审核中或审核通过的记录才能进行审核不通过操作);
		}
		record.notApproved(note);//审核不通过
		merchantSettlementRecordRepo.save(record);

		Merchant merchant = record.getMerchant();
		double withdrawableAmount = NumberUtil.round(merchant.getWithdrawableAmount() + record.getWithdrawAmount(), 4)
				.doubleValue();//审核不通过就需要把余额添加回去
		merchant.setWithdrawableAmount(withdrawableAmount);//设置商户余额
		merchantRepo.save(merchant);//添加商户余额数据

		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(record.getWithdrawAmount(), merchant.getId(),
				merchant.getId(),0.00,record.getOrderNo(),null,"0");
		Date now = new Date();
		long time = 2*1000;//2秒
		Date afterDate1 = new Date(now.getTime() + time);//2秒后的时间 延迟2秒
		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间结束时间 延迟2秒
		actualIncomeRecord.setMerchantTotal(withdrawableAmount);//结余总数据 更新商户总余额
		actualIncomeRecordRepo.save(actualIncomeRecord);// 添加商户交易记录数据


//		//记录商户交易记录数据
//		Specification<ActualIncomeRecord> spec = new Specification<ActualIncomeRecord>() {
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<ActualIncomeRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(merchant.getId())){//商户ID号主键
//					predicates.add(builder.equal(root.get("merchantId"), merchant.getId()));//商户ID号
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//
//		Page<ActualIncomeRecord> result = actualIncomeRecordRepo.findAll(spec,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime")))); //结束时间 通过商户ID号和完成时间查询数据
//
//		List<ActualIncomeRecord> actualIncomeRecordList =result.getContent();
//
//		Double merchantTotal=0.00;//结余
//		if(actualIncomeRecordList!=null && actualIncomeRecordList.size()>0){//查询商户交易列表有数据
//			ActualIncomeRecord  actualIncomeRecord=actualIncomeRecordList.get(0);
//			merchantTotal= NumberUtil.round(actualIncomeRecord.getMerchantTotal()+record.getWithdrawAmount(), 4).doubleValue();//商户交易列表总数据=历史数据第一条+收到的金额
//		}
//		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(record.getWithdrawAmount(), merchant.getId(),
//				merchant.getId(),0.00,record.getOrderNo(),null);
//		Date now = new Date();
//		long time = 2*1000;//2秒
//		Date afterDate1 = new Date(now.getTime() + time);//2秒后的时间 延迟2秒
//		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间结束时间 延迟2秒
//
//		actualIncomeRecord.setMerchantTotal(merchantTotal);//结余总数据 更新商户总余额
//		actualIncomeRecordRepo.save(actualIncomeRecord);// 添加商户交易记录数据


	}

	@Transactional(readOnly = true)
	public MerchantSettlementRecordVO findByMerchantSettlementRecordId(@NotBlank String id) {
		return MerchantSettlementRecordVO.convertFor(merchantSettlementRecordRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantSettlementRecordVO> findTop5TodoSettlementByPage() {
		Specification<MerchantSettlementRecord> spec = new Specification<MerchantSettlementRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantSettlementRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.商户结算状态_审核中));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantSettlementRecord> result = merchantSettlementRecordRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("applyTime"))));
		PageResult<MerchantSettlementRecordVO> pageResult = new PageResult<>(
				MerchantSettlementRecordVO.convertFor(result.getContent()), 1, 5, result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantSettlementRecordVO> findMerchantSettlementRecordByPage(
			MerchantSettlementRecordQueryCondParam param) {
		Specification<MerchantSettlementRecord> spec = new Specification<MerchantSettlementRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantSettlementRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getApplyStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("applyTime").as(Date.class),
							DateUtil.beginOfDay(param.getApplyStartTime())));
				}
				if (param.getApplyEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("applyTime").as(Date.class),
							DateUtil.endOfDay(param.getApplyEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantSettlementRecord> result = merchantSettlementRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("applyTime"))));

		PageResult<MerchantSettlementRecordVO> pageResult = new PageResult<>(
				MerchantSettlementRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	/**
	 * 发起商户提现 记录数据
	 * @param param
	 */
	@ParamValid
	@Transactional
	public void applySettlement(ApplySettlementParam param) {
		if(!CheckIntValue.isNumeric1(param.getWithdrawAmount().toString())){//判断输入必须是数字
			throw new BizException(BizError.输入的手续费不对必须是数字);
		}

		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		if (merchant == null) {
			throw new BizException(BizError.商户号不存在);
		}
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getMoneyPwd(), merchant.getMoneyPwd())) {
			throw new BizException(BizError.资金密码不正确);
		}
		MerchantBankCard merchantBankCard = merchantBankCardRepo.getOne(param.getMerchantBankCardId());
		if (!merchant.getId().equals(merchantBankCard.getMerchantId())) {
			throw new BizException(BizError.银行卡信息异常);
		}
		double withdrawableAmount = merchant.getWithdrawableAmount() - param.getWithdrawAmount();//商户余额-提现金额
		if (withdrawableAmount < 0) {
			throw new BizException(BizError.可提现金额不足);
		}
		merchant.setWithdrawableAmount(NumberUtil.round(withdrawableAmount, 4).doubleValue());//可提现金额
		merchantRepo.save(merchant);//更新商户余额
		MerchantSettlementRecord merchantSettlementRecord = param.convertToPo();
		merchantSettlementRecordRepo.save(merchantSettlementRecord);//添加提现记录数据 添加提现记录数据 进行付款使用的


		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(-param.getWithdrawAmount(), merchantSettlementRecord.getId(),
				merchantSettlementRecord.getMerchantId(),new Double("0.00"),merchantSettlementRecord.getOrderNo(),null,"0");//设置商户交易记录数据
		Date now = new Date();
		long time = 2*1000;//2秒
		Date afterDate1 = new Date(now.getTime() + time);//2秒后的时间 延迟2秒
		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间结束时间 延迟2秒
		actualIncomeRecord.setMerchantTotal(withdrawableAmount);//结余总数据 更新商户总余额
		actualIncomeRecordRepo.save(actualIncomeRecord);// 添加商户交易记录数据


//
//		//*******************************************记录商户交易记录数据**************************************************************
//		ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(-param.getWithdrawAmount(), merchantSettlementRecord.getId(),
//				merchantSettlementRecord.getMerchantId(),new Double("0.00"),merchantSettlementRecord.getOrderNo(),null);//设置商户交易记录数据
//		//查询商户交易列表数据
//		Specification<ActualIncomeRecord> spec1 = new Specification<ActualIncomeRecord>() {
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<ActualIncomeRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(merchantSettlementRecord.getMerchantId())){//商户ID号主键
//					predicates.add(builder.equal(root.get("merchantId"), merchantSettlementRecord.getMerchantId()));//商户ID号
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//		Page<ActualIncomeRecord> result = actualIncomeRecordRepo.findAll(spec1,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime")))); //结束时间  通过商户ID号和结束时间查询最后一个商户交易记录数据
//
//		List<ActualIncomeRecord> actualIncomeRecordList=result.getContent();
//		Double merchantTotal=0.00;//结余
//		if(actualIncomeRecordList!=null && actualIncomeRecordList.size()>0){
//			ActualIncomeRecord actualIncomeRecord1=	actualIncomeRecordList.get(0);
//			merchantTotal= actualIncomeRecord1.getMerchantTotal()-param.getWithdrawAmount();//获取商户交易总数据-扣除当前数据
//		}
//		actualIncomeRecord.setMerchantTotal(NumberUtil.round(merchantTotal, 4).doubleValue());//结余总数据
//		Date now1 = new Date();
//		long time1 = 4*1000;//4秒
//		Date afterDate1 = new Date(now1.getTime() + time1);//4秒后的时间 延迟4秒
//		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间延迟4秒
//		actualIncomeRecordRepo.save(actualIncomeRecord);//记录商户交易列表数据
	}

	@Transactional(readOnly = true)
	public MerchantBankCardVO findMerchantBankCardById(@NotBlank String id) {
		return MerchantBankCardVO.convertFor(merchantBankCardRepo.getOne(id));
	}

	public void deleteMerchantBankCard(String merchantBankCardId, String merchantId) {
		MerchantBankCard merchantBankCard = merchantBankCardRepo.getOne(merchantBankCardId);
		if (!merchantBankCard.getMerchantId().equals(merchantId)) {
			throw new BizException(BizError.参数异常);
		}
		merchantBankCard.delete();
		merchantBankCardRepo.save(merchantBankCard);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateMerchantBankCard(AddOrUpdateMerchantBankCardParam param, String merchantId) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			MerchantBankCard merchantBankCard = param.convertToPo(merchantId);
			merchantBankCardRepo.save(merchantBankCard);
		}
		// 修改
		else {
			MerchantBankCard merchantBankCard = merchantBankCardRepo.getOne(param.getId());
			if (!merchantBankCard.getMerchantId().equals(merchantId)) {
				throw new BizException(BizError.无权修改商户银行卡信息);
			}
			BeanUtils.copyProperties(param, merchantBankCard);
			merchantBankCardRepo.save(merchantBankCard);
		}
	}

	@Transactional(readOnly = true)
	public List<MerchantBankCardVO> findMerchantBankCardByMerchantId(@NotBlank String merchantId) {
		return MerchantBankCardVO.convertFor(merchantBankCardRepo.findByMerchantIdAndDeletedFlagIsFalse(merchantId));
	}

	@ParamValid
	@Transactional
	public void modifyLoginPwd(ModifyLoginPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldLoginPwd(), merchant.getLoginPwd())) {
			throw new BizException(BizError.旧的登录密码不正确);
		}
		modifyLoginPwd(merchant.getId(), param.getNewLoginPwd());
	}

	@ParamValid
	@Transactional
	public void modifyMoneyPwd(ModifyMoneyPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldMoneyPwd(), merchant.getMoneyPwd())) {
			throw new BizException(BizError.旧的资金密码不正确);
		}
		modifyMoneyPwd(merchant.getId(), param.getNewMoneyPwd());
	}

	@Transactional(readOnly = true)
	public List<MerchantVO> findAllMerchant() {
		return MerchantVO.convertFor(merchantRepo.findByDeletedFlagIsFalse());
	}

	@Transactional(readOnly = true)
	public MerchantVO getMerchantInfo(String id) {
		return MerchantVO.convertFor(merchantRepo.getOne(id));
	}

	/**
	 * 更新最近登录时间
	 */
	@Transactional
	public void updateLatelyLoginTime(String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLatelyLoginTime(new Date());
		merchantRepo.save(merchant);
	}

	/**
	 * 登录方法
	 * @param userName
	 * @return
	 */
	@Transactional(readOnly = true)
	public LoginMerchantInfoVO getLoginMerchantInfo(String userName) {
		return LoginMerchantInfoVO.convertFor(merchantRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}

	/**
	 * 修改商户登录密码
	 * @param id
	 * @param newLoginPwd
	 */
	@Transactional
	public void modifyLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));//修改登录密码
		merchantRepo.save(merchant);
	}
	/**
	 * 修改商户登录密码
	 * @param id
	 * @param newLoginPwd
	 */
	@Transactional
	public void modifyNewLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd,@NotBlank String moneyPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));//修改登录密码
		merchant.setMoneyPwd(new BCryptPasswordEncoder().encode(moneyPwd));//设置密码 设置资金密码
		merchantRepo.save(merchant);
	}

	@Transactional
	public void modifyMoneyPwd(@NotBlank String id, @NotBlank String newMoneyPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setMoneyPwd(new BCryptPasswordEncoder().encode(newMoneyPwd));//设置密码 设置资金密码
		merchantRepo.save(merchant);
	}

	/**
	 * 查询商户信息
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public MerchantVO findMerchantById(@NotBlank String id) {
		return MerchantVO.convertFor(merchantRepo.getOne(id));
	}

	@Transactional
	public void delMerchantById(@NotBlank String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setDeletedFlag(true);
		merchantRepo.save(merchant);
	}

	@ParamValid
	@Transactional
	public void addMerchant(AddMerchantParam param) {
		Merchant merchantWithUserName = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (merchantWithUserName != null) {
			throw new BizException(BizError.用户名已使用);
		}
		Merchant merchantWithMerchantNum = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchantWithMerchantNum != null) {
			throw new BizException(BizError.商户号已使用);
		}
		Merchant merchantWithName = merchantRepo.findByMerchantNameAndDeletedFlagIsFalse(param.getMerchantName());
		if (merchantWithName != null) {
			throw new BizException(BizError.商户名称已使用);
		}
		param.setLoginPwd(new BCryptPasswordEncoder().encode(param.getLoginPwd()));
		Merchant merchant = param.convertToPo();
		merchantRepo.save(merchant);
	}

	/**
	 * 更新商户信息
	 * @param param
	 */
	@ParamValid
	@Transactional
	public void updateMerchant(MerchantEditParam param) {
		Merchant merchantWithUserName = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());

		if (merchantWithUserName != null && !merchantWithUserName.getId().equals(param.getId())) {
			throw new BizException(BizError.用户名已使用);
		}
		Merchant merchantWithMerchantNum = merchantRepo.findByMerchantNumAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchantWithMerchantNum != null && !merchantWithMerchantNum.getId().equals(param.getId())) {
			throw new BizException(BizError.商户号已使用);
		}
		Merchant merchantWithName = merchantRepo.findByMerchantNameAndDeletedFlagIsFalse(param.getMerchantName());
		if (merchantWithName != null && !merchantWithName.getId().equals(param.getId())) {
			throw new BizException(BizError.商户名称已使用);
		}
		if(!CheckIntValue.isNumeric1(param.getWithdrawableAmount().toString())){//判断输入必须是数字
			throw new BizException(BizError.商户提现额度金额不对必须是数字);
		}

		Merchant merchant = merchantRepo.getOne(param.getId());
		BigDecimal bqueryvvv=new BigDecimal(merchant.getWithdrawableAmount());
		System.out.println(bqueryvvv);
		if(merchant.getWithdrawableAmount()!=null) {
			BigDecimal bquery=new BigDecimal(merchant.getWithdrawableAmount());
			BigDecimal b1=new BigDecimal(Double.valueOf(param.getWithdrawableAmount()));

			if (!(bquery.compareTo(b1)==0)) {//如果修改商户金额和以前金额不一致就需要添加一个商户交易记录数据b1
				ActualIncomeRecord actualIncomeRecord = ActualIncomeRecord.build(new Double(param.getWithdrawableAmount()), merchant.getId(),
						merchant.getId(),new Double("0.00"),merchant.getId(),null,"0");

				actualIncomeRecord.setMerchantTotal(NumberUtil.round(param.getWithdrawableAmount(), 4).doubleValue());//结余总数据
				Date now1 = new Date();
		long time1 = 4*1000;//4秒
		Date afterDate1 = new Date(now1.getTime() + time1);//4秒后的时间 延迟4秒
		actualIncomeRecord.setSettlementTime(afterDate1);//结束时间延迟4秒
				actualIncomeRecordRepo.save(actualIncomeRecord);//记录商户交易列表
			}
		}
		BeanUtils.copyProperties(param, merchant);
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantVO> findMerchantByPage(MerchantQueryCondParam param) {
		Specification<Merchant> spec = new Specification<Merchant>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getMerchantName())) {
					predicates.add(builder.equal(root.get("merchantName"), param.getMerchantName()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<Merchant> result = merchantRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantVO> pageResult = new PageResult<>(MerchantVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
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
							DateUtil.beginOfDay(param.getSubmitStartTime())));//开始实际
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));//结束时间
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<ActualIncomeRecord> result = actualIncomeRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));


		PageResult<ActuallncomeRecrdReportVO> pageResult = new PageResult<>(ActuallncomeRecrdReportVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	//时间加上秒后的时间 日期
	public static Date timePastTenSecond(Integer second,String otime) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dt=sdf.parse(otime);
			Calendar newTime = Calendar.getInstance();
			newTime.setTime(dt);
			newTime.add(Calendar.SECOND,second);//日期加10秒
			Date dt1=newTime.getTime();
//            String retval = sdf.format(dt1);
			return dt1;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化输出日期
		Date now = new Date();
		long time = 5*1000;//5秒
		Date afterDate = new Date(now .getTime() + time);//10秒后的时间
		Date beforeDate = new Date(now .getTime() - time);//60秒前的时间
		System.out.println(sdf.format(afterDate ));

	}

}
