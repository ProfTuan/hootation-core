package edu.utmb.ontology.hootation.core.triple2nl;

/**
 * @author Lorenz Buehmann
 *
 */
public class PropertyVerbalization {
	
	private PropertyVerbalizationType verbalizationType;
	private String propertyURI;
	private String propertyText;
	private String expandedVerbalization;
	private String posTags;
	
	
	public PropertyVerbalization(String propertyURI, String propertyText, PropertyVerbalizationType verbalizationType) {
		this(propertyURI, propertyText, null, verbalizationType);
	}
	
	public PropertyVerbalization(String propertyURI, String propertyText, String posTags, PropertyVerbalizationType verbalizationType) {
		this.propertyURI = propertyURI;
		this.propertyText = propertyText;
		this.posTags = posTags;
		this.verbalizationType = verbalizationType;
		this.expandedVerbalization = propertyText;
	}
	
	/**
	 * @return the property URI
	 */
	public String getProperty() {
		return propertyURI;
	}
	
	/**
	 * @return the propertyText
	 */
	public String getVerbalizationText() {
		return propertyText;
	}
	
	/**
	 * @return the expanded verbalization text
	 */
	public String getExpandedVerbalizationText() {
		return expandedVerbalization;
	}
	
	/**
	 * @return the verbalizationType
	 */
	public PropertyVerbalizationType getVerbalizationType() {
		return verbalizationType;
	}
	
	/**
	 * @param verbalizationType the verbalizationType to set
	 */
	public void setVerbalizationType(PropertyVerbalizationType verbalizationType) {
		this.verbalizationType = verbalizationType;
	}
	
	/**
	 * @return the POS tags
	 */
	public String getPOSTags() {
		return posTags;
	}
	
	/**
	 * Set the expanded verbalization text.
	 * @param expandedVerbalization the expanded verbalization text
	 */
	public void setExpandedVerbalizationText(String expandedVerbalization) {
		this.expandedVerbalization = expandedVerbalization;
	}
	
	public boolean isNounType(){
		return verbalizationType == PropertyVerbalizationType.NOUN;
	}
	
	public boolean isVerbType(){
		return verbalizationType == PropertyVerbalizationType.VERB;
	}
	
	public boolean isUnspecifiedType(){
		return !(isVerbType() || isNounType());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "URI:" + propertyURI + 
				"\nText:" + propertyText + 
				"\nExpanded Text:" + expandedVerbalization + 
				"\nType: " + verbalizationType;
	}

}

