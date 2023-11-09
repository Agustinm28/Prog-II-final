import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ClientStocksDetailComponent } from './client-stocks-detail.component';

describe('ClientStocks Management Detail Component', () => {
  let comp: ClientStocksDetailComponent;
  let fixture: ComponentFixture<ClientStocksDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientStocksDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ clientStocks: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ClientStocksDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClientStocksDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load clientStocks on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.clientStocks).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
