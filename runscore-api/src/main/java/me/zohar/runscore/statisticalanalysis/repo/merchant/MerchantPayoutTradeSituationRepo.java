package me.zohar.runscore.statisticalanalysis.repo.merchant;

import me.zohar.runscore.statisticalanalysis.domain.merchant.MerchantPayoutTradeSituation;
import me.zohar.runscore.statisticalanalysis.domain.merchant.MerchantTradeSituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 付款各商户交易情况
 */
public interface MerchantPayoutTradeSituationRepo
		extends JpaRepository<MerchantPayoutTradeSituation, String>, JpaSpecificationExecutor<MerchantPayoutTradeSituation> {

}
