package me.zohar.runscore.gatheringcode.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.zohar.runscore.gatheringcode.domain.GatheringCode;

public interface GatheringCodeRepo
		extends JpaRepository<GatheringCode, String>, JpaSpecificationExecutor<GatheringCode> {

	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);

	GatheringCode findTopByUserAccountIdAndGatheringChannelChannelCodeAndGatheringAmountAndStateAndInUseTrue(
			String userAccountId, String gatheringChannelCode, Double gatheringAmount, String state);

	List<GatheringCode> findByUserAccountId(String userAccountId);

	List<GatheringCode> findByFixedGatheringAmount(Boolean fixedGatheringAmount);

	GatheringCode findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndFixedGatheringAmountFalseAndInUseTrue(
			String userAccountId, String gatheringChannelCode, String state);


	GatheringCode findByBankCode(String bankCode);//银行卡号查询收款码对象

	//List<GatheringCode> findByUserAccountIdAndInUseTrue(String userAccountId);

	List<GatheringCode> findByUserAccountIdAndInUseTrue(String userAccountId);

	List<GatheringCode> findByUserAccountIdAndCardUseAndInUseTrue(String userAccountId,String cardUser);
		//CardUseAndInUseTrue

}
