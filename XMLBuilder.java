import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



public class XMLBuilder {
	private Document dom;
	XMLBuilder()
	{

	}
	
	public void build()
	{
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		try {
			Scanner fileScanner = new Scanner(new File("C:\\Users\\krdou_000\\Documents\\Repast Workspace\\LTLtoXML\\src\\patterns.txt"));
			Pattern currentPattern = new Pattern();
			while(fileScanner.hasNext())
			{
				String currentLine = fileScanner.nextLine();
				currentLine = currentLine.trim();
				if(currentLine.equals(""))
				{
					patterns.add(currentPattern);
					currentPattern = new Pattern();
				}
				else
				{
					String[] splitLine = currentLine.split("[\\W]");
					switch(splitLine[0])
					{
					case "Globally":
						currentPattern.setGlobally(currentLine.substring(currentLine.indexOf("ltl")));
						break;
					case "Before":
						currentPattern.setBefore(currentLine.substring(currentLine.indexOf("ltl")));
						break;
					case "After":
						currentPattern.setAfter(currentLine.substring(currentLine.indexOf("ltl")));
						break;
					case "Between":
						currentPattern.setBetween(currentLine.substring(currentLine.indexOf("ltl")));
						break;
					case "AfterUntil":
						currentPattern.setAfterUntil(currentLine.substring(currentLine.indexOf("ltl")));
						break;
					default:
						currentPattern.setName(currentLine);
						break;
					}
				}

			}
			fileScanner.close();
			createDocument();
			createDOMTree(patterns);
			printToFile();
			
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File Not Found");
		}
	}
	
	private String cleanForXml(String in)
	{
		return in.replaceAll("&", "and").replaceAll("<", "lftArr").replaceAll(">", "rtArr");
	}
	
	public void createDocument() {

		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//create an instance of DOM
			dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}

	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(ArrayList<Pattern> patterns){

		Element rootEle = dom.createElement("Patterns");
		dom.appendChild(rootEle);

		Iterator<Pattern> it  = patterns.iterator();
		while(it.hasNext()) {
			Pattern p = (Pattern)it.next();
			Element patternElement = createPatternElement(p);
			rootEle.appendChild(patternElement);
		}

	}
	
	private Element createPatternElement(Pattern p){

		Element patternEle = dom.createElement(p.getName());

		Element type = dom.createElement("Globally");
		Text formula = dom.createTextNode(p.getGlobally());
		type.appendChild(formula);
		patternEle.appendChild(type);

		type = dom.createElement("Before");
		formula = dom.createTextNode(p.getBefore());
		type.appendChild(formula);
		patternEle.appendChild(type);
		
		type = dom.createElement("After");
		formula = dom.createTextNode(p.getAfter());
		type.appendChild(formula);
		patternEle.appendChild(type);
		
		type = dom.createElement("Between");
		formula = dom.createTextNode(p.getBetween());
		type.appendChild(formula);
		patternEle.appendChild(type);
		
		type = dom.createElement("AfterUntil");
		formula = dom.createTextNode(p.getAfterUntil());
		type.appendChild(formula);
		patternEle.appendChild(type);

		return patternEle;

	}
	
	private void printToFile(){

		try
		{
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD,"xml");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

			//to send the output to a file
			tr.transform( new DOMSource(dom),new StreamResult(new FileOutputStream("Patterns.xml")));

		} catch(IOException | TransformerFactoryConfigurationError | TransformerException ie) {
		    ie.printStackTrace();
		}
	}
}

