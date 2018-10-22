package ar.uba.fi.celdas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonIOException;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;

    protected Theories theories;
    
    protected TheoryFactory theoryFactory;
    
    protected Planner planner;
    
    protected Theory presentTheory;
    
    protected TheoryUpdater theoryUpdater;

//    protected int[] actionsIndex;
//    protected int counter;
    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
        theories = new Theories();
        theoryFactory = new TheoryFactory();
        planner = new Planner(theories);
        theoryUpdater = new TheoryUpdater();
        
//        actionsIndex = new int[]{3, 3, 3, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1};
//        actionsIndex = new int[]{3, 3, 3, 1, 1, 2, 2, 2, 1, 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1};
//        counter = -1;
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {    	
    	
    	Perception perception = new Perception(stateObs);
    	
        System.out.println(perception.toString());
        
        if (presentTheory!=null) {theoryUpdater.updateTheoryMidGame(stateObs, presentTheory);}
        addToTheories(presentTheory);
        
        List<Theory> knownTheories = loadKnownTheories(perception);
        
        List<Theory> usefulTheories = filterUsefulTheories(knownTheories);
        
        if ((usefulTheories.size() > 0  && !explore()) || actionsNotIncluded(knownTheories).size() == 0) {
        	planNext(usefulTheories, perception);
        } else {
        	makeRandomTheory(knownTheories, perception);
    	}

//		Map<Integer, Float> chances = planner.ponderateTheories(knownTheories);
    	
//        Theory finalTheory = chooseTheory(knownTheories, chances);
//        finalTheory.addUse();
        
        return presentTheory.getAction();
//        counter ++;
//        return actions.get(actionsIndex[counter]);
    }


	public void result(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
    	
    	boolean gameOver = stateObs.isGameOver();
    	
    	Perception perception = new Perception(stateObs);
    	theoryUpdater.updateTheoryEndGame(stateObs, presentTheory);
        addToTheories(presentTheory);
    	
    	if (gameOver) {    	
	    	try {
				TheoryPersistant.save(theories);
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
    	}
    }
    
    private List<Theory> loadKnownTheories(Perception perception) {
    	List<Theory> finalTheories = new ArrayList<Theory>();
    	List<Theory> existingTheories = theories.getSortedListForCurrentState(perception);
    	for (Theory theory: existingTheories) {
    		finalTheories.add(theory);
    	}
    	return finalTheories;
    }
    
    private List<Theory> filterUsefulTheories(List<Theory> knownTheories) {
    	List<Theory> finalTheories = new ArrayList<Theory>();
    	for (Theory theory: knownTheories) {
    		if (theory.getUtility() > 0) {
    			finalTheories.add(theory);
    		}
    	}
    	return finalTheories;
	}
    
    private boolean explore() {
    	int result = randomGenerator.nextInt(1);
		return (result == 1);
	}
    
    private void planNext(List<Theory> usefulTheories, Perception perception) {
		for (Theory theory:usefulTheories) {
			if (theory.getUtility() > 100) { 
				presentTheory = theory;
				return;
			}
		}
		if (this.theories.knownVictory()) {
			presentTheory = planner.planVictory(usefulTheories);
		} else {
			presentTheory = planner.selectTheory(usefulTheories);
		}
	}
    
    private List<Types.ACTIONS> actionsNotIncluded(List<Theory> exceptions) {
    	List<Types.ACTIONS> actionsExceptions = new ArrayList<Types.ACTIONS>();
		for (Theory exception: exceptions) {
			actionsExceptions.add(exception.getAction());
		}
		List<Types.ACTIONS> availableActions = new ArrayList<Types.ACTIONS>();
		for (Types.ACTIONS action: actions) {
			if (!actionsExceptions.contains(action)) { availableActions.add(action); }
		}
		return availableActions;
    }

	private void makeRandomTheory(List<Theory> exceptions, Perception perception) {
		List<Types.ACTIONS> possibleActions = actionsNotIncluded(exceptions);
		int index = randomGenerator.nextInt(possibleActions.size()-1);
		int counter = 0;
		for (Types.ACTIONS action: possibleActions) {
			if (index==counter) {
				presentTheory = new Theory(perception.getLevel(), action);
				return;
			} else {
				counter += 1;
			}
		}		
	}
	
	private void addToTheories(Theory theory) {
		if (!theories.existsTheory(theory)) { 
        	try {
				theories.add(theory);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}
    
//    private List<Theory> estimatePossibleTheories(Perception perception) {
//    	List<Theory> possibleTheories = new ArrayList<Theory>();
//    	for (Types.ACTIONS action: actions ) {
//    		possibleTheories.add(theoryFactory.create(perception, action));
//    	}
//    	return possibleTheories;
//    }
    
//    private List<Theory> loadPossibleTheories(List<Theory> possibleTheories) {
//    	List<Theory> finalTheories = new ArrayList<Theory>();
//    	List<Theory> existingTheories = theories.getSortedListForCurrentState(possibleTheories.get(0));
//    	for (Theory theory: existingTheories) {
//    		finalTheories.add(theory);
//    	}
//    	for (Theory theory: possibleTheories) {
//    		if (!theories.existsTheory(theory)) { finalTheories.add(theory);};
//    	}
//    	return finalTheories;
//    }
    
    private Theory chooseTheory(List<Theory> possibleTheories, Map<Integer, Float> chances) {
    	List<Theory> finalTheories = planner.filterTheories(possibleTheories);
    	int randomInt = 0;
    	for (Theory theory: finalTheories) {
    		randomInt += Math.round(chances.get(theory.hashCode()));
    	}
    	int index = randomGenerator.nextInt(randomInt);
    	int searchIndex = 0;
    	for (Theory theory: finalTheories) {
    		int theoryChances = Math.round(chances.get(theory.hashCode()));
    		if ((index >= searchIndex) && (index < searchIndex+theoryChances)) {
    			return theory;
    		}
    		searchIndex += theoryChances;
    	}
    	return possibleTheories.get(0);
    }

}
