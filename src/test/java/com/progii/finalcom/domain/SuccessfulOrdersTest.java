package com.progii.finalcom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.progii.finalcom.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SuccessfulOrdersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SuccessfulOrders.class);
        SuccessfulOrders successfulOrders1 = new SuccessfulOrders();
        successfulOrders1.setId(1L);
        SuccessfulOrders successfulOrders2 = new SuccessfulOrders();
        successfulOrders2.setId(successfulOrders1.getId());
        assertThat(successfulOrders1).isEqualTo(successfulOrders2);
        successfulOrders2.setId(2L);
        assertThat(successfulOrders1).isNotEqualTo(successfulOrders2);
        successfulOrders1.setId(null);
        assertThat(successfulOrders1).isNotEqualTo(successfulOrders2);
    }
}
