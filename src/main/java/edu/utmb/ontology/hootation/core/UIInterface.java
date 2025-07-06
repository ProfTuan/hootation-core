/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package edu.utmb.ontology.hootation.core;

import java.awt.Color;

/**
 *
 * @author tuan
 */
public interface UIInterface {

    public boolean performRefinement();

    public String getLLMModelPath();

    public boolean performFactChecking();

    public void printToConsole(String string, Color color);
    
}
