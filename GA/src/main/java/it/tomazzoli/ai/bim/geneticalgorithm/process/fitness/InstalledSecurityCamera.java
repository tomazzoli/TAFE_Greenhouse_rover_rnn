package it.tomazzoli.ai.bim.geneticalgorithm.process.fitness;

import java.util.ArrayList;
import java.util.List;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.SecurityCamera;

public class InstalledSecurityCamera extends SecurityCamera 
{

	double coperturaRischi;
	double coperturaValori;
	double singleFitnessUniqueTiles;
	double InstallationCost;
	List<CoveredTile> piastrelleDiRischioCoperte;
	List<CoveredTile> piastrelleDiRischioCoperteSoloDaQuesta;
	List<CoveredTile> piastrelleDiValoreCoperte;
	

	public InstalledSecurityCamera(SecurityCamera another)
	{
		super(another);
		coperturaRischi = 0;
		coperturaValori = 0;
		singleFitnessUniqueTiles=0;
		piastrelleDiRischioCoperte = new ArrayList<CoveredTile>();
		piastrelleDiRischioCoperteSoloDaQuesta = new ArrayList<CoveredTile>();
		piastrelleDiValoreCoperte = new ArrayList<CoveredTile>();
	}
	
	public boolean equals(Object another)
	{
		boolean result=super.equals(another);
		return result;
	}	
	
	private void aggiornaCoperturaRischi()
	{
		coperturaRischi = 0;
		for(CoveredTile a:piastrelleDiRischioCoperte)
		{
			double tileRisk=a.getValue()*super.getQualityFactor();
			coperturaRischi = coperturaRischi + tileRisk;
		}
	}
	
	private void aggiornaCoperturaValori()
	{
		coperturaValori = 0;
		for(CoveredTile a:piastrelleDiValoreCoperte)
		{
			double tileRisk=a.getValue()*super.getQualityFactor();
			coperturaValori = coperturaValori + tileRisk;
		}
	}
	
	
	public void setSingleFitnessUniqueTiles(double value)
	{
		singleFitnessUniqueTiles = value;
	}
	
	public double getSingleFitnessUniqueTiles()
	{
		return singleFitnessUniqueTiles;
	}
	
	public double getCoperturaValori()
	{
		return coperturaRischi;
	}
	
	public double getCoperturaRischi()
	{
		return coperturaRischi;
	}
	
	public List<CoveredTile> getUniqueCoveredRiskTTiles()
	{
		return piastrelleDiRischioCoperteSoloDaQuesta;
	}
	
	public boolean addUniqueRiskTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiRischioCoperteSoloDaQuesta.addAll(piastrelle);
		return result; 
	}
	
	public boolean removeUniqueRiskTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiRischioCoperteSoloDaQuesta.removeAll(piastrelle);
		return result;  
	}
	
	public List<CoveredTile> getCoveredRiskTiles()
	{
		return piastrelleDiRischioCoperte;
	}
	
	public boolean addRiskTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiRischioCoperte.addAll(piastrelle);
		aggiornaCoperturaRischi();
		return result; 
	}
	
	public boolean addRiskTile(CoveredTile piastrella)
	{
		boolean result = piastrelleDiRischioCoperte.add(piastrella);
		aggiornaCoperturaRischi();
		return result;  
	}
	
	public boolean removeRiskTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiRischioCoperte.removeAll(piastrelle);
		aggiornaCoperturaRischi();
		return result;  
	}
	
	public boolean removeRiskTile(CoveredTile piastrella)
	{
		boolean result = piastrelleDiRischioCoperte.remove(piastrella);
		aggiornaCoperturaRischi();
		return result; 
	}
	
	public List<CoveredTile> getCoveredValueTiles()
	{
		return piastrelleDiValoreCoperte;
	}
	
	public boolean addValueTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiValoreCoperte.addAll(piastrelle);
		aggiornaCoperturaValori();
		return result;  
	}
	
	public boolean addValueTile(CoveredTile piastrella)
	{
		boolean result =piastrelleDiValoreCoperte.add(piastrella);
		aggiornaCoperturaValori();
		return result; 
	}
	
	public boolean removeValueTiles(List<CoveredTile> piastrelle)
	{
		boolean result = piastrelleDiValoreCoperte.removeAll(piastrelle);
		aggiornaCoperturaValori();
		return result; 
	}
	
	public boolean removeValueTile(CoveredTile piastrella)
	{
		boolean result = piastrelleDiValoreCoperte.remove(piastrella);
		aggiornaCoperturaValori();
		return result; 
	}

}
