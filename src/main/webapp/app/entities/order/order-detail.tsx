import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order.reducer';

export const OrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderEntity = useAppSelector(state => state.order.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderDetailsHeading">
          <Translate contentKey="progIiFinalApp.order.detail.title">Order</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{orderEntity.id}</dd>
          <dt>
            <span id="cliente">
              <Translate contentKey="progIiFinalApp.order.cliente">Cliente</Translate>
            </span>
          </dt>
          <dd>{orderEntity.cliente}</dd>
          <dt>
            <span id="accionId">
              <Translate contentKey="progIiFinalApp.order.accionId">Accion Id</Translate>
            </span>
          </dt>
          <dd>{orderEntity.accionId}</dd>
          <dt>
            <span id="accion">
              <Translate contentKey="progIiFinalApp.order.accion">Accion</Translate>
            </span>
          </dt>
          <dd>{orderEntity.accion}</dd>
          <dt>
            <span id="operacion">
              <Translate contentKey="progIiFinalApp.order.operacion">Operacion</Translate>
            </span>
          </dt>
          <dd>{orderEntity.operacion}</dd>
          <dt>
            <span id="precio">
              <Translate contentKey="progIiFinalApp.order.precio">Precio</Translate>
            </span>
          </dt>
          <dd>{orderEntity.precio}</dd>
          <dt>
            <span id="cantidad">
              <Translate contentKey="progIiFinalApp.order.cantidad">Cantidad</Translate>
            </span>
          </dt>
          <dd>{orderEntity.cantidad}</dd>
          <dt>
            <span id="fechaOperacion">
              <Translate contentKey="progIiFinalApp.order.fechaOperacion">Fecha Operacion</Translate>
            </span>
          </dt>
          <dd>
            {orderEntity.fechaOperacion ? <TextFormat value={orderEntity.fechaOperacion} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="modo">
              <Translate contentKey="progIiFinalApp.order.modo">Modo</Translate>
            </span>
          </dt>
          <dd>{orderEntity.modo}</dd>
        </dl>
        <Button tag={Link} to="/order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order/${orderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderDetail;
