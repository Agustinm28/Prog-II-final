import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderHistoryDetailComponent } from './order-history-detail.component';

describe('OrderHistory Management Detail Component', () => {
  let comp: OrderHistoryDetailComponent;
  let fixture: ComponentFixture<OrderHistoryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderHistoryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orderHistory: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrderHistoryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrderHistoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orderHistory on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orderHistory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
