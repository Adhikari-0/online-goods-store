package com.store.common.exception;

//common/exception/ResourceNotFoundException.java

public class ResourceNotFoundException extends RuntimeException {
 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

 public ResourceNotFoundException(String message) {
     super(message);
 }

 public ResourceNotFoundException(String resource, Object id) {
     super(resource + " not found with id: " + id);
 }
}
