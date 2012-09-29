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

  /**
   * A 9-character alpha-numeric code assigned to a committee by the Federal Election Commission. 
   * The committee ID for a specific committee always remains the same.
   */
  public String committeeID() { return String.valueOf(getField(Fields.CMTE_ID.name())); }

  /** The name of the committee. */
  public String committeeName() { return String.valueOf(getField(Fields.CMTE_NM.name())); }

  /** The officially registered treasurer for the committee. */
  public String treasurerName() { return String.valueOf(getField(Fields.TRES_NM.name())); }

  /** Street address, line 1 */
  public String committeeStreet1() { return String.valueOf(getField(Fields.CMTE_ST1.name())); }

  /** Street address, line 2 */
  public String committeeStreet2() { return String.valueOf(getField(Fields.CMTE_ST2.name())); }

  /** City */
  public String committeeCity() { return String.valueOf(getField(Fields.CMTE_CITY.name())); }

  /** State */
  public String committeeState() { return String.valueOf(getField(Fields.CMTE_ST.name())); }

  /** Postal ZIP code */
  public String committeeZip() { return String.valueOf(getField(Fields.CMTE_ZIP.name())); }

  /**
   * Designation of committee. One of:
   *
   * <ul>
   * <li>A = Authorized by a candidate</li>
   * <li>B = Lobbyist/Registrant PAC</li>
   * <li>D = Leadership PAC</li>
   * <li>J = Joint fundraiser</li>
   * <li>P = Principal campaign committee of a candidate</li>
   * <li>U = Unauthorized</li>
   * </ul>
   */
  public String committeeDesgination() { return String.valueOf(getField(Fields.CMTE_DSGN.name())); }

  /**
   * The "type" of committee. One of:
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
  public String committeeType() { return String.valueOf(getField(Fields.CMTE_TP.name())); }

  /**
   * Political party affiliation. Typically "DEM", "REP" or "IND".
   */
  public String committeeParty() { return String.valueOf(getField(Fields.CMTE_PTY_AFFILIATION.name())); }

  /**
   * Filing frequency. One of:
   * <ul>
   * <li>A = Administratively terminated</li>
   * <li>D = Debt</li>
   * <li>M = Monthly filer</li>
   * <li>Q = Quarterly filer</li>
   * <li>T = Terminated</li>
   * <li>W = Waived</li>
   * </ul>
   */
  public String committeeFilingFrequency() { return String.valueOf(getField(Fields.CMTE_FILING_FREQ.name())); }

  /**
   * Interest group category. One of:
   * <ul>
   * <li>C = Corporation</li>
   * <li>L = Labor organization</li>
   * <li>M = Membership organization</li>
   * <li>T = Trade association</li>
   * <li>V = Cooperative</li>
   * <li>W = Corporation without capital stock</li>
   * </ul> 
   */
  public String interestGroupCategory() { return String.valueOf(getField(Fields.ORG_TP.name())); }

  /**
   * Connected organization name. This field has an unclear intention.
   */
  public String connectedOrganizationName() { return String.valueOf(getField(Fields.CONNECTED_ORG_NM.name())); }

  /**
   * When a committee has a committee type designation of H, S, or P the identification number of the candidate will be entered in this field.
   */
  public String candidateID() { return String.valueOf(getField(Fields.CAND_ID.name())); }

}

