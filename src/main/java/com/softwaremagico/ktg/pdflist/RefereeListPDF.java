/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import java.util.List;

/**
 *
 * @author jorge
 */
public class RefereeListPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;

    public RefereeListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        Paragraph p;

        mainTable.addCell(getHeader2(championship.name, 0));
        List<CompetitorWithPhoto> listReferee = KendoTournamentGenerator.getInstance().database.searchRefereeByTournament(championship.name, false, false);
        for (int i = 0; i < listReferee.size(); i++) {
            mainTable.addCell(getCell(listReferee.get(i).getSurnameName() + " (" + listReferee.get(i).club + ")", 0));
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {1};
        return widths;
    }

    @Override
    public void setTablePropierties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        p = new Paragraph(trans.returnTag("RefereeTitle", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize + 26, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(getTableWidths().length);
        //cell.setMinimumHeight(50);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        mainTable.addCell(getEmptyRow());
    }

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    protected String fileCreatedOkTag() {
        return "refereeListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "refereeListBad";
    }
}
