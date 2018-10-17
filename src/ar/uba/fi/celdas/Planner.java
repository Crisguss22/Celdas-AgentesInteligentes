package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Planner {

	public Map<Integer, Float> reevaluateTheories(Theories theories, List<Theory> possibleTheories) {
		// TODO Auto-generated method stub
		Map<Integer, Float> theoriesPunctuations = new HashMap<Integer, Float>();
		List<Theory> filteredTheories = filterTheories(possibleTheories);
		for (Theory theory: filteredTheories) {
			float eval = evaluateTheory(theories, theory);
			if (eval < 0) { eval = 0;}
			theoriesPunctuations.put(theory.hashCode(), eval);
		}
		
		return theoriesPunctuations;
	}
	
	private float evaluateTheory(Theories theories, Theory theory) {
		List<Theory> possibleNextTheories = theories.getSortedListForPredictedtState(theory);
		float theoryValue = theory.getUtility() + evaluateRamifications(filterTheories(possibleNextTheories), 2);
		return theoryValue;
	}
	
	private float evaluateRamifications(List<Theory> theories, int depth) {
		float value = 0;
		
		return value;
	}
	
	public List<Theory> filterTheories(List<Theory> theories) {
		List<Theory> filteredTheories = new ArrayList<Theory>();
		for (Theory theory:theories) {
			if (!theory.hasFailed() && theory.getUtility() > 0) {
				filteredTheories.add(theory);
			}
		}
		Collections.sort(filteredTheories);
		return filteredTheories;
	}

}
