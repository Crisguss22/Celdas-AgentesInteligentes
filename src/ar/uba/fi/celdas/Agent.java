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
    /**
     * Random generator for the agent.
     */
    protected Random randomGenerator;
    /**
     * List of available actions for the agent
     */
    protected ArrayList<Types.ACTIONS> actions;

    protected Theories theories;
    
    protected TheoryFactory theoryFactory;
    
    protected Planner planner;
    
    protected Theory lastTheory;
    
    protected TheoryReevaluator theoryReevaluator;
    
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
        theoryReevaluator = new TheoryReevaluator();
        
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
        
        if (lastTheory!=null) {theoryReevaluator.updateTheoryMidGame(stateObs, lastTheory);}
        
        List<Theory> possibleTheories = estimatePossibleTheories(perception);
        possibleTheories = loadPossibleTheories(possibleTheories);
        
        Map<Integer, Float> chances = planner.ponderateTheories(possibleTheories);
    	
        Theory finalTheory = chooseTheory(possibleTheories, chances);
        finalTheory.addUse();
        if (!theories.existsTheory(finalTheory)) { 
        	try {
				theories.add(finalTheory);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
        
        lastTheory = finalTheory;
        return finalTheory.getAction();
//        counter ++;
//        return actions.get(actionsIndex[counter]);
    }
    
    public void result(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
    	
    	boolean gameOver = stateObs.isGameOver();
    	boolean isAlive = stateObs.isAvatarAlive();
    	
    	Perception perception = new Perception(stateObs);
    	theoryReevaluator.updateTheoryEndGame(stateObs, lastTheory);
    	
    	if (gameOver) {    	
	    	try {
				TheoryPersistant.save(theories);
			} catch (JsonIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
    	}
    }
    
    private List<Theory> estimatePossibleTheories(Perception perception) {
    	List<Theory> possibleTheories = new ArrayList<Theory>();
    	for (Types.ACTIONS action: actions ) {
    		possibleTheories.add(theoryFactory.create(perception, action));
    	}
    	return possibleTheories;
    }
    
    private List<Theory> loadPossibleTheories(List<Theory> possibleTheories) {
    	List<Theory> finalTheories = new ArrayList<Theory>();
    	List<Theory> existingTheories = theories.getSortedListForCurrentState(possibleTheories.get(0));
    	for (Theory theory: existingTheories) {
    		finalTheories.add(theory);
    	}
    	for (Theory theory: possibleTheories) {
    		if (!theories.existsTheory(theory)) { finalTheories.add(theory);};
    	}
    	return finalTheories;
    }
    
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
    
    private void updateTheory(Perception perception) {
    	if (lastTheory.comparePrediction(perception.getLevel())) {
    		lastTheory.addSuccess();
    	}
    }

}
