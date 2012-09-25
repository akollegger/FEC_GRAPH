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
 * AMNDT_IND: String
 * RPT_TP: String
 * TRANSACTION_PGI: String
 * IMAGE_NUM: String
 * TRANSACTION_TP: String
 * ENTITY_TP: String
 * NAME: String
 * CITY: String
 * STATE: String
 * ZIP_CODE: String
 * EMPLOYER: String
 * OCCUPATION: String
 * TRANSACTION_DT: String
 * TRANSACTION_AMT: Double
 * OTHER_ID: String
 * TRAN_ID: String
 * FILE_NUM: Integer
 * MEMO_CD: Integer
 * MEMO_TEXT: String
 * SUB_ID: Integer
 * </pre>
 */
public class FecInterCommitteeContribRecord extends DataRecord {

  public static enum Fields { CMTE_ID, AMNDT_IND, RPT_TP, TRANSACTION_PGI, IMAGE_NUM, TRANSACTION_TP, ENTITY_TP, NAME, CITY, STATE, ZIP_CODE, EMPLOYER, OCCUPATION, 
    TRANSACTION_DT, TRANSACTION_AMT, OTHER_ID, TRAN_ID, FILE_NUM, MEMO_CD, MEMO_TEXT, SUB_ID }

  static FieldSpec[] fieldSpecs = new FieldSpec[] {
    new FieldSpec(Fields.CMTE_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.AMNDT_IND.name(), FieldType.STRING),
    new FieldSpec(Fields.RPT_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.TRANSACTION_PGI.name(), FieldType.STRING),
    new FieldSpec(Fields.IMAGE_NUM.name(), FieldType.STRING),
    new FieldSpec(Fields.TRANSACTION_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.ENTITY_TP.name(), FieldType.STRING),
    new FieldSpec(Fields.NAME.name(), FieldType.STRING),
    new FieldSpec(Fields.CITY.name(), FieldType.STRING),
    new FieldSpec(Fields.STATE.name(), FieldType.STRING),
    new FieldSpec(Fields.ZIP_CODE.name(), FieldType.STRING),
    new FieldSpec(Fields.EMPLOYER.name(), FieldType.STRING),
    new FieldSpec(Fields.OCCUPATION.name(), FieldType.STRING),
    new FieldSpec(Fields.TRANSACTION_DT.name(), FieldType.STRING),
    new FieldSpec(Fields.TRANSACTION_AMT.name(), FieldType.DOUBLE),
    new FieldSpec(Fields.OTHER_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.TRAN_ID.name(), FieldType.STRING),
    new FieldSpec(Fields.FILE_NUM.name(), FieldType.LONG),
    new FieldSpec(Fields.MEMO_CD.name(), FieldType.STRING),
    new FieldSpec(Fields.MEMO_TEXT.name(), FieldType.STRING),
    new FieldSpec(Fields.SUB_ID.name(), FieldType.LONG)
  };

  private static FecInterCommitteeContribRecord unsafeRecord = new FecInterCommitteeContribRecord();
  public static FecInterCommitteeContribRecord parse(String rowData) {
    unsafeRecord.update(rowData);
    return unsafeRecord;
  }

  private FecInterCommitteeContribRecord() {
  }

  @Override
  public FieldSpec[] getFieldSpecs() {
    return fieldSpecs;
  }

  public String committeeID() { return String.valueOf(getField(Fields.CMTE_ID.name())); }
  public Integer amendmentIndicator() { return (Integer)getField(Fields.AMNDT_IND.name()); }
  public Integer reportType() { return (Integer)getField(Fields.RPT_TP.name()); }
  public String primaryGeneralIndicator() { return String.valueOf(getField(Fields.TRANSACTION_PGI.name())); }
  public String imageNum() { return String.valueOf(getField(Fields.IMAGE_NUM.name())); }
  public String transactionType() { return String.valueOf(getField(Fields.TRANSACTION_TP.name())); }
  public String entityType() { return String.valueOf(getField(Fields.ENTITY_TP.name())); }
  public String name() { return String.valueOf(getField(Fields.NAME.name())); }
  public String city() { return String.valueOf(getField(Fields.CITY.name())); }
  public String state() { return String.valueOf(getField(Fields.STATE.name())); }
  public String zipCode() { return String.valueOf(getField(Fields.ZIP_CODE.name())); }
  public String employer() { return String.valueOf(getField(Fields.EMPLOYER.name())); }
  public String occupation() { return String.valueOf(getField(Fields.OCCUPATION.name())); }
  public String transactionDate() { return String.valueOf(getField(Fields.TRANSACTION_DT.name())); }
  public String transactionAmount() { return String.valueOf(getField(Fields.TRANSACTION_AMT.name())); }
  public String otherID() { return String.valueOf(getField(Fields.OTHER_ID.name())); }
  public String transactionID() { return String.valueOf(getField(Fields.TRAN_ID.name())); }
  public String fileNumber() { return String.valueOf(getField(Fields.FILE_NUM.name())); }
  public String memoCode() { return String.valueOf(getField(Fields.MEMO_CD.name())); }
  public String memoText() { return String.valueOf(getField(Fields.MEMO_TEXT.name())); }
  public String subID() { return String.valueOf(getField(Fields.SUB_ID.name())); }

}

