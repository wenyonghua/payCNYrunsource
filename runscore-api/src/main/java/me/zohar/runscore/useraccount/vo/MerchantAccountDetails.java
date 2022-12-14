package me.zohar.runscore.useraccount.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import me.zohar.runscore.merchant.vo.LoginMerchantInfoVO;

public class MerchantAccountDetails implements UserDetails {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String userName;

	private String loginPwd;

	private String merchantName;//商户名称

	private String merchantNum;//商户号
	private String remoteAddress;//IP地址
	private String tokenValue;//sessionid

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}




	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}






	public MerchantAccountDetails(LoginMerchantInfoVO loginMerchantInfo) {
		if (loginMerchantInfo != null) {
			this.id = loginMerchantInfo.getId();
			this.userName = loginMerchantInfo.getUserName();
			this.loginPwd = loginMerchantInfo.getLoginPwd();
			this.merchantName = loginMerchantInfo.getMerchantName();
			this.merchantNum = loginMerchantInfo.getMerchantNum();
			this.tokenValue=loginMerchantInfo.getTokenValue();//获取token
			this.remoteAddress=loginMerchantInfo.getRemoteAddress();
		}
	}

	public String getMerchantId() {
		return this.id;
	}

	/**
	 * 获取登陆用户账号商户名
	 *
	 * @return
	 */
	public String getMerchantName() {
		return this.merchantName;
	}

	public String getMerchantNum() {
		return merchantNum;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return this.loginPwd;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
