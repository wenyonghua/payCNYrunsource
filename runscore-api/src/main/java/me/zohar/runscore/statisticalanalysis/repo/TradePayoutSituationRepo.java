package me.zohar.runscore.statisticalanalysis.repo;

import me.zohar.runscore.statisticalanalysis.domain.TradePayoutSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TradePayoutSituationRepo
		extends JpaRepository<TradePayoutSituation, String>, JpaSpecificationExecutor<TradePayoutSituation> {
	/**
	 * 统计总数
	 * @return
	 */
	TradePayoutSituation findTopBy();

}
