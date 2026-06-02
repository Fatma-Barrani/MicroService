import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamenEditComponent } from './examen-edit.component';

describe('ExamenEditComponent', () => {
  let component: ExamenEditComponent;
  let fixture: ComponentFixture<ExamenEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamenEditComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExamenEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
