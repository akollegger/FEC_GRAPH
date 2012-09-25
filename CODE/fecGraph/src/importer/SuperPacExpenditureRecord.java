package org.followthedata.importer;

import java.util.*;

/**
 * 
 * Raw data header looks like:
 * <pre>
 * SPENDING_COMM|SPENDING_COMM_ID|SUPERPAC|ELECTION_TYPE|CANDIDATE|SUPPORT_OPPOSE|CANDIDATE_ID|CANDIDATE_PARTY|CANDIDATE_OFFICE|CANDIDATE_DISTRICT|CANDIDATE_STATE|EXPEND_AMT|EXPENDITURE_STATE|EXPEND_DATE|ELECTION_TYPE|CONT_RECIP|PURPOSE|TRANSACTION_ID
 * </pre>
 */
public class SuperPacExpenditureRecord extends DataRecord {

  public static enum Fields { spendingComm, spendingCommID, isSuperPac, electionType, candidate,
    supportOppose, candidateID, candidateParty, candidateOffice, candidateDistrict, candidateState,
    expendAmt, expendState, expendDate, electionType2, contRecip, purpose, transactionID }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.spendingComm.name(), FieldType.STRING),
    new FieldSpec(Fields.spendingCommID.name(), FieldType.STRING),
    new FieldSpec(Fields.isSuperPac.name(), FieldType.STRING),
    new FieldSpec(Fields.electionType.name(), FieldType.STRING),
    new FieldSpec(Fields.candidate.name(), FieldType.STRING),
    new FieldSpec(Fields.supportOppose.name(), FieldType.STRING),
    new FieldSpec(Fields.candidateID.name(), FieldType.STRING),
    new FieldSpec(Fields.candidateParty.name(), FieldType.STRING),
    new FieldSpec(Fields.candidateOffice.name(), FieldType.STRING),
    new FieldSpec(Fields.candidateDistrict.name(), FieldType.STRING),
    new FieldSpec(Fields.candidateState.name(), FieldType.STRING),
    new FieldSpec(Fields.expendAmt.name(), FieldType.DOUBLE),
    new FieldSpec(Fields.expendState.name(), FieldType.STRING),
    new FieldSpec(Fields.expendDate.name(), FieldType.STRING),
    new FieldSpec(Fields.electionType2.name(), FieldType.STRING),
    new FieldSpec(Fields.contRecip.name(), FieldType.STRING),
    new FieldSpec(Fields.purpose.name(), FieldType.STRING),
    new FieldSpec(Fields.transactionID.name(), FieldType.STRING)
  };

  public SuperPacExpenditureRecord(String rowData) {
    super(rowData);
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String spendingComm() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.spendingComm.name()));
  }

  public String spendingCommID() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.spendingCommID.name()));
  }

  public String isSuperPac() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.isSuperPac.name()));
  }

  public String electionType() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.electionType.name()));
  }

  public String candidate() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidate.name()));
  }

  public String supportOppose() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.supportOppose.name()));
  }

  public String candidateID() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidateID.name()));
  }

  public String candidateParty() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidateParty.name()));
  }

  public String candidateOffice() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidateOffice.name()));
  }

  public String candidateDistrict() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidateDistrict.name()));
  }

  public String candidateState() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.candidateState.name()));
  }

  public Double expendAmt() {
    return (Double)getField(SuperPacExpenditureRecord.Fields.expendAmt.name());
  }

  public String expendState() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.expendState.name()));
  }

  public String expendDate() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.expendDate.name()));
  }

  public String electionType2() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.electionType2.name()));
  }

  public String contRecip() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.contRecip.name()));
  }

  public String purpose() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.purpose.name()));
  }

  public String transactionID() {
    return String.valueOf(getField(SuperPacExpenditureRecord.Fields.transactionID.name()));
  }
}

