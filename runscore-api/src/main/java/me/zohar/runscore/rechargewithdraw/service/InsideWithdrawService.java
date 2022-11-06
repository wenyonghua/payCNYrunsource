package me.zohar.runscore.rechargewithdraw.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.util.internal.StringUtil;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.mastercontrol.domain.WithdrawSetting;
import me.zohar.runscore.mastercontrol.repo.WithdrawSettingRepo;
import me.zohar.runscore.merchant.domain.BankcardRecord;
import me.zohar.runscore.merchant.repo.BankcardRepo;
import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.domain.WithdrawRecord;
import me.zohar.runscore.rechargewithdraw.param.InsideWithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.param.LowerLevelWithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.param.StartWithdrawParam;
import me.zohar.runscore.rechargewithdraw.param.WithdrawRecordQueryCondParam;
import me.zohar.runscore.rechargewithdraw.repo.InsideWithdrawRecordRepo;
import me.zohar.runscore.rechargewithdraw.repo.WithdrawRecordRepo;
import me.zohar.runscore.rechargewithdraw.vo.InsideWithdrawRecordVO;
import me.zohar.runscore.rechargewithdraw.vo.WithdrawRecordVO;
import me.zohar.runscore.useraccount.domain.AccountChangeLog;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.AccountChangeLogRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InsideWithdrawService {

//	@Autowired
//	private WithdrawRecordRepo withdrawRecordRepo;

	@Autowired
	private InsideWithdrawRecordRepo insideWithdrawRecordRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private WithdrawSettingRepo withdrawSettingRepo;
	@Autowired
	private BankcardRepo bankcardRepo;//银行卡交易记录数据
	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;//收款码表

	@Autowired
	private InsideWithdrawService insideWithdrawService;


	/**
	 * 进行审核
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public InsideWithdrawRecordVO findWithdrawRecordById(@NotBlank String id) {
		InsidewithdrawRecord withdrawRecord = insideWithdrawRecordRepo.getOne(id);
		String codeId=withdrawRecord.getGatheringCodeId();

		GatheringCode gatheringBank=gatheringCodeRepo.getOne(codeId);//原银行卡号
		if(gatheringBank==null){//输入的收款银行卡不对请重新输入
			throw new BizException(BizError.收款的银行卡不对请重新输入);
		}
		String vv= DecimalFormatUtil.formatString(new BigDecimal(gatheringBank.getBankTotalAmount()), null);
		withdrawRecord.setBankTotal(vv);//银行卡总金额

		return InsideWithdrawRecordVO.convertFor(withdrawRecord);
	}


	/**
	 * 添加内部转账功能
	// * @param openAccountBank 开户银行 建设银行
	// * @param accountHolder 开户姓名
	 * @param bankCardAccount 银行卡账号
	// * @param userName 用户
	 * @param note 备注说明
	 * @serviceCharge 手续
	 * @shoukuanNumber 收款卡号 银行卡号
	 * @withdrawAmount 提现金额
	 * @return
	 */
	@ParamValid
	@Transactional
	public void saveInsideWithdraw(String bankCardAccount,String note,String serviceCharge,String shoukuanNumber,String withdrawAmount) {

		GatheringCode gatheringBank=gatheringCodeRepo.findByBankCode(bankCardAccount);//原银行卡号
		if(gatheringBank==null){//输入的收款银行卡不对请重新输入
			throw new BizException(BizError.收款的银行卡不对请重新输入);
		}
    if(note==null || note.equals("")){
	throw new BizException(BizError.必须填写备注信息);
   }



//		if(shoukuanNumber.equals(gatheringBank.getBankCode())){
//			throw new BizException(BizError.输入的收款银行卡和出款卡一直请重新输入);
//		}
//		if(StringUtil.isNullOrEmpty(withdrawAmount) || StringUtil.isNullOrEmpty(shoukuanNumber) || StringUtil.isNullOrEmpty(serviceCharge)){
//			throw new BizException(BizError.请输入提现额度);
//		}
//
//		if(gatheringBank.getBankTotalAmount()<=Double.valueOf(withdrawAmount)){//提现额度大于卡号的总额度
//			throw new BizException(BizError.输入的提现额度大于卡的总额度请从新输入额度);
//		}


//		GatheringCode gatheringCodeShouku=gatheringCodeRepo.findByBankCode(shoukuanNumber);//收款的银行卡号
//		if(gatheringCodeShouku==null){//输入的收款银行卡不对请重新输入
//			throw new BizException(BizError.收款的银行卡不对请重新输入);
//		}
//		if(gatheringCodeShouku.getCardUse().equals("1")){//表示存款卡不能用于内部转折 卡用途 1.存款卡，2.付款卡，3.备用金卡
//			throw new BizException(BizError.输入的银行卡是存款卡不能内部转账);//只能是2.付款卡，3.备用金卡才能转账
//		}

		InsidewithdrawRecord insidewithdrawRecord=new InsidewithdrawRecord();
		insidewithdrawRecord.setId(IdUtils.getId());//订单号
		insidewithdrawRecord.setSubmitTime(new Date());//提交时间
		insidewithdrawRecord.setOrderNo(insidewithdrawRecord.getId());//订单号
		insidewithdrawRecord.setWithdrawWay(Constant.提现方式_银行卡);//提现方式
		insidewithdrawRecord.setState(Constant.提现记录状态_发起提现);

		insidewithdrawRecord.setAccountHolder(gatheringBank.getPayee());//姓名
		insidewithdrawRecord.setBankCardAccount(gatheringBank.getBankCode());//账号1234567
		insidewithdrawRecord.setOpenAccountBank(gatheringBank.getBankAddress());//开户银行 银行名称
		insidewithdrawRecord.setGatheringCodeId(gatheringBank.getId());//设置卡
		insidewithdrawRecord.setNote(note);//备注
		insidewithdrawRecord.setServiceCharge(serviceCharge);//手续费
        insidewithdrawRecord.setShoukuanNumber(shoukuanNumber);//收款卡号
        insidewithdrawRecord.setWithdrawAmount(new Double("0"));//提现额度
		insideWithdrawRecordRepo.save(insidewithdrawRecord);//添加记录数据
	}


	/**
	 * 审核通过
	 * serviceCharge 服务费
	 * shoukuanNumber 收款银行卡
	 * withdrawAmount 提现额度
	 * @param id
	 */
	@ParamValid
	@Transactional
	public void approved(@NotBlank String id, String note,String serviceCharge,String shoukuanNumber,String withdrawAmount) {
		InsidewithdrawRecord insidewithdrawRecord = insideWithdrawRecordRepo.getOne(id);//内部转折记录数据
		if (!Constant.提现记录状态_发起提现.equals(insidewithdrawRecord.getState())) {
			throw new BizException(BizError.只有状态为发起提现的记录才能审核通过);
		}
		if(shoukuanNumber.equals(insidewithdrawRecord.getBankCardAccount())){
			throw new BizException(BizError.输入的收款银行卡和出款卡一直请重新输入);

		}
		if(StringUtil.isNullOrEmpty(withdrawAmount) || StringUtil.isNullOrEmpty(shoukuanNumber) || StringUtil.isNullOrEmpty(serviceCharge)){
			throw new BizException(BizError.请输入提现额度);
		}

		GatheringCode gatheringCode=insidewithdrawRecord.getGatheringCode();
		if(gatheringCode.getBankTotalAmount()<Double.valueOf(withdrawAmount)){//提现额度大于卡号的总额度
			throw new BizException(BizError.输入的提现额度大于卡的总额度请从新输入额度);
		}


		GatheringCode gatheringCodeShouku=gatheringCodeRepo.findByBankCode(shoukuanNumber);//收款的银行卡号
		if(gatheringCodeShouku==null){//输入的收款银行卡不对请重新输入
			throw new BizException(BizError.收款的银行卡不对请重新输入);
		}
//		if(gatheringCodeShouku.getCardUse().equals("1")){//表示存款卡不能用于内部转折 卡用途 1.存款卡，2.付款卡，3.备用金卡
//			throw new BizException(BizError.输入的银行卡是存款卡不能内部转账);//只能是2.付款卡，3.备用金卡才能转账
//		}
		insidewithdrawRecord.approved(note);//备注
		insidewithdrawRecord.setServiceCharge(serviceCharge);//手续费
		insidewithdrawRecord.setShoukuanNumber(shoukuanNumber);//收款卡号
		insidewithdrawRecord.setWithdrawAmount(Double.valueOf(withdrawAmount));//提现额度
		insideWithdrawRecordRepo.save(insidewithdrawRecord);
	}

	/**
	 * 审核不通过
	 * 
	 * @param id
	 */
	@ParamValid
	@Transactional
	public void notApproved(@NotBlank String id, String note) {
		InsidewithdrawRecord withdrawRecord = insideWithdrawRecordRepo.getOne(id);
		if (!(Constant.提现记录状态_发起提现.equals(withdrawRecord.getState())
				|| Constant.提现记录状态_审核通过.equals(withdrawRecord.getState()))) {
			throw new BizException(BizError.只有状态为发起提现或审核通过的记录才能进行审核不通过操作);
		}

		withdrawRecord.notApproved(note);
		insideWithdrawRecordRepo.save(withdrawRecord);

//		UserAccount userAccount = withdrawRecord.getUserAccount();
//		double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() + withdrawRecord.getWithdrawAmount(), 4)
//				.doubleValue();
//		userAccount.setCashDeposit(cashDeposit);
		//userAccountRepo.save(userAccount);
	//	accountChangeLogRepo.save(AccountChangeLog.buildWithWithdrawNotApprovedRefund(userAccount, withdrawRecord));
		return;
	}

	/**
	 * 确认内部转账数据
	 * 
	 * @param id
	 */
	@ParamValid
	@Transactional
	public void confirmCredited(@NotBlank String id) {
		InsidewithdrawRecord withdrawRecord = insideWithdrawRecordRepo.getOne(id);//内部转账提现
		if(withdrawRecord==null){
			throw new BizException(BizError.内部转账没有查询到数据);
		}
		if (!(Constant.提现记录状态_审核通过.equals(withdrawRecord.getState()))) {
			throw new BizException(BizError.只有状态为审核通过的记录才能进行确认到帐操作);
		}
	String note=withdrawRecord.getNote();//获取备注信息

		withdrawRecord.confirmCredited();
		insideWithdrawRecordRepo.save(withdrawRecord);//修改确认到账状态

		//记录银行卡交易列表数据
		double serveiceChange=Double.valueOf(withdrawRecord.getServiceCharge());//手续费
		double withdrawAmount=withdrawRecord.getWithdrawAmount();//提现金额
		GatheringCode gatheringCode=withdrawRecord.getGatheringCode();//获取转账银行卡对象

		Double cardTotal=gatheringCode.getBankTotalAmount()-withdrawAmount-serveiceChange;//银行卡的总结余-提现金额-手续费

		BankcardRecord po = new BankcardRecord();//提现银行卡交易列表数据
		po.setId(IdUtils.getId());//主键
		po.setCreateTime(new Date());
		po.setAvailableFlag(true);//1表示成功
		po.setActualIncome(-withdrawAmount);//实际收款金额
		po.setMerchantOrderId(withdrawRecord.getId());//订单号
		po.setMerchantId(null);//商户号id
		po.setServiceCharge(-serveiceChange);//手续费
		po.setMerchantOrderNo(withdrawRecord.getId());//商户订单号
		po.setGatheringCodeId(withdrawRecord.getGatheringCodeId());//新的收款码 提现没有收款码
		po.setCardTotal(cardTotal);//银行卡总数据卡号结余

		Date now = new Date();
		long time = 3*1000;//3秒
		Date afterDate = new Date(now.getTime() + time);//3秒后的时间 延迟3秒
		po.setSettlementTime(afterDate);//结束时间结束时间 延迟3秒
		po.setNote(note);//备注信息
		bankcardRepo.save(po);//银行卡交易列表
	//	Double bankTotal=cardTotal;
				//gatheringCode.getBankTotalAmount()-withdrawAmount-serveiceChange;//银行卡的总余额=银行卡的总余额-提现额度-手续费
		gatheringCode.setBankTotalAmount(cardTotal);//设置新的额度
		gatheringCodeRepo.save(gatheringCode);//更新银行卡额度 付款银行卡


//		//先处理提现的银行卡
//		Specification<BankcardRecord> spec = new Specification<BankcardRecord>() {
//			/**
//			 *
//			 */
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<BankcardRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(withdrawRecord.getGatheringCode().getBankCode())){//提现银行卡号
//					predicates.add(builder.equal(root.join("gatheringCode", JoinType.INNER).get("bankCode"),
//							withdrawRecord.getGatheringCode().getBankCode()));//提现银行卡查询
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//		Page<BankcardRecord> bankcardComeRecrdReportVOList = bankcardRepo.findAll(spec,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime"))));//完成时间
//		Double cardTotal=0.00;//结余
//
//		List<BankcardRecord> BankcardRecordList=bankcardComeRecrdReportVOList.getContent();
//
//		if(BankcardRecordList!=null && BankcardRecordList.size()>0){//查询有数据
//			BankcardRecord  BankcardRecord=BankcardRecordList.get(0);//获取第一条数据
//			cardTotal= BankcardRecord.getCardTotal()-withdrawAmount-serveiceChange;//银行卡的总结余-提现金额-手续费
//		}else{//查询没有数据
//			cardTotal= gatheringCode.getBankTotalAmount()-withdrawAmount-serveiceChange;//银行卡的总结余-提现金额-手续费
//		}

/*************************************************************
 * 处理收款银行卡数据 添加数据和添加金额
 */
		GatheringCode gatheringCodeShouku=gatheringCodeRepo.findByBankCode(withdrawRecord.getShoukuanNumber());//收款的银行卡号
		if(gatheringCodeShouku==null){//输入的收款银行卡不对请重新输入
			throw new BizException(BizError.收款的银行卡不对请重新输入);
		}

		Double cardShoukuTotal=gatheringCodeShouku.getBankTotalAmount()+withdrawAmount;//收款卡新总金额=卡原以前金额+收款金额


//		//处理收款卡银行记录银行列表数据
//		Specification<BankcardRecord> specShouku = new Specification<BankcardRecord>() {
//			private static final long serialVersionUID = 1L;
//			public Predicate toPredicate(Root<BankcardRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				if(StrUtil.isNotBlank(withdrawRecord.getShoukuanNumber())){//银行卡号
//					predicates.add(builder.equal(root.join("gatheringCode", JoinType.INNER).get("bankCode"),
//							withdrawRecord.getShoukuanNumber()));//通过收款卡银行卡查询
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//		Page<BankcardRecord> bankcardShoukuComeRecrdReportVOList = bankcardRepo.findAll(specShouku,
//				PageRequest.of(0, 1, Sort.by(Sort.Order.desc("settlementTime"))));//收款银行卡查询数据
//
//

		BankcardRecord shoukupo = new BankcardRecord();//银行卡交易列表数据
		shoukupo.setId(IdUtils.getId());//主键
		shoukupo.setCreateTime(new Date());
		shoukupo.setAvailableFlag(true);//1表示成功
		shoukupo.setActualIncome(withdrawAmount);//实际收款金额
		shoukupo.setMerchantOrderId(withdrawRecord.getId());//订单号
		shoukupo.setMerchantId(null);//商户号id
		shoukupo.setServiceCharge(0.00);//手续费
		shoukupo.setMerchantOrderNo(withdrawRecord.getId());//商户订单号
		shoukupo.setGatheringCodeId(gatheringCodeShouku.getId());//新的收款码 提现没有收款码
		shoukupo.setCardTotal(cardShoukuTotal);//银行卡总数据卡号结余
		Date now1 = new Date();
		long time1 = 2*1000;//2秒
		Date afterDate1 = new Date(now1.getTime() + time1);//2秒后的时间 延迟3秒
		shoukupo.setSettlementTime(afterDate1);//结束时间结束时间 延迟3秒
		shoukupo.setNote(note);//备注信息
		bankcardRepo.save(shoukupo);//银行卡交易列表
		gatheringCodeShouku.setBankTotalAmount(cardShoukuTotal);//收款卡新总金额=原以前金额+收款金额
		gatheringCodeRepo.save(gatheringCodeShouku);//更新收款卡额度
	}



	@Transactional(readOnly = true)
	public PageResult<InsideWithdrawRecordVO> findTop5TodoWithdrawRecordByPage() {
		Specification<InsidewithdrawRecord> spec = new Specification<InsidewithdrawRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<InsidewithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.提现记录状态_发起提现));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<InsidewithdrawRecord> result = insideWithdrawRecordRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<InsideWithdrawRecordVO> pageResult = new PageResult<>(InsideWithdrawRecordVO.convertFor(result.getContent()), 1,
				5, result.getTotalElements());
		return pageResult;
	}


	/**
	 * 查询内部转折列表
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<InsideWithdrawRecordVO> insideFindWithdrawRecordByPage(InsideWithdrawRecordQueryCondParam param) {
		Specification<InsidewithdrawRecord> spec = new Specification<InsidewithdrawRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<InsidewithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getSubmitStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<InsidewithdrawRecord> result = insideWithdrawRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));

		PageResult<InsideWithdrawRecordVO> pageResult = new PageResult<>(InsideWithdrawRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

//	@ParamValid
//	@Transactional
//	public void startWithdrawWithBankCard(StartWithdrawParam param) {
//		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
//		if (userAccount.getBankInfoLatelyModifyTime() == null) {
//			throw new BizException(BizError.银行卡未绑定无法进行提现);
//		}
//		if (!new BCryptPasswordEncoder().matches(param.getMoneyPwd(), userAccount.getMoneyPwd())) {
//			throw new BizException(BizError.资金密码不正确);
//		}
//		WithdrawSetting withdrawSetting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
//		List<WithdrawRecord> withdrawRecords = withdrawRecordRepo
//				.findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(userAccount.getId(),
//						DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()));
//		if (withdrawRecords.size() >= withdrawSetting.getEverydayWithdrawTimesUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"每日提现次数为" + withdrawSetting.getEverydayWithdrawTimesUpperLimit() + "次,你已达到上限");
//		}
//		if (param.getWithdrawAmount() < withdrawSetting.getWithdrawLowerLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最低提现金额不能小于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawLowerLimit()));
//		}
//		if (param.getWithdrawAmount() > withdrawSetting.getWithdrawUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最高提现金额不能大于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawUpperLimit()));
//		}
//		double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - param.getWithdrawAmount(), 4)
//				.doubleValue();
//		if (cashDeposit < 0) {
//			throw new BizException(BizError.保证金余额不足);
//		}
//
//		WithdrawRecord withdrawRecord = param.convertToPo();
//		withdrawRecord.setBankInfo(userAccount);
//		withdrawRecordRepo.save(withdrawRecord);
//		userAccount.setCashDeposit(cashDeposit);
//		userAccountRepo.save(userAccount);
//		accountChangeLogRepo.save(AccountChangeLog.buildWithStartWithdraw(userAccount, withdrawRecord));
//	}
//
//	@ParamValid
//	@Transactional
//	public void startWithdrawWithVirtualWallet(StartWithdrawParam param) {
//		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
//		if (userAccount.getVirtualWalletLatelyModifyTime() == null) {
//			throw new BizException(BizError.电子钱包未绑定无法进行提现);
//		}
//		if (!new BCryptPasswordEncoder().matches(param.getMoneyPwd(), userAccount.getMoneyPwd())) {
//			throw new BizException(BizError.资金密码不正确);
//		}
//		WithdrawSetting withdrawSetting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
//		List<WithdrawRecord> withdrawRecords = withdrawRecordRepo
//				.findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(userAccount.getId(),
//						DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()));
//		if (withdrawRecords.size() >= withdrawSetting.getEverydayWithdrawTimesUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"每日提现次数为" + withdrawSetting.getEverydayWithdrawTimesUpperLimit() + "次,你已达到上限");
//		}
//		if (param.getWithdrawAmount() < withdrawSetting.getWithdrawLowerLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最低提现金额不能小于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawLowerLimit()));
//		}
//		if (param.getWithdrawAmount() > withdrawSetting.getWithdrawUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最高提现金额不能大于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawUpperLimit()));
//		}
//		double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - param.getWithdrawAmount(), 4)
//				.doubleValue();
//		if (cashDeposit < 0) {
//			throw new BizException(BizError.保证金余额不足);
//		}
//
//		WithdrawRecord withdrawRecord = param.convertToPo();
//		withdrawRecord.setVirtualWalletInfo(userAccount);
//		withdrawRecordRepo.save(withdrawRecord);
//		userAccount.setCashDeposit(cashDeposit);
//		userAccountRepo.save(userAccount);
//		accountChangeLogRepo.save(AccountChangeLog.buildWithStartWithdraw(userAccount, withdrawRecord));
//	}

//	@Transactional(readOnly = true)
//	public PageResult<WithdrawRecordVO> findLowerLevelWithdrawRecordByPage(
//			LowerLevelWithdrawRecordQueryCondParam param) {
//		UserAccount currentAccount = userAccountRepo.getOne(param.getCurrentAccountId());
//		UserAccount lowerLevelAccount = currentAccount;
//		if (StrUtil.isNotBlank(param.getUserName())) {
//			lowerLevelAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
//			if (lowerLevelAccount == null) {
//				throw new BizException(BizError.用户名不存在);
//			}
//			// 说明该用户名对应的账号不是当前账号的下级账号
//			if (!lowerLevelAccount.getAccountLevelPath().startsWith(currentAccount.getAccountLevelPath())) {
//				throw new BizException(BizError.不是上级账号无权查看该账号及下级的提现记录);
//			}
//		}
//		String lowerLevelAccountLevelPath = lowerLevelAccount.getAccountLevelPath();
//
//		Specification<WithdrawRecord> spec = new Specification<WithdrawRecord>() {
//			/**
//			 *
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public Predicate toPredicate(Root<WithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//				List<Predicate> predicates = new ArrayList<Predicate>();
//				predicates.add(builder.like(root.join("userAccount", JoinType.INNER).get("accountLevelPath"),
//						lowerLevelAccountLevelPath + "%"));
//				if (StrUtil.isNotEmpty(param.getAccountType())) {
//					predicates.add(builder.equal(root.join("userAccount", JoinType.INNER).get("accountType"),
//							param.getAccountType()));
//				}
//				if (StrUtil.isNotBlank(param.getState())) {
//					predicates.add(builder.equal(root.get("state"), param.getState()));
//				}
//				if (param.getSubmitStartTime() != null) {
//					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
//							DateUtil.beginOfDay(param.getSubmitStartTime())));
//				}
//				if (param.getSubmitEndTime() != null) {
//					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
//							DateUtil.endOfDay(param.getSubmitEndTime())));
//				}
//				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
//			}
//		};
//		Page<WithdrawRecord> result = withdrawRecordRepo.findAll(spec,
//				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
//		PageResult<WithdrawRecordVO> pageResult = new PageResult<>(WithdrawRecordVO.convertFor(result.getContent()),
//				param.getPageNum(), param.getPageSize(), result.getTotalElements());
//		return pageResult;
//	}


	/**
	 * 内部转账记录数据
	 * @param withdrawRecord
	 */
	@ParamValid
	@Transactional
	public void startInsideWithdrawWithVirtualWallet(InsidewithdrawRecord withdrawRecord) {


//		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
//		if (userAccount.getVirtualWalletLatelyModifyTime() == null) {
//			throw new BizException(BizError.电子钱包未绑定无法进行提现);
//		}
//		if (!new BCryptPasswordEncoder().matches(param.getMoneyPwd(), userAccount.getMoneyPwd())) {
//			throw new BizException(BizError.资金密码不正确);
//		}
//		WithdrawSetting withdrawSetting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
//		List<WithdrawRecord> withdrawRecords = withdrawRecordRepo
//				.findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(userAccount.getId(),
//						DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()));
//		if (withdrawRecords.size() >= withdrawSetting.getEverydayWithdrawTimesUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"每日提现次数为" + withdrawSetting.getEverydayWithdrawTimesUpperLimit() + "次,你已达到上限");
//		}
//		if (param.getWithdrawAmount() < withdrawSetting.getWithdrawLowerLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最低提现金额不能小于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawLowerLimit()));
//		}
//		if (param.getWithdrawAmount() > withdrawSetting.getWithdrawUpperLimit()) {
//			throw new BizException(BizError.业务异常.getCode(),
//					"最高提现金额不能大于" + new DecimalFormat("###################.###########")
//							.format(withdrawSetting.getWithdrawUpperLimit()));
//		}
//		double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - param.getWithdrawAmount(), 4)
//				.doubleValue();
//		if (cashDeposit < 0) {
//			throw new BizException(BizError.保证金余额不足);
//		}

		//InsidewithdrawRecord withdrawRecord = new InsidewithdrawRecord();

		//withdrawRecord.setVirtualWalletInfo(userAccount);
		insideWithdrawRecordRepo.save(withdrawRecord);//添加记录数据

		//userAccount.setCashDeposit(cashDeposit);
		//userAccountRepo.save(userAccount);
		//accountChangeLogRepo.save(AccountChangeLog.buildWithStartWithdraw(userAccount, withdrawRecord));
	}

	/**
	 * 通过银行卡号和发起提现状态=1来查询内部转账数据
	 * @param bankCardAccount 银行卡号
	 * @param state=1
	 * @return
	 */
	@Transactional(readOnly = true)
	public 	List<InsidewithdrawRecord> findByBankCardAccountAndState(String bankCardAccount,String state) {
		return insideWithdrawRecordRepo.findByBankCardAccountAndState(bankCardAccount,state);
	}

}
