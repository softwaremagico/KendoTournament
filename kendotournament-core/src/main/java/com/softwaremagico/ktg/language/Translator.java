package com.softwaremagico.ktg.language;

/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.core.Configuration;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.RoleTags;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.log.KendoLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Translator {
	private final static String DEFAULT_LANGUAGE = "en";
	private final static String LANGUAGES_FILE = "languages.xml";
	private Document doc = null;
	private boolean errorShowed = false;
	private boolean retried = false;
	private boolean showedMessage = false;
	private static List<Language> languagesList = null;
	private static HashMap<String, RoleTags> rolesList;

	public Translator(String filePath) {
		doc = parseFile(doc, filePath);
		rolesList = new HashMap<>();
	}

	/**
	 * Parse the file
	 *
	 * @param tagName
	 *            Tag of the data to be readed
	 */
	private static Document parseFile(Document usedDoc, String filePath) {
		DocumentBuilderFactory dbf;
		DocumentBuilder db;
		try {
			File file = new File(filePath);
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			usedDoc = db.parse(file);
			usedDoc.getDocumentElement().normalize();
		} catch (SAXParseException ex) {
			String text = "Parsing error" + ".\n Line: " + ex.getLineNumber() + "\nUri: " + ex.getSystemId()
					+ "\nMessage: " + ex.getMessage();
			KendoLog.severe(Translator.class.getName(), text);
			KendoLog.errorMessage(Translator.class.getName(), ex);
		} catch (SAXException ex) {
			KendoLog.errorMessage(Translator.class.getName(), ex);
		} catch (ParserConfigurationException ex) {
			KendoLog.errorMessage(Translator.class.getName(), ex);
		} catch (FileNotFoundException fnf) {
			String text = "The file " + filePath
					+ " containing the translations is not found. Please, check your program files and put the translation XML files on the \"translations\" folder.";
			System.out.println(text);
			// KendoTournamentGenerator.showErrorInformation(fnf);
		} catch (IOException ex) {
			Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
		}
		return usedDoc;
	}

	public String getTranslatedText(String tag) {
		return readTag(tag, KendoTournamentGenerator.getInstance().getLanguage());
	}

	private String readTag(String tag, String language) {
		try {
			NodeList nodeLst = doc.getElementsByTagName(tag);
			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node fstNode = nodeLst.item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element fstElmnt = (Element) fstNode;
					NodeList fstNmElmntLst = fstElmnt.getElementsByTagName(language);
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					try {
						NodeList fstNm = fstNmElmnt.getChildNodes();
						retried = false;
						return ((Node) fstNm.item(0)).getNodeValue().trim();
					} catch (NullPointerException npe) {
						// npe.printStackTrace();
						if (!retried) {
							if (!showedMessage) {
								KendoLog.warning(Translator.class.getName(),
										"There is a problem with tag: " + tag + " in  language: \"" + language
												+ "\". We tray to use english language instead.");
								showedMessage = true;
							}
							retried = true;
							return readTag(tag, DEFAULT_LANGUAGE);
						}
						if (!language.equals(DEFAULT_LANGUAGE)) {
							if (!errorShowed) {
								KendoLog.warning(this.getClass().getName(),
										"Selecting english language by default. You can change it later in Options->Language ");
								Configuration.storeLanguageConfiguration(language);
								errorShowed = true;
							}
							return readTag(tag, DEFAULT_LANGUAGE);
						} else {
							if (!errorShowed) {
								KendoLog.severe(this.getClass().getName(),
										"Language selection failed: " + language + " on " + tag + ".");
								errorShowed = true;
							}
							return null;
						}
					}

				}
			}
			KendoLog.severe(this.getClass().getName(), "No tag for: " + tag + ".");
			return null;
		} catch (NullPointerException npe) {
			return null;
		}
	}

	public synchronized static List<Language> getAvailableLanguages() {
		if (languagesList == null) {
			languagesList = new ArrayList<>();
			Document storedLanguages = null;
			storedLanguages = parseFile(storedLanguages, getTranslatorPath(LANGUAGES_FILE).getPath());
			NodeList nodeLst = storedLanguages.getElementsByTagName("languages");
			for (int s = 0; s < nodeLst.getLength(); s++) {
				Node fstNode = nodeLst.item(s);
				try {
					Language lang = new Language(fstNode.getTextContent(),
							fstNode.getAttributes().getNamedItem("abbrev").getNodeValue(),
							fstNode.getAttributes().getNamedItem("flag").getNodeValue());
					languagesList.add(lang);
				} catch (NullPointerException npe) {
					KendoLog.severe(Translator.class.getName(),
							"Error retrieving the available languages. Check your installation.");
				}
			}
		}
		return languagesList;
	}

	public static RoleTags getAvailableRoles(String language) {
		if (rolesList.get(language) == null) {
			int red, green, blue;
			rolesList.put(language, new RoleTags());
			Document storedRoles = null;
			storedRoles = parseFile(storedRoles, "roles.xml");
			NodeList nodeLst = storedRoles.getElementsByTagName("role");
			for (int i = 0; i < nodeLst.getLength(); i++) {
				org.w3c.dom.Node firstNode = nodeLst.item(i);
				if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) firstNode;
					String tag = element.getAttributes().getNamedItem("tag").getNodeValue();
					String abbrev = element.getAttributes().getNamedItem("abbrev").getNodeValue();
					try {
						red = Integer.parseInt(element.getAttributes().getNamedItem("red").getNodeValue());
					} catch (NullPointerException npe) {
						red = i * 127;
						if (red > 224) {
							red = red % 225;
						}
					}
					try {
						green = Integer.parseInt(element.getAttributes().getNamedItem("green").getNodeValue());
					} catch (NullPointerException npe) {
						green = i * 17;
						if (green > 224) {
							green = green % 225;
						}
					}
					try {
						blue = Integer.parseInt(element.getAttributes().getNamedItem("blue").getNodeValue());
					} catch (NullPointerException npe) {
						blue = 255 - i * 73;
						if (blue < 0) {
							blue = 255 + blue % 255;
						}
					}
					NodeList translatedRoles = element.getElementsByTagName(language);
					Element rol = (Element) translatedRoles.item(0);
					RoleTag role = new RoleTag(tag, rol.getTextContent().trim(), abbrev);
					role.addColor(red, green, blue);
					rolesList.get(language).add(role);
				}
			}
		}
		return rolesList.get(language);
	}

	public static File getTranslatorPath(String xmlFile) {
		File file = new File(Path.getTranslatorPath() + xmlFile);
		if (file.exists()) {
			// Get from folder
			return file;
		} else {
			try {
				if (Translator.class.getClassLoader()
						.getResource(Path.TRANSLATIONS_FOLDER + File.separator + xmlFile) != null) {
					file = new File(Translator.class.getClassLoader()
							.getResource(Path.TRANSLATIONS_FOLDER + File.separator + xmlFile).toURI());
					if (file.exists()) {
						return file;
					}
				}
			} catch (URISyntaxException e) {
			}
		}
		return null;
	}
}
