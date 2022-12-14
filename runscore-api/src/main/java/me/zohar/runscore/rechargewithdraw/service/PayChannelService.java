package me.zohar.runscore.rechargewithdraw.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import cn.hutool.core.util.StrUtil;
import me.zohar.runscore.common.valid.ParamValid;
import me.zohar.runscore.rechargewithdraw.domain.PayChannel;
import me.zohar.runscore.rechargewithdraw.domain.PayType;
import me.zohar.runscore.rechargewithdraw.param.AddOrUpdatePayChannelParam;
import me.zohar.runscore.rechargewithdraw.param.AddOrUpdatePayTypeParam;
import me.zohar.runscore.rechargewithdraw.repo.PayChannelRepo;
import me.zohar.runscore.rechargewithdraw.repo.PayTypeRepo;
import me.zohar.runscore.rechargewithdraw.vo.PayChannelVO;
import me.zohar.runscore.rechargewithdraw.vo.PayTypeVO;

@Validated
@Service
public class PayChannelService {

	@Autowired
	private PayTypeRepo payTypeRepo;

	@Autowired
	private PayChannelRepo payChannelRepo;

	@Transactional(readOnly = true)
	public List<PayTypeVO> findEnabledPayType() {
		List<PayType> payTypes = payTypeRepo.findByEnabledIsTrueAndDeletedFlagIsFalseOrderByOrderNoAsc();
		return PayTypeVO.convertFor(payTypes);
	}

	@Transactional(readOnly = true)
	public List<PayChannelVO> findEnabledPayChannel() {
		List<PayChannel> payChannels = payChannelRepo.findByEnabledIsTrueAndDeletedFlagIsFalseOrderByOrderNoAsc();
		return PayChannelVO.convertFor(payChannels);
	}

	@Transactional(readOnly = true)
	public List<PayTypeVO> findAllPayType() {
		List<PayType> payTypes = payTypeRepo.findByDeletedFlagIsFalseOrderByOrderNoAsc();
		return PayTypeVO.convertFor(payTypes);
	}

	@Transactional(readOnly = true)
	public List<PayChannelVO> findAllPayChannel() {
		List<PayChannel> payChannels = payChannelRepo.findByDeletedFlagIsFalseOrderByOrderNoAsc();
		return PayChannelVO.convertFor(payChannels);
	}

	@ParamValid
	@Transactional
	public void addOrUpdatePayType(AddOrUpdatePayTypeParam param) {
		// ??????
		if (StrUtil.isBlank(param.getId())) {
			PayType payType = param.convertToPo();
			payTypeRepo.save(payType);
		}
		// ??????
		else {
			PayType payType = payTypeRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, payType);
			payTypeRepo.save(payType);
		}
	}

	@Transactional(readOnly = true)
	public PayTypeVO findPayTypeById(@NotBlank String id) {
		return PayTypeVO.convertFor(payTypeRepo.getOne(id));
	}

	@Transactional
	public void delPayTypeById(@NotBlank String id) {
		PayType payType = payTypeRepo.getOne(id);
		payType.deleted();
		payTypeRepo.save(payType);
	}

	@ParamValid
	@Transactional
	public void addOrUpdatePayChannel(AddOrUpdatePayChannelParam param) {
		// ??????
		if (StrUtil.isBlank(param.getId())) {
			PayChannel payChannel = param.convertToPo();
			payChannelRepo.save(payChannel);
		}
		// ??????
		else {
			PayChannel payChannel = payChannelRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, payChannel);
			payChannelRepo.save(payChannel);
		}
	}

	@Transactional(readOnly = true)
	public PayChannelVO findPayChannelById(@NotBlank String id) {
		return PayChannelVO.convertFor(payChannelRepo.getOne(id));
	}

	@Transactional
	public void delPayChannelById(@NotBlank String id) {
		PayChannel payChannel = payChannelRepo.getOne(id);
		payChannel.deleted();
		payChannelRepo.save(payChannel);
	}

}
