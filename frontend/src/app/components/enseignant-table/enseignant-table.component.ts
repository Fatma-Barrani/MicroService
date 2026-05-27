import { Component, OnInit } from '@angular/core';
import { EnseignantService } from '../../services/enseignant.service';
import { Enseignant } from '../../models/enseignant.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EnseignantAddComponent } from "../enseignant-add/enseignant-add.component";
import { EnseignantEditComponent } from "../enseignant-edit/enseignant-edit.component";
import { SidebarComponent } from "../../layouts/sidebar/sidebar.component";

@Component({
  selector: 'app-enseignant-table',
  standalone: true,
  imports: [FormsModule, CommonModule, EnseignantAddComponent, EnseignantEditComponent, SidebarComponent],
  templateUrl: './enseignant-table.component.html',
  styleUrls: ['./enseignant-table.component.css']
})
export class EnseignantTableComponent implements OnInit {

  enseignants: Enseignant[] = [];

  // 🔵 selected for edit popup
  selectedEnseignant: Enseignant | null = null;

  // 🔵 exam id for assignment
  examenId: number | null = null;

  // 🔵 popup controls
  showAddPopup = false;
  showEditPopup = false;

  constructor(private service: EnseignantService) { }

  ngOnInit(): void {
    this.load();
  }

  // =========================
  // GET ALL
  // =========================
  load(): void {
    this.service.getAll().subscribe({
      next: (data) => this.enseignants = data,
      error: (err) => console.error('Error loading enseignants', err)
    });
  }

  // =========================
  // OPEN POPUPS
  // =========================
  openAdd(): void {
    this.showAddPopup = true;
  }

  openEdit(e: Enseignant): void {
    this.selectedEnseignant = { ...e };
    this.showEditPopup = true;
  }

  closePopups(): void {
    this.showAddPopup = false;
    this.showEditPopup = false;
    this.selectedEnseignant = null;
  }

  // =========================
  // DELETE
  // =========================

  delete(id?: number): void {
    if (!id) return;

    this.service.delete(id).subscribe({
      next: () => {
        this.load();
        console.log("Deleted successfully");
      },
      error: (err) => {
        console.error("Delete error details:", err);

        if (err.status === 0) {
          alert("Backend or Gateway is not reachable ❌");
        } else {
          alert("Delete failed: " + err.message);
        }
      }
    });
  }

  // =========================
  // ASSIGN SYNC
  // =========================
  assignSync(enseignantId?: number): void {
    if (!enseignantId || !this.examenId) return;

    this.service.assignExamen(enseignantId, this.examenId).subscribe({
      next: () => alert("Examen assigné (SYNC)"),
      error: (err) => console.error(err)
    });
  }

  // =========================
  // ASSIGN ASYNC (RabbitMQ)
  // =========================
  assignAsync(enseignantId?: number): void {
    if (!enseignantId || !this.examenId) return;

    this.service.assignExamenAsync(enseignantId, this.examenId).subscribe({
      next: () => alert("Message RabbitMQ envoyé (ASYNC)"),
      error: (err) => console.error(err)
    });
  }
}