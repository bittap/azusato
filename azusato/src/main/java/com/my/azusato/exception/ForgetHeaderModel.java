package com.my.azusato.exception;

/**
 * this occurs when programmer forget to make header model in controller.
 * @author Carmel
 *
 */
public class ForgetHeaderModel extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_MESSAGE = "you forgot to make header model in controller.";

	public ForgetHeaderModel() {
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * create with customMessage.
	 * @param customMessage customMessage
	 */
	public ForgetHeaderModel(String customMessage) {
		super(customMessage);
	}
	
}
