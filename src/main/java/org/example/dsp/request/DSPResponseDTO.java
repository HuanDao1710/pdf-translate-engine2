package org.example.dsp.request;

public class DSPResponseDTO<T> {
	private String status;
	private T result;

	public DSPResponseDTO(String status, T result) {
		this.status = status;
		this.result = result;
	}

	public DSPResponseDTO() {
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}
