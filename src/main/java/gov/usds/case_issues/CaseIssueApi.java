package gov.usds.case_issues;

// import java.sql.Connection;
// import java.sql.SQLException;

// import javax.sql.DataSource;

// import org.springframework.beans.BeansException;
// import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.core.Ordered;
// import org.springframework.core.annotation.Order;
// import org.springframework.jdbc.datasource.AbstractDataSource;
// import org.springframework.retry.annotation.Backoff;
// import org.springframework.retry.annotation.EnableRetry;
// import org.springframework.retry.annotation.Retryable;

@SpringBootApplication
@EnableConfigurationProperties
// @EnableRetry
public class CaseIssueApi {

	// @Order(Ordered.HIGHEST_PRECEDENCE)
	// private class RetryableDataSourceBeanPostProcessor implements BeanPostProcessor {
	// 	@Override
	// 	public Object postProcessBeforeInitialization(Object bean, String beanName)
	// 			throws BeansException {
	// 		if (bean instanceof DataSource) {
	// 			bean = new RetryableDataSource((DataSource)bean);
	// 		}
	// 		return bean;
	// 	}

	// 	@Override
	// 	public Object postProcessAfterInitialization(Object bean, String beanName)
	// 			throws BeansException {
	// 		return bean;
	// 	}
	// }

	public static void main(String[] args) {
		SpringApplication.run(CaseIssueApi.class, args);
	}


	// @Bean
	// public BeanPostProcessor dataSouceWrapper() {
	// 	return new RetryableDataSourceBeanPostProcessor();
	// }
}

// class RetryableDataSource extends AbstractDataSource {

// 	private DataSource delegate;

// 	public RetryableDataSource(DataSource delegate) {
// 		this.delegate = delegate;
// 	}

// 	@Override
// 	@Retryable(maxAttempts=10, backoff=@Backoff(multiplier=2.3, maxDelay=1000))
// 	public Connection getConnection() throws SQLException {
// 		return delegate.getConnection();
// 	}

// 	@Override
// 	@Retryable(maxAttempts=10, backoff=@Backoff(multiplier=2.3, maxDelay=1000))
// 	public Connection getConnection(String username, String password)
// 			throws SQLException {
// 		return delegate.getConnection(username, password);
// 	}

// }
