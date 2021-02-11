package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationExcepetion  extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	//aqui o primeiro string indica o noome do campo e o segundo a msg de erro
	private Map<String, String> errors = new HashMap<>();
	
	public ValidationExcepetion (String msg) {
		super(msg);
	}
	
	public Map<String, String> getErrors(){
		return errors;
	}
	
	//add um elemento na coleção de erros
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
	
}
