package tn.comping.spring.examen.Services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.comping.spring.examen.Entites.Participation;
import tn.comping.spring.examen.Repositories.ParticipationRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final ParticipationRepository participationRepository;

    public byte[] exportNotesParExamen(Long examenId) throws DocumentException {
        List<Participation> participations = participationRepository.findByExamenId(examenId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Titre
        Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph titre = new Paragraph("Rapport des Notes - Examen N°" + examenId, titreFont);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Date
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        document.add(new Paragraph("Généré le : " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), dateFont));
        document.add(Chunk.NEWLINE);

        // Tableau
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 2f, 4f});

        // En-têtes
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        String[] headers = {"#", "Étudiant ID", "Note", "Commentaire"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(63, 81, 181));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Données
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
        int index = 1;
        for (Participation p : participations) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(index++), cellFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(p.getEtudiantId()), cellFont)));
            table.addCell(new PdfPCell(new Phrase(
                    p.getNote() != null ? p.getNote().toString() : "—", cellFont)));
            table.addCell(new PdfPCell(new Phrase(
                    p.getCommentaire() != null ? p.getCommentaire() : "—", cellFont)));
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }
}
