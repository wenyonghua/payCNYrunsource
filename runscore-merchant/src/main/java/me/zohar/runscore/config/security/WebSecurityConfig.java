package me.zohar.runscore.config.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Autowired
	private AuthenticationSuccessHandler successHandler;

	@Autowired
	private AuthenticationFailHandler failHandler;

	@Autowired
	private LogoutHandler logoutHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable();//开启跨域访问
		http
				.exceptionHandling()
				.authenticationEntryPoint(customAuthenticationEntryPoint)
				.and()
				.csrf().disable()
				.headers().frameOptions().disable()
				.and()
				.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/pay").permitAll()
				.antMatchers("/Pay247").permitAll()
				.antMatchers("/FastPay").permitAll()
				.antMatchers("/BankQr").permitAll()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/storage/fetch/**").permitAll()
				.antMatchers("/masterControl/**").permitAll()
				.antMatchers("/merchantOrder/startOrder").permitAll()
				.antMatchers("/merchantOrder/merchantOrdernewExport").permitAll()
				.antMatchers("/merchantOrder/newrderPayMerhantlistExport").permitAll()
				.antMatchers("/merchantOrder/outstartAddOrder").permitAll()
				.antMatchers("/merchantOrder/outstartNewAddOrder").permitAll()
				.antMatchers("/merchantOrder/outFindOrderDetailsById").permitAll()
				.antMatchers("/merchantPayoutOrder/payoutSaveOrder").permitAll()//代付下单接口
				.antMatchers("/merchantPayoutOrder/outFindPayOutOrderDetailsById").permitAll()//代付查询接口
				.anyRequest().authenticated()
				.and().formLogin().loginPage("/login").loginProcessingUrl("/login")
				.successHandler(successHandler).failureHandler(failHandler).permitAll()
				.and().logout().logoutUrl("/logout").logoutSuccessHandler(logoutHandler).permitAll();

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/images/**", "/js/**", "/plugins/**");

	}

	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.authenticationProvider(customAuthenticationProvider);
	}

	@Bean
	public FilterRegistrationBean<Filter> updateLastAccessTimeFilter() {
		FilterRegistrationBean<Filter> updateLastAccessTimeFilter = new FilterRegistrationBean<Filter>(
				new UpdateLastAccessTimeFilter());
		updateLastAccessTimeFilter.setOrder(1);
		updateLastAccessTimeFilter.setEnabled(true);
		updateLastAccessTimeFilter.addUrlPatterns("/*");
		return updateLastAccessTimeFilter;
	}
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		configuration.setAllowCredentials(false);//是否支持安全证书(必需参数)
		configuration.addAllowedOrigin(CorsConfiguration.ALL); //允许任何域名
		configuration.addAllowedHeader(CorsConfiguration.ALL); //允许任何请求头
		configuration.addAllowedMethod(CorsConfiguration.ALL); //允许任何请求方法
		source.registerCorsConfiguration("/**", configuration);//访问路径
		return source;
	}

}
