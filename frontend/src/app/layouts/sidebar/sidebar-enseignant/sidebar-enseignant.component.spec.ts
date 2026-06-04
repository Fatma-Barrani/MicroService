import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidebarEnseignantComponent } from './sidebar-enseignant.component';

describe('SidebarEnseignantComponent', () => {
  let component: SidebarEnseignantComponent;
  let fixture: ComponentFixture<SidebarEnseignantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarEnseignantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidebarEnseignantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
