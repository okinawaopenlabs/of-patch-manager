package org.okinawaopenlabs.orientdb.exception;

public class BusinessLogicException extends Exception{

    private static final long serialVersionUID = 1L;

    public BusinessLogicException(){
    	super();
    }
	
    public BusinessLogicException(String mes){
    	super(mes);
    }

}
