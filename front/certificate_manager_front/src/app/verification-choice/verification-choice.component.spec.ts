import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificationChoiceComponent } from './verification-choice.component';

describe('VerificationChoiceComponent', () => {
  let component: VerificationChoiceComponent;
  let fixture: ComponentFixture<VerificationChoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VerificationChoiceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerificationChoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
