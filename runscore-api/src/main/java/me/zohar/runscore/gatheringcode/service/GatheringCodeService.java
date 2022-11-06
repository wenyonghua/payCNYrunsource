package me.zohar.runscore.gatheringcode.service;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.criteria.*;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import me.zohar.runscore.common.utils.HttpUtils;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.dictconfig.ConfigHolder;
import me.zohar.runscore.merchant.domain.BankcardRecord;
import me.zohar.runscore.merchant.domain.GatheringChannel;
import me.zohar.runscore.merchant.repo.BankcardRepo;
import me.zohar.runscore.merchant.repo.GatheringChannelRepo;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.util.StrUtil;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.param.GatheringCodeParam;
import me.zohar.runscore.gatheringcode.param.GatheringCodeQueryCondParam;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.gatheringcode.vo.GatheringCodeUsageVO;
import me.zohar.runscore.gatheringcode.vo.GatheringCodeVO;
import me.zohar.runscore.mastercontrol.domain.ReceiveOrderSetting;
import me.zohar.runscore.mastercontrol.repo.ReceiveOrderSettingRepo;
import me.zohar.runscore.storage.domain.Storage;
import me.zohar.runscore.storage.repo.StorageRepo;
import me.zohar.runscore.useraccount.domain.AccountReceiveOrderChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.AccountReceiveOrderChannelRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;

@Validated
@Service
public class GatheringCodeService {

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private GatheringCodeUsageRepo gatheringCodeUsageRepo;

	@Autowired
	private StorageRepo storageRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;

	@Autowired
	private ReceiveOrderSettingRepo receiveOrderSettingRepo;

	@Autowired
	private BankcardRepo bankcardRepo;//银行卡交易记录数据

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Transactional
	public void switchGatheringCode(List<String> gatheringCodeIds, String userAccountId) {
		Map<String, Boolean> channelMap = new HashMap<>();
		List<GatheringCode> gatheringCodes = gatheringCodeRepo.findByUserAccountId(userAccountId);
		for (GatheringCode gatheringCode : gatheringCodes) {
			boolean inUse = false;
			for (String gatheringCodeId : gatheringCodeIds) {
				if (gatheringCode.getId().equals(gatheringCodeId)) {
					inUse = true;
					break;
				}
			}
			gatheringCode.setInUse(inUse);
			gatheringCodeRepo.save(gatheringCode);
			if (channelMap.get(gatheringCode.getGatheringChannelId()) == null) {
				channelMap.put(gatheringCode.getGatheringChannelId(), inUse);
			} else {
				channelMap.put(gatheringCode.getGatheringChannelId(),
						channelMap.get(gatheringCode.getGatheringChannelId()) || inUse);
			}

		}
		List<AccountReceiveOrderChannel> accountReceiveOrderChannels = accountReceiveOrderChannelRepo
				.findByUserAccountIdAndChannelDeletedFlagFalse(userAccountId);
		for (AccountReceiveOrderChannel accountReceiveOrderChannel : accountReceiveOrderChannels) {
			if (Constant.账号接单通道状态_已禁用.equals(accountReceiveOrderChannel.getState())) {
				continue;
			}
			boolean inUse = channelMap.get(accountReceiveOrderChannel.getChannelId()) != null
					&& channelMap.get(accountReceiveOrderChannel.getChannelId());
			accountReceiveOrderChannel.setState(inUse ? Constant.账号接单通道状态_开启中 : Constant.账号接单通道状态_关闭中);
			accountReceiveOrderChannelRepo.save(accountReceiveOrderChannel);
		}
	}

	@Transactional
	public void delMyGatheringCodeById(String id, String userAccountId) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		if (!userAccountId.equals(gatheringCode.getUserAccountId())) {
			throw new BizException(BizError.无权删除数据);
		}

		ReceiveOrderSetting setting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting.getAuditGatheringCode()) {
			gatheringCode.initiateAudit(Constant.收款码审核类型_删除);
		} else {
			delGatheringCodeById(id,gatheringCode.getBankCode());
		}
	}

	/**
	 * 删除收款码
	 * @param id
	 */
	@Transactional
	public void delGatheringCodeById(String id,String titleBankCode) {
		//GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);//查看收款码
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);

		if(!titleBankCode.equals(gatheringCode.getBankCode())){//删除的银行卡号进行对比不等于就提示给前台
			throw new BizException(BizError.删除的银行卡号不匹配);
		}
		if(gatheringCode.getStorageId()!=null) {
			storageRepo.deleteById(gatheringCode.getStorageId());
			//	disassociationGatheringCodeStorage(gatheringCode.getStorageId());
		}
			gatheringCodeRepo.delete(gatheringCode);
		//gatheringCodeUsageRepo.delete(gatheringCodeUsage);
	}

	@Transactional(readOnly = true)
	public GatheringCodeVO findMyGatheringCodeById(String id, String userAccountId) {
		GatheringCodeVO vo = findGatheringCodeById(id);
		if (!userAccountId.equals(vo.getUserAccountId())) {
			throw new BizException(BizError.无权查看数据);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public GatheringCodeVO findGatheringCodeById(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		return GatheringCodeVO.convertFor(gatheringCode);
	}

	@Transactional(readOnly = true)
	public GatheringCodeUsageVO findGatheringCodeUsageById(String id) {
		GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);
//		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
//		gatheringCodeUsage.setDailyQuota(gatheringCode.getDailyQuota());//单日额度限制

		return GatheringCodeUsageVO.convertFor(gatheringCodeUsage);
	}


	/**
	 * 新方法查看单个收款卡详情
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public GatheringCodeVO findNewGatheringCodeUsageById(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		return GatheringCodeVO.convertNewFor(gatheringCode);
	}

	@Transactional(readOnly = true)
	public List<GatheringCodeUsageVO> findAllGatheringCode(String userAccountId) {
		return GatheringCodeUsageVO.convertFor(gatheringCodeUsageRepo.findByUserAccountId(userAccountId));
	}

	@Transactional(readOnly = true)
	public PageResult<GatheringCodeUsageVO> findMyGatheringCodeUsageByPage(GatheringCodeQueryCondParam param) {
		if (StrUtil.isBlank(param.getUserAccountId())) {
			throw new BizException(BizError.无权查看数据);
		}
		return findGatheringCodeUsageByPage(param);
	}

	@Transactional(readOnly = true)
	public PageResult<GatheringCodeUsageVO> findGatheringCodeUsageByPage(GatheringCodeQueryCondParam param) {
		Specification<GatheringCodeUsage> spec = new Specification<GatheringCodeUsage>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCodeUsage> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotEmpty(param.getGatheringChannelId())) {
					predicates.add(builder.equal(root.get("gatheringChannelId"), param.getGatheringChannelId()));
				}
				if (StrUtil.isNotEmpty(param.getPayee())) {//收款人
					predicates.add(builder.equal(root.get("payee"), param.getPayee()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {//所属账号
					predicates.add(builder.equal(root.join("userAccount", JoinType.INNER).get("userName"),
							param.getUserName()));
				}
				//银行卡号
				if (StrUtil.isNotEmpty(param.getBankCode())) {//银行卡号
					predicates.add(builder.equal(root.get("bankCode"), param.getBankCode()));//银行卡号
				}
				if (StrUtil.isNotEmpty(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));//所属账号
				}
				if(StrUtil.isNotEmpty(param.getCardUse())){//卡用途
					predicates.add(builder.equal(root.get("cardUse"), param.getCardUse()));
				}
				if(StrUtil.isNotEmpty(param.getInUse())){//卡状态 1是使用，0是停用
					Boolean inUse=false;
					if(param.getInUse().equals("1")){//使用中
						inUse=true;
					}
					predicates.add(builder.equal(root.get("inUse"), inUse));//卡状态 1是使用，0是停用
				}
				if(StrUtil.isNotEmpty(param.getQiXianUser())){//卡期限  1:永久使用，0：永久停用
					predicates.add(builder.equal(root.get("qiXianUser"), param.getQiXianUser()));
				}


				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCodeUsage> result = gatheringCodeUsageRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));

		PageResult<GatheringCodeUsageVO> pageResult = new PageResult<>(
				GatheringCodeUsageVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}


	/**
	 * 查询新的银行卡码
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<GatheringCodeVO> findGatheringCodeByPage(GatheringCodeQueryCondParam param) {
		Specification<GatheringCode> spec = new Specification<GatheringCode>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCode> root, CriteriaQuery<?> query,
										 CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}

				if (StrUtil.isNotEmpty(param.getGatheringChannelId())) {
					predicates.add(builder.equal(root.get("gatheringChannelId"), param.getGatheringChannelId()));//渠道ID
				}

				if(StrUtil.isNotEmpty(param.getCardUse())){//卡用途 1存款卡，2付款卡，3备用金卡
					predicates.add(builder.equal(root.get("cardUse"), param.getCardUse()));
				}
				if(StrUtil.isNotEmpty(param.getInUse())){//卡状态 1是使用，0是停用
					Boolean inUse=false;
					if(param.getInUse().equals("1")){//使用中
						inUse=true;
					}
					predicates.add(builder.equal(root.get("inUse"), inUse));//卡状态 1是使用，0是停用
				}

				if (StrUtil.isNotEmpty(param.getPayee())) {//收款人
					predicates.add(builder.equal(root.get("payee"), param.getPayee()));
				}
				if(StrUtil.isNotEmpty(param.getAutoRun())){//自动机状态 自动机 1:启用，0:停用
					predicates.add(builder.equal(root.get("autoRun"), param.getAutoRun()));//自动机状态 自动机 1:启用，0:停用
				}

				if (StrUtil.isNotEmpty(param.getUserName())) {//所属账号
					predicates.add(builder.equal(root.join("userAccount", JoinType.INNER).get("userName"),
							param.getUserName()));
				}
				//银行卡号单个查询
				if (StrUtil.isNotEmpty(param.getBankCode())) {//银行卡号
					predicates.add(builder.equal(root.get("bankCode"), param.getBankCode()));//银行卡号
				}


				if (StrUtil.isNotBlank(param.getBankCardAccount())) {//查询银行卡数据
					String cardAccount=	param.getBankCardAccount();
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.get("bankCode");//查询卡号数据
							//root.join("gatheringCode", JoinType.INNER).get("bankCode");//定义查询的字
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//存入值
					}
					predicates.add(builder.and(builder.and(in)));//存入条件集合里
				}//查询银行卡数据 多个银行卡查询


				if (StrUtil.isNotEmpty(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));//所属账号
				}
				if(StrUtil.isNotEmpty(param.getQiXianUser())){//卡期限  1:永久使用，0：永久停用
					predicates.add(builder.equal(root.get("qiXianUser"), param.getQiXianUser()));
				}
				if(StrUtil.isNotEmpty(param.getCheckOrderModeState())){//查单模式  1:自动，0：人工
					predicates.add(builder.equal(root.get("checkOrderModeState"), param.getCheckOrderModeState()));
				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCode> result = gatheringCodeRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));

		PageResult<GatheringCodeVO> pageResult = new PageResult<>(
				GatheringCodeVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());

		return pageResult;
	}




	@Transactional(readOnly = true)
	public PageResult<GatheringCodeUsageVO> findTop5TodoAuditGatheringCodeByPage() {
		Specification<GatheringCodeUsage> spec = new Specification<GatheringCodeUsage>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCodeUsage> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.收款码状态_待审核));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCodeUsage> result = gatheringCodeUsageRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("initiateAuditTime"))));
		PageResult<GatheringCodeUsageVO> pageResult = new PageResult<>(
				GatheringCodeUsageVO.convertFor(result.getContent()), 1, 5, result.getTotalElements());
		return pageResult;
	}

	/**
	 * 添加收款码和图片的关系
	 * @param storageId
	 * @param gatheringCodeId
	 */
	@Transactional
	public void associateGatheringCodeStorage(String storageId, String gatheringCodeId) {
		Storage storage = storageRepo.getOne(storageId);
		storage.setAssociateId(gatheringCodeId);//渠道编码id 号 更新渠道编码ID号
		storage.setAssociateBiz("gatheringCode");
		storageRepo.save(storage);
	}

	/**
	 * 更新收款码
	 * @param storageId
	 */
	@Transactional
	public void disassociationGatheringCodeStorage(String storageId) {
		Storage oldStorage = storageRepo.getOne(storageId);
		oldStorage.setAssociateId(null);
		oldStorage.setAssociateBiz(null);
		storageRepo.save(oldStorage);
	}




	/**
	 * 更新收款码
	 * @param id
	 */
	@Transactional
	public void updateToNormalState(String id) {
		//GatheringCodeUsage gatheringCodeUsage = gatheringCodeUsageRepo.getOne(id);//查看收款码

		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setAuditType(null);
		gatheringCode.setInitiateAuditTime(null);
		gatheringCode.setState(Constant.收款码状态_正常);
		gatheringCodeRepo.save(gatheringCode);
	}

	/**
	 * 添加收款码
	 * @param param
	 */
	@Transactional
	public void addOrUpdateGatheringCode(GatheringCodeParam param) {
		String userAccountId = null;
		if (StrUtil.isBlank(param.getId())) {
			if (StrUtil.isBlank(param.getUserName())) {
				throw new BizException(BizError.参数异常);
			}
			UserAccount userAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
			if (userAccount == null) {
				throw new BizException(BizError.找不到所属账号无法新增收款码);
			}

			String id=param.getGatheringChannelId();//收款通道ID
			GatheringChannel gatheringChannel= gatheringChannelRepo.getOne(id);
			if(gatheringChannel!=null) {
				//if (gatheringChannel.getChannelCode().equals("bankcard")) {//如果是卡转卡才进行判断
					if (StringUtil.isNullOrEmpty(param.getBankCode()) || StringUtil.isNullOrEmpty(param.getBankReflect()) || StringUtil.isNullOrEmpty(param.getBankTotalAmount()) || StringUtil.isNullOrEmpty(param.getCardUse())) {
						throw new BizException(BizError.请输入正确数据);
					}
					GatheringCode gatheringCodeShouku = gatheringCodeRepo.findByBankCode(param.getBankCode());//查看收款的银行卡号
					if (gatheringCodeShouku != null) {//表里面有同样的收款卡
						throw new BizException(BizError.不能添加相同的收款卡);
					}
			//	}
//				else{//其他支付方式比如支付宝扫码，微信扫码 就设置默认值
//					param.setBankReflect("99999");//卡收款额度限制
//					param.setBankTotalAmount("0.00");//设置总额度
//					param.setInUser("1");//1默认使用中
//					param.setCardUse("1");//银行卡用途 1存款卡
//					param.setQiXianUser("1");//1：永久使用，0：永久停用
//				}
			}
			userAccountId = userAccount.getId();
			addGatheringCode(param, userAccountId);//添加数据
		} else {//修改数据
			if(StringUtil.isNullOrEmpty(param.getBankCode()) || StringUtil.isNullOrEmpty(param.getBankReflect()) || StringUtil.isNullOrEmpty(param.getBankTotalAmount())|| StringUtil.isNullOrEmpty(param.getCardUse())){
				throw new BizException(BizError.请输入正确数据);
			}
			GatheringCode gatheringCode = gatheringCodeRepo.getOne(param.getId());
			updateGatheringCode(param, gatheringCode.getUserAccountId());//修改数据
		}
	}

	@ParamValid
	@Transactional
	public void addGatheringCode(GatheringCodeParam param, String userAccountId) {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		param.setFixedGatheringAmount(!receiveOrderSetting.getUnfixedGatheringCodeReceiveOrder());

		if (param.getFixedGatheringAmount()) {
			if (param.getGatheringAmount() == null) {
				throw new BizException(BizError.参数异常);
			}
			if (param.getGatheringAmount() <= 0) {
				throw new BizException(BizError.参数异常);
			}
		}
		GatheringCode gatheringCode = param.convertToPo(userAccountId);
		ReceiveOrderSetting setting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting.getAuditGatheringCode()) {//true
			gatheringCode.initiateAudit(Constant.收款码审核类型_新增);
		}
		gatheringCode.setBankReflect(Double.valueOf(param.getBankReflect()));//银行卡的限制额度
		gatheringCode.setBankTotalAmount(Double.valueOf(param.getBankTotalAmount()));//银行卡的总额度
		gatheringCode.setCardUse(param.getCardUse());//银行卡用途
		if(param.getInUser().equals("1")){//使用中
			gatheringCode.setInUse(true);
		}else{
			gatheringCode.setInUse(false);
		}

		gatheringCodeRepo.save(gatheringCode);//添加收款码gathering_code表的数据
		if(StrUtil.isNotBlank(param.getStorageId())){//添加卡的二维码图片地址
			associateGatheringCodeStorage(param.getStorageId(), gatheringCode.getId());
		}

//	      //v_gathering_code_usage
//		GatheringCodeUsage ghc =new GatheringCodeUsage();
//		ghc.setId(IdUtils.getId());//id 号
//		ghc.setState(Constant.收款码状态_正常);
//		ghc.setPayee(param.getPayee());//收款人
//		ghc.setInUse(true);//使用中
//		ghc.setAuditType("1");
//		ghc.setUserAccountId(userAccountId);//账户ID
//
//
//		ghc.setTotalPaidOrderNum(0l);//收款次数
//		ghc.setTotalOrderNum(0l);//总收款次数
//		ghc.setTotalPaidOrderNum(0l);//总接单次数
//		ghc.setTodaySuccessRate(0.0);//成功率
//		ghc.setTodayOrderNum(0l);//总
//
//		ghc.setTodayTradeAmount(new Double(0));//今日收款今日收款金额
//		ghc.setTodayOrderNum(0l);//今日收款次数
//		ghc.setTodayPaidOrderNum(0l);//今日接单次数
//		ghc.setTodaySuccessRate(0.0);//今日成功率
//
//
//		ghc.setBankCode(param.getBankCode());//银行名称
//		ghc.setBankAddress(param.getBankAddress());//银行卡号
//		ghc.setBankUsername(param.getBankUsername());//户主
//		ghc.setGatheringChannelId(param.getGatheringChannelId());//渠道编号
//		ghc.setCreateTime(new Date());
//		ghc.setGathering_code_id(gatheringCode.getId());//编码表
//		gatheringCodeUsageRepo.save(ghc);
	}

	@ParamValid
	@Transactional
	public void updateGatheringCode(GatheringCodeParam param, String userAccountId) {

		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		param.setFixedGatheringAmount(!receiveOrderSetting.getUnfixedGatheringCodeReceiveOrder());
		if (param.getFixedGatheringAmount()) {
			if (param.getGatheringAmount() == null) {
				throw new BizException(BizError.参数异常);
			}
			if (param.getGatheringAmount() <= 0) {
				throw new BizException(BizError.参数异常);
			}
		}

		GatheringCode gatheringCode = gatheringCodeRepo.getOne(param.getId());
		if (!gatheringCode.getUserAccountId().equals(userAccountId)) {
			throw new BizException(BizError.无权修改收款码);
		}

		String 	totalAmountParam=param.getBankTotalAmount();//系统参数金额
		Double bankTotalAmountSystem =gatheringCode.getBankTotalAmount();//数据库金
		boolean vaa=Double.valueOf(totalAmountParam).compareTo(bankTotalAmountSystem)==0;//如果等于0就表示金额一致
		if(!vaa) {//修改金额不一致和数据库金额不一致就需要添加一个银行卡交易记录
				BankcardRecord po = new BankcardRecord();
				po.setId(IdUtils.getId());
				po.setCreateTime(new Date());
				po.setAvailableFlag(true);//1表示成功
				po.setActualIncome(Double.valueOf(param.getBankTotalAmount()));//实际收款金额
				po.setMerchantOrderId(param.getId());//订单号
				po.setMerchantId(param.getId());//商户号id
				po.setServiceCharge(Double.valueOf("0.00"));//手续费
				po.setMerchantOrderNo(param.getId());//商户订单号
				po.setGatheringCodeId(param.getId());//新的收款码 提现没有收款码
				po.setCardTotal(Double.valueOf(param.getBankTotalAmount()));//银行卡总数据
				po.setSettlementTime(new Date());//结束时间
				bankcardRepo.save(po);//银行卡交易列表
		}
//		// 取消关联旧的收款码图片
		if(gatheringCode.getStorageId()!=null) {
			if (!param.getStorageId().equals(gatheringCode.getStorageId())) {//如果收款码不一致
				//storageRepo.deleteById(gatheringCode.getStorageId());//删除数据
				disassociationGatheringCodeStorage(gatheringCode.getStorageId());//把以前的更新掉 不能删除数据因为有订单关联了这个图片

				associateGatheringCodeStorage(param.getStorageId(), gatheringCode.getId());//重新添加图片
			}
		}
		gatheringCode.setBankAddress(param.getBankAddress());//银行名称
		gatheringCode.setBankCode(param.getBankCode());//卡号
		gatheringCode.setBankUsername(param.getBankUsername());//卡户主
		gatheringCode.setPayee(param.getPayee());//收款人
		gatheringCode.setBankReflect(Double.valueOf(param.getBankReflect()));//设置银行卡的限制额度
		gatheringCode.setBankTotalAmount(Double.valueOf(param.getBankTotalAmount()));//卡的总余额
		gatheringCode.setCardUse(param.getCardUse());//银行卡用途
		gatheringCode.setQiXianUser(param.getQiXianUser());//银行卡使用期限范围 1是永久使用,0是永久停用
		gatheringCode.setDailyQuota(param.getDailyQuota());//设置银行卡的单日额度限制
		if(param.getStorageId()!=null) {
			gatheringCode.setStorageId(param.getStorageId());
		}
		boolean inUse=false;
		if(param.getInUser().equals("1")){//启用
			inUse=true;
		}
		if(param.getQiXianUser().equals("0")){//如果银行卡的期限状态是0永久停用状态,就需要把银行卡的状态设置成停用状态
			inUse=false;
		}
		gatheringCode.setInUse(inUse);//设置卡的状态 1是使用true，0是停用false
		if(param.getBankAccount()!=null) {
			gatheringCode.setBankAccount(param.getBankAccount());//银行的登录账号
		}
		if(param.getBankPassord()!=null) {
			gatheringCode.setBankPassord(param.getBankPassord());//银行的登录密码
		}
		if(param.getBankIp()!=null) {
			gatheringCode.setBankIp(param.getBankIp());//登录IP
		}
		if(param.getAutoRun()!=null) {
			gatheringCode.setAutoRun(param.getAutoRun());//自动机状态
		}
		if(param.getCheckOrderModeState()!=null) {
			gatheringCode.setCheckOrderModeState(param.getCheckOrderModeState());//自动机状态 0人工1是自动
		}
		gatheringCodeRepo.save(gatheringCode);//更新数据


//      if(param.getStorageId()!=null){
//		  associateGatheringCodeStorage(param.getStorageId(), gatheringCode.getId());
//	  }
	}


	/**
	 * 查询银行列表在使用的存款卡列表数据
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<GatheringCodeUsageVO> findBankUserStateByPage(GatheringCodeQueryCondParam param) {
		Specification<GatheringCodeUsage> spec = new Specification<GatheringCodeUsage>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCodeUsage> root, CriteriaQuery<?> query,
										 CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
//				if (StrUtil.isNotEmpty(param.getState())) {
//					predicates.add(builder.equal(root.get("state"), param.getState()));
//				}
//				if (StrUtil.isNotEmpty(param.getGatheringChannelId())) {
//					predicates.add(builder.equal(root.get("gatheringChannelId"), param.getGatheringChannelId()));
//				}

				if(StrUtil.isNotEmpty(param.getCardUse())){//卡用途
					predicates.add(builder.equal(root.get("cardUse"), param.getCardUse()));
				}
				if(StrUtil.isNotEmpty(param.getInUse())){//卡状态 1是使用，0是停用
					Boolean inUse=false;
					if(param.getInUse().equals("1")){//使用中
						inUse=true;
					}
					predicates.add(builder.equal(root.get("inUse"), inUse));//卡状态 1是使用，0是停用
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCodeUsage> result = gatheringCodeUsageRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));

		PageResult<GatheringCodeUsageVO> pageResult = new PageResult<>(
				GatheringCodeUsageVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		List<GatheringCodeUsageVO> GatheringCodeUsageVOList =pageResult.getContent();

		return GatheringCodeUsageVOList;
	}

	/**
	 * 更新收款码状态
	 * @param id
	 * @param state 状态
	 */
	@Transactional
	public void updateToNormalState(String id,boolean state) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setInUse(state);//状态
		gatheringCodeRepo.save(gatheringCode);
	}

	/**
	 * 启动自动机
	 * @param id
	 */
	@Transactional
	public void startRun(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setAutoRun("1");//启动自动机

		//api url地址
		String url=ConfigHolder.getConfigValue("autoInterfaseAddress");
		if(url==null){
			url="http://144.34.163.64:9000";
		}
		url=url+"/api/v1/cmd";
		//post请求
		HttpMethod method =HttpMethod.POST;
		JSONObject json = new JSONObject();
		json.put("cmd", "start");//启用
		json.put("card", gatheringCode.getBankCode());//银行卡号
		json.put("bank", gatheringCode.getBankAddress());//银行名称
		json.put("name", gatheringCode.getBankAccount());//银行的登录账号
		json.put("pwd", gatheringCode.getBankPassord());//银行登录密码
		System.out.print("发送数据："+json.toString());
		//发送http请求并返回结果
		String result = "";
		try {
			result=HttpUtils.HttpRestClient(url, method, json);
			JSONObject jsonValue= JSONObject.parseObject(result);
			result=jsonValue.toJSONString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}catch (Exception ex){
			result=ex.toString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}
		gatheringCode.setAutoRunMessage(result);//错误数据记录
		gatheringCodeRepo.save(gatheringCode);//修改自动机状态
	}

	/**
	 * 停用自动机
	 * @param id
	 */
	@Transactional
	public void stopRun(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setAutoRun("0");//启动自动机


		//api url地址
		String url=ConfigHolder.getConfigValue("autoInterfaseAddress");
		if(url==null){
			url="http://144.34.163.64:9000";
		}
		url=url+"/api/v1/cmd";
		//post请求
		HttpMethod method =HttpMethod.POST;
		JSONObject json = new JSONObject();
		json.put("cmd", "stop");//停用
		json.put("card", gatheringCode.getBankCode());//银行卡号
		json.put("bank", gatheringCode.getBankAddress());//银行名称
		json.put("name", gatheringCode.getBankAccount());//银行的登录账号
		json.put("pwd", gatheringCode.getBankPassord());//银行登录密码
		System.out.print("发送数据："+json.toString());
		//发送http请求并返回结果
		String result = "";
		try {
			result=HttpUtils.HttpRestClient(url, method, json);
			JSONObject jsonValue= JSONObject.parseObject(result);
			result=jsonValue.toJSONString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}catch (Exception ex){
			result=ex.toString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}
		gatheringCode.setAutoRunMessage(result);//错误数据记录
		gatheringCodeRepo.save(gatheringCode);//修改自动机状态
	}

	/**
	 * 测试自动机链接
	 * @param id
	 */
	@Transactional
	public void testAutoUse(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		//api url地址
		String url =ConfigHolder.getConfigValue("autoInterfaseAddress");
		if(url==null){
			url="http://144.34.163.64:9000";
		}
		url=url+"/api/v1/verify";
		//post请求
		HttpMethod method =HttpMethod.POST;
		JSONObject json = new JSONObject();
		json.put("card", gatheringCode.getBankCode());//银行卡号
		json.put("bank", gatheringCode.getBankAddress());//银行名称
		json.put("name", gatheringCode.getBankAccount());//银行的登录账号
		json.put("pwd", gatheringCode.getBankPassord());//银行登录密码
		System.out.print("发送数据："+json.toString());
		//发送http请求并返回结果
		String result = "";
		try {
			result=HttpUtils.HttpRestClient(url, method, json);
			JSONObject jsonValue= JSONObject.parseObject(result);
			result=jsonValue.toJSONString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}catch (Exception ex){
			result=ex.toString();
			System.out.print("请求地址="+url+"请求参数="+json+"接收反馈："+result);
		}
		gatheringCode.setAutoRunMessage(result);//错误数据记录
		gatheringCodeRepo.save(gatheringCode);//修改自动机状态
	}

	/**
	 * 银行卡号查询数据
	 * @param bankCard
	 */
	@Transactional
	public GatheringCodeVO getBankBalance(String bankCard) {
		GatheringCode gatheringCode = gatheringCodeRepo.findByBankCode(bankCard);//查看收款的银行卡号

		return GatheringCodeVO.convertFor(gatheringCode);
	}

}
