/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.ontology.hootation.core.llm.util;

import de.kherud.llama.args.MiroStat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mac
 */
public class LLMConfiguration {

    private int threads;
    private int layers;
    private float temperature;
    private boolean penalize;
    private int version;
    private int predict;
    private String modelPath;

    private final String propertyFile = "llm.properties";

    private final String modelsFile = "models.csv";

    private static LLMConfiguration INSTANCE = null;

    private String configPath;

    private String rootPath;

    private Properties property = new Properties();
    
    final private String PROMPT_FACT_CHECK = "User: Evaluate the accuracy of the statement: [statement]. Is this sentence factually true, false, or don't know. Answer the request only with a \"Yes\", \"No\", or \"Don't Know\". Do not provide explanation.";
    final private String PROMPT_TRANSLATION = "You are a helpful assistant\n. User: Please translate the ontology axiom using natural langauge. The axiom type is: [axiom_type]. The axiom you need to translate is:  [axiom] . Your translation for this axiom should be one sentence and do not add an explaination.";

    private LLMConfiguration() {

        InputStream i_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);

        try {
            property.load(i_stream);
            //property.load(new FileInputStream(configPath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

        init();
    }
    
    public String getPROMPT_FACT_CHECK(){
        return PROMPT_FACT_CHECK;
    }
    
    public String getPROMPT_TRANSLATION(){
        return PROMPT_TRANSLATION;
    }

    public static LLMConfiguration getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new LLMConfiguration();
        }

        return INSTANCE;

    }

    public Map<String, String> collectLLMList() {
        Map<String, String> collect_llm_list = new HashMap<String, String>();

        InputStream i_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(modelsFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(i_stream));
        boolean header = true;
        String line_read;
        try {

            while ((line_read = reader.readLine()) != null) {

                if (header == true) {
                    header = false;
                } else {
                    String[] split = line_read.split(",");
                    String name = split[0];
                    String url = split[1];

                    collect_llm_list.put(name, url);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

        return collect_llm_list;

    }

    private void init() {

        threads = Integer.parseInt(property.getProperty("threads", "16"));

        layers = Integer.parseInt(property.getProperty("layers", "43"));

        temperature = Float.parseFloat(property.getProperty("temperature", "0.7f"));

        penalize = Boolean.parseBoolean(property.getProperty("should_penalize", "true"));

        version = Integer.parseInt(property.getProperty("mirostat_v"));

        predict = Integer.parseInt(property.getProperty("predict", "30"));

        modelPath = property.getProperty("file_path", "");

    }

    public int getNumThreads() {

        return threads;

    }
    
    public void setNumThread(int value){
        threads = value;
    }

    public int getLayers() {

        return layers;
    }

    public void setLayers(int value){
        layers = value;
    }
    
    public float getTemperature() {

        return temperature;

    }
    
    public void setTemperature(float value){
        temperature = value;
    }

    public boolean getShouldPenalize() {

        return penalize;
    }
    
    public void setShouldPenalize(boolean value){
        penalize = value;
    }

    public MiroStat getMiroStatType() {

        if (version == 1) {

            return MiroStat.V1;
        } else if (version == 2) {
            return MiroStat.V2;
        } else {
            return null;
        }

    }
    
    public void setMiroStatType(int version_value){
        version = version_value;
    }

    public int predictNumber() {

        return predict;
    }
    
    public void setPredictNumber(int value){
        predict = value;
    }

    public String getModelFilePath() {

        return this.modelPath;
    }
    
    public void setModelFilePath(String path_value){
        modelPath = path_value;
    }

}
