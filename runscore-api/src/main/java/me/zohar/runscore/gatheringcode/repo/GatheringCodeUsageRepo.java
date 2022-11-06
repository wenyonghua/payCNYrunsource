package me.zohar.runscore.gatheringcode.repo;

import java.util.List;

import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.zohar.runscore.gatheringcode.domain.GatheringCodeUsage;

public interface GatheringCodeUsageRepo
		extends JpaRepository<GatheringCodeUsage, String>, JpaSpecificationExecutor<GatheringCodeUsage> {

	List<GatheringCodeUsage> findByUserAccountId(String userAccountId);

	GatheringCodeUsage findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndFixedGatheringAmountFalseAndInUseTrue(
			String userAccountId, String gatheringChannelCode, String state);


	GatheringCodeUsage findTopByUserAccountIdAndGatheringChannelChannelCodeAndGatheringAmountAndStateAndInUseTrue(
			String userAccountId, String gatheringChannelCode, Double gatheringAmount, String state);

	GatheringCodeUsage findTopByUserAccountIdAndGatheringChannelChannelCodeAndStateAndInUseTrue(
			String userAccountId, String gatheringChannelCode, String state);
}
