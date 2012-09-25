Follow the Data
===============

Basic importers for importing FEC Campaign Finance data into the [Neo4j](http://neo4j.org) Graph Database.

Requires
--------

- [Java](http://www.oracle.com/us/technologies/java/overview/index.html)
- [Apache Ant](http://ant.apache.org)

*Note*: that Java is just required for the initial batch import of data. The dataset can then 
be explored with Neo4j's own Cypher query language, or using one of the language drivers
listed below.

Follow these Steps
------------------

1. `git clone https://github.com/akollegger/FEC_GRAPH.git`
2. `cd FEC_GRAPH`
3. `ant initialize`
4. `ant`
5. `./bin/fec2graph --force --importer=[RAW|CONNECTED|RELATED]`
  - choose one of the importers, like `./bin/fec2graph --force --impoerter=RAW`
  - RAW: imports records with no modifications
  - CONNECTED: connects imported records based on cross-referenced IDs
  - RELATED: replaces "join table" records with graph relationships
6. `ant neo4j-start`

Wanna code? Get a Neo4j Driver
------------------------------

- [Ruby - Neograph](https://github.com/maxdemarzi/neography)
- [Java - Spring Data Neo4j](http://www.springsource.org/spring-data/neo4j)
- [Javascript - Node Neo4js](https://github.com/thingdom/node-neo4j)
- [Neo4jPHP - PHP](https://github.com/jadell/Neo4jPHP/)
- [Bulbflow - Python](http://bulbflow.com)
- [Neo4Django - Django](https://github.com/scholrly/neo4django/)
- [Neo4jClient - .net](http://nuget.org/packages/Neo4jClient/)
- [Neo4j-GO - Google Go](https://github.com/davemeehan/Neo4j-GO)

References
----------

- [Neo4j](http://neo4j.org) - the graph database
- [Neo4j Cypher Reference](http://docs.neo4j.org/chunked/milestone/cypher-query-lang.html)
- [FEC Campaign Finance Data](http://www.fec.gov/finance/disclosure/ftpdet.shtml)
