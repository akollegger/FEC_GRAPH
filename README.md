Follow the Data
===============

Basic importers for importing FEC Campaign Finance data into the [Neo4j](http://neo4j.org) Graph Database.

Requires
--------

- [Java](http://www.oracle.com/us/technologies/java/overview/index.html)
- [Apache Ant](http://ant.apache.org)

Follow these Steps
------------------

1. `git clone https://github.com/akollegger/FEC_GRAPH.git`
2. `cd FEC_GRAPH`
3. `ant initialize`
4. `ant`
5. `./bin/fec2graph --force --importer=[RAW|CONNECTED|RELATED]`
6. `ant neo4j-start`


References
----------

- [Neo4j](http://neo4j.org) - the graph database
- [Neo4j Cypher Reference](http://docs.neo4j.org/chunked/milestone/cypher-query-lang.html)
- [FEC Campaign Finance Data](http://www.fec.gov/finance/disclosure/ftpdet.shtml)
