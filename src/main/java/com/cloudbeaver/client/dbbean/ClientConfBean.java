package com.cloudbeaver.client.dbbean;

import org.junit.Assert;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ClientConfBean implements InitializingBean{
	@JsonIgnore
	private String clientId;

	@JsonIgnore
	private String userEmail;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private String webServerUrl;

	@JsonIgnore
	public String kafkaBrokerList = "br0:9092,br1:9092,br2:9092";

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getWebServerUrl() {
		return webServerUrl;
	}

	public void setWebServerUrl(String webServerUrl) {
		this.webServerUrl = webServerUrl;
	}

	public String getKafkaBrokerList() {
		return kafkaBrokerList;
	}

	public void setKafkaBrokerList(String kafkaBrokerList) {
		this.kafkaBrokerList = kafkaBrokerList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.assertNotNull("clientId is null", clientId);
		Assert.assertNotEquals("clientId is empty", clientId, "");

		Assert.assertNotNull("userEmail is null", userEmail);
		Assert.assertNotEquals("userEmail is empty", userEmail, "");

		Assert.assertNotNull("password is null", password);
		Assert.assertNotEquals("password is empty", password, "");

		Assert.assertNotNull("webServer is null", webServerUrl);
		Assert.assertNotEquals("webServer is empty", webServerUrl, "");
	}
}
