/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.hootation.core;

import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConversionException;
import edu.utmb.ontology.hootation.core.owl2nl.OWLAxiomConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;

/**
 *
 * @author tuan
 */
public class Hootation {
    
    
    
    private OWLOntologyManager man;
    private OWLOntology ontology;
    private OWLDataFactory factory = null;
    private OWLAxiomConverter converter;
    private ArrayList<String> nl_statements;
    
    public Hootation(){
        
        System.out.println("Hootation created");
        
    }
    
    public Hootation(OWLOntology ontology, OWLOntologyManager manager){
        
        this.ontology = ontology;
        this.man = manager;
        
    }
    
    public ArrayList<String> get_naturalLanguageStatements(Set<OWLAxiom> seeded_axioms){
        ArrayList<String> nl_statements = new ArrayList<String>();
        
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        ToStringRenderer.setRenderer(() -> renderer);
        
        converter = new OWLAxiomConverter(ontology);
        
        for(OWLAxiom axiom : seeded_axioms){
            
            if(axiom.isLogicalAxiom()){
                
                
                try {
                    
                    String output = converter.convert(axiom);
                    //System.out.println(output);
                    nl_statements.add(output);
                    
                    
                } catch (OWLAxiomConversionException ex) {
                    System.getLogger(Hootation.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
                
            }
            
            
        }
        
        return nl_statements;
    }
    
    public ArrayList<String> get_naturalLangaugeStatements(String ontology_file){
        
        System.out.println("hootation:" + ontology_file);
        
        ArrayList<String> nl_statements = new ArrayList<String>();
        
        

        OWLOntologyManager man = OWLManager.createConcurrentOWLOntologyManager();
        try {
            ontology = man.loadOntologyFromOntologyDocument(new File(ontology_file));
            

            //set rendering
            DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();

            

            ToStringRenderer.setRenderer(() -> renderer);

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
    
    public void outputQuickStatements(String ontology_file, String output_file){
        
        StringBuilder export_content = new StringBuilder();
        
        export_content.append("AXIOM TYPE \t AXIOM \t NATURAL LANGUAGE TRANSLATION");

        
        try {
            
            man = OWLManager.createOWLOntologyManager();
            ontology = man.loadOntologyFromOntologyDocument(new File(ontology_file));
            factory = ontology.getOWLOntologyManager().getOWLDataFactory();
            
       
             
            
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
 
        DLSyntaxObjectRenderer renderer = new DLSyntaxObjectRenderer();
        
        ToStringRenderer.setRenderer(() -> renderer);
        
        converter = new OWLAxiomConverter(ontology);
        for (OWLAxiom axiom : ontology.getAxioms()) {
            if (axiom.isLogicalAxiom()) {
                try {

                    String output = converter.convert(axiom);
                    
                    //System.out.println(output);
                    //nl_statements.add(output);
                    String new_axiom = lazyRendering(axiom, ontology, factory);
                    export_content.append("\n" +axiom.getAxiomType().toString() + "\t" + new_axiom + "\t" + output);

                } catch (OWLAxiomConversionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        
        Path path_file = Paths.get(output_file );
        byte[] string_bytes = export_content.toString().getBytes();
        try {
            Files.write(path_file, string_bytes);
        } catch (IOException ex) {
            System.getLogger(Hootation.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
    
    //function to display the axiom's IRI (if there is) to labels
    public String lazyRendering(OWLAxiom axiom, OWLOntology ontology, OWLDataFactory factory){
        AtomicReference<String> a_string = new AtomicReference<>();
        a_string.set(axiom.toString());
        
        String str_axiom = axiom.toString();
 
        ontology.classesInSignature().forEach(oc->{
            
            String fragment = oc.getIRI().getFragment();
            
            if(str_axiom.contains(fragment)){
                
                Stream<OWLAnnotation> annotations = EntitySearcher.getAnnotations(oc, ontology, factory.getRDFSLabel());
                
                for(OWLAnnotation oa : annotations.collect(Collectors.toList())){
                    
                    if(oa.getValue() instanceof OWLLiteral){
                        
                         a_string.set( a_string.get().replaceAll(fragment, ((OWLLiteral)oa.getValue()).getLiteral()) );
                    }
                    
                }
                
                
            }
            
        });
        
        
        ontology.objectPropertiesInSignature().forEach(op->{
            
            String fragment = op.getIRI().getFragment();
            
            if(str_axiom.contains(fragment)){
                
                Stream<OWLAnnotation> annotations = EntitySearcher.getAnnotations(op, ontology, factory.getRDFSLabel());
                
                for(OWLAnnotation oa : annotations.collect(Collectors.toList())){
                    a_string.set( a_string.get().replaceAll(fragment, ((OWLLiteral)oa.getValue()).getLiteral()) );
                }
                
            }
            
        });
        
        ontology.dataPropertiesInSignature().forEach(dp->{
            String fragment = dp.getIRI().getFragment();
            
            if(str_axiom.contains(fragment)){
                Stream<OWLAnnotation> annotations = EntitySearcher.getAnnotations(dp, ontology, factory.getRDFSLabel());
                
                for(OWLAnnotation oa : annotations.collect(Collectors.toList())){
                    a_string.set( a_string.get().replaceAll(fragment, ((OWLLiteral)oa.getValue()).getLiteral()) );
                }
            }
            
        });
        
        
        return a_string.get().toString();
    }
    
    public static void main(String[] args) {
        
        
    }
    
  

    
    
    
}
