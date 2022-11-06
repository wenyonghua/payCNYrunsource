package me.zohar.runscore.merchant.param;

import lombok.Data;
import me.zohar.runscore.useraccount.param.AccountReceiveOrderChannelParam;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class MerchantBankSettlementParam {

	@NotBlank
	private String id;

	private List<MerchantBankListParam> bankList;

}
