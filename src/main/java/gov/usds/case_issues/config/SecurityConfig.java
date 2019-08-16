package gov.usds.case_issues.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LOGGER.info("Configuring HTTP Security");
		http
			.cors()
				.and()
			.authorizeRequests()
				.antMatchers("/health")
					.permitAll()
				.and()
			.csrf()
				.ignoringRequestMatchers(AnyRequestMatcher.INSTANCE)
		;
	}
}
