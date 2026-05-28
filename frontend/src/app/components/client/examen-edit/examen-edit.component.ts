import { Component, OnInit } from '@angular/core';
import { Examen } from '../../../models/examen';
import { ActivatedRoute, Router } from '@angular/router';
import { ExamenService } from '../../../services/examen.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-examen-edit',
  imports: [CommonModule, FormsModule],
  templateUrl: './examen-edit.component.html',
  styleUrl: './examen-edit.component.css'
})
export class ExamenEditComponent  implements OnInit{
  examen!: Examen;

  constructor(
    private route: ActivatedRoute,
    private examenService: ExamenService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.examenService.getExamenById(id).subscribe(data => {
      this.examen = data;
    });
  }

  updateExamen() {
    this.examenService.updateExamen(this.examen.id!, this.examen)
      .subscribe(() => {
        alert('Examen modifié avec succès');
        this.router.navigate(['/listExamen']);
      });
  }

}
