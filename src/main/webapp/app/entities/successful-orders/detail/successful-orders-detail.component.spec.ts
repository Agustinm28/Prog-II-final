import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SuccessfulOrdersDetailComponent } from './successful-orders-detail.component';

describe('SuccessfulOrders Management Detail Component', () => {
  let comp: SuccessfulOrdersDetailComponent;
  let fixture: ComponentFixture<SuccessfulOrdersDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SuccessfulOrdersDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ successfulOrders: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SuccessfulOrdersDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SuccessfulOrdersDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load successfulOrders on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.successfulOrders).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
