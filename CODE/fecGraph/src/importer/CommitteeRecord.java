package org.followthedata.importer;

import java.util.*;

/**
 * 
 * Raw data header looks like:
 * <pre>
 * commID|commNAME|name|commTREAS|commADDR1|commADDR2|commCITY|commSTATE|commZIP|commDSG|commTYPE|commPARTYAFFIL|commFILING
 * </pre>
 */
public class CommitteeRecord extends DataRecord {

  public static enum Fields { commID, commNAME, name, commTREAS, commADDR1, commADDR2, commCITY, commSTATE, commZIP, commDSG, commTYPE, commPARTYAFFIL, commFILING }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.commID.name(), FieldType.STRING),
    new FieldSpec(Fields.commNAME.name(), FieldType.STRING),
    new FieldSpec(Fields.name.name(), FieldType.STRING),
    new FieldSpec(Fields.commTREAS.name(), FieldType.STRING),
    new FieldSpec(Fields.commADDR1.name(), FieldType.STRING),
    new FieldSpec(Fields.commADDR2.name(), FieldType.STRING),
    new FieldSpec(Fields.commCITY.name(), FieldType.STRING),
    new FieldSpec(Fields.commSTATE.name(), FieldType.STRING),
    new FieldSpec(Fields.commZIP.name(), FieldType.STRING),
    new FieldSpec(Fields.commDSG.name(), FieldType.STRING),
    new FieldSpec(Fields.commTYPE.name(), FieldType.STRING),
    new FieldSpec(Fields.commPARTYAFFIL.name(), FieldType.STRING),
    new FieldSpec(Fields.commFILING.name(), FieldType.STRING),
  };

  public CommitteeRecord(String rowData) {
    super(rowData);
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String commID() {
    return String.valueOf(getField(CommitteeRecord.Fields.commID.name()));
  }
  public String commNAME() {
    return String.valueOf(getField(CommitteeRecord.Fields.commNAME.name()));
  }
  public String name() {
    return String.valueOf(getField(CommitteeRecord.Fields.name.name()));
  }
  public String commTREAS() {
    return String.valueOf(getField(CommitteeRecord.Fields.commTREAS.name()));
  }
  public String commADDR1() {
    return String.valueOf(getField(CommitteeRecord.Fields.commADDR1.name()));
  }
  public String commADDR2() {
    return String.valueOf(getField(CommitteeRecord.Fields.commADDR2.name()));
  }
  public String commCITY() {
    return String.valueOf(getField(CommitteeRecord.Fields.commCITY.name()));
  }
  public String commSTATE() {
    return String.valueOf(getField(CommitteeRecord.Fields.commSTATE.name()));
  }
  public String commZIP() {
    return String.valueOf(getField(CommitteeRecord.Fields.commZIP.name()));
  }
  public String commDSG() {
    return String.valueOf(getField(CommitteeRecord.Fields.commDSG.name()));
  }
  public String commTYPE() {
    return String.valueOf(getField(CommitteeRecord.Fields.commTYPE.name()));
  }
  public String commPARTYAFFIL() {
    return String.valueOf(getField(CommitteeRecord.Fields.commPARTYAFFIL.name()));
  }
  public String commFILING() {
    return String.valueOf(getField(CommitteeRecord.Fields.commFILING.name()));
  }


}

