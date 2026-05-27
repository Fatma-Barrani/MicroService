import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnseignantService } from '../../services/enseignant.service';

@Component({
  selector: 'app-enseignant-add',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './enseignant-add.component.html',
  styleUrl: './enseignant-add.component.css'
})
export class EnseignantAddComponent {

  @Output() close = new EventEmitter<void>();
  @Output() refresh = new EventEmitter<void>();

  form = {
    nom: '',
    prenom: '',
    email: '',
    telephone: '',
    specialite: ''
  };

  constructor(private service: EnseignantService) {}

  save() {
    this.service.create(this.form as any).subscribe(() => {
      this.refresh.emit();
      this.close.emit();
    });
  }

  closePopup() {
    this.close.emit();
  }
}