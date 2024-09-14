package com.dish.auth.configuration;

 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;



@Component
@PropertySource("classpath:messages_en.properties")
public class MessageConfig {
	@Value("${auth.details.save}")
	public String authDetailsSavedSuccessfully;

	@Value("${admin.contact}")
	public String adminContact;

	@Value("${auth.email.duplicate}")
	public String emailDuplicate;
	 
	@Value("${invalid.Credentials}")
	public String invalidCredentials;

	@Value("${login.Successful}")
	public String loginSuccessful;

}
