package me.zohar.runscore.config.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.zohar.runscore.common.exception.BizError;
import me.zohar.runscore.common.exception.BizException;
import me.zohar.runscore.useraccount.domain.UserAccount;
import me.zohar.runscore.useraccount.repo.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgentUtil;
import me.zohar.runscore.common.vo.Result;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.useraccount.service.LoginLogService;
import me.zohar.runscore.useraccount.service.UserAccountService;
import me.zohar.runscore.useraccount.vo.UserAccountDetails;

/**
 * 登录成功处理类
 * 
 * @author zohar
 * @date 2019年1月23日
 *
 */
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private UserAccountService userAccountService;

	@Autowired
	private LoginLogService loginLogService;
	@Autowired
	private UserAccountRepo userAccountRepo;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		UserAccountDetails user = (UserAccountDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		loginLogService.recordLoginLog(RequestContextHolder.currentRequestAttributes().getSessionId(),
				user.getUsername(), Constant.系统_后台管理, Constant.登录状态_成功, Constant.登录提示_登录成功,
				HttpUtil.getClientIP(request), UserAgentUtil.parse(request.getHeader("User-Agent")));
		userAccountService.updateLatelyLoginTime(user.getUserAccountId());

//		UserAccount existUserAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(user.getUsername());
//		String ipwhitelist=existUserAccount.getIpWhitelist();//ip白名单
//
//		String ipValue=getRemortIP(request);
//		System.out.println(">>>>>>请求的IP="+ipValue);//
//		if(!(user.getUsername().equals("admin") || user.getUsername().equals("admin1")|| user.getUsername().equals("admin2") || user.getUsername().equals("system") || user.getUsername().equals("zohar001"))){//其他用户需要校验IP
//			if(ipwhitelist==null){//其他用户没有配置IP
//				throw new BizException("1","请管理人员配置IP="+ipValue);
//			}
//			if (!ipwhitelist.contains(ipValue)) {//ip不在白名单里面
//				throw new BizException("1","ip不在白名单里面,请管理人员配置IP="+ipValue);
//			}
//		}


		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(JSONObject.toJSONString(Result.success().setMsg(Constant.登录提示_登录成功)));
		out.flush();
		out.close();
	}

	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

}
