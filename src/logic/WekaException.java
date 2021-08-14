package logic;

public class WekaException extends Exception{
	
	static final long serialVersionUID = 1;
	
	public WekaException(String errorMessage) {
		super(errorMessage);
	}
	
	public WekaException(Throwable err) {
	    super(err);
	}
	
	public WekaException(String errorMessage, Throwable err) {
	    super(errorMessage, err);
	}
	
	

}
