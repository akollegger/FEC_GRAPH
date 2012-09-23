package org.followthedata.importer;

import org.apache.commons.cli.*;
import java.io.*;
import java.util.*;
import org.neo4j.kernel.impl.util.FileUtils;


public class Tool {

  enum ImportImplementors {
      DFAUTH,
      AKOLLEGGER
  }

  FecBatchImporter fecBatchImporter;

  public Tool(FecBatchImporter fecBatchImporter)
  {
    this.fecBatchImporter = fecBatchImporter;
  }

  public void importAll(String[] committeeFilenames, 
    String[] candidateFilenames, 
    String[] individualFilenames, 
    String[] contributionFilenames, 
    String[] superPacFilenames, 
    String[] superPacContributionFilenames, 
    String[] superPacExpenditureFilenames)
  throws IOException
  {
    try {
      for (String committee : committeeFilenames) {
        importCommitteesFrom(new File(committee));
      }
      for (String candidate : candidateFilenames) {
        importCandidatesFrom(new File(candidate));
      }
      for (String individual : individualFilenames) {
        importIndividualsFrom(new File(individual));
      }
      for (String contribution : contributionFilenames) {
        importContributionsFrom(new File(contribution));
      }
      for (String superPac : superPacFilenames) {
        importCommitteesFrom(new File(superPac));
      }
      for (String superPacContributions : superPacContributionFilenames) {
        importSuperPacContributionsFrom(new File(superPacContributions));
      }
      for (String superPacExpenditure : superPacExpenditureFilenames) {
        importSuperPacExpendituresFrom(new File(superPacExpenditure));
      }
 
      System.out.println("finished");
    } finally {
      fecBatchImporter.finish();
    }
  }

  private void importCommitteesFrom(File committeeFile) throws IOException {
    if (committeeFile.exists()) fecBatchImporter.importCommittees(new FileReader(committeeFile));
  }
  private void importCandidatesFrom(File candidateFile) throws IOException {
    if (candidateFile.exists()) fecBatchImporter.importCandidates(new FileReader(candidateFile));
  }
  private void importIndividualsFrom(File individualFile) throws IOException {  
    if (individualFile.exists()) fecBatchImporter.importIndiv(new FileReader(individualFile),0); // ABK TODO - what's the flag for?
  }
  private void importContributionsFrom(File contributionFile) throws IOException {
    if (contributionFile.exists()) fecBatchImporter.importContrib(new FileReader(contributionFile));
  }
  private void importSuperPacContributionsFrom(File superPacContributionFile) throws IOException {
    if (superPacContributionFile.exists()) fecBatchImporter.importSuperPacContrib(new FileReader(superPacContributionFile));
  }
  private void importSuperPacExpendituresFrom(File superPacExpenditureFile) throws IOException {
    if (superPacExpenditureFile.exists()) fecBatchImporter.importSuperPacExpend(new FileReader(superPacExpenditureFile));
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


      File dataDir = new File(line.getOptionValue(datadir.getOpt(), "DATA"));
      if (!dataDir.exists()) {
        System.err.println("ERROR: FEC data file does not exist at " + dataDir.getPath() + ". Aborting.");
        System.exit(3);
      }

      String[] committees = new String[] {
        dataDir.getPath() + File.separator + "committee.dta"
      };
      String[] candidates = new String[] {
        dataDir.getPath() + File.separator + "candidate.dta"
      };
      String[] individuals = new String[] {
        dataDir.getPath() + File.separator + "indivContrib1.dta",
        dataDir.getPath() + File.separator + "indivContrib2.dta"
      };
      String[] contributions = new String[] {
        dataDir.getPath() + File.separator + "allIndivContrib1.dta",
        dataDir.getPath() + File.separator + "allIndivContrib2.dta",
        dataDir.getPath() + File.separator + "allIndivContrib3.dta",
        dataDir.getPath() + File.separator + "allIndivContrib4.dta",
        dataDir.getPath() + File.separator + "allIndivContrib5.dta"
      };
      String[] superPacs = new String[] {
        dataDir.getPath() + File.separator + "superPacList.dta"
      };
      String[] superPacContributions = new String[] {
        dataDir.getPath() + File.separator + "superPacDonors.dta"
      };
      String[] superPacExpenditures = new String[] {
        dataDir.getPath() + File.separator + "superPacExpend.dta"
      };

      // Pick a batch-importer implementation
      ImportImplementors selectedImplementor = ImportImplementors.valueOf(line.getOptionValue(importer.getOpt(), "AKOLLEGGER").toUpperCase());
      FecBatchImporter selectedImporter = null;
      switch (selectedImplementor) {
        // case DFAUTH: selectedImporter = new Importer(graphDbDirectory);
        default: selectedImporter = new AbkImporter(graphdbDirectory);
      }

      // run the batch import
      System.out.println("Importing raw data from " + dataDir.getPath() + " to graph at " + graphdbDirectory.getPath() + " using " + selectedImplementor);
      Tool fec2graph = new Tool(selectedImporter);

      fec2graph.importAll( 
        committees, 
        candidates, 
        individuals,
        contributions,
        superPacs,
        superPacContributions,
        superPacExpenditures
        );
   }
   catch( ParseException exp ) {
     System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
   }
   catch (IOException ioe) {
    System.err.println( "Import failed, because: " + ioe.getMessage() );
   }
  }
}