/*
 * #%L
 * Triple2NL
 * %%
 * Copyright (C) 2015 Agile Knowledge Engineering and Semantic Web (AKSW)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.utmb.ontology.hootation.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Class holds a set of prepositions.
 * @author Axel Ngonga
 */
public class Preposition extends HashSet<String> {

	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private static final String filename = "/preposition_list.txt";
	
	public Preposition() {
		//this(Preposition.class.getClass().getResourceAsStream(filename));
		this(Preposition.class.getResourceAsStream(filename));
	}

	public Preposition(InputStream is) {
		try (BufferedReader bufRdr = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = bufRdr.readLine()) != null) {
				add(line.toLowerCase().trim());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * Determines whether the given token is contained in the list of prepositions.
	 * @param s the input token
	 * @return TRUE if the token is a preposition, otherwise FALSE
	 */
	public boolean isPreposition(String s) {
		return contains(s);
	}
}
