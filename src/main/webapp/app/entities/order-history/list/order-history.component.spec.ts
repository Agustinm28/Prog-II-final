import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OrderHistoryService } from '../service/order-history.service';

import { OrderHistoryComponent } from './order-history.component';

describe('OrderHistory Management Component', () => {
  let comp: OrderHistoryComponent;
  let fixture: ComponentFixture<OrderHistoryComponent>;
  let service: OrderHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'order-history', component: OrderHistoryComponent }]), HttpClientTestingModule],
      declarations: [OrderHistoryComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(OrderHistoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderHistoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OrderHistoryService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.orderHistories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to orderHistoryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getOrderHistoryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getOrderHistoryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
