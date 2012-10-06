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
  /**
   * A 9-character alpha-numeric code assigned to a committee by the Federal Election Commission. 
   */
  public String committeeID() { return String.valueOf(getField(Fields.CMTE_ID.name())); }

  /** Indicates if the report being filed is new (N), an amendment (A) to a previous report, or a termination (T) report. */
  public Integer amendmentIndicator() { return (Integer)getField(Fields.AMNDT_IND.name()); }

  /** 
   * Indicates the type of report filed. One of:
   * <table>
   * <tr><th>Code</th><th>Type</th><th>Explanation</th></tr>
   * <tr><td>C</td><td>Communication</td><td>Cost  Organizations like corporations or unions may prepare communications for their employees or members that advocate the election of specific candidates and they must disclose them under certain circumstances. These are usually paid with direct corporate or union funds rather than from PACs.</td></tr>
   * <tr><td>D</td><td>Delegate Committee</td><td>Delegate committees are organized for the purpose of influencing the selection of delegates to Presidential nominating conventions. The term includes a group of delegates, a group of individuals seeking to become delegates, and a group of individuals supporting delegates.</td></tr>
   * <tr><td>E</td><td>Electioneering Communication</td><td>Groups (other than PACs) making Electioneering Communications</td></tr>
   * <tr><td>H</td><td>House</td><td>Campaign committees for candidates for the House of Representatives</td></tr>
   * <tr><td>I</td><td>Independent Expenditor (Person or Group)</td><td>Individuals or groups (other than PACs) making independent expenditures over $250 in a year must disclose those expenditures</td></tr>
   * <tr><td>N</td><td>PAC - Nonqualified</td><td>PACs that have not yet been in existence for six months and received contributions from 50 people and made contributions to five federal candidates. These committees have lower limits for their contributions to candidates.</td></tr>
   * <tr><td>O</td><td>Independent Expenditure-Only (Super PACs)</td><td>Political Committee that has filed a statement consistent with AO 2010-09 or AO 2010-11. For more information about independent expenditures</td></tr>
   * <tr><td>P</td><td>Presidential</td><td>Campaign committee for candidate for President</td></tr>
   * <tr><td>Q</td><td>PAC - Qualified</td><td>PACs that have been in existence for six months and received contributions from 50 people and made contributions to five federal candidates</td></tr>
   * <tr><td>S</td><td>Senate</td><td>Campaign committee for candidate for Senate</td></tr>
   * <tr><td>U</td><td>Single Candidate Independent Expenditure</td><td>Political Committee For more information about independent expenditures</td></tr>
   * <tr><td>V</td><td>PAC with Non-Contribution Account - Nonqualified</td><td>Political committees with non-contribution accounts</td></tr>
   * <tr><td>W</td><td>PAC with Non-Contribution Account - Qualified</td><td>Political committees with non-contribution accounts</td></tr>
   * <tr><td>X</td><td>Party - Nonqualified</td><td>Party committees that have not yet been in existence for six months and received contributions from 50 people, unless they are affiliated with another party committee that has met these requirements.</td></tr>
   * <tr><td>Y</td><td>Party - Qualified</td><td>Party committees that have existed for at least six months and received contributions from 50 people or are affiliated with another party committee that meets these requirements.</td></tr>
   * <tr><td>Z</td><td>National Party Nonfederal Account</td><td>National party nonfederal accounts. Not permitted after enactment of Bipartisan Campaign Reform Act of 2002.</td></tr>
   */
  public Integer reportType() { return (Integer)getField(Fields.RPT_TP.name()); }

  /**
   * This code indicates the election for which the contribution was made. EYYYY (election Primary, General, Other plus election year).
   * In practice, seems to not include election year, ust 'P', 'G', or 'O' (or null).
   */
  public String primaryGeneralIndicator() { return String.valueOf(getField(Fields.TRANSACTION_PGI.name())); }

  /** Indicates the physical location of the filing. */
  public String imageNum() { return String.valueOf(getField(Fields.IMAGE_NUM.name())); }

  /** 
   * Transaction types 10J, 11J, 13, 15J, 15Z, 16C, 16F, 16G, 16R, 17R, 17Z, 18G, 18J, 18K, 18U, 19J, 20, 20C, 20F, 20G, 20R, 22H, 22Z, 23Y, 24A, 24C, 24E, 24F, 24G, 24H, 24K, 24N, 24P, 24R, 24U, 24Z and 29 are included in the OTH file.
   * @see http://www.fec.gov/finance/disclosure/metadata/DataDictionaryTransactionTypeCodes.shtml
   */
  public String transactionType() { return String.valueOf(getField(Fields.TRANSACTION_TP.name())); }

  /**
   * One of:
   * <ul>
   * <li>CAN = Candidate</li>
   * <li>CCM = Candidate Committee</li>
   * <li>COM = Committee</li>
   * <li>IND = Individual (a person)</li>
   * <li>ORG = Organization (not a committee and not a person)</li>
   * <li>PAC = Political Action Committee</li>
   * <li>PTY = Party Organizationv
   * </ul>
   */
  public String entityType() { return String.valueOf(getField(Fields.ENTITY_TP.name())); }

  /**
   */
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

