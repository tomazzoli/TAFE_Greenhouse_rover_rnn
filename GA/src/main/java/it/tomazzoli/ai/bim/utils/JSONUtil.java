package it.tomazzoli.ai.bim.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

import it.tomazzoli.ai.bim.geneticalgorithm.BuiltEnvironment;
import jdk.nashorn.internal.parser.JSONParser;

public class JSONUtil 
{

	public JSONUtil() 
	{
		// poi vedo
	}
	
	public void scriviAmbiente(BuiltEnvironment ambiente, File fileToWrite) throws IOException
	{
		FileWriter fw = new FileWriter(fileToWrite);
		if(fw != null)
		{
			JSONObject obj=ambiente.toJSonObject();
			obj.write(fw);
			fw.flush();
			fw.close();
		}		
	}
	
	public BuiltEnvironment leggiAmbiente(File fileJSON) throws FileNotFoundException, JSONException
	{
		BuiltEnvironment result = null;
		if(fileJSON != null)
		{
			String myJson = new Scanner(fileJSON).useDelimiter("\\Z").next();
			JSONObject myJsonobject = new JSONObject(myJson);
			result = BuiltEnvironment.fromJSonObject(myJsonobject);
		}
		return result;
	}
}
