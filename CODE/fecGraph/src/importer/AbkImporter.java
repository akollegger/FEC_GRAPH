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

public class AbkImporter extends FecBatchImporter {
 	
    public static final int USERS = 3000000;
    
    enum MyRelationshipTypes implements RelationshipType {SUPPORTS, FOR, CONTRIBUTES, RECEIVES, GAVE,SUPERPACGIFT,SUPERPACEXPEND,SUPERPACACTION}
   	Map<String,Long> cache = new HashMap<String,Long>(USERS);
    Map<String,Long> contribCache = new HashMap<String,Long>(USERS);
    
    public AbkImporter(File graphDb) {
        super(graphDb);
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
        	idxIndivContrib.setCacheCapacity( "indivName", 2000000 );
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long caller = db.createNode(data.update(line));
        	//System.out.println(caller);
        	Map<String, Object> properties = MapUtil.map( "indivName", strTemp[1]);
    		properties.put("indivCity", strTemp[2]);
    		properties.put("indivState", strTemp[3]);
    		properties.put("indivZip", strTemp[4]);
    		properties.put("indivOCC", strTemp[6]);
    		idxIndivContrib.add(caller,properties);
        	cache.put(strTemp[0], caller);
           
            report.dots();
        }
        idxIndivContrib.flush();
        indexProvider.shutdown();
        report.finishImport("Nodes");
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
        idxCommittees.setCacheCapacity( "commName", 100000 );

        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long committee = db.createNode(data.update(line));
        	Map<String, Object> properties = MapUtil.map( "commName", strTemp[1]);
    		properties.put("commID", strTemp[0]);
    		properties.put("commTreas", strTemp[3]);
    		properties.put("commState", strTemp[7]);
    		idxCommittees.add(committee,properties);
        	//System.out.println(caller);
        	cache.put(strTemp[0], committee);
           idxCommittees.flush();
            report.dots();
        }
        idxCommittees.flush();
        indexProvider.shutdown();
        
        report.finishImport("Nodes");
    }

    @Override
    protected void importSuperPac(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	Long lCommId = cache.get(strTemp[1]);
            if (lCommId!=null){
            	
            }else{
            	long caller = db.createNode(data.update(line));
            	cache.put(strTemp[0], caller);
            }
        	//System.out.println(caller);
           
            report.dots();
        }
        report.finishImport("Nodes");
    }

    @Override     
    protected void importSuperPacContrib(Reader reader) throws IOException {
    	String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db); 	
        BatchInserterIndex idxSuperPacContribs = indexProvider.nodeIndex( "superPacDonations", MapUtil.stringMap( "type", "fulltext" ) );
        idxSuperPacContribs.setCacheCapacity( "commID", 200000 );

        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long pacCont = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[2]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, pacCont, MyRelationshipTypes.SUPERPACGIFT, null);
            }   
            
            Map<String, Object> properties = MapUtil.map( "commID", strTemp[2]);
    		properties.put("donatingOrg", strTemp[3]);
    		properties.put("donorLast", strTemp[4]);
            properties.put("donorFirst", strTemp[5]);
    		properties.put("donorState", strTemp[7]);
    		// properties.put("donorFullName", strTemp[15]);
    		idxSuperPacContribs.add(pacCont,properties);
            report.dots();
        }
        System.out.println("Finished with SUPERPAC Contributions");
        report.finishImport("Nodes");
        idxSuperPacContribs.flush();
        indexProvider.shutdown();
    }
 
    @Override   
    protected void importSuperPacExpend(Reader reader) throws IOException {
    	 String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        LuceneBatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(db); 	
        BatchInserterIndex idxSuperPacExpend = indexProvider.nodeIndex( "superPacExpend", MapUtil.stringMap( "type", "exact" ) );
        idxSuperPacExpend.setCacheCapacity( "commID", 200000 );

        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        //	System.out.println(line);
        	long pacExpend = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[3]);
        	Long lCandId = cache.get(strTemp[7]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, pacExpend, MyRelationshipTypes.SUPERPACEXPEND, null);
            }         
            if (lCandId!=null){
            	db.createRelationship(lCandId, pacExpend, MyRelationshipTypes.SUPERPACACTION, null);
            }  
            
            Map<String, Object> properties = MapUtil.map( "commID", strTemp[2]);
    		properties.put("isSuperPAC", strTemp[3]);
    		properties.put("candidate", strTemp[5]);
    		properties.put("SUPPORT_OPPOSE", strTemp[6]);
    		properties.put("expendAmt", strTemp[12]);
    		idxSuperPacExpend.add(pacExpend,properties);
            report.dots();
        }
        idxSuperPacExpend.flush();
        indexProvider.shutdown();
        System.out.println("Finished with SUPERPAC Expenditures");
        report.finishImport("Nodes");
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
        	Long lCommId = cache.get(strTemp[10]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, polCand, MyRelationshipTypes.SUPPORTS, null);
            }         
            report.dots();
        }
    	candidates.flush();
    	indexProvider.shutdown();
        report.finishImport("Nodes");
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
        	Long lCommId = cache.get(strTemp[1]);
        	Long lIndivId = cache.get(strTemp[0]);
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