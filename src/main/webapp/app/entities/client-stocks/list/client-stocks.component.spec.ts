import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClientStocksService } from '../service/client-stocks.service';

import { ClientStocksComponent } from './client-stocks.component';

describe('ClientStocks Management Component', () => {
  let comp: ClientStocksComponent;
  let fixture: ComponentFixture<ClientStocksComponent>;
  let service: ClientStocksService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'client-stocks', component: ClientStocksComponent }]), HttpClientTestingModule],
      declarations: [ClientStocksComponent],
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
      .overrideTemplate(ClientStocksComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientStocksComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClientStocksService);

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
    expect(comp.clientStocks?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to clientStocksService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getClientStocksIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getClientStocksIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
