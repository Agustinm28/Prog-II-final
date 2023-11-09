import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderHistoryFormService } from './order-history-form.service';
import { OrderHistoryService } from '../service/order-history.service';
import { IOrderHistory } from '../order-history.model';

import { OrderHistoryUpdateComponent } from './order-history-update.component';

describe('OrderHistory Management Update Component', () => {
  let comp: OrderHistoryUpdateComponent;
  let fixture: ComponentFixture<OrderHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderHistoryFormService: OrderHistoryFormService;
  let orderHistoryService: OrderHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderHistoryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderHistoryFormService = TestBed.inject(OrderHistoryFormService);
    orderHistoryService = TestBed.inject(OrderHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const orderHistory: IOrderHistory = { id: 456 };

      activatedRoute.data = of({ orderHistory });
      comp.ngOnInit();

      expect(comp.orderHistory).toEqual(orderHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderHistory>>();
      const orderHistory = { id: 123 };
      jest.spyOn(orderHistoryFormService, 'getOrderHistory').mockReturnValue(orderHistory);
      jest.spyOn(orderHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderHistory }));
      saveSubject.complete();

      // THEN
      expect(orderHistoryFormService.getOrderHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(orderHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderHistory>>();
      const orderHistory = { id: 123 };
      jest.spyOn(orderHistoryFormService, 'getOrderHistory').mockReturnValue({ id: null });
      jest.spyOn(orderHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderHistory }));
      saveSubject.complete();

      // THEN
      expect(orderHistoryFormService.getOrderHistory).toHaveBeenCalled();
      expect(orderHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderHistory>>();
      const orderHistory = { id: 123 };
      jest.spyOn(orderHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
