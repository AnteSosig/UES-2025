import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoviCentarComponent } from './novi-centar.component';

describe('NoviCentarComponent', () => {
  let component: NoviCentarComponent;
  let fixture: ComponentFixture<NoviCentarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoviCentarComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(NoviCentarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
