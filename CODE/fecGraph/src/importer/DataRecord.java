package org.followthedata.importer;

import java.util.*;


public abstract class DataRecord {

  protected Map<String,Object> fieldMap = new HashMap<String,Object>();

  public DataRecord() {
    ; // no-op so factories can re-use instances
  }

  public DataRecord(String rowData) {
    update(rowData);
  }

  public DataRecord update(String rowData) {
    fieldMap.clear();
    String[] fieldValues = rowData.split("\\|");

    int fieldCount = 0;
    for (FieldSpec fieldSpec : getFieldSpecs()) {
      if (fieldCount < fieldValues.length) {
        String currentField = fieldValues[fieldCount++];
        if (currentField != null && currentField.length()>0)
          fieldMap.put(fieldSpec.name(), fieldSpec.type().convert(currentField));
      }
    }

    return this;
  }

  public abstract FieldSpec[] getFieldSpecs();

  public Object getField(String fieldName) {
    return fieldMap.get(fieldName);
  }

  public Map<String,Object> getMappedFields() {
    return fieldMap;
  }

  @Override
  public String toString() {
    return fieldMap.toString();
  }
}

