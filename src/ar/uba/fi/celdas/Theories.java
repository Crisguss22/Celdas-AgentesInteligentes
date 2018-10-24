package ar.uba.fi.celdas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Theories {
	
	Map<Integer,List<Theory>> theoriesByCurrent;
	Map<Integer,List<Theory>> theoriesByPredicted;
	List<Theory> winningTheories;
	Set<Integer> existenceSet;
	
	public Theories(){
		this.theoriesByCurrent = new HashMap<Integer, List<Theory>>();
		this.theoriesByPredicted = new HashMap<Integer, List<Theory>>();
		this.existenceSet = new HashSet<Integer>();
		this.winningTheories = new ArrayList<Theory>();
	}

	public void add(Theory theory) throws Exception{
		if(!existsTheory(theory)){			
			List<Theory> theoryListC = this.theoriesByCurrent.get(theory.hashCodeOnlyCurrentState());
			if(theoryListC == null){
				theoryListC = new ArrayList<Theory>();
				this.theoriesByCurrent.put(theory.hashCodeOnlyCurrentState(), theoryListC);
			}			
			List<Theory> theoryListP = this.theoriesByPredicted.get(theory.hashCodeOnlyPredictedState());
			if(theoryListP == null){
				theoryListP = new ArrayList<Theory>();
				this.theoriesByPredicted.put(theory.hashCodeOnlyPredictedState(), theoryListP);
			}
			theoryListP.add(theory);
			theoryListC.add(theory);
			
			if (theory.getUtility() > 100) { winningTheories.add(theory);}
			
			this.existenceSet.add(theory.hashCode());
		}else{
			throw new Exception("Theory already exist!");
		}
	}
	
	public boolean existsTheory(Theory theory){		
		return this.existenceSet.contains(theory.hashCode());
	}
	
	public List<Theory> getSortedListByCurrentState(Perception perception){
		
		Theory theory = new Theory(perception.getLevel());
		
		return getSortedListByCurrentState(theory);
	}
	
	public List<Theory> getSortedListByCurrentState(Theory theory){
		
		List<Theory> theoryList = this.theoriesByCurrent.get(theory.hashCodeOnlyCurrentState());
		if(theoryList == null){
			theoryList = new ArrayList<Theory>();
		}		
		Collections.sort(theoryList);
		return theoryList;
	}
	
	public List<Theory> getSortedListByPredictedState(Perception perception){
		
		Theory theory = new Theory(perception.getLevel());
		
		return getSortedListByPredictedState(theory);
	}
	
	public List<Theory> getSortedListByPredictedState(Theory theory){
		
		List<Theory> theoryList = this.theoriesByPredicted.get(theory.hashCodeOnlyPredictedState());
		if(theoryList == null){
			theoryList = new ArrayList<Theory>();
		}		
		Collections.sort(theoryList);
		return theoryList;
	}
	
	public List<Theory> getSortedListForPredictedtState(Theory theory){
		
		List<Theory> theoryList = this.theoriesByCurrent.get(theory.hashCodeOnlyPredictedState());
		if(theoryList == null){
			theoryList = new ArrayList<Theory>();
		}		
		Collections.sort(theoryList);
		return theoryList;
	}

	public Set<Integer> getExistenceSet() {
		return existenceSet;
	}

	public void setExistenceSet(Set<Integer> existenceSet) {
		this.existenceSet = existenceSet;
	}

	public Map<Integer, List<Theory>> getTheories() {
		return theoriesByCurrent;
	}

	public void setTheories(Map<Integer, List<Theory>> theories) {
		this.theoriesByCurrent = theories;
	}

	public boolean knownVictory() {
		return (winningTheories.size() > 0);
	}
	
	public List<Theory> getWinningTheories() {
		return winningTheories;
	}

	public List<Theory> getSortedListByPredictedState(int predictedState) {
		List<Theory> theoryList = this.theoriesByPredicted.get(predictedState);
		if(theoryList == null){
			theoryList = new ArrayList<Theory>();
		}		
		Collections.sort(theoryList);
		return theoryList;
	}

}
