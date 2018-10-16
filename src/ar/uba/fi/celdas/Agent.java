package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    
    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        randomGenerator = new Random();
        actions = so.getAvailableActions();
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    	
    	
    	//TODO: Replace here the content and create an autonomous agent
    	Perception perception = new Perception(stateObs);
        System.out.println(perception.toString());
    	
        int index = randomGenerator.nextInt(actions.size());
        return actions.get(index);
    }
    
    private List<Theory> estimatePossibleTheories(Perception perception) {
    	List<Theory> possibleTheories = new ArrayList<Theory>();
    	for (Types.ACTIONS action: actions ) {
        	char[][] predictedState = predictState(perception, action);
	    	if (!predictedState.equals(perception.getLevel())) {
	    		possibleTheories.add(new Theory(perception.getLevel(), action, predictedState));
	    	}
    	}
    	return possibleTheories;
    }
    
    private char[][] predictState(Perception perception, Types.ACTIONS action) {
    	char[][] predictedState = new char[perception.getLevelHeight()][perception.getLevelWidth()];
    	for (int i = 0; i < perception.getLevelHeight(); i++) {
    		predictedState[i] = Arrays.copyOf(perception.getLevel()[i], perception.getLevel()[i].length);
		}
    	int actualAgentYPos = perception.getAgentY();
    	int actualAgentXPos = perception.getAgentX();
    	int predictedAgentYPos = perception.getAgentY();
    	int predictedAgentXPos = perception.getAgentX();
    	if (action.toString().equals("ACTION_RIGHT")&& (actualAgentXPos < perception.getLevelWidth()-1)) {
    		predictedAgentXPos = actualAgentXPos + 1;
    	}
    	if (action.toString().equals("ACTION_LEFT") && (actualAgentXPos > 0)) {
    		predictedAgentXPos = actualAgentXPos - 1;
    	}
    	if (action.toString().equals("ACTION_DOWN")&& (actualAgentYPos < perception.getLevelHeight()-1)) {
    		predictedAgentYPos = actualAgentYPos + 1;
    	}
    	if (action.toString().equals("ACTION_UP") && (actualAgentYPos > 0)) {
    		predictedAgentYPos = actualAgentYPos - 1;
    	}
    	predictedState[actualAgentYPos][actualAgentXPos] = '.';
    	predictedState[predictedAgentYPos][predictedAgentXPos] = 'A';
    	return predictedState;
    }

}
