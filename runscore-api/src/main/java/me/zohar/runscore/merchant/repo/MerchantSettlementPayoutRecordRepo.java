package me.zohar.runscore.merchant.repo;

import me.zohar.runscore.merchant.domain.MerchantSettlementPayOutRecord;
import me.zohar.runscore.merchant.domain.MerchantSettlementRecord;
import me.zohar.runscore.rechargewithdraw.domain.RechargeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface MerchantSettlementPayoutRecordRepo
		extends JpaRepository<MerchantSettlementPayOutRecord, String>, JpaSpecificationExecutor<MerchantSettlementPayOutRecord> {

	//long deleteByApplyTimeGreaterThanEqualAndApplyTimeLessThanEqual(Date startTime, Date endTime);

	/**
	 * 通过提现记录查询提现付款银行卡
	 * @param merchantSettlId
	 * @return
	 */
	List<MerchantSettlementPayOutRecord> findByMerchantSettlId(String merchantSettlId);
	
}
