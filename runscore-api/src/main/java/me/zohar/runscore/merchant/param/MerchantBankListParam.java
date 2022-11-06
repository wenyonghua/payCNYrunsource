package me.zohar.runscore.merchant.param;

import lombok.Data;
import me.zohar.runscore.common.utils.IdUtils;
import me.zohar.runscore.useraccount.domain.AccountReceiveOrderChannel;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class MerchantBankListParam {

	private String payCardNo;//银行卡号
	private String serverCharge;//服务费用
	private String note;//备注信息
	private String payAmount;//付款金额

}
