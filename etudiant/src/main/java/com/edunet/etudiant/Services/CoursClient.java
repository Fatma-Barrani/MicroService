package com.edunet.etudiant.Services;

import com.edunet.etudiant.Dtos.CoursDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "cours", url = "http://localhost:8082")  // adapte le port si nécessaire
public interface CoursClient {

    @GetMapping("/api/cours/all")
    List<CoursDTO> getAllCours();

    @GetMapping("/api/cours/search/categorie")
    List<CoursDTO> searchByCategorie(@RequestParam("categorie") String categorie);
}