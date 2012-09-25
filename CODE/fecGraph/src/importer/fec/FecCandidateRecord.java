package org.followthedata.importer.fec;

import org.followthedata.importer.DataRecord;
import org.followthedata.importer.FieldSpec;
import org.followthedata.importer.FieldType;

import java.util.*;

import org.neo4j.helpers.collection.MapUtil;

import static org.neo4j.helpers.collection.MapUtil.map;
import static org.neo4j.helpers.collection.MapUtil.stringMap;
import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.EXACT_CONFIG;
import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.FULLTEXT_CONFIG;


/**
 * 
 * Raw data header looks like:
 * <pre>
 * CAND_ID: String
 * CAND_NAME: String
 * CAND_PTY_AFFILIATION: String
 * CAND_ELECTION_YR: String
 * CAND_OFFICE_ST: String
 * CAND_OFFICE: String
 * CAND_OFFICE_DISTRICT: String
 * CAND_ICI: String
 * CAND_STATUS: String
 * CAND_PCC: String
 * CAND_ST1: String
 * CAND_ST2: String
 * CAND_CITY: String
 * CAND_ST: String
 * CAND_ZIP: String * </pre>
 */
public class FecCandidateRecord extends DataRecord {

  public static enum Fields { CAND_ID, CAND_NAME, CAND_PTY_AFFILIATION, CAND_ELECTION_YR, CAND_OFFICE_ST, CAND_OFFICE, CAND_OFFICE_DISTRICT, CAND_ICI, CAND_STATUS, 
    CAND_PCC, CAND_ST1, CAND_ST2, CAND_CITY, CAND_ST, CAND_ZIP }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.CAND_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_NAME.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_PTY_AFFILIATION.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ELECTION_YR.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_OFFICE_ST.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_OFFICE.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_OFFICE_DISTRICT.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ICI.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_STATUS.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_PCC.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ST1.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ST2.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_CITY.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ST.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ZIP.name(), FieldType.STRING)
  };

  private static FecCandidateRecord unsafeRecord = new FecCandidateRecord();
  public static FecCandidateRecord parse(String rowData) {
    unsafeRecord.update(rowData);
    return unsafeRecord;
  }

  private FecCandidateRecord() {
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String candidateID() { return String.valueOf(getField(Fields.CAND_ID.name())); }
  public String candidateName() { return String.valueOf(getField(Fields.CAND_NAME.name())); }
  public String canddateParty() { return String.valueOf(getField(Fields.CAND_PTY_AFFILIATION.name())); }
  public String candidateElectionYear() { return String.valueOf(getField(Fields.CAND_ELECTION_YR.name())); }
  public String candidateOfficeState() { return String.valueOf(getField(Fields.CAND_OFFICE_ST.name())); }
  public String candidateOffice() { return String.valueOf(getField(Fields.CAND_OFFICE.name())); }
  public String candidateDistrict() { return String.valueOf(getField(Fields.CAND_OFFICE_DISTRICT.name())); }
  public String candidateIncumbentChallengerStatus() { return String.valueOf(getField(Fields.CAND_ICI.name())); }
  public String candidateStatus() { return String.valueOf(getField(Fields.CAND_STATUS.name())); }
  public String candidatePrincipalCommittee() { return String.valueOf(getField(Fields.CAND_PCC.name())); }
  public String candidateStreet1() { return String.valueOf(getField(Fields.CAND_ST1.name())); }
  public String candidateStreet2() { return String.valueOf(getField(Fields.CAND_ST2.name())); }
  public String candidateCity() { return String.valueOf(getField(Fields.CAND_CITY.name())); }
  public String candidateState() { return String.valueOf(getField(Fields.CAND_ST.name())); }
  public String candidateZip() { return String.valueOf(getField(Fields.CAND_ZIP.name())); }

}

