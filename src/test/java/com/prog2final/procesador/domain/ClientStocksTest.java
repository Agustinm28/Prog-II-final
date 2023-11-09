package com.prog2final.procesador.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.prog2final.procesador.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClientStocksTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClientStocks.class);
        ClientStocks clientStocks1 = new ClientStocks();
        clientStocks1.setId(1L);
        ClientStocks clientStocks2 = new ClientStocks();
        clientStocks2.setId(clientStocks1.getId());
        assertThat(clientStocks1).isEqualTo(clientStocks2);
        clientStocks2.setId(2L);
        assertThat(clientStocks1).isNotEqualTo(clientStocks2);
        clientStocks1.setId(null);
        assertThat(clientStocks1).isNotEqualTo(clientStocks2);
    }
}
