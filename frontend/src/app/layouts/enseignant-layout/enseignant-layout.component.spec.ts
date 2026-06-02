import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnseignantLayoutComponent } from './enseignant-layout.component';

describe('EnseignantLayoutComponent', () => {
  let component: EnseignantLayoutComponent;
  let fixture: ComponentFixture<EnseignantLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnseignantLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnseignantLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
