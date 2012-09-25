package org.followthedata.importer;

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

public class AbkImporter extends DFauthImporter {
    
    public static final int USERS = 3000000;
    
    enum MyRelationshipTypes implements RelationshipType {SUPPORTS, FOR, CONTRIBUTES, RECEIVES, GAVE,SUPERPACGIFT,SUPERPACEXPEND,SUPERPACACTION}

    /* Cached donors -- individuals, committees and Super PACs -- anyway who can spend money. */
    Map<String,Long> cachedDonors = new HashMap<String,Long>(USERS);


    Map<String,Long> cachedContributions = new HashMap<String,Long>(USERS);
    
    
    public AbkImporter() {
    }

    @Override
    protected void importIndiv(Reader reader, int flag) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex idxIndivContrib = indexProvider.nodeIndex( "individuals", MapUtil.stringMap( "type", "exact" ) );
        idxIndivContrib.setCacheCapacity( IndividualRecord.Fields.indivNAME.name(), 2000000 );
        while ((line = bf.readLine()) != null) {
            IndividualRecord record = new IndividualRecord(line);

            long caller = db.createNode(record.getMappedFields());
            //System.out.println(caller);
            Map<String, Object> properties = MapUtil.map( 
                IndividualRecord.Fields.indivNAME.name(), record.indivNAME(),
                IndividualRecord.Fields.indivCITY.name(), record.indivCITY(),
                IndividualRecord.Fields.indivSTATE.name(), record.indivSTATE(),
                IndividualRecord.Fields.indivZIP.name(), record.indivZIP(),
                IndividualRecord.Fields.indivOCC.name(), record.indivOCC()
            );
            idxIndivContrib.add(caller,properties);
            cachedDonors.put(record.indivKEY(),caller);
           
            report.dots();
        }
        idxIndivContrib.flush();
        indexProvider.shutdown();
        report.finishImport("Individuals");
    }
    
    @Override
    protected void importCommittees(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex idxCommittees = indexProvider.nodeIndex( "committees", MapUtil.stringMap( "type", "exact" ) );
        idxCommittees.setCacheCapacity( CommitteeRecord.Fields.commNAME.name(), 100000 );

        while ((line = bf.readLine()) != null) {
            CommitteeRecord record = new CommitteeRecord(line);
            long committee = db.createNode(record.getMappedFields());
            Map<String, Object> properties = MapUtil.map( 
                CommitteeRecord.Fields.commNAME.name(), record.commNAME(),
                CommitteeRecord.Fields.commID.name(), record.commID(),
                CommitteeRecord.Fields.commTREAS.name(), record.commTREAS(),
                CommitteeRecord.Fields.commSTATE.name(), record.commSTATE()
            );
            idxCommittees.add(committee,properties);
            cachedDonors.put(record.commID(), committee);
            idxCommittees.flush();
            report.dots();
        }
        idxCommittees.flush();
        indexProvider.shutdown();
        
        report.finishImport("Committees");
    }

    @Override
    protected void importSuperPacs(Reader reader) throws IOException {
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
            SuperPacRecord record = new SuperPacRecord(line);
            Long lCommId = cachedDonors.get(record.committeeID());
            if (lCommId!=null){
                
            }else{
                long superPac = db.createNode(record.getMappedFields());
                cachedDonors.put(record.committeeID(), superPac);
            }
           
            report.dots();
        }
        report.finishImport("Super PACs");
    }

    @Override     
    protected void importSuperPacContrib(Reader reader) throws IOException {
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex idxSuperPacContribs = indexProvider.nodeIndex( "superPacDonations", MapUtil.stringMap( "type", "fulltext" ) );
        idxSuperPacContribs.setCacheCapacity( SuperPacDonationRecord.Fields.superPacID.name(), 200000 );

        report.reset();
        while ((line = bf.readLine()) != null) {
            SuperPacDonationRecord record = new SuperPacDonationRecord(line);
            long pacCont = db.createNode(record.getMappedFields());
            Long lCommId = cachedDonors.get(record.superPacID());
            if (lCommId!=null){
                db.createRelationship(lCommId, pacCont, MyRelationshipTypes.SUPERPACGIFT, null);
            }   
            
            Map<String, Object> properties = MapUtil.map( 
                SuperPacDonationRecord.Fields.superPacID.name(), record.superPacID(),
                SuperPacDonationRecord.Fields.donatingOrg.name(), record.donatingOrg(),
                SuperPacDonationRecord.Fields.donorLast.name(), record.donorLast(),
                SuperPacDonationRecord.Fields.donorFirst.name(), record.donorFirst(),
                SuperPacDonationRecord.Fields.donorState.name(), record.donorState()
            );
            idxSuperPacContribs.add(pacCont,properties);
            report.dots();
        }
        report.finishImport("Super PAC Contributions");
        idxSuperPacContribs.flush();
        indexProvider.shutdown();
    }
 
    @Override   
    protected void importSuperPacExpend(Reader reader) throws IOException {
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);  
        BatchInserterIndex idxSuperPacExpend = indexProvider.nodeIndex( "superPacExpend", MapUtil.stringMap( "type", "exact" ) );
        idxSuperPacExpend.setCacheCapacity( "commID", 200000 );

        report.reset();
        while ((line = bf.readLine()) != null) {
            SuperPacExpenditureRecord record = new SuperPacExpenditureRecord(line);
            long pacExpend = db.createNode(record.getMappedFields());
            Long lCommId = cachedDonors.get(record.spendingCommID());
            Long lCandId = cachedDonors.get(record.candidateID());
            if (lCommId!=null){
                db.createRelationship(lCommId, pacExpend, MyRelationshipTypes.SUPERPACEXPEND, null);
            }         
            if (lCandId!=null){
                db.createRelationship(lCandId, pacExpend, MyRelationshipTypes.SUPERPACACTION, null);
            }  
            
            Map<String, Object> properties = MapUtil.map(
                SuperPacExpenditureRecord.Fields.spendingCommID.name(), record.spendingCommID(),
                SuperPacExpenditureRecord.Fields.isSuperPac.name(), record.isSuperPac(),
                SuperPacExpenditureRecord.Fields.candidate.name(), record.candidate(),
                SuperPacExpenditureRecord.Fields.supportOppose.name(), record.supportOppose(),
                SuperPacExpenditureRecord.Fields.expendAmt.name(), record.expendAmt()
                );
            idxSuperPacExpend.add(pacExpend,properties);
            report.dots();
        }
        idxSuperPacExpend.flush();
        indexProvider.shutdown();
        report.finishImport("Super PAC Expenditures");
    }

    @Override
    protected void importCandidates(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);
        
        BatchInserterIndex candidates = indexProvider.nodeIndex( "candidates", MapUtil.stringMap( "type", "exact" ) );
        candidates.setCacheCapacity( "candidateName", 100000 );
        while ((line = bf.readLine()) != null) {
            strTemp = line.split("\\|");
            long polCand = db.createNode(data.update(line));
                Map<String, Object> properties = MapUtil.map( "candidateName", strTemp[1]);
                properties.put("candidateID", strTemp[0]);
                properties.put("candidateParty", strTemp[3]);
                properties.put("candidateOfficeState", strTemp[5]);
                properties.put("candidateElectionYear",strTemp[4]);
                candidates.add(polCand,properties);
                candidates.flush();
            Long lCommId = cachedDonors.get(strTemp[10]);
            if (lCommId!=null){
                db.createRelationship(lCommId, polCand, MyRelationshipTypes.SUPPORTS, null);
            }         
            report.dots();
        }
        candidates.flush();
        indexProvider.shutdown();
        report.finishImport("Candidates");
    }
    
    @Override
    protected void importContrib(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db);
        BatchInserterIndex contributors = indexProvider.nodeIndex( "contributions", MapUtil.stringMap( "type", "exact" ) );
        contributors.setCacheCapacity( "commID", 2500000 );
       
        while ((line = bf.readLine()) != null) {
            strTemp = line.split("\\|",-1);
            //System.out.println(line);
            long indContr = db.createNode(data.update(line));
            Long lCommId = cachedDonors.get(strTemp[1]);
            Long lIndivId = cachedDonors.get(strTemp[0]);
            if (lCommId!=null){
                db.createRelationship(lCommId, indContr, MyRelationshipTypes.RECEIVES, null);
                
            }  
            if (lIndivId!=null){
                long indRel = db.createRelationship(lIndivId, indContr, MyRelationshipTypes.GAVE, null);
            }   
            
            try{
                Map<String, Object> properties = MapUtil.map( "commID", strTemp[1]);
                properties.put("contribDate", strTemp[3]);
                properties.put("contribAmt", strTemp[4]);
                contributors.add(indContr,properties);
            } catch (Exception e){
                System.out.println(e);
            }
            report.dots();
            
        }
        contributors.flush();
        indexProvider.shutdown();
        report.finishImport("Nodes");
    }
   
    @Override 
    protected void importRelationships(Reader reader) throws IOException {
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 3);
        Object[] rel = new Object[3];
        final RelType relType = new RelType();
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
            final Map<String, Object> properties = data.update(line, rel);
            db.createRelationship(id(rel[0]), id(rel[1]), relType.update(rel[2]), properties);
            report.dots();
        }
        report.finishImport("Relationships");
    }

    @Override
    protected void importIndex(String indexName, BatchInserterIndex index, Reader reader) throws IOException {

        BufferedReader bf = new BufferedReader(reader);
        
        final Data data = new Data(bf.readLine(), "\\|", 1);
        Object[] node = new Object[1];
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {        
            final Map<String, Object> properties = data.update(line, node);
            index.add(id(node[0]), properties);
            report.dots();
        }
                
        report.finishImport("Done inserting into " + indexName + " Index");
    }

}