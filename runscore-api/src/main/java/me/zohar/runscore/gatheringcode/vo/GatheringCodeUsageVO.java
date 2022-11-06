package me.zohar.runscore.gatheringcode.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import me.zohar.runscore.util.DecimalFormatUtil;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import me.zohar.runscore.dictconfig.DictHolder;
import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;

@Data
public class GatheringCodeUsageVO implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	private String id;

	private String gatheringChannelId;

	/**
	 * 通道名称
	 */
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
	 * 使用中
	 */
	private Boolean  inUse;

	/**
	 * 使用中名称
	 */
	private String  inUseName;

	/**
	 * 发起审核时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date initiateAuditTime;

	/**
	 * 审核类型
	 */
	private String auditType;

	private String auditTypeName;

	/**
	 * 银行卡总数据 总余额
	 */
	private Double bankTotalAmount;
	/**
	 * 银行卡总数据 总余额 这个界面展示
	 */
	private String bankTotalAmountView;
	/**
	 * 银行卡总额度限制
	 */
	private Double bankReflect;
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
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date createTime;

	private String storageId;

	private String userName;

	private Double totalTradeAmount;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double todayTradeAmount;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;//成功率

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
	private String inUser;

	public static List<GatheringCodeUsageVO> convertFor(Collection<GatheringCodeUsage> gatheringCodeUsages) {
		if (CollectionUtil.isEmpty(gatheringCodeUsages)) {
			return new ArrayList<>();
		}
		List<GatheringCodeUsageVO> vos = new ArrayList<>();
		for (GatheringCodeUsage gatheringCodeUsage : gatheringCodeUsages) {
			vos.add(convertFor(gatheringCodeUsage));
		}
		return vos;
	}

	public static GatheringCodeUsageVO convertFor(GatheringCodeUsage gatheringCodeUsage) {
		if (gatheringCodeUsage == null) {
			return null;
		}
		GatheringCodeUsageVO vo = new GatheringCodeUsageVO();
		BeanUtils.copyProperties(gatheringCodeUsage, vo);
		vo.setStateName(DictHolder.getDictItemName("gatheringCodeState", vo.getState()));//1.正常,2.待审核
		if (StrUtil.isNotBlank(vo.getAuditType())) {
			vo.setAuditTypeName(DictHolder.getDictItemName("gatheringCodeAuditType", vo.getAuditType()));
		}
		if (gatheringCodeUsage.getUserAccount() != null) {
			vo.setUserName(gatheringCodeUsage.getUserAccount().getUserName());
		}
		if (gatheringCodeUsage.getGatheringChannel() != null) {
			vo.setGatheringChannelName(gatheringCodeUsage.getGatheringChannel().getChannelName());
		}
		vo.setCardUseName(DictHolder.getDictItemName("cardUseState", vo.getCardUse()));//卡用途
		String inuser="0";
		if(vo.getInUse()){//true
			inuser="1";//使用中
		}
		vo.setInUseName(DictHolder.getDictItemName("cardState", inuser));//卡状态
		vo.setInUser(inuser);

		vo.setQiXianUser(gatheringCodeUsage.getQiXianUser());//期限代码 1和0
		vo.setQiXianUserName(DictHolder.getDictItemName("cardQixianState", gatheringCodeUsage.getQiXianUser()));//银行卡期限名称 1是永久使用，0是永久停用

		String vv=DecimalFormatUtil.formatString(new BigDecimal(vo.getBankTotalAmount()), null);
		vo.setBankTotalAmountView(vv);//银行卡总余额使用界面展示吧金额格式化了 "#,###.00";
		vo.setDailyQuota(gatheringCodeUsage.getDailyQuota());//单日额度限制
		return vo;
	}

	public static void main(String args[]){

	}

}
