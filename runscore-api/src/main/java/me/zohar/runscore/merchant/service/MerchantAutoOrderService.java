package me.zohar.runscore.merchant.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.common.utils.HttpUtils;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.common.vo.PageResult;
import me.zohar.runscore.common.vo.PageResult1;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.dictconfig.service.ConfigService;
import me.zohar.runscore.dictconfig.vo.ConfigItemVO;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeRepo;
import me.zohar.runscore.gatheringcode.repo.GatheringCodeUsageRepo;
import me.zohar.runscore.mastercontrol.domain.ReceiveOrderSetting;
import me.zohar.runscore.mastercontrol.repo.ReceiveOrderSettingRepo;
import me.zohar.runscore.mastercontrol.repo.SystemSettingRepo;
import me.zohar.runscore.merchant.domain.*;
import me.zohar.runscore.merchant.param.*;
import me.zohar.runscore.merchant.repo.*;
import me.zohar.runscore.merchant.vo.*;
import me.zohar.runscore.rechargewithdraw.domain.InsidewithdrawRecord;
import me.zohar.runscore.rechargewithdraw.service.InsideWithdrawService;
import me.zohar.runscore.useraccount.domain.AccountChangeLog;
import me.zohar.runscore.useraccount.domain.AccountReceiveOrderChannel;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.AccountChangeLogRepo;
import me.zohar.runscore.useraccount.repo.AccountReceiveOrderChannelRepo;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;
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

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * ?????????????????????
 */
@Validated
@Slf4j
@Service
public class MerchantAutoOrderService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;



	@Autowired
	private MerchantAutoOrderRepo merchantAutOrderRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;//????????????







	/**
	 * ??????????????????????????????????????????
	 * @param param
	 * @return
	 */
	@Transactional(readOnly = true)
	public PageResult<MerchantAutoOrderVO> findMerchantAutoOrderByPage(MerchantAutoOrderQueryCondParam param) {
		Specification<MerchantAutoOrder> spec = new Specification<MerchantAutoOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantAutoOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(StrUtil.isNotBlank(param.getBankFlow())){//????????????
					predicates.add(builder.equal(root.get("bankFlow"), param.getBankFlow()));
				}

				if (StrUtil.isNotBlank(param.getOrderNo())) {//?????????
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}

				if (StrUtil.isNotBlank(param.getOutOrderNo())) {//???????????????
					predicates.add(builder.equal(root.get("outOrderNo"), param.getOutOrderNo()));
				}

				if (StrUtil.isNotBlank(param.getOrderState())) {//????????????
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getAutoAmount())) {//?????????????????????
					predicates.add(builder.equal(root.get("autoAmount"), param.getAutoAmount()));
				}
				if (StrUtil.isNotBlank(param.getNote())) {//????????????
					predicates.add(builder.equal(root.get("note"), param.getNote()));
				}

				if (StrUtil.isNotBlank(param.getBankRemark())) {//????????????????????????
					predicates.add(builder.equal(root.get("bankRemark"), param.getBankRemark()));
				}

				if (StrUtil.isNotBlank(param.getBankCardAccount())) {//?????????????????????
					String cardAccount=	param.getBankCardAccount();
					String companyEntities11[]=cardAccount.split(";");
					List<String> companyEntities=new ArrayList<>();
					for(int i=0;i<companyEntities11.length;i++){
						companyEntities.add(companyEntities11[i]);
					}
					Path<Object> path = root.get("bankNumber");//????????????

							//root.join("gatheringCode", JoinType.INNER).get("bankCode");//??????????????????
					CriteriaBuilder.In<Object> in = builder.in(path);
					for (int i = 0; i <companyEntities.size() ; i++) {
						in.value(companyEntities.get(i));//?????????
					}
					predicates.add(builder.and(builder.and(in)));//?????????????????????
				  }//?????????????????????



				if (param.getSubmitStartTime() != null) {
					//System.out.println("????????????="+param.getSubmitStartTime());
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));//????????????
				}
				if (param.getSubmitEndTime() != null) {
					//System.out.println("????????????="+param.getSubmitEndTime());
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));//????????????
				}
//				if (param.getReceiveOrderStartTime() != null) {
//					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
//							DateUtil.beginOfDay(param.getReceiveOrderStartTime())));
//				}
//				if (param.getReceiveOrderEndTime() != null) {
//					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
//							DateUtil.endOfDay(param.getReceiveOrderEndTime())));
//				}



				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantAutoOrder> result = merchantAutOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));

		PageResult<MerchantAutoOrderVO> pageResult = new PageResult<>(MerchantAutoOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	/**
	 * ??????????????????????????????
	 * JSONObject json
	 * @return
	 */
	@Transactional
	public boolean saveMerchantAutoOrder(JSONObject json,List<MerchantOrder> merchantOrderList,String saveFailError) {
		try{
			MerchantAutoOrder merchantAutoOrder=new MerchantAutoOrder();
			String status="2";//????????????
			String card=json.getString("card");//????????????
			String amount=json.getString("amount");//????????????
			String balance=json.getString("balance");//??????????????????
			String remark=json.getString("remark");//????????????
			String bankFlow=json.getString("order");//??????????????????
			String bankRemark=json.getString("bankRemark");//????????????????????????
			String time=json.getString("time");//????????????

			if(merchantOrderList!=null && merchantOrderList.size()>=1){//??????????????????
				MerchantOrder merchantOrder=merchantOrderList.get(0);
				status="1";//????????????
				merchantAutoOrder.setOrderNo(merchantOrder.getOrderNo());//?????????
				merchantAutoOrder.setOutOrderNo(merchantOrder.getOutOrderNo());//???????????????
				merchantAutoOrder.setGatheringAmount(merchantOrder.getGatheringAmount());//??????????????????
			}
			GatheringCode gatheringBank=gatheringCodeRepo.findByBankCode(card);//???????????????
			if(gatheringBank!=null){//?????????????????????????????????????????????
				merchantAutoOrder.setBankName(gatheringBank.getBankAddress());//???????????? ??????????????????
				merchantAutoOrder.setCardName(gatheringBank.getBankUsername());//?????????
			}

			merchantAutoOrder.setId(IdUtils.getId());//?????????
			merchantAutoOrder.setBankFlow(bankFlow);//??????????????????
			merchantAutoOrder.setBankNumber(card);//??????
			merchantAutoOrder.setAutoAmount(amount);//????????????
			merchantAutoOrder.setNote(remark);//??????
			merchantAutoOrder.setBalance(balance);//???????????????
			merchantAutoOrder.setOrderState(status);//????????????
			SimpleDateFormat sdf =new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
			Date finshDate = sdf.parse(time);
			merchantAutoOrder.setSubmitTime(finshDate);//????????????
			merchantAutoOrder.setReturnMessage(json.toJSONString());//??????????????????
			merchantAutoOrder.setBankRemark(bankRemark);////????????????????????????
			merchantAutoOrder.setSaveFailError(saveFailError);//??????????????????????????????
			merchantAutOrderRepo.save(merchantAutoOrder);//????????????????????????
			return true;
		}catch (Exception ex){
			ex.toString();
			return false;
		}
	}

	@ParamValid
	@Transactional
	public MerchantAutoOrder findByBankFlowEquals(String bankFlow){
		return  merchantAutOrderRepo.findByBankFlowEquals(bankFlow);
	}






























}
