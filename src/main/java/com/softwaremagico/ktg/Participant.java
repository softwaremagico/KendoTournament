/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg;

/**
 *
 * @author jorge
 */
public class Participant {

    protected String id;
    protected String name;
    protected String surname;

    public Participant(String tmp_id, String name, String surname) {
        storeName(name);
        storeSurname(surname);
        storeId(tmp_id);
    }

    protected final void storeId(String value) {
        id = value.replaceAll("-", "").replaceAll(" ", "").trim().toUpperCase();
    }

    public String getId() {
        return id;
    }

    protected final void storeName(String value) {
        name = "";
        String[] names = value.split(" ");
        for (int i = 0; i < names.length; i++) {
            if (names[i].length() > 0) {
                name += names[i].substring(0, 1).toUpperCase();
                if (names[i].length() > 1) {
                    name += names[i].substring(1).toLowerCase();
                }
                if (i < names.length - 1) {
                    name += " ";
                }
            }
        }
        name = name.trim();
    }

    public String getShortName(int length) {
        return name.substring(0, Math.min(length, name.length()));
    }

    public String getShortName() {
        return getShortName(8);
    }

    public String getShortSurname(int length) {
        String[] shortSurname = surname.split(" ");
        if (shortSurname[0].length() > 3) {
            return shortSurname[0].substring(0, Math.min(length, shortSurname[0].length()));
        } else {
            return surname.substring(0, Math.min(length - 1, surname.length()));
        }
    }

    public String getShortSurname() {
        return getShortSurname(8);
    }

    public String returnShortSurnameName(int maxLength) {
        if (name.length() + surname.length() == 0) {
            return "";
        }

        float rateSurname = (name.length() + getShortSurname(20).length()) / (float) surname.length();
        float rateName = (name.length() + getShortSurname(20).length()) / (float) name.length();
        String ret = getShortSurname((int) (maxLength / rateSurname)) + ", " + getShortName((int) (maxLength / rateName));
        return ret;
    }

    public String returnSurnameName() {
        if (surname.length() > 0 || name.length() > 0) {
            return surname + ", " + name;
        } else {
            return " --- --- ";

        }
    }

    public String returnSurnameNameIni() {
        if (surname.length() > 0 || name.length() > 0) {
            String surnameShort = surname.substring(0, Math.min(13, surname.length())).toUpperCase();
            if (surname.length() > 13) {
                surnameShort += ".";
            }
            return surnameShort + ", " + name.substring(0, 1) + ".";
        } else {
            return " --- --- ";
        }
    }

    protected final void storeSurname(String value) {
        surname = "";
        String[] surnames = value.split(" ");
        for (int i = 0; i < surnames.length; i++) {
            if (surnames[i].length() > 0) {
                surname += surnames[i].substring(0, 1).toUpperCase();
                if (surnames[i].length() > 1) {
                    surname += surnames[i].substring(1).toLowerCase();
                }
            }
            if (i < surnames.length - 1) {
                surname += " ";
            }
        }
        surname = surname.trim();
    }

    public String returnName() {
        return name;
    }

    public String returnSurname() {
        return surname;
    }

    public String getAcronim() {
        String acronim = "";
        acronim += name.substring(0, 1).toUpperCase();
        String[] shortSurname = surname.split(" ");
        if (shortSurname[0].length() < 4 && shortSurname.length > 1) {
            acronim += shortSurname[0].substring(0, 1).toLowerCase();
            acronim += shortSurname[1].substring(0, 1).toUpperCase();
        } else {
            acronim += shortSurname[0].substring(0, 1).toUpperCase();
        }
        return acronim;
    }

    public static String nifFromDni(String dni) {
        String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
        return String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(Integer.parseInt(dni) % 23);
    }
}
