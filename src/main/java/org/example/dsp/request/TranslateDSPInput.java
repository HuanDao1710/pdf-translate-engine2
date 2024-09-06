package org.example.dsp.request;

public class TranslateDSPInput {
	private String from;
	private String to;
	private String query;

	public TranslateDSPInput(String from, String to, String query) {
		this.from = from;
		this.to = to;
		this.query = query;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
