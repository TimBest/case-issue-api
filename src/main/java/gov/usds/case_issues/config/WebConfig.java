package gov.usds.case_issues.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration that customizes Spring MVC for our specific needs.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(WebConfig.class);

	@Autowired
	private WebConfigurationProperties _customProperties;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String[] origins = _customProperties.getCorsOrigins();
		LOG.info("Configuring CORS allowed origins for API to {}", Arrays.toString(origins));
		if (origins != null && 0 < origins.length) {
			registry
				.addMapping("/api/**")
					.allowCredentials(true)
					.allowedMethods("*")
					.allowedOrigins(origins)
			;
		}
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addStatusController("/health", HttpStatus.OK);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new InputStreamMessageConverter());
	}

	/**
	 * Trivial {@link HttpMessageConverter} implementation to allow handler methods to accept
	 * "text/csv" input as a raw input stream.
	 */
	private static class InputStreamMessageConverter extends AbstractHttpMessageConverter<InputStream> {

		public InputStreamMessageConverter() {
			super(new MediaType("text", "csv"));
		}

		@Override
		protected boolean supports(Class<?> clazz) {
			return clazz.equals(InputStream.class);
		}

		@Override
		protected InputStream readInternal(Class<? extends InputStream> clazz, HttpInputMessage inputMessage)
				throws IOException, HttpMessageNotReadableException {
			return inputMessage.getBody();
		}

		@Override
		protected void writeInternal(InputStream t, HttpOutputMessage outputMessage)
				throws IOException, HttpMessageNotWritableException {
			throw new IllegalArgumentException("This HttpMessageConverter does not support output.");
		}

		@Override
		public boolean canWrite(Class<?> clazz, MediaType mediaType) {
			return false; // this is an input-only converter.
		}
	}
}