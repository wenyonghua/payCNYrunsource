package me.zohar.runscore.statisticalanalysis.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;
import me.zohar.runscore.statisticalanalysis.domain.merchant.MerchantEverydayStatistical;

@Data
public class IndexStatisticalVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	private Date date;

	private Double tradeAmount;

	private Long paidOrderNum;

	private Long orderNum;

	public static List<IndexStatisticalVO> convertForEvery(List<MerchantEverydayStatistical> statisticals) {
		if (CollectionUtil.isEmpty(statisticals)) {
			return new ArrayList<>();
		}
		List<IndexStatisticalVO> vos = new ArrayList<>();
		for (MerchantEverydayStatistical statistical : statisticals) {
			vos.add(convertForEvery(statistical));
		}
		return vos;
	}

	public static IndexStatisticalVO convertForEvery(MerchantEverydayStatistical statistical) {
		if (statistical == null) {
			return null;
		}
		IndexStatisticalVO vo = new IndexStatisticalVO();
		BeanUtils.copyProperties(statistical, vo);
		vo.setDate(statistical.getEveryday());
		return vo;
	}

}
