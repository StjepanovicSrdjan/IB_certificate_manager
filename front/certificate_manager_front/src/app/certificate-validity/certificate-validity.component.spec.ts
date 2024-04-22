import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateValidityComponent } from './certificate-validity.component';

describe('CertificateValidityComponent', () => {
  let component: CertificateValidityComponent;
  let fixture: ComponentFixture<CertificateValidityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CertificateValidityComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CertificateValidityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
