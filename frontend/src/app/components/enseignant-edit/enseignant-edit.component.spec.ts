import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnseignantEditComponent } from './enseignant-edit.component';

describe('EnseignantEditComponent', () => {
  let component: EnseignantEditComponent;
  let fixture: ComponentFixture<EnseignantEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnseignantEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnseignantEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
