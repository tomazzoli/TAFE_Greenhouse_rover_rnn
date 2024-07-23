package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.reasoned;

import java.util.ArrayList;
import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class AlcoveBestFatherSingleMotherNoSuperPosition extends ReasonedAlcove 
{

	public AlcoveBestFatherSingleMotherNoSuperPosition() 
	{
		;
	}

	@Override
	public Individual figlio(Individual padre, Individual madre) 
	{
		BreedingParameters param=new BreedingParameters(padre);
		
		List<SecurityCamera> telecamerePadre=padre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);
		telecamerePadre = aCaso(telecamerePadre,param.quantiElementiDalPadre,new ArrayList<SecurityCamera>());
		
		List<SecurityCamera> telecamereMadreCandidate=leastConflictual(padre,telecamerePadre,madre.getCameras());
		
		List<SecurityCamera> telecamereMadre =  numeroGiusto(telecamereMadreCandidate,param.quantiElementiDallaMadre, madre.getCameras(),telecamerePadre);
		

		Individual result=incubatrice(telecamerePadre,telecamereMadre,param.quantiElementiDalPadre,param.quantiElementiDallaMadre);
		return result;
	}

}
