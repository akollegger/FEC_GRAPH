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
 * CMTE_ID: String
 * CMTE_NM: String
 * TRES_NM: String
 * CMTE_ST1: String
 * CMTE_ST2: String
 * CMTE_CITY: String
 * CMTE_ST: String
 * CMTE_ZIP: String
 * CMTE_DSGN: String
 * CMTE_TP: String
 * CMTE_PTY_AFFILIATION: String
 * CMTE_FILING_FREQ: String
 * ORG_TP: String
 * CONNECTED_ORG_NM: String
 * CAND_ID: String
 * </pre>
 */
public class FecCommitteeRecord extends DataRecord {

  public static enum Fields { CMTE_ID, CMTE_NM, TRES_NM, CMTE_ST1, CMTE_ST2, CMTE_CITY, CMTE_ST, CMTE_ZIP, CMTE_DSGN, CMTE_TP, CMTE_PTY_AFFILIATION, CMTE_FILING_FREQ, ORG_TP, CONNECTED_ORG_NM, CAND_ID }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.CMTE_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_NM.name(), FieldType.STRING),
    new FieldSpec(Fields.TRES_NM.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_ST1.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_ST2.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_CITY.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_ST.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_ZIP.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_DSGN.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_PTY_AFFILIATION.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_FILING_FREQ.name(), FieldType.STRING),
    new FieldSpec(Fields.ORG_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.CONNECTED_ORG_NM.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ID.name(), FieldType.STRING)
  };

  private static FecCommitteeRecord unsafeRecord = new FecCommitteeRecord();
  public static FecCommitteeRecord parse(String rowData) {
    unsafeRecord.update(rowData);
    return unsafeRecord;
  }

  private FecCommitteeRecord() {
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String committeeID() { return String.valueOf(getField(Fields.CMTE_ID.name())); }
  public String committeeName() { return String.valueOf(getField(Fields.CMTE_NM.name())); }
  public String treasurerName() { return String.valueOf(getField(Fields.TRES_NM.name())); }
  public String committeeStreet1() { return String.valueOf(getField(Fields.CMTE_ST1.name())); }
  public String committeeStreet2() { return String.valueOf(getField(Fields.CMTE_ST2.name())); }
  public String committeeCity() { return String.valueOf(getField(Fields.CMTE_CITY.name())); }
  public String committeeState() { return String.valueOf(getField(Fields.CMTE_ST.name())); }
  public String committeeZip() { return String.valueOf(getField(Fields.CMTE_ZIP.name())); }
  public String committeeDesgination() { return String.valueOf(getField(Fields.CMTE_DSGN.name())); }
  public String committeeType() { return String.valueOf(getField(Fields.CMTE_TP.name())); }
  public String committeeParty() { return String.valueOf(getField(Fields.CMTE_PTY_AFFILIATION.name())); }
  public String committeeFilingFrequency() { return String.valueOf(getField(Fields.CMTE_FILING_FREQ.name())); }
  public String interestGroupCategory() { return String.valueOf(getField(Fields.ORG_TP.name())); }
  public String connectedOrganizationName() { return String.valueOf(getField(Fields.CONNECTED_ORG_NM.name())); }
  public String candidateID() { return String.valueOf(getField(Fields.CAND_ID.name())); }

}

