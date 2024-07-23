package it.tomazzoli.ai.bim.geneticalgorithm.process.breeding;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AlcoveFactory 
{
	private final String basePackage = "it.tomazzoli.ai.bim.geneticalgorithm.process.breeding.";
	private final String randomPackage = "random.";
	private final String noSuperPositionPackage = "reasoned.";
	
	private final String[] classiRandom = //{"AlcoveRandomBestFatherSingleMotherUnique"};
									{
											"AlcoveRandomBestFatherSingleMotherUnique",
											"AlcoveRandomBestFatherSingleMotherUnique",
											"AlcoveRandomBestFatherSingleMotherUnique",
											"AlcoveRandomBestFatherSingleMotherUnique"
									};
	
	private final String[] classiVoronoi = //{};
									{
											"AlcoveBestFatherSingleMotherNoSuperPosition",
											"AlcoveBestMotheSingleFatherNoSuperPosition",
											"AlcoveBestFatherUniqueMotherNoSuperPosition",
											"AlcoveBestMotherUniqueFatherNoSuperPosition"
									};
	
	private final List<String> possibiliRiproduzioni;
	
	public AlcoveFactory() 
	{
		possibiliRiproduzioni =new ArrayList<String>();
		for(String s:classiRandom)
		{
			String classe=basePackage+randomPackage+s;
			possibiliRiproduzioni.add(classe);
		}
		for(String s:classiVoronoi)
		{
			String classe=basePackage+noSuperPositionPackage+s;
			possibiliRiproduzioni.add(classe);
		}
	}
	
	public List<String> possibiliRiproduzioni()
	{
		return possibiliRiproduzioni;
	}
	
	public Alcove getInstance(String nomeClasse)
	{
		Alcove result=null;
		try
	    {
	         Class<?> concreteClass = Class.forName(nomeClasse);
			 Constructor<?> ct= concreteClass.getConstructor();
	         result = (Alcove) ct.newInstance();
	    }
	    catch(Exception e)
	    {
	    	//logger.error(e.getMessage());
	    	//logger.error(nomeClasse);
	    	e.printStackTrace();
	    }
	    return result;
	}

}
