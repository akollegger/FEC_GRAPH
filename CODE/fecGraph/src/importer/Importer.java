package importer;

import org.neo4j.graphdb.RelationshipType;
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

public class Importer {
    private static Report report;
    private BatchInserter db;
    private BatchInserterIndexProvider lucene;
    public static final File STORE_DIR = new File("/Volumes/HD1/Users/dsfauth/fec_200");
    public static final File CAND_FILE = new File("/Volumes/HD1/Users/dsfauth/fecdata/candidate.dta");
    public static final File COMMITTEE_FILE = new File("/Volumes/HD1/Users/dsfauth/fecdata/committee.dta");
    public static final File INDIV_FILE1 = new File("/Volumes/HD1/Users/dsfauth/fecdata/indivContrib1.dta");
    public static final File INDIV_FILE2 = new File("/Volumes/HD1/Users/dsfauth/fecdata/indivContrib2.dta");
    public static final File CONTRIB_FILE1 = new File("/Volumes/HD1/Users/dsfauth/fecdata/allIndivContrib1.dta");
    public static final File CONTRIB_FILE2 = new File("/Volumes/HD1/Users/dsfauth/fecdata/allIndivContrib2.dta");
    public static final File CONTRIB_FILE3 = new File("/Volumes/HD1/Users/dsfauth/fecdata/allIndivContrib3.dta");
    public static final File CONTRIB_FILE4 = new File("/Volumes/HD1/Users/dsfauth/fecdata/allIndivContrib4.dta");
    public static final File CONTRIB_FILE5 = new File("/Volumes/HD1/Users/dsfauth/fecdata/allIndivContrib5.dta");
    public static final File SUPERPAC_FILE = new File("/Volumes/HD1/Users/dsfauth/fecdata/superPacList.dta");
    public static final File SUPERPACEXPEND_FILE = new File("/Volumes/HD1/Users/dsfauth/fecdata/superPacExpend.dta");
    public static final File SUPERPACCONTRIB_FILE = new File("/Volumes/HD1/Users/dsfauth/fecdata/superPacDonors.dta");
    public static final int USERS = 3000000;
    enum MyRelationshipTypes implements RelationshipType {SUPPORTS, FOR, CONTRIBUTES, RECEIVES, GAVE,SUPERPACGIFT,SUPERPACEXPEND,SUPERPACACTION}
   	Map<String,Long> cache = new HashMap<String,Long>(USERS);
    Map<String,Long> contribCache = new HashMap<String,Long>(USERS);
    
    public Importer(File graphDb) {
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
    }

    protected StdOutReport createReport() {
        return new StdOutReport(10 * 1000 * 1000, 100);
    }

    protected LuceneBatchInserterIndexProvider createIndexProvider() {
        return new LuceneBatchInserterIndexProvider(db);
    }

    protected BatchInserter createBatchInserter(File graphDb, Map<String, String> config) {
        return BatchInserters.inserter(graphDb.getAbsolutePath(), config);
    }

    public static void main(String[] args) throws IOException {
    	   
 //       if (args.length < 3) {
  //          System.err.println("Usage java -jar batchimport.jar data/dir nodes.csv relationships.csv [node_index node-index-name fulltext|exact nodes_index.csv rel_index rel-index-name fulltext|exact rels_index.csv ....]");
   //     }
//        File graphDb = new File(args[0]);
        File graphDb = STORE_DIR;
        File candFile = CAND_FILE;
        File commFile = COMMITTEE_FILE;
        File indivFile1 = INDIV_FILE1;
        File indivFile2 = INDIV_FILE2;
        File contribFile1 = CONTRIB_FILE1;
        File contribFile2 = CONTRIB_FILE2;
        File contribFile3 = CONTRIB_FILE3;
        File contribFile4 = CONTRIB_FILE4;
        File contribFile5 = CONTRIB_FILE5;
        File superPacList = SUPERPAC_FILE;
        File superExpend = SUPERPACEXPEND_FILE;
        File superContrib = SUPERPACCONTRIB_FILE;
//        File nodesFile = new File(args[1]);
//        File relationshipsFile = new File(args[2]);
        File indexFile;
        String indexName;
        String indexType;
        
        if (graphDb.exists()) {
            FileUtils.deleteRecursively(graphDb);
        }
        Importer importBatch = new Importer(graphDb);
        try {
            if (commFile.exists()) importBatch.importNodes(new FileReader(commFile));
            if (candFile.exists()) importBatch.importCandidates(new FileReader(candFile));
            if (indivFile1.exists()) importBatch.importNodes(new FileReader(INDIV_FILE1));
            if (indivFile2.exists()) importBatch.importNodes(new FileReader(INDIV_FILE2));
            if (contribFile1.exists()) importBatch.importContrib(new FileReader(contribFile1));
            if (contribFile2.exists()) importBatch.importContrib(new FileReader(contribFile2));
            if (contribFile3.exists()) importBatch.importContrib(new FileReader(contribFile3));
            if (contribFile4.exists()) importBatch.importContrib(new FileReader(contribFile4));
            if (contribFile5.exists()) importBatch.importContrib(new FileReader(contribFile5));
            if (superPacList.exists()) importBatch.importSuperPac(new FileReader(superPacList));
            if (superContrib.exists()) importBatch.importSuperPacContrib(new FileReader(superContrib));
            if (superExpend.exists()) importBatch.importSuperPacExpend(new FileReader(superExpend));
 //           if (relationshipsFile.exists()) importBatch.importRelationships(new FileReader(relationshipsFile));
//			for (int i = 3; i < args.length; i = i + 4) {
//				indexFile = new File(args[i + 3]);
 //               if (!indexFile.exists()) continue;
  //              indexName = args[i+1];
   //             indexType = args[i+2];
    //            BatchInserterIndex index = args[i].equals("node_index") ? importBatch.nodeIndexFor(indexName, indexType) : importBatch.relationshipIndexFor(indexName, indexType);
     //           importBatch.importIndex(indexName, index, new FileReader(indexFile));
	//		}
            System.out.println("finished");
		} finally {
            importBatch.finish();
        }
    }

    void finish() {
        lucene.shutdown();
        db.shutdown();
 //       report.finish();
    }

    public static class Data {
        private Object[] data;
        private final int offset;
        private final String delim;
        private final String[] fields;
        private final String[] lineData;
        private final Type types[];
        private final int lineSize;
        private int dataSize;

        public Data(String header, String delim, int offset) {
            this.offset = offset;
            this.delim = delim;
            fields = header.split(delim);
            lineSize = fields.length;
            types = parseTypes(fields);
            lineData = new String[lineSize];
            createMapData(lineSize, offset);
        }

        private Object[] createMapData(int lineSize, int offset) {
            dataSize = lineSize - offset;
            data = new Object[dataSize*2];
            for (int i = 0; i < dataSize; i++) {
                data[i * 2] = fields[i + offset];
            }
            return data;
        }

        private Type[] parseTypes(String[] fields) {
            Type[] types = new Type[lineSize];
            Arrays.fill(types, Type.STRING);
            for (int i = 0; i < lineSize; i++) {
                String field = fields[i];
                int idx = field.indexOf(':');
                if (idx!=-1) {
                   fields[i]=field.substring(0,idx);
                   types[i]= Type.fromString(field.substring(idx + 1));
                }
            }
            return types;
        }

        private int split(String line) {
            final StringTokenizer st = new StringTokenizer(line, delim,true);
//            System.out.println(line);
            int count=0;
            for (int i = 0; i < lineSize; i++) {
                String value = st.nextToken();
                if (value.equals(delim)) {
                    lineData[i] = null;
                } else {
                    lineData[i] = value.trim().isEmpty() ? null : value;
                    if (i< lineSize -1) st.nextToken();
                }
                if (i >= offset && lineData[i]!=null) {
                    data[count++]=fields[i];
                    data[count++]=types[i].convert(lineData[i]);
                }
            }
            return count;
        }

        public Map<String,Object> update(String line, Object... header) {
            int nonNullCount = split(line);
            if (header.length > 0) {
                System.arraycopy(lineData, 0, header, 0, header.length);
            }

            if (nonNullCount == dataSize*2) {
                return map(data);
            }
            Object[] newData=new Object[nonNullCount];
            System.arraycopy(data,0,newData,0,nonNullCount);
            return map(newData);
        }

    }

    static class StdOutReport implements Report {
        private final long batch;
        private final long dots;
        private long count;
        private long total = System.currentTimeMillis(), time, batchTime;

        public StdOutReport(long batch, int dots) {
            this.batch = batch;
            this.dots = batch / dots;
        }

        @Override
        public void reset() {
            count = 0;
            batchTime = time = System.currentTimeMillis();
        }

        @Override
        public void finish() {
            System.out.println("\nTotal import time: "+ (System.currentTimeMillis() - total) / 1000 + " seconds ");
        }

        @Override
        public void dots() {
            if ((++count % dots) != 0) return;
            System.out.print(".");
            if ((count % batch) != 0) return;
            long now = System.currentTimeMillis();
            System.out.println(" "+ (now - batchTime) + " ms for "+batch);
            batchTime = now;
        }

        @Override
        public void finishImport(String type) {
            System.out.println("\nImporting " + count + " " + type + " took " + (System.currentTimeMillis() - time) / 1000 + " seconds ");
        }
    }

    void importNodes(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long caller = db.createNode(data.update(line));
        	//System.out.println(caller);
        	cache.put(strTemp[0], caller);
           
            report.dots();
        }
        report.finishImport("Nodes");
    }
    
    void importSuperPac(Reader reader) throws IOException {
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

    void importSuperPacContrib(Reader reader) throws IOException {
    	System.out.println("SuperPac Contributions");
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long pacCont = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[2]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, pacCont, MyRelationshipTypes.SUPERPACGIFT, null);
            }         
            report.dots();
        }
        report.finishImport("Nodes");
    }
    
    void importSuperPacExpend(Reader reader) throws IOException {
    	System.out.println("SuperPac Expenditures");
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long pacExpend = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[2]);
        	Long lCandId = cache.get(strTemp[6]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, pacExpend, MyRelationshipTypes.SUPERPACEXPEND, null);
            }         
            if (lCandId!=null){
            	db.createRelationship(lCandId, pacExpend, MyRelationshipTypes.SUPERPACACTION, null);
            }         
            report.dots();
        }
        report.finishImport("Nodes");
    }

    void importCandidates(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long polCand = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[10]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, polCand, MyRelationshipTypes.SUPPORTS, null);
            }         
            report.dots();
        }
        report.finishImport("Nodes");
    }
    
    void importContrib(Reader reader) throws IOException {
        String[] strTemp;
        BufferedReader bf = new BufferedReader(reader);
        final Data data = new Data(bf.readLine(), "\\|", 0);
        String line;
        report.reset();
        while ((line = bf.readLine()) != null) {
        	strTemp = line.split("\\|");
        	long indContr = db.createNode(data.update(line));
        	Long lCommId = cache.get(strTemp[1]);
        	Long lIndivId = cache.get(strTemp[0]);
            if (lCommId!=null){
            	db.createRelationship(lCommId, indContr, MyRelationshipTypes.RECEIVES, null);
            }  
            if (lIndivId!=null){
            	db.createRelationship(lIndivId, indContr, MyRelationshipTypes.GAVE, null);
            }         

            report.dots();
        }
        report.finishImport("Nodes");
    }
   
    
    void importRelationships(Reader reader) throws IOException {
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

    void importIndex(String indexName, BatchInserterIndex index, Reader reader) throws IOException {

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

    private BatchInserterIndex nodeIndexFor(String indexName, String indexType) {
        return lucene.nodeIndex(indexName, configFor(indexType));
    }

    private BatchInserterIndex relationshipIndexFor(String indexName, String indexType) {
        return lucene.relationshipIndex(indexName, configFor(indexType));
    }

    private Map<String, String> configFor(String indexType) {
        return indexType.equals("fulltext") ? FULLTEXT_CONFIG : EXACT_CONFIG;
    }

    static class RelType implements RelationshipType {
        String name;

        public RelType update(Object value) {
            this.name = value.toString();
            return this;
        }

        public String name() {
            return name;
        }
    }

    public enum Type {
        BOOLEAN {
            @Override
            public Object convert(String value) {
                return Boolean.valueOf(value);
            }
        },
        INT {
            @Override
            public Object convert(String value) {
                return Integer.valueOf(value);
            }
        },
        LONG {
            @Override
            public Object convert(String value) {
                return Long.valueOf(value);
            }
        },
        DOUBLE {
            @Override
            public Object convert(String value) {
                return Double.valueOf(value);
            }
        },
        FLOAT {
            @Override
            public Object convert(String value) {
                return Float.valueOf(value);
            }
        },
        BYTE {
            @Override
            public Object convert(String value) {
                return Byte.valueOf(value);
            }
        },
        SHORT {
            @Override
            public Object convert(String value) {
                return Short.valueOf(value);
            }
        },
        CHAR {
            @Override
            public Object convert(String value) {
                return value.charAt(0);
            }
        },
        STRING {
            @Override
            public Object convert(String value) {
                return value;
            }
        };

        private static Type fromString(String typeString) {
            if (typeString==null || typeString.isEmpty()) return Type.STRING;
            try {
                return valueOf(typeString.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Unknown Type "+typeString);
            }
        }

        public abstract Object convert(String value);
    }

    private long id(Object id) {
        return Long.parseLong(id.toString());
    }
}