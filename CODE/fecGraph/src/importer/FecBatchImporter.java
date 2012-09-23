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

public abstract class FecBatchImporter {
    protected static Report report;
    protected BatchInserter db;
    protected BatchInserterIndexProvider lucene;
    
    
    public FecBatchImporter(File graphDb) {
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

    public void finish() {
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
            // final StringTokenizer st = new StringTokenizer(line, delim,true);
            final String[] values = line.split(delim);

//            System.out.println(line);
            if (values.length < lineSize) {
                System.err.println("ERROR: line has fewer than expected fields (" + lineSize + ")");
                System.err.println(line);
                System.exit(1); // ABK TODO: manage error codes
            }
            int count=0;
            for (int i = 0; i < lineSize; i++) {
                // String value = st.nextToken();
                String value = values[i];
                lineData[i] = value.trim().isEmpty() ? null : value;
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

    protected abstract void importIndiv(Reader reader, int flag) throws IOException;

    protected abstract void importCommittees(Reader reader) throws IOException;

    protected abstract void importSuperPac(Reader reader) throws IOException;

    protected abstract void importSuperPacContrib(Reader reader) throws IOException;

    protected abstract void importSuperPacExpend(Reader reader) throws IOException;

    protected abstract void importCandidates(Reader reader) throws IOException;

    protected abstract void importContrib(Reader reader) throws IOException;

    protected abstract void importRelationships(Reader reader) throws IOException;

    protected abstract void importIndex(String indexName, BatchInserterIndex index, Reader reader) throws IOException;

    protected BatchInserterIndex nodeIndexFor(String indexName, String indexType) {
        return lucene.nodeIndex(indexName, configFor(indexType));
    }

    protected BatchInserterIndex relationshipIndexFor(String indexName, String indexType) {
        return lucene.relationshipIndex(indexName, configFor(indexType));
    }

    protected Map<String, String> configFor(String indexType) {
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

    protected long id(Object id) {
        return Long.parseLong(id.toString());
    }
}