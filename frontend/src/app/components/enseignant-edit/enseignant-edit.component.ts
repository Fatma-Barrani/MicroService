import { Component, EventEmitter, Input, Output, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnseignantService } from '../../services/enseignant.service';
import { Enseignant } from '../../models/enseignant.model';

@Component({
  selector: 'app-enseignant-edit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './enseignant-edit.component.html',
  styleUrl: './enseignant-edit.component.css'
})
export class EnseignantEditComponent implements OnChanges {

  @Input() enseignant!: Enseignant;
  @Output() close = new EventEmitter<void>();
  @Output() refresh = new EventEmitter<void>();

  form: Enseignant = {} as Enseignant;

  constructor(private service: EnseignantService) {}

  ngOnChanges() {
    this.form = { ...this.enseignant };
  }

  update() {
    if (!this.form.id) return;

    this.service.update(this.form.id, this.form).subscribe(() => {
      this.refresh.emit();
      this.close.emit();
    });
  }

  closePopup() {
    this.close.emit();
  }
}