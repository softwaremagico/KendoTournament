package com.softwaremagico.ktg.language;

public interface ITranslator {

	String getTranslatedText(String tag);

	String getTranslatedText(String tag, String language);

}
