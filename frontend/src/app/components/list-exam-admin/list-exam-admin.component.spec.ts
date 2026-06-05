import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListExamAdminComponent } from './list-exam-admin.component';

describe('ListExamAdminComponent', () => {
  let component: ListExamAdminComponent;
  let fixture: ComponentFixture<ListExamAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListExamAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListExamAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
