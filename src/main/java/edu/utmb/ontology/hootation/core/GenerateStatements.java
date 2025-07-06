package edu.utmb.ontology.hootation.core;


import edu.utmb.ontology.hootation.core.llm.LLMAdapter;
import edu.utmb.ontology.hootation.core.models.OutputRecord;
import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConversionException;
import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConverter;
import edu.utmb.ontology.hootation.core.util.CSVWriter;
import edu.utmb.ontology.hootation.core.util.ExcelWriter;
import java.io.File;
import java.util.ArrayList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;



import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateStatements {

    private UIInterface gui = null;

    private OWLOntology ontology;

    private OWLAxiomConverter converter;

    private ArrayList<String> nl_statements;

    private static GenerateStatements instance = null;

    private StringBuilder outputContent = null;

    private ArrayList<OutputRecord> outputRecords = null;
    
    private ArrayList<String> headers = new ArrayList<String>();

    protected GenerateStatements() {
        // TODO Auto-generated constructor stub
        
        headers.add("AXIOM TYPE");
        headers.add("AXIOM");
        headers.add( "NATURAL LANGUAGE TRANSLATION");
        
        
    }

    public static GenerateStatements getInstance() {
        if (instance == null) {
            instance = new GenerateStatements();
        }

        return instance;
    }

    public void init(File ontologyFile) {
        nl_statements = new ArrayList<String>();

        //old owlapi 4
        //ToStringRenderer.getInstance().setRenderer(new DLSyntaxObjectRenderer());
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ToStringRenderer.setRenderer(() -> renderer);

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        try {
            ontology = man.loadOntologyFromOntologyDocument(ontologyFile);

        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void convertAxiomsToStatements() {
        converter = new OWLAxiomConverter(ontology);
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isLogicalAxiom()) {
                try {

                    String output = converter.convert(axiom);
                    System.out.println(output);
                    nl_statements.add(output);

                } catch (OWLAxiomConversionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    public void outputAsCSVFile(String outputPathFile) {

        try {
            //String[] headers = {"AXIOM TYPE", "AXIOM", "NATURAL LANGUAGE TRANSLATION"};
            
            String [] header_array = new String[headers.size()];
            header_array = headers.toArray(header_array);
            
            CSVWriter csv_writer = new CSVWriter(header_array);
            
            csv_writer.write(outputPathFile, outputRecords, header_array);
        } catch (IOException ex) {
            Logger.getLogger(GenerateStatements.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void outputAsExcel(String outputPathFile) {

        //String[] headers = {"AXIOM TYPE", "AXIOM", "NATURAL LANGUAGE TRANSLATION"};
        
        String [] header_array = new String[headers.size()];
        header_array = headers.toArray(header_array);

        ExcelWriter xl_writer = new ExcelWriter(header_array);

        xl_writer.write(outputPathFile, outputRecords, header_array);

    }

    
    
    
    public void generateStatementsFromAxioms(File ontologyFile, UIInterface parent) {

        gui = parent;

        outputRecords = new ArrayList<OutputRecord>();
        //outputContent = new StringBuilder();
        LLMAdapter llm = LLMAdapter.getInstance();
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ToStringRenderer.setRenderer(() -> renderer);

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();

        try {
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(ontologyFile);
            OWLAxiomConverter converter = new OWLAxiomConverter(ontology);
            
            if(gui.performRefinement() && !gui.getLLMModelPath().trim().isBlank()){
                llm.initializeLLMModelEnrichement();
            }
            
            if(gui.performFactChecking() && !gui.getLLMModelPath().trim().isBlank()){
                llm.initializeLLMModelFactChecker();
            }
            

            for (OWLAxiom axiom : ontology.getAxioms()) {
                if (axiom.isLogicalAxiom()) {
                    String output = converter.convert(axiom);
                    if (output != null) {
                        
                        System.out.println("**Output: " + output + "\n");

                        gui.printToConsole("\n**Converting: " + axiom + " (" + axiom.getAxiomType().getName() + ")", Color.BLUE);
                        gui.printToConsole("**Output: " + output, Color.BLUE);

                        String output_line = axiom.getAxiomType().getName() + "\t" + axiom + "\t" + output + "\n";

                        OutputRecord output_record
                                = new OutputRecord();
                        output_record.setAxiom(axiom);
                        output_record.setAxiom_type(axiom.getAxiomType());
                        output_record.setNatural_language(output);
                        


                        outputRecords.add(output_record);

                    }
                }
            }
            
            //add the LLM improvements
            
            //refinement
             if(gui.performRefinement() && !gui.getLLMModelPath().trim().isBlank()){
                 
                 gui.printToConsole("\n\nPreparing LLM enrichment....Please wait", Color.RED);
                 
                 llm.initLLMEnrichment();
                 
                 headers.add("LLM ENHANCEMENT");
                 
                 llm.executeLLMEnhancement(outputRecords);
                 
                 gui.printToConsole("\nLLM enrichment completed.", Color.RED);
             }
            
            //fact checking option
            if(gui.performFactChecking() && !gui.getLLMModelPath().trim().isBlank()){
                
                gui.printToConsole("\n\nPreparing LLM fact checking.... Please wait", Color.RED);
                
                llm.initFactChecker();
                
                headers.add("FACT CHECK");
                
                llm.executeFactChecking(outputRecords);
                
                gui.printToConsole("\nLLM fact checking completed.", Color.RED);
            }
            
            

        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(GenerateStatements.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OWLAxiomConversionException ex) {
            Logger.getLogger(GenerateStatements.class.getName()).log(Level.SEVERE, null, ex);
        }
       

    }

    public void printNLStatements() {
        nl_statements.forEach(System.out::println);
    }

    public void reset() {
        nl_statements.clear();
    }

    public ArrayList<String> getNl_statements() {
        return nl_statements;
    }

    public void setNl_statements(ArrayList<String> nl_statements) {
        this.nl_statements = nl_statements;
    }

    public static void main(String[] args) throws Exception {
        GenerateStatements gs = GenerateStatements.getInstance();
        gs.init(new File("/Users/mac/HPVCO_Final_Draft_007.rdf"));
        gs.convertAxiomsToStatements();

    }

}
