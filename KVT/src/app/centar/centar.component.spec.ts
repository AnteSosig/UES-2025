import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CentarComponent } from './centar.component';

describe('CentarComponent', () => {
  let component: CentarComponent;
  let fixture: ComponentFixture<CentarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CentarComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CentarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
