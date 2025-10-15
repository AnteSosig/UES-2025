import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZahtevTableComponent } from './zahtev-table.component';

describe('ZahtevTableComponent', () => {
  let component: ZahtevTableComponent;
  let fixture: ComponentFixture<ZahtevTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZahtevTableComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ZahtevTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
