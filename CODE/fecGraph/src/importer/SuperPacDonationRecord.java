package org.followthedata.importer;

import java.util.*;

/**
 * 
 * Raw data header looks like:
 * <pre>
 * RECEIPT_TYPE|SUPER_PAC|SUPER_PAC_ID|DONATING_ORG|DONOR_LAST|DONOR_FIRST|DONOR_CITY|DONOR_STATE|DONOR_OCCUPATION|DONOR_EMPLOYER|DONOR_AMOUNT|DONATION_DATE|TOTAL_AMT|TRANS_ID
 * </pre>
 */
public class SuperPacDonationRecord extends DataRecord {

  public static enum Fields { receiptType, superPac, superPacID, donatingOrg, donorLast, donorFirst, donorCity, donorState, donorOccupation, donorEmployer, donorAmount, donationDate, totalAmount, transactionID }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.receiptType.name(), FieldType.STRING),
    new FieldSpec(Fields.superPac.name(), FieldType.STRING),
    new FieldSpec(Fields.superPacID.name(), FieldType.STRING),
    new FieldSpec(Fields.donatingOrg.name(), FieldType.STRING),
    new FieldSpec(Fields.donorLast.name(), FieldType.STRING),
    new FieldSpec(Fields.donorFirst.name(), FieldType.STRING),
    new FieldSpec(Fields.donorCity.name(), FieldType.STRING),
    new FieldSpec(Fields.donorState.name(), FieldType.STRING),
    new FieldSpec(Fields.donorOccupation.name(), FieldType.STRING),
    new FieldSpec(Fields.donorEmployer.name(), FieldType.STRING),
    new FieldSpec(Fields.donorAmount.name(), FieldType.DOUBLE),
    new FieldSpec(Fields.donationDate.name(), FieldType.STRING),
    new FieldSpec(Fields.totalAmount.name(), FieldType.DOUBLE),
    new FieldSpec(Fields.transactionID.name(), FieldType.STRING)
  };

  public SuperPacDonationRecord(String rowData) {
    super(rowData);
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String receiptType() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.receiptType.name()));
  }

  public String superPac() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.superPac.name()));
  }

  public String superPacID() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.superPacID.name()));
  }

  public String donatingOrg() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donatingOrg.name()));
  }

  public String donorLast() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorLast.name()));
  }

  public String donorFirst() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorFirst.name()));
  }

  public String donorCity() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorCity.name()));
  }

  public String donorState() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorState.name()));
  }

  public String donorOccupation() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorOccupation.name()));
  }

  public String donorEmployer() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donorEmployer.name()));
  }

  public Double donorAmount() {
    return (Double)getField(SuperPacDonationRecord.Fields.donorAmount.name());
  }

  public String donationDate() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.donationDate.name()));
  }

  public Double totalAmount() {
    return (Double)getField(SuperPacDonationRecord.Fields.totalAmount.name());
  }

  public String transactionID() {
    return String.valueOf(getField(SuperPacDonationRecord.Fields.transactionID.name()));
  }
}

