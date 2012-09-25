package org.followthedata.importer;

import java.util.*;

/**
 * 
 * Raw data header looks like:
 * <pre>
 * SuperPacName|CommitteeID|Treasurer|SuperPacAddr1|SuperPacAddr2|SuperPacCity|SuperPacZip|SuperPacState
 * </pre>
 */
public class SuperPacRecord extends DataRecord {

  public static enum Fields { superPacName, committeeID, treasurer, superPacAddr1, superPacAddr2, superPacCity, superPacZip, superPacState }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.superPacName.name(), FieldType.STRING),
    new FieldSpec(Fields.committeeID.name(), FieldType.STRING),
    new FieldSpec(Fields.treasurer.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacAddr1.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacAddr2.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacCity.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacZip.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacState.name(), FieldType.STRING)
  };

  public SuperPacRecord(String rowData) {
    super(rowData);
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String superPacName() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacName.name()));
  }

  public String committeeID() {
    return String.valueOf(getField(SuperPacRecord.Fields.committeeID.name()));
  }

  public String treasurer() {
    return String.valueOf(getField(SuperPacRecord.Fields.treasurer.name()));
  }

  public String superPacAddr1() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacAddr1.name()));
  }

  public String superPacAddr2() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacAddr2.name()));
  }

  public String superPacCity() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacCity.name()));
  }

  public String superPacZip() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacZip.name()));
  }

  public String superPacState() {
    return String.valueOf(getField(SuperPacRecord.Fields.superPacState.name()));
  }
}

