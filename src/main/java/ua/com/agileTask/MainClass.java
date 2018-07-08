package ua.com.agileTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MainClass {

	private static String originFilePath = "./files/sample-0-origin.html";
	private static String originIdValue = "make-everything-ok-button";
	private static String attributeKey = "id";
	private static String encoding = "UTF-8";
	private static String resultFilePath = "./files/result";
	private static String platform = "java -cp ";
	private static String jarFilePath = "src/main/java/ua.com.agileTask.MainClass.java ";
	private static int countMaches = 4;

	private static Document originDocument = crateOriginDocument();

	private static String tetsFile1 = "./files/sample-1-evil-gemini.html";
	private static String tetsFile2 = "./files/sample-2-container-and-clone.html";
	private static String tetsFile3 = "./files/sample-3-the-escape.html";
	private static String tetsFile4 = "./files/sample-4-the-mash.html";

	public static void main(String[] args) {

		String[] files = new String[] { tetsFile1, tetsFile2, tetsFile3, tetsFile4 };
		for (String file : files) {
			executApp(file);
		}

	}

	public static void executApp(String filePath) {
		for (Element elem : findEqualElements(filePath)) {
			String result = getOutput(elem);
			writeResultToFile(result);
			System.out.println(result);
		}
	}

	private static Document crateOriginDocument() {
		Document document = null;
		try {
			document = Jsoup.parse(new File(originFilePath), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	private static Element getOriginElement() {
		Element element = originDocument.getElementsByAttributeValue(attributeKey, originIdValue).first();
		return element;

	}

	private static Set<Element> getSimilarElementFromFile(String filePath) {
		Set<Element> elements = new HashSet<>();
		Document document = getDocumentFromFile(filePath);
		document.getElementsByTag(getOriginElement().tagName()).stream()
				.filter((element) -> element.attributes().asList().size() == getOriginElementAtribytes().size())
				.map((element) -> elements.add(element)).findFirst();

		return elements;
	}

	private static Document getDocumentFromFile(String filePath) {
		Document document = null;
		try {
			document = Jsoup.parse(new File(filePath), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	private static List<Attribute> getOriginElementAtribytes() {
		return getOriginElement().attributes().asList().stream().filter((attr) -> !attr.getKey().equals(attributeKey))
				.collect(Collectors.toList());
	}

	private static List<Element> findEqualElements(String filePath) {
		List<Element> similarElement = new ArrayList<>();
		for (Element element : getSimilarElementFromFile(filePath)) {
			int count = 0;
			for (Attribute attr : getOriginElementAtribytes()) {
				if (attr.getValue().equals(element.attr(attr.getKey()))) {
					count++;
				}
			}
			if (getOriginElement().html().equals(element.html())) {
				count++;
			}
			if (count >= countMaches) {
				similarElement.add(element);
			}
		}
		return similarElement;
	}

	private static String originElementDOMPath() {
		String originPath = "";
		for (Element element : reverse(getOriginElement().parents())) {
			originPath += element.nodeName() + "[" + element.elementSiblingIndex() + "]" + "> ";
		}
		return originPath;
	}

	private static String similsrElementDOMPath(Element element) {
		String elementPath = "";
		for (Element elem : reverse(element.parents())) {
			elementPath += elem.nodeName() + "[" + elem.elementSiblingIndex() + "]" + "> ";
		}
		return elementPath;
	}

	private static List<Element> reverse(List<Element> elements) {
		List<Element> reversed = elements;
		Collections.reverse(reversed);
		return reversed;
	}

	private static void writeResultToFile(String result) {
		LocalDateTime dateTime = LocalDateTime.now();
		String toFile = dateTime.toString() + "-> " + result;

		try (FileWriter writer = new FileWriter(new File(resultFilePath), true)) {
			writer.write(toFile);
			writer.write(System.getProperty("line.separator"));
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getOutput(Element element) {
		String outPut = platform + jarFilePath + " " + originElementDOMPath() + " " + similsrElementDOMPath(element);
		return outPut;
	}

}
