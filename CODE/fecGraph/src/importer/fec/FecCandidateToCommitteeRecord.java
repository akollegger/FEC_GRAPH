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
 * CAND_ELECTION_YR: Integer
 * FEC_ELECTION_YR: Integer
 * CMTE_ID: String
 * CMTE_TP: String
 * CMTE_DSGN: String
 * LINKAGE_ID: String
* </pre>
 */
public class FecCandidateToCommitteeRecord extends DataRecord {

  public static enum Fields { CAND_ID, CAND_ELECTION_YR, FEC_ELECTION_YR, CMTE_ID, CMTE_TP, CMTE_DSGN, LINKAGE_ID }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.CAND_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.CAND_ELECTION_YR.name(), FieldType.INT),
    new FieldSpec(Fields.FEC_ELECTION_YR.name(), FieldType.INT),
    new FieldSpec(Fields.CMTE_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.CMTE_DSGN.name(), FieldType.STRING),
    new FieldSpec(Fields.LINKAGE_ID.name(), FieldType.STRING)
  };

  private static FecCandidateToCommitteeRecord unsafeRecord = new FecCandidateToCommitteeRecord();
  public static FecCandidateToCommitteeRecord parse(String rowData) {
    unsafeRecord.update(rowData);
    return unsafeRecord;
  }

  private FecCandidateToCommitteeRecord() {
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String candidateID() { return String.valueOf(getField(Fields.CAND_ID.name())); }
  public Integer candidateElectionYear() { return (Integer)getField(Fields.CAND_ELECTION_YR.name()); }
  public Integer fecElectionYear() { return (Integer)getField(Fields.FEC_ELECTION_YR.name()); }
  public String committeeID() { return String.valueOf(getField(Fields.CMTE_ID.name())); }
  public String committeeType() { return String.valueOf(getField(Fields.CMTE_TP.name())); }
  public String committeeDesgination() { return String.valueOf(getField(Fields.CMTE_DSGN.name())); }
  public String linkageID() { return String.valueOf(getField(Fields.LINKAGE_ID.name())); }

}

