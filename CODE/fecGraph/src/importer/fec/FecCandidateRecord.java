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

  /** A 9-character alpha-numeric code assigned to a candidate by the Federal Election Commission. The candidate ID for a specific candidate remains the same across election cycles as long as the candidate is running for the same office.  */
  public String candidateID() { return String.valueOf(getField(Fields.CAND_ID.name())); }

  /** Candidate's name. */
  public String candidateName() { return String.valueOf(getField(Fields.CAND_NAME.name())); }

  /** The political party affiliation reported by the candidate. */
  public String canddateParty() { return String.valueOf(getField(Fields.CAND_PTY_AFFILIATION.name())); }

  /** Candidate's election year from a Statement of Candidacy or state ballot list. */
  public String candidateElectionYear() { return String.valueOf(getField(Fields.CAND_ELECTION_YR.name())); }

  /**
   * The state of the office for which the candidate is running. Interpret according to the office:
   * <ul>
   * <li>House = state of race (CT, NM, PA, etc)</li>
   * <li>President  = US</li>
   * <li>Senate = state of race</li>
   * </ul>
   */
  public String candidateOfficeState() { return String.valueOf(getField(Fields.CAND_OFFICE_ST.name())); }

  /**
   * The office for which the candidatite is running:
   * <ul>
   * <li>H = House</li>
   * <li>P = President</li>
   * <li>S = Senate</li>
   * </ul>
   */
  public String candidateOffice() { return String.valueOf(getField(Fields.CAND_OFFICE.name())); }

  /** 
   * The district of the office for which a house candidate is running:
   * <ul>
   * <li>Congressional district number.</li>
   * <li>Congressional At Large 00</li>
   * <li>Senate 00</li>
   * <li>Presidential 00</li>
   * </ul>
   */
  public String candidateDistrict() { return String.valueOf(getField(Fields.CAND_OFFICE_DISTRICT.name())); }

  /**
   * Whether the candidate is a challenger, incumbent or running for an open seat. One of:
   * <ul>
   * <li>C = Challenger</li>
   * <li>I = Incumbent</li>
   * <li> O = Open Seat is used to indicate an open seat.  Open seats are defined as seats where the incumbent never sought re-election.</li>
   * </ul>
   */
  public String candidateIncumbentChallengerStatus() { return String.valueOf(getField(Fields.CAND_ICI.name())); }

  /**
   * The "status" of a candidate. One of:
   * <ul>
   * <li>C = Statutory candidate</li>
   * <li>F = Statutory candidate for future election</li>
   * <li>N = Not yet a statutory candidate</li>
   * <li>P = Statutory candidate in prior cycle</li>
   * </ul>
   */
  public String candidateStatus() { return String.valueOf(getField(Fields.CAND_STATUS.name())); }

  /** The ID assigned by the Federal Election Commission to the candidate's principal campaign committee for a given election cycle. */
  public String candidatePrincipalCommittee() { return String.valueOf(getField(Fields.CAND_PCC.name())); }

  /** Postal address, street line 1 */
  public String candidateStreet1() { return String.valueOf(getField(Fields.CAND_ST1.name())); }
  /** Postal address, street line 2 */
  public String candidateStreet2() { return String.valueOf(getField(Fields.CAND_ST2.name())); }
  /** Postal address, city */
  public String candidateCity() { return String.valueOf(getField(Fields.CAND_CITY.name())); }
  /** Postal address, state */
  public String candidateState() { return String.valueOf(getField(Fields.CAND_ST.name())); }
  /** Postal address, zip code */
  public String candidateZip() { return String.valueOf(getField(Fields.CAND_ZIP.name())); }

}

