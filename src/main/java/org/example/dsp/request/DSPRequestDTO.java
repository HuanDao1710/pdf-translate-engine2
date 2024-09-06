package org.example.dsp.request;

import java.util.Set;

public class DSPRequestDTO {
	private TranslateDSPInput input;
	private String dcsId;
	private Set<String> includeCountry;
	private long repeat;
	private boolean receiveResponse;
	private Integer minDspVersion;
	private boolean preferHighSpeed;

	public DSPRequestDTO(TranslateDSPInput input, String dcsId, Set<String> includeCountry, long repeat, boolean receiveResponse, Integer minDspVersion, boolean preferHighSpeed) {
		this.input = input;
		this.dcsId = dcsId;
		this.includeCountry = includeCountry;
		this.repeat = repeat;
		this.receiveResponse = receiveResponse;
		this.minDspVersion = minDspVersion;
		this.preferHighSpeed = preferHighSpeed;
	}

	public DSPRequestDTO() {
	}

	public TranslateDSPInput getInput() {
		return input;
	}

	public void setInput(TranslateDSPInput input) {
		this.input = input;
	}

	public String getDcsId() {
		return dcsId;
	}

	public void setDcsId(String dcsId) {
		this.dcsId = dcsId;
	}

	public Set<String> getIncludeCountry() {
		return includeCountry;
	}

	public void setIncludeCountry(Set<String> includeCountry) {
		this.includeCountry = includeCountry;
	}

	public long getRepeat() {
		return repeat;
	}

	public void setRepeat(long repeat) {
		this.repeat = repeat;
	}

	public boolean isReceiveResponse() {
		return receiveResponse;
	}

	public void setReceiveResponse(boolean receiveResponse) {
		this.receiveResponse = receiveResponse;
	}

	public Integer getMinDspVersion() {
		return minDspVersion;
	}

	public void setMinDspVersion(Integer minDspVersion) {
		this.minDspVersion = minDspVersion;
	}

	public boolean isPreferHighSpeed() {
		return preferHighSpeed;
	}

	public void setPreferHighSpeed(boolean preferHighSpeed) {
		this.preferHighSpeed = preferHighSpeed;
	}
}
