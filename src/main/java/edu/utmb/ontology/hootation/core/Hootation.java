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
        
        System.out.println("Hootation created");
        
    }
    
    public ArrayList<String> get_naturalLangaugeStatements(String ontology_file){
        
        System.out.println("hootation:" + ontology_file);
        
        ArrayList<String> nl_statements = new ArrayList<String>();
        
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ToStringRenderer.setRenderer(() -> renderer);

        OWLOntologyManager man = OWLManager.createConcurrentOWLOntologyManager();
        try {
            ontology = man.loadOntologyFromOntologyDocument(new File(ontology_file));
            
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
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
        
        return nl_statements;
        
    }
    
    public static void main(String[] args) {
        Hootation h = new Hootation();
        h.get_naturalLangaugeStatements("/Users/mac/NetBeansProjects/nasa_dag_cdss/src/main/resources/rbo.owl");
    }
    
}
