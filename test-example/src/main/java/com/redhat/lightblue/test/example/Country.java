package com.redhat.lightblue.test.example;

public class Country {

	private String name, iso2Code, iso3Code, optionalField;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIso2Code() {
		return iso2Code;
	}

	public void setIso2Code(String iso2code) {
		this.iso2Code = iso2code;
	}

	public String getIso3Code() {
		return iso3Code;
	}

	public void setIso3Code(String iso3code) {
		this.iso3Code = iso3code;
	}

	public String getOptionalField() {
		return optionalField;
	}

	public void setOptionalField(String optionalField) {
		this.optionalField = optionalField;
	}

}
