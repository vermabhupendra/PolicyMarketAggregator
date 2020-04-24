package com.synechron.aggregator.util;

import java.io.Serializable;

import lombok.Data;

@Data
public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;
	private Object data;

	public Response() {
		super();
	}

	public Response(String message, Object data) {
		super();
		this.message = message;
		this.data = data;
	}
}
