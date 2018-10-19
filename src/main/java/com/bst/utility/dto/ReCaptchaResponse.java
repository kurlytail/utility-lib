package com.bst.utility.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "success", "challenge_ts", "hostname", "error-codes" })
public class ReCaptchaResponse {

	static enum ErrorCode {
		InvalidResponse, InvalidSecret, MissingResponse, MissingSecret;

		private static Map<String, ErrorCode> errorsMap = new HashMap<>(4);

		static {
			ErrorCode.errorsMap.put("missing-input-secret", MissingSecret);
			ErrorCode.errorsMap.put("invalid-input-secret", InvalidSecret);
			ErrorCode.errorsMap.put("missing-input-response", MissingResponse);
			ErrorCode.errorsMap.put("invalid-input-response", InvalidResponse);
		}

		@JsonCreator
		public static ErrorCode forValue(final String value) {
			return ErrorCode.errorsMap.get(value.toLowerCase());
		}
	}

	@JsonProperty("challenge_ts")
	private Date challengeTs;

	@JsonProperty("error-codes")
	private ErrorCode[] errorCodes;

	@JsonProperty("hostname")
	private String hostname;

	@JsonProperty("success")
	private boolean success;

	public Date getChallengeTs() {
		return this.challengeTs;
	}

	public ErrorCode[] getErrorCodes() {
		return this.errorCodes;
	}

	public String getHostname() {
		return this.hostname;
	}

	@JsonIgnore
	public boolean hasClientError() {
		final ErrorCode[] errors = this.getErrorCodes();
		if (errors == null) {
			return false;
		}
		for (final ErrorCode error : errors) {
			switch (error) {
			case InvalidResponse:
			case MissingResponse:
				return true;
			default:
				break;
			}
		}
		return false;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void setChallengeTs(final Date challengeTs) {
		this.challengeTs = challengeTs;
	}

	public void setErrorCodes(final ErrorCode[] errorCodes) {
		this.errorCodes = errorCodes;
	}

	public void setHostname(final String hostname) {
		this.hostname = hostname;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}
}
