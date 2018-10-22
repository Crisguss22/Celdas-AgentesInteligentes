package ar.uba.fi.celdas;

import core.game.StateObservation;

public class TheoryUpdater {
	
	public void updateTheoryEndGame(StateObservation stateObs, Theory lastTheory) {
		// TODO Auto-generated method stub
		
	}

	public void updateTheoryMidGame(StateObservation stateObs, Theory lastTheory) {
		// TODO Auto-generated method stub
		
	}
	
	private void theoryPredictedWell(Theory theory, Perception perception) {
    	if (theory.comparePrediction(perception.getLevel())) {
    		theory.addSuccess();
    	}
    }

}
