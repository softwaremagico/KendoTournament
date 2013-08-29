package com.softwaremagico.ktg.core;

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

/**
 * Defines a club. A club is an organization where competitors came from. This
 * discrimination is not important now, but in future can be used to generate
 * statistics
 * 
 */
public class Club implements Comparable<Club> {

	private String name = "";
	private String country = "";
	private String city = "";
	private String address = "";
	private String representativeID = "";
	private String email = "";
	private String phone = null;
	private String web = "";

	public Club(String name, String country, String city) {
		setName(name);
		setCountry(country);
		setCity(city);
	}

	/**
	 * Representative is a registered person of the club that will be use to
	 * establish contact in future tournaments.
	 * 
	 * @param representative
	 *            Identification number of the registered person.
	 * @param email
	 * @param phone
	 */
	public void setRepresentative(String representative, String email, String phone) {
		representativeID = representative;
		this.email = email;
		this.phone = phone;
	}

	private void setName(String value) {
		name = "";
		String[] data = value.split(" ");
		for (int i = 0; i < data.length; i++) {
			// There is no capital letters.
			if ((data[i].length() > 2) && (data[i].substring(1).equals(data[i].substring(1).toLowerCase()))) {
				name += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
			} else {
				name += data[i] + " ";
			}
		}
		name = name.trim();
	}

	public String getName() {
		return name;
	}

	/**
	 * City of the club. Only is an information value.
	 * 
	 * @param value
	 */
	private void setCountry(String value) {
		country = "";
		String[] data = value.split(" ");
		for (int i = 0; i < data.length; i++) {
			if (data[i].length() > 2) {
				country += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
			} else {
				country += data[i] + " ";
			}
		}
		country = country.trim();
	}

	/**
	 * City of the club. Only is an information value.
	 * 
	 * @param value
	 */
	private void setCity(String value) {
		city = "";
		String[] data = value.split(" ");
		for (int i = 0; i < data.length; i++) {
			if (data[i].length() > 2) {
				city += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
			} else {
				city += data[i] + " ";
			}
		}
		city = city.trim();
	}

	/**
	 * Address of the club. Only is an information value.
	 * 
	 * @param value
	 */
	public void setAddress(String value) {
		address = "";
		String[] data = value.split(" ");
		for (int i = 0; i < data.length; i++) {
			if (data[i].length() > 2) {
				address += data[i].substring(0, 1).toUpperCase() + data[i].substring(1).toLowerCase() + " ";
			} else {
				address += data[i] + " ";
			}
		}
		address = address.trim();
	}

	/**
	 * Sets the email of the representative. For future contacts reference.
	 * 
	 * @param email
	 */
	public void setMail(String email) {
		this.email = email;
	}

	/**
	 * Sets the phone of the representative. For future contacts reference.
	 * 
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setRepresentative(String representative) {
		this.representativeID = representative;
	}

	public void storeWeb(String value) {
		web = value.trim();
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getWeb() {
		return web;
	}

	public String getMail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getRepresentativeID() {
		return representativeID;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Club)) {
			return false;
		}
		Club otherClub = (Club) object;
		return this.name.equals(otherClub.name) && this.city.equals(otherClub.city);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 67 * hash + (this.city != null ? this.city.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * A club is compared only using the name and the city.
	 */
	@Override
	public int compareTo(Club c) {
		return (getName() + city).compareTo(c.getName() + c.getCity());
	}
}
