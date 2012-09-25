package org.followthedata.importer;

public class FieldSpec {
  private String fieldName;
  private FieldType fieldType;

  public FieldSpec(String name, FieldType type) {
    this.fieldName = name;
    this.fieldType = type;
  }

  public String name() {
    return fieldName;
  }

  public FieldType type() {
    return fieldType;
  }
}