package it.tomazzoli.ai.bim.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
	
public class CameraPositioningParameters 
{
		private static final String BUNDLE_NAME = "camerapositioning"; //$NON-NLS-1$

		private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		public static final String STR_CHAR_YES = "Y";
		public static final String STR_CHAR_NO = "N";
		public static final String STR_VUOTO = "";
		public static final String STR_PIPE = "|";
		public static final String STR_UNDERSCORE = "_";
		public static final String STR_SPAZIO = " ";
		public static final String ESTENSIONE_PDF=".pdf";
		public static final String ESTENSIONE_TXT=".txt";
		public static final String ESTENSIONE_WAV=".wav";
		public static final String HTML_openTag="<html>";
		public static final String HTML_closeTag="</html>";
		public static final String ICC_PROFILE_TYPE="Custom";
		public static final String ICC_PROFILE_URL="http://www.color.org";
		public static final String ICC_PROFILE_STANDARD="sRGB IEC61966-2.1";
		public static final String _closeBracket="]";
		public static final String _openBracket="[";
		public static final String STR_COMMA=",";
		public static final String STR_STOPMARK=".";
		public static final String STR_UGUALE="=";
		public static final String STR_QUESTION_MARK="?";
		public static final String STR_NEWLINE="\n";
		public static final int FULLCIRCLE = 360;// il cerchio in gradi
		public static final int OGNIDIECI = 10;// ogni dieci gradi
		
		private CameraPositioningParameters() {
		}

		public static String getString(String key) 
		{
			try 
			{
				return RESOURCE_BUNDLE.getString(key);
			} 
			catch (MissingResourceException e) 
			{
				return '!' + key + '!';
			}
		}
		
		public static int getInt(String key) 
		{
			try 
			{
				String valueString = RESOURCE_BUNDLE.getString(key);
				int result = Integer.parseInt(valueString);
				return result;
			} 
			catch (MissingResourceException e) 
			{
				return Integer.MIN_VALUE;
			}
			catch (NumberFormatException e) 
			{
				return -1;
			}
		}
		
		public static double getDouble(String key) 
		{
			try 
			{
				String valueString = RESOURCE_BUNDLE.getString(key);
				double result = Double.parseDouble(valueString);
				return result;
			} 
			catch (MissingResourceException e) 
			{
				return Integer.MIN_VALUE;
			}
			catch (NumberFormatException e) 
			{
				return -1;
			}
		}

}
