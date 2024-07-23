package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.reasoned;

import java.util.ArrayList;
import java.util.List;

import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;

public class AlcoveBestMotheSingleFatherNoSuperPosition extends ReasonedAlcove 
{

	public AlcoveBestMotheSingleFatherNoSuperPosition() 
	{
		;
	}

	@Override
	public Individual figlio(Individual padre, Individual madre) 
	{
		BreedingParameters param=new BreedingParameters(padre);
		
		List<SecurityCamera> telecamereMadre=madre.getBestItemsOnRiskTiles(param.quantiElementiDaCuiPartire);
		
		telecamereMadre = aCaso(telecamereMadre,param.quantiElementiDallaMadre,new ArrayList<SecurityCamera>());
		
		List<SecurityCamera> telecamerePadreCandidate=leastConflictual(madre,telecamereMadre,padre.getCameras());
		
		List<SecurityCamera> telecamerePadre =  numeroGiusto(telecamerePadreCandidate,param.quantiElementiDalPadre, padre.getCameras(),telecamereMadre);
		

		Individual result=incubatrice(telecamerePadre,telecamereMadre,param.quantiElementiDalPadre,param.quantiElementiDallaMadre);
		return result;
	}

}
