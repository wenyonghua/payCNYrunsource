package me.zohar.runscore.useraccount.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.zohar.runscore.agent.param.AdjustRebateParam;
import me.zohar.runscore.agent.param.GenerateInviteCodeParam;
import me.zohar.runscore.agent.service.AgentService;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.useraccount.param.LowerLevelAccountQueryCondParam;
import me.zohar.runscore.useraccount.service.AccountChannelRebateService;
import me.zohar.runscore.useraccount.service.UserAccountService;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;

@Controller
@RequestMapping("/agent")
public class AgentController {

	@Autowired
	private AgentService agentService;

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private AccountChannelRebateService accountChannelRebateService;

	@PostMapping("/adjustRebate")
	@ResponseBody
	public Result adjustRebate(@RequestBody List<AdjustRebateParam> params) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		accountChannelRebateService.adjustRebate(user.getUserAccountId(), params);
		return Result.success();
	}

	@PostMapping("/generateInviteCodeAndGetInviteRegisterLink")
	@ResponseBody
	public Result generateInviteCodeAndGetInviteRegisterLink(@RequestBody GenerateInviteCodeParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setInviterId(user.getUserAccountId());
		String inviteCodeId = agentService.generateInviteCode(param);
		return Result.success().setData(agentService.getInviteCodeDetailsInfoById(inviteCodeId));
	}

	@GetMapping("/findLowerLevelAccountDetailsInfoByPage")
	@ResponseBody
	public Result findLowerLevelAccountDetailsInfoByPage(LowerLevelAccountQueryCondParam param) {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		param.setCurrentAccountId(user.getUserAccountId());
		return Result.success().setData(userAccountService.findLowerLevelAccountDetailsInfoByPage(param));
	}

}
