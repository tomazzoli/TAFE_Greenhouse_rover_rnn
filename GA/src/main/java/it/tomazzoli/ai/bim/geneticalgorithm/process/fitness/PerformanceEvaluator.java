package it.tomazzoli.ai.bim.geneticalgorithm.process.fitness;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;

import it.tomazzoli.ai.bim.beans.CoveredTile;
import it.tomazzoli.ai.bim.beans.RiskArea;
import it.tomazzoli.ai.bim.beans.SecurityCamera;
import it.tomazzoli.ai.bim.beans.ValuableArea;
import it.tomazzoli.ai.bim.beans.Wall;
import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import it.tomazzoli.ai.bim.geneticalgorithm.Individual;
import it.tomazzoli.ai.bim.utils.CameraPositioningParameters;
import it.tomazzoli.ai.bim.utils.GeometryUtil;

public class PerformanceEvaluator 
{
	protected BuiltEnvironment _ambiente;
	protected final GeometryUtil util;
	private final String s_distanzaMaxMuro = CameraPositioningParameters.getString("CameraPositioning.distanzaMaxElementoPortante");
	private final String s_moltiplicatoreCostoInstallazioneMuro = CameraPositioningParameters.getString("CameraPositioning.moltiplicatoreCostoInstallazioneVicinanza");
	private final String s_moltiplicatoreCostoInstallazionePalo = CameraPositioningParameters.getString("CameraPositioning.moltiplicatoreCostoInstallazionePalo");
	protected int DISTANZAMAXELEMENTOPORTANTE = 10;
	protected int MOLTIPLICATORECOSTOINSTALLAZIONEVICINANZA = 2;
	protected int MOLTIPLICATORECOSTOINSTALLAZIONEPALO = 10;
	
	
	public PerformanceEvaluator(BuiltEnvironment ambiente)
	{
		_ambiente = ambiente;
		util=new GeometryUtil();
		try 
		{
			DISTANZAMAXELEMENTOPORTANTE = Integer.parseInt(s_distanzaMaxMuro);
		}
		catch(NumberFormatException ne)
		{
			; //non faccio nulla, mi tengo il default
		}
		try 
		{
			MOLTIPLICATORECOSTOINSTALLAZIONEVICINANZA = Integer.parseInt(s_moltiplicatoreCostoInstallazioneMuro);
		}
		catch(NumberFormatException ne)
		{
			; //non faccio nulla, mi tengo il default
		}
		try 
		{
			MOLTIPLICATORECOSTOINSTALLAZIONEPALO = Integer.parseInt(s_moltiplicatoreCostoInstallazionePalo);
		}
		catch(NumberFormatException ne)
		{
			; //non faccio nulla, mi tengo il default
		}
	}
		
	/***
	 * Determina se la piastrella è intersecata dalla copertura della telecamere e non si sovrappone alcun ostacolo tra la telecamera e la piastrella 
	 * @param util delegation pattern per verificare la intersezione di  copertura della telecamera e piastrella
	 * @param tile la piastrella (parte di area di risìchio
	 * @param cam la telecamera
	 * @return true se la piastrella è intersecata dalla copertura della telecamere e non è coperta da un ostacolo
	 */
	protected boolean visibleFrom(CoveredTile tile,SecurityCamera cam)
	{
		boolean covered=util.intersects(tile.getTile(), cam.getCoverage());
		boolean visible=true;
		if(covered)
		{
			for(Wall o:_ambiente.getObstacles())
			{
				Coordinate a=tile.getTile().centre();
				Coordinate b=cam.getRegistrationPoint();
				Coordinate[] coordinates= {a,b};
				LineString linea=new GeometryFactory().createLineString(coordinates);
				if(o.getShape().intersects(linea))
				{
					visible=false;
					break;
				}
			}
		}
		boolean result=covered&&visible;
		return result;
	}
	
	/***
	 * creo una lista di tutte le piastrelle da coprire con il relativo rischio dovuto all'area di appartenenza
	 * @return la lista di CoveredTile
	 */
	protected List<CoveredTile> allRiskTiles()
	{
		List<CoveredTile> tiles=new ArrayList<CoveredTile>();
		for(RiskArea area:_ambiente.getRiskAreas())
		{
			for(Envelope r:area.getTiles())
			{
				CoveredTile a= new CoveredTile(r,area.getRiskFactor());
				tiles.add(a);
			}
		}
		return tiles;
	}
	
	/***
	 * creo una lista di tutte le piastrelle da coprire con il relativo valore dovuto all'oggetto di valore di appartenenza
	 * @return la lista di CoveredTile
	 */
	protected List<CoveredTile> allValueTiles()
	{
		List<CoveredTile> tiles=new ArrayList<CoveredTile>();
		for(ValuableArea area:_ambiente.getValuables())
		{
			for(Envelope r:area.getTiles())
			{
				CoveredTile a=new CoveredTile(r,area.getValueFactor());
				tiles.add(a);
			}
		}
		return tiles;
	}
	
	public List<Individual> valutaSingoliItem(List<Individual> parents)
	{	
		SingleItemEvaluator valutatore = new SingleItemEvaluator(_ambiente);
		UniqueCoverageEvaluator valutatoreDiCopertureUniche = new UniqueCoverageEvaluator(_ambiente);
		List<Individual> result=new ArrayList<Individual>();
		for(Individual individuo:parents)
		{
			Individual parzialmenteValutato = valutatore.elaboraCoperturaTelecamere(individuo);
			Individual valutato = valutatoreDiCopertureUniche.elaboraCoperturaTelecamere(parzialmenteValutato);
			result.add(valutato);
		}
		return result;
		
	}
}
