import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClientStocksFormService } from './client-stocks-form.service';
import { ClientStocksService } from '../service/client-stocks.service';
import { IClientStocks } from '../client-stocks.model';

import { ClientStocksUpdateComponent } from './client-stocks-update.component';

describe('ClientStocks Management Update Component', () => {
  let comp: ClientStocksUpdateComponent;
  let fixture: ComponentFixture<ClientStocksUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientStocksFormService: ClientStocksFormService;
  let clientStocksService: ClientStocksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClientStocksUpdateComponent],
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
      .overrideTemplate(ClientStocksUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientStocksUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientStocksFormService = TestBed.inject(ClientStocksFormService);
    clientStocksService = TestBed.inject(ClientStocksService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const clientStocks: IClientStocks = { id: 456 };

      activatedRoute.data = of({ clientStocks });
      comp.ngOnInit();

      expect(comp.clientStocks).toEqual(clientStocks);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientStocks>>();
      const clientStocks = { id: 123 };
      jest.spyOn(clientStocksFormService, 'getClientStocks').mockReturnValue(clientStocks);
      jest.spyOn(clientStocksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientStocks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientStocks }));
      saveSubject.complete();

      // THEN
      expect(clientStocksFormService.getClientStocks).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientStocksService.update).toHaveBeenCalledWith(expect.objectContaining(clientStocks));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientStocks>>();
      const clientStocks = { id: 123 };
      jest.spyOn(clientStocksFormService, 'getClientStocks').mockReturnValue({ id: null });
      jest.spyOn(clientStocksService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientStocks: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: clientStocks }));
      saveSubject.complete();

      // THEN
      expect(clientStocksFormService.getClientStocks).toHaveBeenCalled();
      expect(clientStocksService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClientStocks>>();
      const clientStocks = { id: 123 };
      jest.spyOn(clientStocksService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clientStocks });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientStocksService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
