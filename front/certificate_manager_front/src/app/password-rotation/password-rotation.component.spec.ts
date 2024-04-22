import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordRotationComponent } from './password-rotation.component';

describe('PasswordRotationComponent', () => {
  let component: PasswordRotationComponent;
  let fixture: ComponentFixture<PasswordRotationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PasswordRotationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PasswordRotationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
