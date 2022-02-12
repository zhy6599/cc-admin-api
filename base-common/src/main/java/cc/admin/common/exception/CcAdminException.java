package cc.admin.common.exception;

import com.google.common.base.Strings;
public class CcAdminException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CcAdminException(String message){
		super(message);
	}

	public CcAdminException(Throwable cause)
	{
		super(cause);
	}

	public CcAdminException(String message,Throwable cause)
	{
		super(message,cause);
	}

	public CcAdminException(String message, Object... args) {
		super(Strings.lenientFormat(message, args));
	}

	public static void runTimeException(String message) {
		throw new CcAdminException(message);
	}

	public static void runTimeException(String message, Object... args) {
		throw new CcAdminException(message, args);
	}

	public static void runTimeException(String message, Throwable cause) {
		throw new CcAdminException(message, cause);
	}
}
