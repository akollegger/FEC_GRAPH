package org.followthedata.importer.fec;

import org.followthedata.importer.FecBatchImporter;
import org.followthedata.importer.Report;
import org.followthedata.importer.StdOutReport;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.LuceneBatchInserterIndexProvider;

import java.io.*;
import java.util.*;

import org.neo4j.helpers.collection.MapUtil;

import static org.neo4j.helpers.collection.MapUtil.map;
import static org.neo4j.helpers.collection.MapUtil.stringMap;
import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.EXACT_CONFIG;
import static org.neo4j.index.impl.lucene.LuceneIndexImplementation.FULLTEXT_CONFIG;

public class PresidentialFecImporter implements FecBatchImporter {
    protected static Report report;
    protected BatchInserter db;
    protected BatchInserterIndexProvider lucene;

    public static final int COMMITTEE_CAPACITY =   1500;
    public static final int CANDIDATE_CAPACITY =    600;
    public static final int CONTRIBUTION_CAPACITY = 10000;
    
    enum FecRelationshipTypes implements RelationshipType {CAMPAIGNS_FOR, SUPPORTS, CONNECTS, INTER_COMMITTEE_CONTRIBUTION, CANDIDATE_CONTRIBUTION, INDIVIDUAL_CONTRIBUTION, EARMARKED_BY }

    Map<String,Long> cachedCommittees = new HashMap<String,Long>(COMMITTEE_CAPACITY);
    Map<String,Long> cachedCandidates = new HashMap<String,Long>(CANDIDATE_CAPACITY);
    
    protected StdOutReport createReport() {
        return new StdOutReport(10 * 1000 * 1000, 100);
    }

    protected LuceneBatchInserterIndexProvider createIndexProvider() {
        return new LuceneBatchInserterIndexProvider(db);
    }

    protected BatchInserter createBatchInserter(File graphDb, Map<String, String> config) {
        return BatchInserters.inserter(graphDb.getAbsolutePath(), config);
    }

    public void finish() {
        lucene.shutdown();
        db.shutdown();
    }

    public void batchImport(File dataDir, File graphDb) throws IOException {
        Map<String, String> config = new HashMap<String, String>();
        try {
            if (new File("batch.properties").exists()) {
                System.out.println("Using Existing Configuration File");
            } else {
                System.out.println("Writing Configuration File to batch.properties");
                FileWriter fw = new FileWriter( "batch.properties" );
                fw.append( "use_memory_mapped_buffers=true\n"
                        + "neostore.nodestore.db.mapped_memory=100M\n"
                        + "neostore.relationshipstore.db.mapped_memory=500M\n"
                        + "neostore.propertystore.db.mapped_memory=1G\n"
                        + "neostore.propertystore.db.strings.mapped_memory=200M\n"
                         + "neostore.propertystore.db.arrays.mapped_memory=0M\n"
                         + "neostore.propertystore.db.index.keys.mapped_memory=15M\n"
                         + "neostore.propertystore.db.index.mapped_memory=15M" );
                fw.close();
            }
            
        config = MapUtil.load( new File(
                "batch.properties" ) );

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
                
        db = createBatchInserter(graphDb, config);
        lucene = createIndexProvider();
        report = createReport();

        try {
            importCandidates(new File(dataDir.getPath() + File.separator + "cn.txt"));
            importCommittees(new File(dataDir.getPath() + File.separator + "cm.txt"));
            importCandidateToCommitteeLinks(new File(dataDir.getPath() + File.separator + "ccl.txt"));
            importInterCommitteeContributions(new File(dataDir.getPath() + File.separator + "itoth.txt"));
            importCandidateContributions(new File(dataDir.getPath() + File.separator + "itpas2.txt"));
            importIndividualContributions(new File(dataDir.getPath() + File.separator + "itcont.txt"));
        } finally {
            finish();
        }
    }
     

    protected void importCandidates(File candidateFile) throws IOException {
        Reader reader = new FileReader(candidateFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex nodeIndex = indexProvider.nodeIndex( "candidates", MapUtil.stringMap( "type", "exact" ) );
        nodeIndex.setCacheCapacity( FecCandidateRecord.Fields.CAND_NAME.name(), 100000 );

        int count=0;
        while (((line = bf.readLine()) != null) && (count < CANDIDATE_CAPACITY)) {
            FecCandidateRecord record = FecCandidateRecord.parse(line);
            if ("P".equals(record.candidateOffice())) {
                count++;
                long nodeid = db.createNode(record.getMappedFields());
                Map<String, Object> properties = MapUtil.map( 
                    FecCandidateRecord.Fields.CAND_ID.name(), record.candidateID(),
                    FecCandidateRecord.Fields.CAND_NAME.name(), record.candidateName()
                );
                nodeIndex.add(nodeid,properties);
                cachedCandidates.put(record.candidateID(), nodeid);
                nodeIndex.flush();
                report.dots();
            }
        }
        nodeIndex.flush();
        indexProvider.shutdown();
        
        report.finishImport("Candidates");
    }

    protected void importCommittees(File committeeFile) throws IOException {
        Reader reader = new FileReader(committeeFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex nodeIndex = indexProvider.nodeIndex( "committees", MapUtil.stringMap( "type", "exact" ) );
        nodeIndex.setCacheCapacity( FecCommitteeRecord.Fields.CMTE_NM.name(), 100000 );

        int count=0;
        while ((line = bf.readLine()) != null) {
            FecCommitteeRecord record = FecCommitteeRecord.parse(line);

            Long candidate = cachedCandidates.get(record.candidateID());
            if (candidate != null) {
                long nodeid = db.createNode(record.getMappedFields());
                Map<String, Object> properties = MapUtil.map( 
                    FecCommitteeRecord.Fields.CMTE_ID.name(), record.committeeID(),
                    FecCommitteeRecord.Fields.CMTE_NM.name(), record.committeeName()
                );
                nodeIndex.add(nodeid,properties);
                cachedCommittees.put(record.committeeID(), nodeid);
                nodeIndex.flush();
                db.createRelationship(nodeid, candidate, FecRelationshipTypes.CAMPAIGNS_FOR, null);
            }
            report.dots();
        }
        nodeIndex.flush();
        indexProvider.shutdown();
        
        report.finishImport("Committees");
    }

    protected void importCandidateToCommitteeLinks(File cclLinkFile) throws IOException {
        Reader reader = new FileReader(cclLinkFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        while ((line = bf.readLine()) != null) {
            FecCandidateToCommitteeRecord record = FecCandidateToCommitteeRecord.parse(line);

            // add relationship, if both nodes found
            Long candidate = cachedCandidates.get(record.candidateID());
            if (candidate != null) {
            
                Long committee = cachedCommittees.get(record.committeeID());
                if (committee != null) {
                    long relid = db.createRelationship(committee, candidate, FecRelationshipTypes.SUPPORTS, record.getMappedFields());
                }
            }
            report.dots();
        }
        
        report.finishImport("Candidate to Committee Links");
    }

    protected void importInterCommitteeContributions(File interCommitteeFile) throws IOException {
        Reader reader = new FileReader(interCommitteeFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        while ((line = bf.readLine()) != null) {
            FecInterCommitteeContribRecord record = FecInterCommitteeContribRecord.parse(line);

            // add connections
            Long toCommittee = cachedCommittees.get(record.committeeID());
            if (toCommittee != null) {
                String otherID = record.otherID();
                if ((otherID != null) && (otherID.length() > 0)) {
                    String entityType = record.entityType();
                    Long fromCandidate = cachedCandidates.get(otherID);
                    if (fromCandidate != null) {
                        db.createRelationship(fromCandidate, toCommittee, FecRelationshipTypes.INTER_COMMITTEE_CONTRIBUTION, record.getMappedFields());
                    } else {
                        Long fromCommittee = cachedCommittees.get(otherID);
                        if (fromCommittee != null) {
                            db.createRelationship(fromCommittee, toCommittee, FecRelationshipTypes.INTER_COMMITTEE_CONTRIBUTION, record.getMappedFields());
                        } else {
                            // not found? perhaps an older identifier. TODO - look for FEC identifier master file
                            // System.err.println("inter-committee record failed to lookup otherID, for subID: "+record.subID());
                        }
                    }
                } 
            } 

            report.dots();
        }
        
        report.finishImport("Inter Committee Contributions");
    }

    protected void importCandidateContributions(File contribFile) throws IOException {
        Reader reader = new FileReader(contribFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        int count=0;
        while ((line = bf.readLine()) != null) {
            FecCandidateContributionRecord record = FecCandidateContributionRecord.parse(line);

            // add connections
            Long candidate = cachedCandidates.get(record.candidateID());
            if (candidate != null) {
                Long committee = cachedCommittees.get(record.committeeID());
                if (committee != null) {
                    db.createRelationship(committee, candidate, FecRelationshipTypes.CANDIDATE_CONTRIBUTION, record.getMappedFields());
                }
            }

            report.dots();
        }
        
        report.finishImport("Candidate Contributions");
    }

    protected void importIndividualContributions(File contribFile) throws IOException {
        Reader reader = new FileReader(contribFile);
        BufferedReader bf = new BufferedReader(reader);
        String line;
        report.reset();

        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex nodeIndex = indexProvider.nodeIndex( "individuals", MapUtil.stringMap( "type", "exact" ) );
        nodeIndex.setCacheCapacity( FecIndividualContributionRecord.Fields.SUB_ID.name(), 100000 );

        while ((line = bf.readLine()) != null) {
            FecIndividualContributionRecord record = FecIndividualContributionRecord.parse(line);
            Long toCommittee = cachedCommittees.get(record.committeeID());
            if (toCommittee != null) {

                long nodeid = db.createNode(record.getMappedFields());
                Map<String, Object> properties = MapUtil.map( 
                    FecIndividualContributionRecord.Fields.CMTE_ID.name(), record.committeeID(),
                    FecIndividualContributionRecord.Fields.SUB_ID.name(), record.subID()
                );

                nodeIndex.add(nodeid,properties);
                nodeIndex.flush();

                // add connections
                db.createRelationship(nodeid, toCommittee, FecRelationshipTypes.INDIVIDUAL_CONTRIBUTION, null);
            
                String otherID = record.otherID();
                if ((otherID != null) && (otherID.length() > 0)) {
                    String entityType = record.entityType();
                    Long fromCandidate = cachedCandidates.get(otherID);
                    if (fromCandidate != null) {
                        db.createRelationship(nodeid, fromCandidate, FecRelationshipTypes.EARMARKED_BY, null);
                    } else {
                        Long fromCommittee = cachedCommittees.get(otherID);
                        if (fromCommittee != null) {
                            db.createRelationship(nodeid, fromCommittee, FecRelationshipTypes.EARMARKED_BY, null);
                        }
                    }
                }
            }
            report.dots();
        }
        nodeIndex.flush();
        indexProvider.shutdown();
        
        report.finishImport("Individual Contributions");
    }

}