package cc.admin.common.exception;

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
}
