import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnseignantTableComponent } from './enseignant-table.component';

describe('EnseignantTableComponent', () => {
  let component: EnseignantTableComponent;
  let fixture: ComponentFixture<EnseignantTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnseignantTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnseignantTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
