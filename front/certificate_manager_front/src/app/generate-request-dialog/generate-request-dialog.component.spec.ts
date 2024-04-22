import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateRequestDialogComponent } from './generate-request-dialog.component';

describe('GenerateRequestDialogComponent', () => {
  let component: GenerateRequestDialogComponent;
  let fixture: ComponentFixture<GenerateRequestDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenerateRequestDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateRequestDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
