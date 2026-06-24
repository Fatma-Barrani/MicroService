package tn.comping.spring.examen.Controllers;

import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.comping.spring.examen.Entites.Examen;
import tn.comping.spring.examen.Entites.Participation;
import tn.comping.spring.examen.Repositories.ExamenRepository;
import tn.comping.spring.examen.Repositories.ParticipationRepository;
import tn.comping.spring.examen.Services.ExamenService;
import tn.comping.spring.examen.Services.ExportService;
import tn.comping.spring.examen.dto.ExamenRequestDTO;
import tn.comping.spring.examen.dto.ExamenResponseDTO;
import tn.comping.spring.examen.dto.ParticipationDTO;

import java.util.List;

@RestController
@RequestMapping("/api/examens")
@RequiredArgsConstructor
public class ExamenController {

    private final ExamenService service;
    private final ExamenRepository examenRepository;
    private final ExportService exportService;
    private final ParticipationRepository participationRepository;

    @PostMapping("/createExamen")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ExamenResponseDTO create(@RequestBody ExamenRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/ExamenById/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT')")
    public ExamenResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/GetAllExamens")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT')")
    public List<ExamenResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/UpdateExamenBYId/{id}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ExamenResponseDTO update(@PathVariable Long id,
                                    @RequestBody ExamenRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/DeleteExamen/{id}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{idExamen}/affecter/{idEns}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Examen affecter(@PathVariable Long idExamen,
                           @PathVariable Long idEns) {
        return service.affecterEnseignant(idExamen, idEns);
    }

    @PostMapping("/affecter-async")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<String> affecterAsync(@RequestParam Long examId,
                                                @RequestParam Long teacherId) {
        service.affecterEnseignantAsync(examId, teacherId);
        return ResponseEntity.accepted().body("Affectation en cours (asynchrone)");
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT')")
    public List<ExamenResponseDTO> filter(
            @RequestParam(required = false) String matiere,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String statut) {
        return service.filterExamen(matiere, niveau, statut);
    }

    @GetMapping("/sort")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT')")
    public List<ExamenResponseDTO> sort(@RequestParam String sortBy,
                                        @RequestParam String direction) {
        List<Examen> examens = examenRepository.findAll();
        return service.sortExamens(examens, sortBy, direction);
    }

    @PostMapping("/participer")
    @PreAuthorize("hasRole('ETUDIANT')")
    public Participation participer(@RequestParam Long etudiantId,
                                    @RequestParam Long examenId) {
        return service.ajouterParticipation(etudiantId, examenId);
    }

    @PutMapping("/noter")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Participation noter(@RequestParam Long etudiantId,
                               @RequestParam Long examenId,
                               @RequestParam Double note,
                               @RequestParam String commentaire) {
        return service.noterEtudiant(etudiantId, examenId, note, commentaire);
    }

    @GetMapping("/export/pdf/{examenId}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long examenId) throws DocumentException {
        byte[] pdf = exportService.exportNotesParExamen(examenId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "notes_examen_" + examenId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/enseignant/{id}")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public List<ExamenResponseDTO> getByEnseignant(@PathVariable Long id) {
        return service.getExamensByEnseignant(id);
    }

    @GetMapping("/count/en-cours")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Long countExamensEnCours() {
        return service.countByStatut("EN_COURS");
    }

    @GetMapping("/count/en-attente")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Long countExamensEnAttente() {
        return service.countByStatut("EN_ATTENTE");
    }

    @GetMapping("/count/terminee")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Long countExamensCorriges() {
        return service.countByStatut("TERMINE");
    }

    @GetMapping("/count/total")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public Long countTotalExamens() {
        return service.countTotal();
    }

    @GetMapping("/participations/etudiant/{etudiantId}")
public ResponseEntity<List<ParticipationDTO>> getParticipationsByEtudiant(
        @PathVariable Long etudiantId) {
    List<Participation> participations = participationRepository.findByEtudiantId(etudiantId);
    List<ParticipationDTO> dtos = participations.stream()
        .map(p -> new ParticipationDTO(
            p.getId(),
            p.getExamenId(),
            p.getNote(),
            (p.getNote() != null) ? "NOTÉ" : "NON_NOTÉ"
        ))
        .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(dtos);
}
}