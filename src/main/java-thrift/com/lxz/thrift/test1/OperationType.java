/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.lxz.thrift.test1;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum OperationType implements org.apache.thrift.TEnum {
  ADD(0),
  MUL(1),
  DIV(2),
  SUB(3);

  private final int value;

  private OperationType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static OperationType findByValue(int value) { 
    switch (value) {
      case 0:
        return ADD;
      case 1:
        return MUL;
      case 2:
        return DIV;
      case 3:
        return SUB;
      default:
        return null;
    }
  }
}