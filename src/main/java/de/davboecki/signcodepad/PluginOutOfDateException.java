package de.davboecki.signcodepad;

public class PluginOutOfDateException extends Exception {
	private static final long serialVersionUID = 218617962121208916L;

	public PluginOutOfDateException() {
		super();
	}

	public PluginOutOfDateException(String message) {
		super(message);
	}

	public PluginOutOfDateException(Throwable cause) {
		super(cause);
	}

	public PluginOutOfDateException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginOutOfDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}