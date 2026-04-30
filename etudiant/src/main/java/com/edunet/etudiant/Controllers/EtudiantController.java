package com.edunet.etudiant.Controllers;

import com.edunet.etudiant.Entities.Etudiant;
import com.edunet.etudiant.Services.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    // Endpoint de test
    @RequestMapping("/hello")
    public String sayHello() {
        return "Hello, i'm the Etudiant MS";
    }

    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        return new ResponseEntity<>(etudiantService.getAllEtudiants(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Etudiant> addEtudiant(@RequestBody Etudiant etudiant) {
        return new ResponseEntity<>(etudiantService.addEtudiant(etudiant), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        Etudiant e = etudiantService.getEtudiantById(id);
        if (e != null)
            return new ResponseEntity<>(e, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        Etudiant updated = etudiantService.updateEtudiant(id, etudiant);
        if (updated != null)
            return new ResponseEntity<>(updated, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEtudiant(@PathVariable Long id) {
        String result = etudiantService.deleteEtudiant(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}