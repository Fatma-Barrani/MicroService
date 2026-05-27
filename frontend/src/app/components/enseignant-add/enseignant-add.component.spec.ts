import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnseignantAddComponent } from './enseignant-add.component';

describe('EnseignantAddComponent', () => {
  let component: EnseignantAddComponent;
  let fixture: ComponentFixture<EnseignantAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnseignantAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnseignantAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
