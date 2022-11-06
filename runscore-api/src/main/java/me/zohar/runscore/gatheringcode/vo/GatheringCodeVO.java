package me.zohar.runscore.gatheringcode.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;
import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;

import javax.persistence.Column;

@Data
public class GatheringCodeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	private String id;

	private String gatheringChannelName;

	/**
	 * 状态,启用:1;禁用:0
	 */
	private String state;

	private String stateName;

	private Boolean fixedGatheringAmount;

	/**
	 * 收款金额
	 */
	private Double gatheringAmount;

	/**
	 * 收款人
	 */
	private String payee;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date createTime;

	private String storageId;

	/**
	 * 投注人用户账号id
	 */
	private String userAccountId;

	private String userName;

	private String gatheringChannelId;
	/**
	 * 银行卡总数据
	 */
	private Double bankTotalAmount;
	/**
	 * 银行卡总数据 总余额 这个界面展示
	 */
	private String bankTotalAmountView;
	/**
	 * 收款卡额度限制
	 */
	private Double bankReflect;//收款卡额度限制

	/**
	 * 卡用途 1.存款卡，2.付款卡，3.备用金卡，
	 * @param userAccountId
	 * @return
	 */
	private String cardUse;
	/**
	 * 卡用途 1.存款卡，2.付款卡，3.备用金卡，
	 * @param userAccountId
	 * @return
	 */
	private String cardUseName;




	/**
	 * 使用中名称
	 */
	private String  inUseName;
	/**
	 * 使用中
	 */
	private Boolean  inUse;

	private String inUser;
	/**
	 * 银行卡单日额度限制
	 */
	private String dailyQuota;

	/**
	 * 银行卡期限使用 名称 1是永久使用，0是永久停用
	 */
	private String qiXianUser;
	/**
	 *银行卡期限使用 名称
	 */
	private String qiXianUserName;
	/**
	 * 审核类型
	 */
	private String auditType;
	/**
	 * 审核类型名称
	 */
	private String auditTypeName;
	/**
	 * 银行卡号
	 */
	private String bankCode;

	/**
	 * 开户行
	 */
	private String bankAddress;

	/**
	 * 卡户主
	 */
	private String bankUsername;

	/**
	 * 银行的登录账号
	 * @param auditType
	 */
	private String bankAccount;

	/**
	 * 银行的登录账号密码
	 * @param auditType
	 */
	private String bankPassord;
	/**
	 * 银行的登录账号Ip
	 * @param auditType
	 */
	private String bankIp;

	/**
	 * 自动机状态 1:启用，0:停用
	 * @param auditType
	 */
	private String autoRun;
	/**
	 * 自动机状态名称 1:启用，0:停用
	 * @param auditType
	 */
	private String autoRunName;

	/**
	 * 同步自动机返回的数据
	 */
	private String autoRunMessage;

	private String checkOrderModeState;//查单方式 0=人工，1是自动
	private String checkOrderModeName;




	public static GatheringCodeVO convertFor(GatheringCode gatheringCode) {
		if (gatheringCode == null) {
			return null;
		}
		GatheringCodeVO vo = new GatheringCodeVO();
		BeanUtils.copyProperties(gatheringCode, vo);
		vo.setStateName(DictHolder.getDictItemName("gatheringCodeState", vo.getState()));
		if (gatheringCode.getUserAccount() != null) {
			vo.setUserName(gatheringCode.getUserAccount().getUserName());
		}
		if (gatheringCode.getGatheringChannel() != null) {
			vo.setGatheringChannelName(gatheringCode.getGatheringChannel().getChannelName());
		}
		String vv= DecimalFormatUtil.formatString(new BigDecimal(gatheringCode.getBankTotalAmount()), null);
		vo.setBankTotalAmountView(vv);//银行卡总余额使用界面展示吧金额格式化了 "#,###.00";
		return vo;
	}

	/**
	 * 新的方法
	 * @param gatheringCode
	 * @return
	 */
	public static List<GatheringCodeVO> convertFor(Collection<GatheringCode> gatheringCode) {
		if (CollectionUtil.isEmpty(gatheringCode)) {
			return new ArrayList<>();
		}
		List<GatheringCodeVO> vos = new ArrayList<>();
		for (GatheringCode GatheringCodeVOList : gatheringCode) {
			vos.add(convertNewFor(GatheringCodeVOList));
		}
		return vos;
	}


	/**
	 * 新方法处理
	 * @param gatheringCode
	 * @return
	 */
	public static GatheringCodeVO convertNewFor(GatheringCode gatheringCode) {
		if (gatheringCode == null) {
			return null;
		}
		GatheringCodeVO vo = new GatheringCodeVO();
		BeanUtils.copyProperties(gatheringCode, vo);
		vo.setStateName(DictHolder.getDictItemName("gatheringCodeState", vo.getState()));//1.正常,2.待审核
		if (StrUtil.isNotBlank(vo.getAuditType())) {
			vo.setAuditTypeName(DictHolder.getDictItemName("gatheringCodeAuditType", vo.getAuditType()));
		}

		if (gatheringCode.getUserAccount() != null) {
			vo.setUserName(gatheringCode.getUserAccount().getUserName());
		}
		if (gatheringCode.getGatheringChannel() != null) {
			vo.setGatheringChannelName(gatheringCode.getGatheringChannel().getChannelName());//渠道名称
		}
		vo.setCardUseName(DictHolder.getDictItemName("cardUseState", vo.getCardUse()));//卡用途
		String inuser="0";
		if(vo.getInUse()){//true
			inuser="1";//使用中
		}
		vo.setInUseName(DictHolder.getDictItemName("cardState", inuser));//卡状态
		vo.setInUser(inuser);

		vo.setQiXianUser(gatheringCode.getQiXianUser());//期限代码 1和0
		vo.setQiXianUserName(DictHolder.getDictItemName("cardQixianState", gatheringCode.getQiXianUser()));//银行卡期限名称 1是永久使用，0是永久停用

		String vv=DecimalFormatUtil.formatString(new BigDecimal(vo.getBankTotalAmount()), null);
		vo.setBankTotalAmountView(vv);//银行卡总余额使用界面展示吧金额格式化了 "#,###.00";
		vo.setDailyQuota(gatheringCode.getDailyQuota());//单日额度限制
		vo.setAutoRunName(DictHolder.getDictItemName("autoRunState", gatheringCode.getAutoRun()));//自动机状态名称 1是启用0是停用
		vo.setCheckOrderModeName(DictHolder.getDictItemName("checkOrderMode", gatheringCode.getCheckOrderModeState()));//查单方式 0=人工，1是自动

		return vo;
	}



}
