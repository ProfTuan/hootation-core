package edu.utmb.ontology.hootation.core.triple2nl;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;

/**
 * @author Lorenz Buehmann
 *
 */
public class SimpleIRIConverter implements IRIConverter {
	
	private IRIShortFormProvider sfp = new SimpleIRIShortFormProvider();
	
	private boolean splitCamelCase = true;
	private boolean replaceUnderScores = true;
	private boolean toLowerCase = false;
	private boolean omitContentInBrackets = true;

	/* (non-Javadoc)
	 * @see org.aksw.triple2nl.IRIConverter#convert(java.lang.String)
	 */
	@Override
	public String convert(String iri) {
		// get short form
		String shortForm = sfp.getShortForm(IRI.create(iri));
		//String shortForm = iri;
		
		//System.out.println("Short form: " + shortForm);
		
		// normalize
		shortForm = normalize(shortForm);
		
		return shortForm;
	}
	
	/* (non-Javadoc)
	 * @see org.aksw.triple2nl.IRIConverter#convert(java.lang.String, boolean)
	 */
	@Override
	public String convert(String iri, boolean dereferenceIRI) {
		return convert(iri);
	}
	
	private String splitCamelCase(String s) {
		// we only split if it contains a vowel
		if(!(s.matches(".*[aeiou].*"))){
			return s;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String token : s.split(" ")) {
			String[] tokenSplit = StringUtils.splitByCharacterTypeCamelCase(token);
			
			String noVowels = "";
			for (String t : tokenSplit) {
				if(t.matches(".*[aeiou].*") || !StringUtils.isAllUpperCase(t)){
					if(!noVowels.isEmpty()){
						sb.append(noVowels).append(" ");
						noVowels = "";
					}
					sb.append(t).append(" ");
				} else {
					noVowels += t;
				}
//				sb = new StringBuilder(sb.toString().trim());
			}
			sb.append(noVowels);
//			sb.append(" ");
		}
		return sb.toString().trim();
		//	    	return s.replaceAll(
		//	    	      String.format("%s|%s|%s",
		//	    	         "(?<=[A-Z])(?=[A-Z][a-z])",
		//	    	         "(?<=[^A-Z])(?=[A-Z])",
		//	    	         "(?<=[A-Za-z])(?=[^A-Za-z])"
		//	    	      ),
		//	    	      " "
		//	    	   );
	}
	
	private String normalize(String s){
		if(replaceUnderScores){
			s = s.replace("_", " ");
		}
        if(splitCamelCase){
        	s = splitCamelCase(s);
        }
        if(toLowerCase){
        	s = s.toLowerCase();
        }
        if(omitContentInBrackets){
        	s = s.replaceAll("\\(.+?\\)", "").trim();
        }
        return s;
	}
}
