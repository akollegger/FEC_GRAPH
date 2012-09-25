package org.followthedata.importer;

import java.util.*;

/**
 * 
 * Raw data header looks like:
 * <pre>
 * indivKEY|indivNAME|indivCITY|indivSTATE|indivZIP|indivEMP|indivOCC
 * </pre>
 */
public class IndividualRecord extends DataRecord {

  public static enum Fields { indivKEY, indivNAME, indivCITY, indivSTATE, indivZIP, indivEMP, indivOCC }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.indivKEY.name(), FieldType.STRING),
    new FieldSpec(Fields.indivNAME.name(), FieldType.STRING),
    new FieldSpec(Fields.indivCITY.name(), FieldType.STRING),
    new FieldSpec(Fields.indivSTATE.name(), FieldType.STRING),
    new FieldSpec(Fields.indivZIP.name(), FieldType.STRING),
    new FieldSpec(Fields.indivEMP.name(), FieldType.STRING),
    new FieldSpec(Fields.indivOCC.name(), FieldType.STRING)
  };

  public IndividualRecord(String rowData) {
    super(rowData);
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String indivKEY() {
    return String.valueOf(getField(IndividualRecord.Fields.indivKEY.name()));
  }

  public String indivNAME() {
    return String.valueOf(getField(IndividualRecord.Fields.indivNAME.name()));
  }

  public String indivCITY() {
    return String.valueOf(getField(IndividualRecord.Fields.indivCITY.name()));
  }

  public String indivSTATE() {
    return String.valueOf(getField(IndividualRecord.Fields.indivSTATE.name()));
  }

  public String indivZIP() {
    return String.valueOf(getField(IndividualRecord.Fields.indivZIP.name()));
  }

  public String indivEMP() {
    return String.valueOf(getField(IndividualRecord.Fields.indivEMP.name()));
  }

  public String indivOCC() {
    return String.valueOf(getField(IndividualRecord.Fields.indivOCC.name()));
  }

}

