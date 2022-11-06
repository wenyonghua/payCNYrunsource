package me.zohar.runscore.merchant.repo;

import me.zohar.runscore.merchant.domain.MerchantAutoOrder;
import me.zohar.runscore.merchant.domain.MerchantOrder;
import me.zohar.runscore.merchant.domain.MerchantOrderPayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface MerchantAutoOrderRepo
		extends JpaRepository<MerchantAutoOrder, String>, JpaSpecificationExecutor<MerchantAutoOrder> {




	MerchantAutoOrder findByBankFlowEquals(String bankFlow);//通银行流水记录查询数据

}
