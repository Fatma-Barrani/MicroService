import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Examen } from '../../models/examen';
import { ExamenService } from '../../services/examen.service';

@Component({
  selector: 'app-list-exam-admin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list-exam-admin.component.html',
  styleUrl: './list-exam-admin.component.css'
})
export class ListExamAdminComponent {

  examens: Examen[] = [];

 constructor(private examenService: ExamenService) {}
ngOnInit(): void {
  this.examenService.getExamens().subscribe(data => {
    this.examens = data;
  });
}}