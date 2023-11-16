import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SuccessfulOrdersFormService } from './successful-orders-form.service';
import { SuccessfulOrdersService } from '../service/successful-orders.service';
import { ISuccessfulOrders } from '../successful-orders.model';

import { SuccessfulOrdersUpdateComponent } from './successful-orders-update.component';

describe('SuccessfulOrders Management Update Component', () => {
  let comp: SuccessfulOrdersUpdateComponent;
  let fixture: ComponentFixture<SuccessfulOrdersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let successfulOrdersFormService: SuccessfulOrdersFormService;
  let successfulOrdersService: SuccessfulOrdersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SuccessfulOrdersUpdateComponent],
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
      .overrideTemplate(SuccessfulOrdersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SuccessfulOrdersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    successfulOrdersFormService = TestBed.inject(SuccessfulOrdersFormService);
    successfulOrdersService = TestBed.inject(SuccessfulOrdersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const successfulOrders: ISuccessfulOrders = { id: 456 };

      activatedRoute.data = of({ successfulOrders });
      comp.ngOnInit();

      expect(comp.successfulOrders).toEqual(successfulOrders);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuccessfulOrders>>();
      const successfulOrders = { id: 123 };
      jest.spyOn(successfulOrdersFormService, 'getSuccessfulOrders').mockReturnValue(successfulOrders);
      jest.spyOn(successfulOrdersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ successfulOrders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: successfulOrders }));
      saveSubject.complete();

      // THEN
      expect(successfulOrdersFormService.getSuccessfulOrders).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(successfulOrdersService.update).toHaveBeenCalledWith(expect.objectContaining(successfulOrders));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuccessfulOrders>>();
      const successfulOrders = { id: 123 };
      jest.spyOn(successfulOrdersFormService, 'getSuccessfulOrders').mockReturnValue({ id: null });
      jest.spyOn(successfulOrdersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ successfulOrders: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: successfulOrders }));
      saveSubject.complete();

      // THEN
      expect(successfulOrdersFormService.getSuccessfulOrders).toHaveBeenCalled();
      expect(successfulOrdersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISuccessfulOrders>>();
      const successfulOrders = { id: 123 };
      jest.spyOn(successfulOrdersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ successfulOrders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(successfulOrdersService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
