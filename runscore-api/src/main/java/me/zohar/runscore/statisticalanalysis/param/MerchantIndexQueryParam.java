package me.zohar.runscore.statisticalanalysis.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class MerchantIndexQueryParam {

	/**
	 * 商户号
	 */
	@NotBlank
	private String merchantId;

	/**
	 * 开始时间
	 */
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime;

	/**
	 * 结束时间
	 */
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime;

}
