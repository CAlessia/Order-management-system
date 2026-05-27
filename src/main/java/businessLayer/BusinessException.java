package businessLayer;
/**
 * Custom exception class for representing business logic errors.
 * Extends {@link RuntimeException} so it can be thrown without declaring in method signatures.
 */
public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }
}
