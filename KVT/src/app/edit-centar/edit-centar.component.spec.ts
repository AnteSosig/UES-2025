import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCentarComponent } from './edit-centar.component';

describe('EditCentarComponent', () => {
  let component: EditCentarComponent;
  let fixture: ComponentFixture<EditCentarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCentarComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditCentarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
