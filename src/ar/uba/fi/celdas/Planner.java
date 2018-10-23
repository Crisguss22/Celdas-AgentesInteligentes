package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Planner {
	
	private Theories theories;
	
	protected Random randomGenerator;
    
	public Planner(Theories theories) {
		this.theories = theories;
		randomGenerator = new Random();
	}

	public Theory planVictory(List<Theory> usefulTheories) {
		// TODO Auto-generated method stub
		return null;
	}

	public Theory selectTheory(List<Theory> usefulTheories) {
		if (usefulTheories.size() < 2) { 
			return usefulTheories.get(0);
		}
		float sum = 0;
		for (Theory theory: usefulTheories) {
			sum += theory.getUtility()*100;
		}
		int index = randomGenerator.nextInt(Math.round(sum-1));
		int counter = 0;
		Theory chosenTheory;
		for (Theory theory: usefulTheories) {
			int theoryChances = Math.round(theory.getUtility()*100);
    		if ((index >= counter) && (index < counter+theoryChances)) {
    			return theory;
    		}
    		counter += theoryChances;
		}
		return usefulTheories.get(0);
	}

//	public Map<Integer, Float> ponderateTheories(List<Theory> possibleTheories) {
//		// TODO Auto-generated method stub
//		Map<Integer, Float> theoriesPunctuations = new HashMap<Integer, Float>();
//		List<Theory> filteredTheories = filterTheories(possibleTheories);
//		for (Theory theory: filteredTheories) {
//			float eval = evaluateTheory(theories, theory);
//			if (eval < 0) { eval = 0;}
//			theoriesPunctuations.put(theory.hashCode(), eval);
//		}
//		
//		return theoriesPunctuations;
//	}
//	
//	private float evaluateTheory(Theories theories, Theory theory) {
//		List<Theory> possibleNextTheories = theories.getSortedListForPredictedtState(theory);
//		List<Integer> statesPassed = new ArrayList<Integer>();
//		statesPassed.add(theory.hashCodeOnlyCurrentState());
//		float theoryValue = theory.getUtility() + evaluateRamifications(filterTheories(possibleNextTheories), statesPassed, 2);
//		return theoryValue;
//	}
//	
//	private float evaluateRamifications(List<Theory> ramifications, List<Integer> statesPassed, int depth) {
//		float value = 0;
//		if (depth < 1 || ramifications.size() == 0) {return value;}
//		for (Theory theory: ramifications) {
//			List<Theory> possibleNextTheories = cleanLoops(theories.getSortedListForPredictedtState(theory), statesPassed);
//			float tempVal = theory.getUtility() + evaluateRamifications(possibleNextTheories, statesPassed, depth-1);
//			if (tempVal > value) {value = tempVal;}
//		}
//		return value;
//	}
//	
//	private List<Theory> cleanLoops(List<Theory> possibleTheories, List<Integer> statesPassed) {
//		List<Theory> notLoopedTheories = new ArrayList<Theory>();
//		for (Theory theory: possibleTheories) {
//			if (statesPassed.contains(theory.hashCodeOnlyPredictedState())) {
//				notLoopedTheories.add(theory);
//			}
//		}
//		return notLoopedTheories;
//	}
//
//	public List<Theory> filterTheories(List<Theory> theories) {
//		List<Theory> filteredTheories = new ArrayList<Theory>();
//		for (Theory theory:theories) {
//			if (!theory.hasFailed() && theory.getUtility() > 0) {
//				filteredTheories.add(theory);
//			}
//		}
//		Collections.sort(filteredTheories);
//		return filteredTheories;
//	}

}
