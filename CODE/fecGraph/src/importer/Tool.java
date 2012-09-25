package org.followthedata.importer;

import org.apache.commons.cli.*;
import java.io.*;
import java.util.*;
import org.neo4j.kernel.impl.util.FileUtils;


public class Tool {

  enum ImportImplementors {
      DFAUTH,
      AKOLLEGGER,
      RAW,
      CONNECTED,
      RELATED
  }

  public static void main(String[] args) {

    Option help = new Option( "h", "help", false, "print this message" );
    Option force = new Option( "f", "force", false, "force overwrite of existing database, if it exists" );
    Option graphdb = new Option( "g", "graphdb", true, "location of graph database store directory (DEFAULT: fec.graphdb)" );
    Option datadir = new Option("d", "data", true, "location of FEC data files (DEFAULT: DATA)");
    Option importer = new Option("i", "importer", true, "name of importer to use for creating graph");

    Options options = new Options();
    options.addOption( help );
    options.addOption( force );
    options.addOption( graphdb );
    options.addOption( datadir );
    options.addOption( importer );

    CommandLineParser parser = new GnuParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse( options, args );

      if (line.hasOption(help.getOpt())) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(120);
        formatter.printHelp( "fec2graph", options );
      }

      File graphdbDirectory = new File(line.getOptionValue(graphdb.getOpt(), "fec.graphdb"));
      if (graphdbDirectory.exists()) {
        if (line.hasOption("force")) {
          try {
            FileUtils.deleteRecursively(graphdbDirectory);
          } catch (IOException ioe) {
            System.err.println("Failed to clear datbase directory " + graphdbDirectory.getPath() + " because: " + ioe.getMessage());
            System.exit(1);
          }
        } else {
          // database exists, without force
          System.err.println("WARNING: Graph database exists at " + graphdbDirectory.getPath());
          System.err.println("\tUse --force to overwrite. Aborting.");
          System.exit(2);
        }
      }

      // Pick a batch-importer implementation
      ImportImplementors selectedImplementor = ImportImplementors.valueOf(line.getOptionValue(importer.getOpt(), "AKOLLEGGER").toUpperCase());
      FecBatchImporter selectedImporter = null;
      String selectedDataDir = "DATA";
      switch (selectedImplementor) {
        case DFAUTH: 
          // selectedImporter = new Importer("DATA");
        case RAW:
          selectedImporter = new org.followthedata.importer.fec.RawFecImporter();
          selectedDataDir = "FEC-DATA";
          break;
        case CONNECTED:
          selectedImporter = new org.followthedata.importer.fec.ConnectedFecImporter();
          selectedDataDir = "FEC-DATA";
          break;
        case RELATED:
          selectedImporter = new org.followthedata.importer.fec.RelatedFecImporter();
          selectedDataDir = "FEC-DATA";
          break;
        default: 
          selectedImporter = new org.followthedata.importer.fec.RawFecImporter();
          selectedDataDir = "FEC-DATA";
          break;
      }

      File dataDir = new File(line.getOptionValue(datadir.getOpt(), selectedDataDir));
      if (!dataDir.exists()) {
        System.err.println("ERROR: FEC data directory does not exist at " + dataDir.getPath() + ". Aborting.");
        System.exit(3);
      }

      // run the batch import
      System.out.println("Importing data from " + dataDir.getPath() + " to graph at " + graphdbDirectory.getPath() + " using " + selectedImplementor);

      selectedImporter.batchImport(dataDir, graphdbDirectory);

   }
   catch( ParseException exp ) {
     System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
   }
   catch (IOException ioe) {
    System.err.println( "Import failed, because: " + ioe.getMessage() );
   }
  }
}