/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.hootation.core;

import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConversionException;
import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConverter;
import java.io.File;
import java.util.ArrayList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author tuan
 */
public class Hootation {
    
    
    private OWLOntology ontology;
    private OWLAxiomConverter converter;
    private ArrayList<String> nl_statements;
    
    public Hootation(){
        
    }
    
    public ArrayList<String> get_naturalLangaugeStatements(File ontology_file) throws OWLOntologyCreationException, OWLAxiomConversionException{
        ArrayList<String> nl_statements = new ArrayList<String>();
        
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ToStringRenderer.setRenderer(() -> renderer);

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
       
            ontology = man.loadOntologyFromOntologyDocument(ontology_file);

      
        
        
        converter = new OWLAxiomConverter(ontology);
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isLogicalAxiom()) {
                

                    String output = converter.convert(axiom);
                    System.out.println(output);
                    nl_statements.add(output);

                

            }
        }
        
        return nl_statements;
        
    }
    
    public static void main(String[] args) {
        
    }
    
}
