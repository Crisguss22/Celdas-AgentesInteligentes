package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Planner {
	
	private Theories theories;
	
	protected Random randomGenerator;
	Map<Integer,Integer> statesUndergone;
	List<Theory> currentPlan;
	Integer lastState;
    
	public Planner(Theories theories) {
		this.theories = theories;
		this.statesUndergone = new HashMap<Integer, Integer>();
		randomGenerator = new Random();
		currentPlan = new ArrayList<Theory>();
	}

	public Theory selectTheory(List<Theory> usefulTheories) {
		if (usefulTheories.size() < 2) { 
			return usefulTheories.get(0);
		}
		float sum = 0;
		for (Theory theory: usefulTheories) {
			sum += theoryScore(theory);
		}
		int index = randomGenerator.nextInt(Math.round(sum-1));
		int counter = 0;
		for (Theory theory: usefulTheories) {
			int theoryChances = theoryScore(theory);
    		if ((index >= counter) && (index < counter+theoryChances)) {
    			return theory;
    		}
    		counter += theoryChances;
		}
		return usefulTheories.get(0);
	}

	public void registerTheory(Theory theory) {
		int key = theory.hashCodeOnlyCurrentState();
		if (!statesUndergone.containsKey(key)) {
			statesUndergone.put(key, 1);
		}
		lastState = key;
		updateStatesUndergone();
	}

	private void updateStatesUndergone() {
		Set<Integer> keys = statesUndergone.keySet();
		for (Integer key: keys) {
			int oldValue = statesUndergone.get(key);
			statesUndergone.replace(key, oldValue+1);
		}
	}
	
	public int theoryScore(Theory theory) {
		int key = theory.hashCodeOnlyPredictedState();
		int modifier = 1;
		if (statesUndergone.containsKey(key)) {
			modifier = statesUndergone.get(key);
			if (modifier > 8) {modifier = 8;}
		}
		if (key==lastState) { modifier = 20;}
		return Math.round((theory.getUtility()*1000) / modifier);
	}

	public Theory planVictory(List<Theory> usefulTheories) {		
		Theory nextTheory = getNextTheoryInPlan(usefulTheories.get(0).hashCodeOnlyCurrentState());
		
		if (nextTheory==null) {
			makeNewPlan(usefulTheories);
			nextTheory = getNextTheoryInPlan(usefulTheories.get(0).hashCodeOnlyCurrentState());
		}
		
		if (nextTheory==null) {
			return selectTheory(usefulTheories);
		}
		
		return nextTheory;
	}

	private Theory getNextTheoryInPlan(int currentState) {
		for (Theory theory: currentPlan) {
			if (theory.hashCodeOnlyCurrentState() == currentState) {
				return theory;
			}
		}
		return null;
	}

	private void makeNewPlan(List<Theory> usefulTheories) {
		List<Theory> winningTheories = theories.getWinningTheories();
		int currentState = usefulTheories.get(0).hashCodeOnlyCurrentState();
		for (Theory theory: winningTheories) {
			List<Integer> statesAlreadyCovered = new ArrayList<Integer>();
			statesAlreadyCovered.add(theory.hashCodeOnlyCurrentState());
			List<Theory> possiblePlan = makePathLeadingHere(currentState, statesAlreadyCovered, theory.hashCodeOnlyCurrentState());
			currentPlan = possiblePlan;
		}
	}

	private List<Theory> makePathLeadingHere(int currentState, List<Integer> statesAlreadyCovered,
			int here) {
		List<Theory> theoriesLeadingHere = theories.getSortedListByPredictedState(here);
		List<Theory> formingPlan = new ArrayList<Theory>();
		for (Theory theory:theoriesLeadingHere) {
			if (theory.hashCodeOnlyCurrentState() == currentState) {
				formingPlan.add(theory);
				return formingPlan;
			}
			statesAlreadyCovered.add(theory.hashCodeOnlyCurrentState());
			List<Theory> tryingPlan = makePathLeadingHere(currentState, statesAlreadyCovered, theory.hashCodeOnlyCurrentState());
			if (tryingPlan!=null) {
				formingPlan = tryingPlan;
				formingPlan.add(theory);
				return formingPlan;
			}
		}
		return null;
	}

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
