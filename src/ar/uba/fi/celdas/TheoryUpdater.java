package ar.uba.fi.celdas;

import core.game.StateObservation;

public class TheoryUpdater {
	
	public void updateTheoryEndGame(StateObservation stateObs, Theory theory) {
		Perception perception = new Perception(stateObs);
		float utility;
		if (stateObs.getGameWinner().toString()=="PLAYER_WINS") {
			utility = 10000;
		} else {
			utility = -10000;
		}
		updateTheory(perception, theory, utility);
	}

	public void updateTheoryMidGame(StateObservation stateObs, Theory theory) {
		Perception perception = new Perception(stateObs);
		float utility;
		if (theory.compareCurrent(perception.getLevel())) {
			utility = 0;
		} else {
			utility = 1;
		}
		updateTheory(perception, theory, utility);
	}
	
	private void updateTheory(Perception perception, Theory theory, float utility) {
		theory.addUse();
		if (!theory.incomplete()) {
			theoryPredictedWell(theory, perception);
			return;
		}
		theory.setUtility(utility);
		theory.setPredictedState(perception.getLevel());
		theory.addSuccess();		
	}
	
	private void theoryPredictedWell(Theory theory, Perception perception) {
    	if (theory.comparePrediction(perception.getLevel())) {
    		theory.addSuccess();
    	}
    }

}
