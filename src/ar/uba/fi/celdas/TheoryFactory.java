package ar.uba.fi.celdas;

import java.util.Arrays;

import ontology.Types;

public class TheoryFactory {
	
	private char[][] presentState;
	private char[][] predictedState;
	Types.ACTIONS action;
	int utility;
	
	public Theory create(Perception perception, Types.ACTIONS action) {
		this.action = action;
		this.presentState = perception.getLevel();
		predictState(perception);
		evaluateUtility();
		Theory newTheory = new Theory(presentState, action, predictedState);
		newTheory.setUtility(utility);
		return newTheory;
	}
	
	private void predictState(Perception perception) {
    	this.predictedState = new char[perception.getLevelHeight()][perception.getLevelWidth()];
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
    }
	
	private void evaluateUtility() {
		utility = 0;
		if (!presentState.equals(predictedState)) {
			if (action.toString().equals("ACTION_RIGHT") || action.toString().equals("ACTION_UP")) {
				utility = 2;
			} else {
				utility = 1;
			}
		}
	}
}
