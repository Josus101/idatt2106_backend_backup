package org.ntnu.idatt2106.backend.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for testing purposes.
 * Provides methods to set private fields and call private methods using reflection.
 */
public class TestUtils {
  /**
   * Sets the value of a private field in the given target object.
   *
   * @param target    The object containing the field to set.
   * @param fieldName The name of the field to set.
   * @param value     The value to set the field to.
   */
  public static void setField(Object target, String fieldName, Object value) {
    try {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  /**
   * Calls a private method on the given target object.
   *
   * @param target      The object containing the method to call.
   * @param paramTypes  The parameter types of the method to call.
   * @param args        The arguments to pass to the method.
   * @param methodName  The name of the method to call.
   * @return The result of the method call.
   */
  public static <T> T callPrivateMethod(Object target, Class<?>[] paramTypes, Object[] args, String methodName) {
    try {
      Method method = target.getClass().getDeclaredMethod(methodName, paramTypes);
      method.setAccessible(true);
      return (T) method.invoke(target, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  /**
   * Calls a private method on the given target object with no parameters.
   *
   * @param target      The object containing the method to call.
   * @param methodName  The name of the method to call.
   * @return The result of the method call.
   */
  public static <T> T callPrivateMethod(Object target, String methodName, Class<?>[] paramTypes, Object[] args) {
    return callPrivateMethod(target, paramTypes, args, methodName);
  }
}
